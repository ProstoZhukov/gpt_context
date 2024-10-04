package ru.tensor.sbis.design.view.input.mask

/**
 * Возможные символы маски
 *
 * @author ps.smirnyh
 */
internal enum class MaskSymbol(val symbol: Char, val placeholder: Char = WIDE_SPACE_PLACEHOLDER) {
    /** Любая буква. */
    LETTER('A') {
        override fun matches(char: Char) = char.isLetter()
    },

    /** Любая цифра. */
    DIGIT('0') {
        override fun matches(char: Char) = char.isDigit()
    },

    /** Любой символ. */
    ANY('*') {
        override fun matches(char: Char) = true
    },

    /** Фиксированный символ, заданный маской. */
    FIXED('@', '@') {
        override fun matches(char: Char) = false
    };

    abstract fun matches(char: Char): Boolean

    companion object Mapper {
        fun getByChar(c: Char) = values().firstOrNull { it.symbol == c } ?: FIXED
    }
}

/**
 * Символ широкого пробела.
 * Нужен для замены пустых значений без сдвига маски при вводе символов из-за разницы ширины.
 */
internal const val WIDE_SPACE_PLACEHOLDER = 0x2007.toChar()