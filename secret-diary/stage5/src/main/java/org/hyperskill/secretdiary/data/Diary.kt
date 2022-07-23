package org.hyperskill.secretdiary.data

class Diary() : MutableList<Writing> by mutableListOf() {
    override fun toString(): String = joinToString("\n\n")

    constructor(rawText: String) : this() {
        initFromText(rawText)
    }

    fun initFromText(rawText: String) {
        if (rawText.isNotBlank()) {
            val rawList = rawText.split("\n\n")
            rawList.forEach {
                val date = it.substringBefore("\n")
                val text = it.substringAfter("\n")
                this.add(Writing(date, text))
            }
        }
    }
}
