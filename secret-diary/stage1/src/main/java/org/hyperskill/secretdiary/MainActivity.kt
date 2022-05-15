package org.hyperskill.secretdiary

import android.os.Bundle
import android.view.View
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
        binding.tvDiary.text = binding.etNewWriting.text.toString()
        binding.etNewWriting.text.clear()
    }
}