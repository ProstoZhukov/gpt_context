package ru.tensor.sbis.design.text_span.text.masked.mask

import ru.tensor.sbis.design.text_span.text.masked.formatter.Formatter
import java.lang.StringBuilder

/**
 * Инструмент для разбора маски на последовательность значимых символов [types] для [MaskInputFilter]
 * и последотельностей символов форматирования [placeholders] для [Formatter]
 *
 * @author ma.kolpakov
 * Создан 3/29/2019
 */
internal class MaskParser(mask: CharSequence) {

    /**
     * Типы значимы символов без учёта символов форматирования из [placeholders]
     */
    val types: Array<MaskSymbol>

    /**
     * Символы форматирования. Ключ - позиция, с которой начинается последовательность в маске
     */
    val placeholders: Map<Int, CharSequence>
    @Suppress("RegExpRedundantEscape")
    private val parcer = Regex("[^\\?#A*]+")

    init {
        val placeholderBuffer = HashMap<Int, CharSequence>()
        placeholders = placeholderBuffer
        val typesBuffer = ArrayList<MaskSymbol>()

        val matches = parcer.findAll(mask)

        /* для совместимости с предыдущим алгоритмом мы указываем отступ не в маскированой строке,
           а в форматируемой строке. Для этого необходимо вычесть суммарную длинну предыдущих холдеров из позиции холдера */
        var holdersLength = 0
        matches.forEach {
            val position = it.range.first - holdersLength
            placeholderBuffer[position] = it.value
            holdersLength += it.range.length
        }
        parcer.replace(mask, "").forEach { typesBuffer.add(MaskSymbol.getByChar(it)) }

        types = typesBuffer.toTypedArray()
    }

    val IntRange.length get() = this.last + 1 - this.first

    override fun toString(): String =
        "types = %s, placeholders:%s"
            .format(
                types.let {
                    val result = StringBuilder()
                    it.forEach { maskSymbol -> result.append(maskSymbol.symbol) }
                    result.toString()
                },
                placeholders.map { "(%s:`%s`".format(it.key, it.value) }
            )
}