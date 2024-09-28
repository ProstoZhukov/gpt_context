package ru.tensor.sbis.hallscheme.v2.business.model.textconfig

/**
 * Оформление текста (подчёркнутый, перечёркнутый, всё сразу или ничего).
 */
enum class HallSchemeFontDecoration(val stringValue: String) {

    NORMAL("normal"),
    UNDERLINE("underline"),
    LINE_THROUGH("line-through"),
    UNDERLINE_LINE_THROUGH("underline line-through");

    companion object {
        fun fromString(value: String?): HallSchemeFontDecoration =
            values().find { it.stringValue == value } ?: NORMAL
    }
}