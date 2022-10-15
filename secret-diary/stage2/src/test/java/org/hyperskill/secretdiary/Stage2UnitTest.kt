package org.hyperskill.secretdiary

import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toKotlinInstant
import kotlinx.datetime.toLocalDateTime
import org.hyperskill.secretdiary.internals.AbstractUnitTest
import org.hyperskill.secretdiary.internals.CustomClockSystemShadow
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowToast
import java.text.SimpleDateFormat
import java.time.Duration
import java.util.*


@RunWith(RobolectricTestRunner::class)
@Config(shadows = [CustomClockSystemShadow::class])
class Stage2UnitTest : AbstractUnitTest<MainActivity>(MainActivity::class.java) {

    private val etNewWriting by lazy {
        val etNewWriting = activity.findViewByString<EditText>("etNewWriting")

        val messageEtWrongHint =
            "etNewWriting should have a hint property with \"Dear Diary...\" value"
        assertEquals(messageEtWrongHint, "Dear Diary...", etNewWriting.hint)

        etNewWriting
    }

    private val btnSave by lazy {
        val btnSave = activity.findViewByString<Button>("btnSave")

        val messageBtnSaveWrongText = "The text of btnSave should be \"Save\""
        assertEquals(messageBtnSaveWrongText, "Save", btnSave.text.toString())

        btnSave
    }

    private val tvDiary by lazy {
        val tvDiary = activity.findViewByString<TextView>("tvDiary")

        val messageTvWrongText = "Initially the text of tvDiary should be empty"
        assertTrue(messageTvWrongText, tvDiary.text.isEmpty())

        tvDiary
    }

    @Test
    fun testShouldCheckEditText() {
        testActivity {
            etNewWriting
        }
    }

    @Test
    fun testShouldCheckButtonSave() {
        testActivity {
            btnSave
        }
    }

    @Test
    fun testShouldCheckTextView() {
        testActivity {
            tvDiary
        }
    }

    @Test
    fun testShouldCheckSaving() {
        testActivity {
            // ensure all views used on test are initialized with initial state
            etNewWriting
            btnSave
            tvDiary
            //

            // First input

            val sampleInputText1 = "This was an awesome day"
            etNewWriting.setText(sampleInputText1)
            val instant1 = Clock.System.now()
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val dateTimeText1 = simpleDateFormat.format(instant1.toEpochMilliseconds())
            val dateTime1 = simpleDateFormat.parse(dateTimeText1)?.toInstant()?.toKotlinInstant()
                ?.toLocalDateTime(TimeZone.currentSystemDefault())
            btnSave.clickAndRun()

            val expectedOutput1 = """
            $dateTimeText1
            $sampleInputText1
        """.trimIndent()
            val userOutput1 = tvDiary.text.toString()

            // check number of lines
            val linesExpected1 = expectedOutput1.split("\n").size
            val linesUser1 = userOutput1.split("\n").size
            val messageWrongNumberOfLines1 =
                "The diary should contain $linesExpected1 lines, but $linesUser1 found"
            assertTrue(messageWrongNumberOfLines1, linesExpected1 == linesUser1)

            // check date and time format
            val dateTimeRegex = Regex("\\d{4}-\\d{2}-\\d{2}[ ]\\d{2}:\\d{2}:\\d{2}")
            val userDateTimeText1 = userOutput1.substringBefore("\n")
            val userDateTime1 =
                simpleDateFormat.parse(userDateTimeText1)?.toInstant()?.toKotlinInstant()
                    ?.toLocalDateTime(TimeZone.currentSystemDefault())
            val messageWrongDateTimeFormat =
                "Date and time should be on the first line in the following format: \"yyyy-MM-dd HH:mm:ss\""
            assertTrue(messageWrongDateTimeFormat, dateTimeRegex.matches(userDateTimeText1))

            // check date (year, month, day) values
            val messageWrongYearValue = "Wrong year value!"
            assertEquals(messageWrongYearValue, dateTime1?.year, userDateTime1?.year)

            val messageWrongMonthValue = "Wrong month value!"
            assertEquals(messageWrongMonthValue, dateTime1?.monthNumber, userDateTime1?.monthNumber)

            val messageWrongDayValue = "Wrong day value!"
            assertEquals(messageWrongDayValue, dateTime1?.dayOfMonth, userDateTime1?.dayOfMonth)


            // check HOUR matching (Time Zone error)
            val messageWrongTimeZone = "You should use your local time zone!"
            assertEquals(messageWrongTimeZone, dateTime1?.hour, userDateTime1?.hour)


            val messageWrongOutput1 =
                "The first line should be the date and time and the second line is the saved text"
            assertEquals(messageWrongOutput1, expectedOutput1, userOutput1)

            val messageEtNotCleared = "EditText should be cleared after each saving"
            assertTrue(messageEtNotCleared, etNewWriting.text.isEmpty())

            val randSec = (100_000..300_000).random()
            shadowLooper.idleFor(Duration.ofSeconds(randSec.toLong()))

            // Second input

            val sampleInputText2 = "I had a date with my crush"
            etNewWriting.setText(sampleInputText2)
            val instant2 = Clock.System.now()
            val dateTimeText2 = simpleDateFormat.format(instant2.toEpochMilliseconds())
            btnSave.clickAndRun()

            val expectedOutput2 = """
            $dateTimeText2
            $sampleInputText2
            
            $dateTimeText1
            $sampleInputText1
        """.trimIndent()
            val userOutput2 = tvDiary.text.toString()

            val linesExpected2 = expectedOutput2.split("\n").size
            val linesUser2 = userOutput2.split("\n").size
            val messageWrongNumberOfLines2 =
                "The diary should contain $linesExpected2 lines, but $linesUser2 found"
            assertTrue(messageWrongNumberOfLines2, linesExpected2 == linesUser2)

            val messageWrongOutput2 =
                "The newer writing should be on the top, separated by an empty line from the older one"
            assertEquals(messageWrongOutput2, expectedOutput2, userOutput2)
        }
    }

    @Test
    fun testShouldCheckSavingBlank() {
        testActivity {
            // ensure all views used on test are initialized with initial state
            etNewWriting
            btnSave
            tvDiary
            //

            val sampleInputText1 = "First input"
            etNewWriting.setText(sampleInputText1)
            btnSave.performClick()

            val sampleInputText2 = "Second input"
            etNewWriting.setText(sampleInputText2)
            btnSave.performClick()

            val diaryTextBeforeSaveBlank = tvDiary.text

            val inputBlankString = """
                  
                          
            """.trimIndent()
            etNewWriting.setText(inputBlankString)
            btnSave.clickAndRun()

            val userToastText = ShadowToast.getTextOfLatestToast()
            val savingBlankToastText = "Empty or blank input cannot be saved"
            val messageWrongToastText =
                "When trying to save an empty or blank string, the appropriate Toast message should be shown"
            assertEquals(messageWrongToastText, savingBlankToastText, userToastText)

            val diaryTextAfterSaveBlank = tvDiary.text
            val messageWrongInputFormat = "Do not save blank text!"
            assertEquals(messageWrongInputFormat, diaryTextBeforeSaveBlank, diaryTextAfterSaveBlank)
        }
    }

}