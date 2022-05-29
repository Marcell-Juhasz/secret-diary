package org.hyperskill.secretdiary

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import org.junit.Assert.*
import kotlinx.datetime.Clock
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import java.text.SimpleDateFormat
import java.time.Duration
import java.util.*

@RunWith(RobolectricTestRunner::class)
@Config(shadows = [CustomClockSystemShadow::class])
class Stage2UnitTest {

    private val activityController = Robolectric.buildActivity(MainActivity::class.java)

    private val activity: Activity by lazy {
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

    private val shadowLooper by lazy {
        shadowOf(activity.mainLooper)
    }


    fun Context.identifier(id: String, `package`: String = packageName): Int {
        return resources.getIdentifier(id, "id", `package`)
    }

    inline fun <reified T : View> Activity.find(id: String): T {

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
        val messageEtWrongHint = "etNewWriting should have a hint property with \"Dear Diary...\" value"
        assertEquals(messageEtWrongHint, "Dear Diary...", etNewWriting.hint)
    }

    @Test
    fun testShouldCheckButtonSave() {
        val messageBtnSaveWrongText = "The text of btnSave should be \"Save\""
        assertEquals(messageBtnSaveWrongText, "Save", btnSave.text.toString())
    }

    @Test
    fun testShouldCheckTextView() {val messageTvWrongText = "Initially the text of tvDiary should be empty"
        assertTrue(messageTvWrongText, tvDiary.text.isEmpty())
    }

    @Test
    fun testShouldCheckSaving() {
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
        val messageWrongNumberOfLines2 = "The diary should contain $linesExpected2 lines, but $linesUser2 found"
        assertTrue(messageWrongNumberOfLines2, linesExpected2 == linesUser2)

        val messageWrongOutput2 = "The newer writing should be on the top, separated by an empty line from the older one"
        assertEquals(messageWrongOutput2, expectedOutput2, userOutput2)
    }


    private fun View.clickAndRun(millis: Long = 500){
        this.performClick()
        shadowLooper.idleFor(Duration.ofMillis(millis))
    }

}