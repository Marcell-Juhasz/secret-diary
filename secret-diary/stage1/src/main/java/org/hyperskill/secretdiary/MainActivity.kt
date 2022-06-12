package org.hyperskill.secretdiary

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.hyperskill.secretdiary.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSave.setOnClickListener(btnSaveOnClick())
    }

    private fun btnSaveOnClick() = fun(_: View) {
        val userInput = binding.etNewWriting.text.toString()
        if (userInput.isNotBlank()) {
            binding.tvDiary.text = userInput
            binding.etNewWriting.text.clear()
        } else {
            Toast.makeText(this, "Empty or blank input cannot be saved", Toast.LENGTH_SHORT).show()
        }
    }
}