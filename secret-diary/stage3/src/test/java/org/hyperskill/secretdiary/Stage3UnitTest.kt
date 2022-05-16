package org.hyperskill.secretdiary

import android.content.Context
import android.os.Looper.getMainLooper
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import kotlinx.datetime.Clock
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowAlertDialog
import java.text.SimpleDateFormat
import java.time.Duration
import java.util.*


@RunWith(RobolectricTestRunner::class)
@Config(shadows = [CustomClockSystemShadow::class])
class Stage3UnitTest {

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

    private val btnUndo by lazy {
        activity.find<Button>("btnUndo")
    }


    @Test
    fun testSas() {

    }


    fun Context.identifier(id: String, `package`: String = packageName): Int {
        return resources.getIdentifier(id, "id", `package`)
    }

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
    fun testShouldCheckButtonUndo() {
        val messageBtnUndoWrongText = "The text of btnUndo should be \"Undo\""
        assertEquals(messageBtnUndoWrongText, "Undo", btnUndo.text.toString())
    }

    @Test
    fun testShouldCheckSavingAndUndo() {

        // First input

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

        shadowOf(getMainLooper()).idleFor(Duration.ofSeconds(300_000))

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
        val messageWrongNumberOfLines2 =
            "The diary should contain $linesExpected2 lines, but $linesUser2 found"
        assertTrue(messageWrongNumberOfLines2, linesExpected2 == linesUser2)

        val messageWrongOutput2 =
            "The newer writing should be on the top, separated by an empty line from the older one"
        assertEquals(messageWrongOutput2, expectedOutput2, userOutput2)

        // Until this, this test function is the same as in the previous stage
        // Now let's test the "Undo" button

        btnUndo.performClick()

        shadowOf(getMainLooper()).runToEndOfTasks()

        ShadowAlertDialog.getLatestAlertDialog()
            .getButton(android.app.AlertDialog.BUTTON_POSITIVE)
            .performClick()

        // After pressing the Undo button, the result should be the same as after the first save

        shadowOf(getMainLooper()).runToEndOfTasks()

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



        btnUndo.performClick()

        shadowOf(getMainLooper()).runToEndOfTasks()

        ShadowAlertDialog.getLatestAlertDialog()
            .getButton(android.app.AlertDialog.BUTTON_NEGATIVE)
            .performClick()

        shadowOf(getMainLooper()).runToEndOfTasks()

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