package org.hyperskill.android.secretdiary.data

data class Writing(
    val date: String,
    val text: String
) {
    override fun toString(): String = "$date\n$text"
}
