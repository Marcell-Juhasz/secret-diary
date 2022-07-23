package org.hyperskill.secretdiary

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import kotlinx.datetime.Clock
import org.hyperskill.secretdiary.internals.AbstractUnitTest
import org.hyperskill.secretdiary.internals.CustomClockSystemShadow
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowAlertDialog
import org.robolectric.shadows.ShadowToast
import java.text.SimpleDateFormat
import java.time.Duration
import java.util.*

@RunWith(RobolectricTestRunner::class)
@Config(shadows = [CustomClockSystemShadow::class])
class Stage4UnitTest : AbstractUnitTest<MainActivity>(MainActivity::class.java) {

    companion object {
        const val PREF_DIARY = "PREF_DIARY"
        const val KEY_DIARY_TEXT = "KEY_DIARY_TEXT"
    }

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

        tvDiary
    }

    private val btnUndo by lazy {
        val btnUndo = activity.findViewByString<Button>("btnUndo")

        val messageBtnUndoWrongText = "The text of btnUndo should be \"Undo\""
        assertEquals(messageBtnUndoWrongText, "Undo", btnUndo.text.toString())

        btnUndo
    }

    private val sharedPreferences: SharedPreferences by lazy {
        RuntimeEnvironment.getApplication().getSharedPreferences(
            PREF_DIARY, MODE_PRIVATE
        )
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
    fun testShouldCheckButtonUndo() {
        testActivity {
            btnUndo
        }
    }

    @Test
    fun testShouldCheckSavingAndUndo() {
        testActivity {
            // ensure all views used on test are initialized with initial state
            etNewWriting
            btnUndo
            btnSave
            tvDiary
            //

            // First input

            val sampleInputText1 = "This was an awesome day"
            etNewWriting.setText(sampleInputText1)
            val instant1 = Clock.System.now()
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val dateText1 = simpleDateFormat.format(instant1.toEpochMilliseconds())
            btnSave.clickAndRun()

            val expectedOutput1 = """
            $dateText1
            $sampleInputText1
        """.trimIndent()
            val userOutput1 = tvDiary.text.toString()

            val messageWrongDate1 =
                "Make sure your date/time has the following format with the correct values: \"yyyy-MM-dd HH:mm:ss\""
            assertTrue(messageWrongDate1, userOutput1.contains(dateText1))

            val linesExpected1 = expectedOutput1.split("\n").size
            val linesUser1 = userOutput1.split("\n").size
            val messageWrongNumberOfLines1 =
                "The diary should contain $linesExpected1 lines, but $linesUser1 found"
            assertTrue(messageWrongNumberOfLines1, linesExpected1 == linesUser1)

            val messageWrongOutput1 =
                "The first line should be the date and time, the second line is the saved text"
            assertEquals(messageWrongOutput1, expectedOutput1, userOutput1)

            val messageEtNotCleared = "EditText should be cleared after each saving"
            assertTrue(messageEtNotCleared, etNewWriting.text.isEmpty())

            shadowLooper.idleFor(Duration.ofSeconds(300_000))

            // Second input

            val sampleInputText2 = "I had a date with my crush"
            etNewWriting.setText(sampleInputText2)
            val instant2 = Clock.System.now()
            val dateText2 = simpleDateFormat.format(instant2.toEpochMilliseconds())
            btnSave.clickAndRun()

            val expectedOutput2 = """
            $dateText2
            $sampleInputText2
            
            $dateText1
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

            // Until this, this test function is the same as in the previous stage
            // Now let's test the "Undo" button

            performUndoAndYesClick()

            // After pressing the Undo button, the result should be the same as after the first save

            val expectedOutput3 = expectedOutput1
            val userOutput3 = tvDiary.text.toString()

            val linesExpected3 = linesExpected1
            val linesUser3 = userOutput3.split("\n").size
            val messageWrongNumberOfLines3 =
                "The diary should contain $linesExpected3 lines, but $linesUser3 found"
            assertTrue(messageWrongNumberOfLines3, linesExpected3 == linesUser3)

            val messageWrongOutput3 =
                "The \"Undo\" button should remove the last note if \"Yes\" selected on the AlertDialog"
            assertEquals(messageWrongOutput3, expectedOutput3, userOutput3)


            btnUndo.clickAndRun()

            ShadowAlertDialog.getLatestAlertDialog()
                .getButton(android.app.AlertDialog.BUTTON_NEGATIVE)
                .clickAndRun()

            val expectedOutput4 = expectedOutput3
            val userOutput4 = tvDiary.text.toString()
            val linesExpected4 = linesExpected3
            val linesUser4 = userOutput4.split("\n").size
            val messageWrongNumberOfLines4 =
                "The diary should contain $linesExpected4 lines, but $linesUser4 found"
            assertTrue(messageWrongNumberOfLines4, linesExpected4 == linesUser4)

            val messageWrongOutput4 =
                "The \"Undo\" button should not do anything if \"No\" selected on the AlertDialog"
            assertEquals(messageWrongOutput4, expectedOutput4, userOutput4)
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

    @Test
    fun testShouldCheckUndoBlank() {
        testActivity {
            // ensure all views used on test are initialized with initial state
            etNewWriting
            btnSave
            tvDiary
            btnUndo
            //

            tvDiary.text = ""

            performUndoAndYesClick()
            performUndoAndYesClick()
        }
    }

    @Test
    fun testShouldCheckSave() {

        testActivity {
            // ensure all views used on test are initialized with initial state
            etNewWriting
            btnSave
            tvDiary
            btnUndo
            //

            // First input

            val sampleInputText1 = "This was an awesome day"
            etNewWriting.setText(sampleInputText1)
            val instant1 = Clock.System.now()
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val dateText1 = simpleDateFormat.format(instant1.toEpochMilliseconds())
            btnSave.clickAndRun()

            val diaryText1 = """
            $dateText1
            $sampleInputText1
        """.trimIndent()

            shadowLooper.idleFor(Duration.ofSeconds(300_000))

            // Second input

            val sampleInputText2 = "I had a date with my crush"
            etNewWriting.setText(sampleInputText2)
            val instant2 = Clock.System.now()
            val dateText2 = simpleDateFormat.format(instant2.toEpochMilliseconds())
            btnSave.clickAndRun()

            val diaryText2 = """
            $dateText2
            $sampleInputText2
            
            $dateText1
            $sampleInputText1
        """.trimIndent()

            sharedPreferences

            val actualPersistedValue = sharedPreferences.getString(KEY_DIARY_TEXT, "null")
            val messagePersistenceNotWorking =
                "\"Save\" button should store the text of the diary in SharedPreferences"
            assertEquals(messagePersistenceNotWorking, diaryText2, actualPersistedValue)
        }
    }

    @Test
    fun testShouldCheckRestoreAndButtons() {

        val sampleInputText1 = "This was an awesome day"
        val instant1 = Clock.System.now()
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val dateText1 = simpleDateFormat.format(instant1.toEpochMilliseconds())

        shadowLooper.idleFor(Duration.ofSeconds(200_000))

        val sampleInputText2 = "I had a date with my crush"
        val instant2 = Clock.System.now()
        val dateText2 = simpleDateFormat.format(instant2.toEpochMilliseconds())

        val persistedText = """
            $dateText2
            $sampleInputText2
            
            $dateText1
            $sampleInputText1
        """.trimIndent()

        sharedPreferences.edit().putString(KEY_DIARY_TEXT, persistedText).commit()


        testActivity {

            tvDiary

            val actualRestoredValue = tvDiary.text.toString()//.lowercase()
            val messagePersistenceNotWorking =
                "Saved text should be restored into the diary on Activity start"
            assertEquals(messagePersistenceNotWorking, persistedText, actualRestoredValue)


            // Text after performing an Undo operation on the restored diary
            val diaryText1 = """
            $dateText1
            $sampleInputText1
        """.trimIndent()

            performUndoAndYesClick()

            val diaryAfterUndo = tvDiary.text.toString()
            val messageUndoNotWorking = "\"Undo\" button should also work for the restored text"
            assertEquals(messageUndoNotWorking, diaryText1, diaryAfterUndo)


            // Add a new writing
            val sampleInputText3 = "I was sunbathing on the beach"
            etNewWriting.setText(sampleInputText3)
            val instant3 = Clock.System.now()
            val dateText3 = simpleDateFormat.format(instant3.toEpochMilliseconds())

            btnSave.clickAndRun(3000)

            val diaryText3 = """
            $dateText3
            $sampleInputText3
            
            $dateText1
            $sampleInputText1
        """.trimIndent()


            val diaryAfterSave = tvDiary.text.toString()
            val messageSaveNotWorking = "\"Save\" button should also work for the restored text"
            assertEquals(messageSaveNotWorking, diaryText3, diaryAfterSave)
        }
    }


    // I will delete this if we don't need
    /*@Test
    fun testShouldCheckPersistAndRestoreWithLifecycle() {
        // I don't think this is really necessary, you can test persist and restore separately, but if you prefer this way it is possible

        val returnedValue = testActivity {
            // ensure all views used on test are initialized with initial state
            etNewWriting
            btnSave
            tvDiary
            btnUndo
            //

            // First input

            val sampleInputText1 = "This was an awesome day"
            etNewWriting.setText(sampleInputText1)
            val instant1 = Clock.System.now()
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val dateText1 = simpleDateFormat.format(instant1.toEpochMilliseconds())
            btnSave.clickAndRun()

            val diaryText1 = """
            $dateText1
            $sampleInputText1
        """.trimIndent()

            shadowLooper.idleFor(Duration.ofSeconds(300_000))

            // Second input

            val sampleInputText2 = "I had a date with my crush"
            etNewWriting.setText(sampleInputText2)
            val instant2 = Clock.System.now()
            val dateText2 = simpleDateFormat.format(instant2.toEpochMilliseconds())
            btnSave.clickAndRun()

            val diaryText2 = """
            $dateText2
            $sampleInputText2
            
            $dateText1
            $sampleInputText1
        """.trimIndent()

            sharedPreferences

            val actualPersistedValue = sharedPreferences.getString(KEY_DIARY_TEXT, "null")
            assertEquals("some error message", diaryText2, actualPersistedValue)

            //return the value
            actualPersistedValue
        }


        activityController
            .pause()
            .stop()
            .destroy()

        // instantiate an anonymous AbstractUnitTest
        val recreateActivityUnitTest =
            object : AbstractUnitTest<MainActivity>(MainActivity::class.java) {}

        // set the state to be restored
        val sharedPreferences = recreateActivityUnitTest.activity.application.getSharedPreferences(
            PREF_DIARY, MODE_PRIVATE
        )
        sharedPreferences.edit().putString(KEY_DIARY_TEXT, returnedValue).commit()

        //do the testing
        recreateActivityUnitTest.testActivity(/*savedInstanceState = bundle*/) {

            // I have changed the member of AbstractUnitTest to public so it is possible to have access to its members
            // I did this change some days ago on the template too, so you can make everything public on AbstractUnitClass for all stages to follow along (files with same name should have same content across stages)
            recreateActivityUnitTest.activityController
            recreateActivityUnitTest.activity

            // be careful the original activity is destroyed, you have use recreateActivity.thePropertyYouWant
            // also the old views are gone, find what you are going to use again

            val tvDiary = recreateActivityUnitTest.activity.findViewByString<TextView>("tvDiary")

            val actualRestoredValue = tvDiary.text.toString().lowercase()
            assertEquals("Some error message!", "wrong value expected", actualRestoredValue)
        }
    }*/

    private fun performUndoAndYesClick() {
        btnUndo.clickAndRun()

        ShadowAlertDialog.getLatestAlertDialog()
            .getButton(android.app.AlertDialog.BUTTON_POSITIVE)
            .clickAndRun()
    }

}