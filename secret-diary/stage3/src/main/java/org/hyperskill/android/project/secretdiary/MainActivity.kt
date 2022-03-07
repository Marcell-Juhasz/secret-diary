package org.hyperskill.android.secretdiary

import android.content.Context
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.datetime.Clock
import org.hyperskill.android.secretdiary.data.Diary
import org.hyperskill.android.secretdiary.data.Writing
import org.hyperskill.android.secretdiary.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.*

// "Undo" button + AlertDialog
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    companion object {
        private lateinit var diary: Diary
        const val PREF_DIARY = "PREF_DIARY"
        const val KEY_DIARY_TEXT = "KEY_DIARY_TEXT"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSave.setOnClickListener(btnSaveOnClick())
        binding.btnUndo.setOnClickListener(btnUndoOnClick())
        binding.tvDiary.movementMethod = ScrollingMovementMethod()

        diary = Diary()
        // initDiaryFromSaved()
        binding.tvDiary.text = diary.toString()

    }


    private fun initDiaryFromSaved() {
        getSharedPreferences(PREF_DIARY, MODE_PRIVATE)
            .getString(KEY_DIARY_TEXT, "")
            ?.let { diary.initFromText(it) }
    }

    private fun btnSaveOnClick() = fun(_: View) {
        val instant = Clock.System.now()
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        diary.add(0,
            Writing(simpleDateFormat.format(instant.toEpochMilliseconds()),
                binding.etNewWriting.text.toString()))
        binding.tvDiary.text = diary.toString()
        binding.etNewWriting.text.clear()
    }

    private fun btnUndoOnClick() = fun(_: View) {
        android.app.AlertDialog.Builder(this)
            .setTitle("Remove last note")
            .setMessage("Do you really want to remove the last writing? This operation cannot be undone!")
            .setPositiveButton("Yes") { dialog, which ->
                diary.removeFirstOrNull()
                print("HEEEEEEELLLLLLOOOOOO")
                updateTvDiary()
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }


    private fun updateTvDiary() {
        binding.tvDiary.text = diary.toString()
    }


    override fun onStop() {
        super.onStop()
        val sp = getSharedPreferences(PREF_DIARY, Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.putString(KEY_DIARY_TEXT, diary.toString())
        editor.apply()
    }
}