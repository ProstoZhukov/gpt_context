package ru.tensor.sbis.design.text_span.text.masked.formatter.simple

import ru.tensor.sbis.design.text_span.text.masked.mask.MaskSymbol

/**
 * Реализация [SimpleFormatter] для форматирования текста с использованием заданной маски [mask]
 *
 * @author us.bessonov
 */
internal class SimpleFormatterImpl(private val mask: CharSequence) : SimpleFormatter {

    override fun apply(s: CharSequence): CharSequence {
        val formatted = StringBuilder()
        var maskPos = 0
        var inputPos = 0

        while (maskPos < mask.length && inputPos < s.length) {
            val maskEntry = mask[maskPos]
            val char = s[inputPos]
            val type = MaskSymbol.getByChar(maskEntry)

            if (type == MaskSymbol.FIXED) {
                formatted.append(maskEntry)
                maskPos ++
            } else {
                if (type.matches(char)) {
                    formatted.append(char)
                    maskPos++
                }
                inputPos++
            }
        }

        if (inputPos < s.length) {
            formatted.append(s.subSequence(inputPos, s.length))
        }

        return formatted
    }
}