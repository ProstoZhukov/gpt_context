package ru.tensor.sbis.hallscheme.v2.business.model.textconfig

/**
 * Насыщенность текста на столе (обычный или жирный).
 */
enum class HallSchemeFontWeight(val stringValue: String) {

    NORMAL("normal"),
    BOLD("bold");

    companion object {
        fun fromString(value: String?): HallSchemeFontWeight =
            values().find { it.stringValue == value } ?: NORMAL
    }
}