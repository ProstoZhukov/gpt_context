package ru.tensor.sbis.design.text_span.text.masked.mask

/**
 * Возможные символы маски.
 *
 * @author ma.kolpakov
 */
internal enum class MaskSymbol(val symbol: Char, val placeholder: Char = ' ') {
    /** Любая буква. */
    LETTER('A') {
        override fun matches(char: Char) = char.isLetter()
    },
    /** Любая цифра. */
    DIGIT('#') {
        override fun matches(char: Char) = char.isDigit()
    },
    /** Любая цифра или буква. */
    LETTER_OR_DIGIT('?') {
        override fun matches(char: Char) = char.isLetterOrDigit()
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