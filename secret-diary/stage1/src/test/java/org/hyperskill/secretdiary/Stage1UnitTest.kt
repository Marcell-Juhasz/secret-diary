package org.hyperskill.secretdiary

import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class Stage1UnitTest {

    private val activityController = Robolectric.buildActivity(MainActivity::class.java)

    private val activity: MainActivity by lazy {
        activityController.setup().get()
    }

    private val etNewWriting by lazy {
        activity.find<EditText>("etNewWriting")
    }

    private val btnSave by lazy {
        activity.find<Button>("btnSave")
    }

    private val tvDiary by lazy {
        activity.find<TextView>("tvDiary")
    }


    fun Context.identifier(id: String, `package`: String = packageName): Int {
        return resources.getIdentifier(id, "id", `package`)
    }

    // using this "find()" method instead of "findViewById() prevents build failure if the id does not exist"
    inline fun <reified T : View> MainActivity.find(id: String): T {

        val maybeView: View? = findViewById(identifier(id))

        val idNotFoundMessage = "View with id \"$id\" was not found"
        val wrongClassMessage = "View with id \"$id\" is not from expected class. " +
                "Expected ${T::class.java.simpleName} found ${maybeView?.javaClass?.simpleName}"

        assertNotNull(idNotFoundMessage, maybeView)
        assertTrue(wrongClassMessage, maybeView is T)
        return maybeView as T
    }


    @Test
    fun testShouldCheckEditText() {
        val messageEtWrongHint =
            "etNewWriting should have a hint property with \"Dear Diary...\" value"
        assertEquals(messageEtWrongHint, "Dear Diary...", etNewWriting.hint)
    }

    @Test
    fun testShouldCheckButtonSave() {
        val messageBtnSaveWrongText = "The text of btnSave should be \"Save\""
        assertEquals(messageBtnSaveWrongText, "Save", btnSave.text.toString())
    }

    @Test
    fun testShouldCheckTextView() {
        val messageTvWrongText = "Initially the text of tvDiary should be empty"
        assertTrue(messageTvWrongText, tvDiary.text.isEmpty())
    }

    @Test
    fun testShouldCheckSaving() {
        val sampleInputText = "This was an awesome day"
        etNewWriting.setText(sampleInputText)
        btnSave.performClick()

        val messageWritingNotSaved =
            "By clicking on the Save button, the writing should appear on tvDiary"
        assertEquals(messageWritingNotSaved, sampleInputText, tvDiary.text.toString())

        val messageEtNotCleared = "EditText should be cleared after each saving"
        assertTrue(messageEtNotCleared, etNewWriting.text.isEmpty())

        val sampleInputText2 = "I had a date with my crush"
        etNewWriting.setText(sampleInputText2)
        btnSave.performClick()

        val messageDiaryNotOverwritten =
            "By clicking on the Save button, the writing should overwrite the text of tvDiary"
        assertEquals(messageDiaryNotOverwritten, sampleInputText2, tvDiary.text.toString())
    }
}