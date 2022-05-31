package org.hyperskill.secretdiary

import android.content.Context
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import org.hyperskill.secretdiary.internals.AbstractUnitTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class Stage1UnitTest : AbstractUnitTest<MainActivity>(MainActivity::class.java) {

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

            val sampleInputText = "This was an awesome day"
            etNewWriting.setText(sampleInputText)
            btnSave.performClick()

            val messageWritingNotSaved =
                "By clicking on the Save button, the writing should appear on tvDiary"
            assertEquals(messageWritingNotSaved, sampleInputText, tvDiary.text.toString())

            val messageEtNotCleared = "EditText should be cleared after each saving"
            assertTrue(messageEtNotCleared, etNewWriting.text.isEmpty())

            // Second input

            val sampleInputText2 = "I had a date with my crush"
            etNewWriting.setText(sampleInputText2)
            btnSave.performClick()

            val messageDiaryNotOverwritten =
                "By clicking on the Save button, the writing should overwrite the text of tvDiary"
            assertEquals(messageDiaryNotOverwritten, sampleInputText2, tvDiary.text.toString())
        }
    }


}