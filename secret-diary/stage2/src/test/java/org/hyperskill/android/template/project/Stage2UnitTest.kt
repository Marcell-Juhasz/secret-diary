package org.hyperskill.android.secretdiary

import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import junit.framework.Assert.*
import kotlinx.datetime.Clock
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import java.text.SimpleDateFormat
import java.util.*

@RunWith(RobolectricTestRunner::class)
class Stage2UnitTest {

    private val activityController = Robolectric.buildActivity(MainActivity::class.java)

    @Test
    fun testSas() {

    }

    @Test
    fun testShouldCheckEditTextExist() {
        val activity = activityController.setup().get()
        val etNewWriting = activity.findViewById<EditText>(R.id.etNewWriting)

        val messageEtNotExist = "Cannot find EditText with id \"etNewWriting\""
        assertNotNull(messageEtNotExist, etNewWriting)

        val messageEtWrongHint = "etNewWriting should have a hint property with \"Dear Diary...\" value"
        assertEquals(messageEtWrongHint, "Dear Diary...", etNewWriting.hint)
    }

    @Test
    fun testShouldCheckButtonSaveExist() {
        val activity = activityController.setup().get()
        val btnSave = activity.findViewById<Button>(R.id.btnSave)

        val messageBtnSaveNotExist = "Cannot find Button with id \"btnSave\""
        assertNotNull(messageBtnSaveNotExist, btnSave)

        val messageBtnSaveWrongText = "The text of btnSave should be \"Save\""
        assertEquals(messageBtnSaveWrongText, "Save", btnSave.text.toString())
    }

    @Test
    fun testShouldCheckTextViewExist() {
        val activity = activityController.setup().get()
        val tvDiary = activity.findViewById<TextView>(R.id.tvDiary)

        val messageTvNotExist = "Cannot find TextView with id \"tvDiary\""
        assertNotNull(messageTvNotExist, tvDiary)

        val messageTvWrongText = "Initially the text of tvDiary should be empty"
        assertTrue(messageTvWrongText, tvDiary.text.isEmpty())
    }

    @Test
    fun testShouldCheckSaving() {
        val activity = activityController.setup().get()
        val etNewWriting = activity.findViewById<EditText>(R.id.etNewWriting)
        val btnSave = activity.findViewById<Button>(R.id.btnSave)
        val tvDiary = activity.findViewById<TextView>(R.id.tvDiary)

        val sampleInputText1 = "This was an awesome day"
        etNewWriting.setText(sampleInputText1)
        val instant1 = Clock.System.now()
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val dateText1 = simpleDateFormat.format(instant1.toEpochMilliseconds())
        btnSave.performClick()

        val expectedOutput1 = """
            $dateText1
            $sampleInputText1
        """.trimIndent()
        val userOutput1 = tvDiary.text.toString()

        val messageWrongDate1 = "Make sure your date/time has the following format with the correct values: \"yyyy-MM-dd HH:mm:ss\""
        assertTrue(messageWrongDate1, userOutput1.contains(dateText1))

        val linesExpected1 = expectedOutput1.split("\n").size
        val linesUser1 = userOutput1.split("\n").size
        val messageWrongNumberOfLines1 = "The diary should contain $linesExpected1 lines, but $linesUser1 found"
        assertTrue(messageWrongNumberOfLines1, linesExpected1 == linesUser1)

        val messageWrongOutput1 = "The first line should be the date and time, the second line is the saved text"
        assertEquals(messageWrongOutput1, expectedOutput1, userOutput1)

        val messageEtNotCleared = "EditText should be cleared after each saving"
        assertTrue(messageEtNotCleared, etNewWriting.text.isEmpty())

        Thread.sleep(3000) // wait 3 seconds
        // Second input

        val sampleInputText2 = "I had a date with my crush"
        etNewWriting.setText(sampleInputText2)
        val instant2 = Clock.System.now()
        val dateText2 = simpleDateFormat.format(instant2.toEpochMilliseconds())
        btnSave.performClick()

        val expectedOutput2 = """
            $dateText2
            $sampleInputText2
            
            $dateText1
            $sampleInputText1
        """.trimIndent()
        val userOutput2 = tvDiary.text.toString()

        val linesExpected2 = expectedOutput2.split("\n").size
        val linesUser2 = userOutput2.split("\n").size
        val messageWrongNumberOfLines2 = "The diary should contain $linesExpected2 lines, but $linesUser2 found"
        assertTrue(messageWrongNumberOfLines2, linesExpected2 == linesUser2)

        val messageWrongOutput2 = "The newer writing should be on the top, separated by an empty line from the older one"
        assertEquals(messageWrongOutput2, expectedOutput2, userOutput2)
    }

}