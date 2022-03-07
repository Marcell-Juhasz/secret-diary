package org.hyperskill.android.secretdiary

import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import junit.framework.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class Stage1UnitTest {

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

        val sampleInputText = "This was an awesome day"
        etNewWriting.setText(sampleInputText)
        btnSave.performClick()

        val messageWritingNotSaved = "By clicking on the Save button, the writing should appear on tvDiary"
        assertEquals(messageWritingNotSaved, sampleInputText, tvDiary.text.toString())

        val messageEtNotCleared = "EditText should be cleared after each saving"
        assertTrue(messageEtNotCleared, etNewWriting.text.isEmpty())

        val sampleInputText2 = "I had a date with my crush"
        etNewWriting.setText(sampleInputText2)
        btnSave.performClick()

        val messageDiaryNotOverwritten = "By clicking on the Save button, the writing should overwrite the text of tvDiary"
        assertEquals(messageDiaryNotOverwritten, sampleInputText2, tvDiary.text.toString())
    }

}