package ru.tensor.sbis.design.view.input.mask.phone.formatter

import ru.tensor.sbis.design.text_span.text.masked.formatter.Formatter
import ru.tensor.sbis.design.view.input.mask.MaskSymbol

/**
 * Инструмент для разбора маски на последовательность символов форматирования для [Formatter].
 *
 * @author ps.smirnyh
 */
internal class MaskParser(mask: CharSequence) {

    /**
     * Типы значимы символов без учёта символов форматирования из [placeholders].
     */
    val types: Array<MaskSymbol>

    /**
     * Символы форматирования. Ключ - позиция, с которой начинается последовательность в маске.
     */
    val placeholders: Map<Int, CharSequence>

    @Suppress("RegExpRedundantEscape")
    private val parcer = Regex("[^\\?0A*]+")

    init {
        val placeholderBuffer = HashMap<Int, CharSequence>()
        placeholders = placeholderBuffer
        val typesBuffer = ArrayList<MaskSymbol>()

        val matches = parcer.findAll(mask)

        /* для совместимости с предыдущим алгоритмом мы указываем отступ не в маскированой строке,
           а в форматируемой строке. Для этого необходимо вычесть суммарную длину предыдущих холдеров из позиции холдера */
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