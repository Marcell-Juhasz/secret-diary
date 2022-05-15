package org.hyperskill.android.secretdiary.data

import kotlinx.datetime.toLocalDateTime

class Diary() : MutableList<Writing> by mutableListOf() {
    override fun toString(): String = joinToString("\n\n")

    constructor(rawText: String) : this() {
        initFromText(rawText)
    }

    fun initFromText(rawText: String) {
        val rawList = rawText.split("\n\n")
        rawList.forEach {
            val date = it.substringBefore("\n")
            val text = it.substringAfter("\n")
            this.add(Writing(date, text))
        }
    }
}
