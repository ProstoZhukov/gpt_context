package ru.tensor.sbis.date_picker

/**
 * @author mb.kruglova
 */
class PeriodText(private val fromText: String?, private val toText: String? = null) {

    val isNotEmpty: Boolean
        get() = !fromText.isNullOrEmpty()

    fun toTwoLinesString(): String {
        return StringBuilder()
            .append(fromText)
            .append(" -")
            .append("\n")
            .append(toText)
            .toString()
    }

    override fun toString(): String {
        val stringBuilder = StringBuilder().append(fromText)
        if (toText != null) {
            stringBuilder.append(" - ").append(toText)
        }
        return stringBuilder.toString()
    }
}