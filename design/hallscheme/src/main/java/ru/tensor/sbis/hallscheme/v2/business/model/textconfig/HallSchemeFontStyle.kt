package ru.tensor.sbis.hallscheme.v2.business.model.textconfig

/**
 * Тип начертания текста (нормальный или курсив).
 */
enum class HallSchemeFontStyle(val stringValue: String) {

    NORMAL("normal"),
    ITALIC("italic");

    companion object {
        fun fromString(value: String?): HallSchemeFontStyle =
            values().find { it.stringValue == value } ?: NORMAL
    }
}