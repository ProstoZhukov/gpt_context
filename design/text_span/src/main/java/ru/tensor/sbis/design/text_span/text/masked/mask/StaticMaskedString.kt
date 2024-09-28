package ru.tensor.sbis.design.text_span.text.masked.mask

import kotlin.math.max
import kotlin.math.min

/**
 * Класс, представляющий строку со статичной маской.
 *
 * @author us.bessonov
 */
internal class StaticMaskedString(mask: CharSequence) : MaskedString {

    /**
     * Длина строки.
     */
    override val length: Int
        get() = chars.size

    /**
     * Символы в строке.
     */
    private val chars: CharArray = CharArray(mask.length, mask::get)

    /**
     * Типы символов в строке.
     */
    private val types: Array<MaskSymbol>

    /**
     * Позиция первого изменяемого символа в строке.
     */
    private val firstDynamic: Int

    init {
        var fixedPrefix = true
        var firstDynamic = 0
        types = Array(
            mask.length,
            fun(position: Int): MaskSymbol {
                val char = mask[position]
                for (s in MaskSymbol.values()) {
                    if (s.symbol == char) {
                        // Запоминаем тип символа и заменяем его на соответствующий placeholder
                        chars[position] = s.placeholder
                        if (s != MaskSymbol.FIXED && fixedPrefix) {
                            // Запоминаем позицию первого изменяемого символа
                            fixedPrefix = false
                            firstDynamic = position
                        }
                        return s
                    }
                }
                return MaskSymbol.FIXED
            }
        )
        this.firstDynamic = firstDynamic
    }

    // region CharSequence impl
    override fun get(index: Int): Char = chars[index]

    override fun subSequence(startIndex: Int, endIndex: Int): CharSequence =
        String(chars.slice(IntRange(startIndex, endIndex)).toCharArray())

    override fun toString(): String = String(chars)
    // endregion

    override fun restore(input: CharSequence) {
        // Встраиваем символы из входной строки в строку с маской, не изменяя позиций символов
        for (pos in 0 until min(length, input.length)) {
            val type = types[pos]
            val char = input[pos]
            if (type != MaskSymbol.FIXED) {
                if (type.matches(char)) {
                    chars[pos] = char
                }
            }
        }
    }

    override fun validatePosition(position: Int): Int {
        val result = min(position, length)
        return max(result, firstDynamic)
    }

    override fun insert(start: Int, input: CharSequence): Int {
        return insert(start, input, allowShiftText = false)
    }

    private fun insert(start: Int, input: CharSequence, allowShiftText: Boolean): Int {
        var pos = start
        var i = 0
        val replacedSymbols = mutableListOf<Char>()
        var replacementStart = start
        while (pos < length && i < input.length) {
            val type = types[pos]
            val char = input[i]
            if (type == MaskSymbol.FIXED) {
                // Обрабатываем фиксированный символ
                if (chars[pos] == char) {
                    // Фиксированный символ совпал с символом из input
                    ++i // Переходим к следующему символу из input
                }
                ++pos
            } else {
                // Обрабатываем динамический символ
                if (type.matches(char) || char == type.placeholder) {
                    // Символ из input подошел к маске
                    if (chars[pos] != type.placeholder || replacedSymbols.isNotEmpty()) {
                        if (replacedSymbols.isEmpty()) replacementStart = pos
                        // сохраняем заменённый символ
                        replacedSymbols.add(chars[pos])
                    }
                    chars[pos] = char
                    ++pos // Переходим к заполнению следующего символа
                }
                ++i // Переходим к следующему символу из input
            }
        }
        if (allowShiftText) shiftExistingTextIfNeeded(replacementStart, replacedSymbols)
        return validatePosition(pos)
    }

    override fun delete(start: Int, count: Int, atLeastOne: Boolean): Int {
        var pos = start
        var endPosition = start
        var prefix = true
        while (pos < start + count) {
            val type = types[pos]
            if (type != MaskSymbol.FIXED) {
                prefix = false
                // Сбрасываем символ на позиции с нефиксированным символом
                chars[pos] = type.placeholder
            } else if (prefix) {
                // Если мы находимся в префиксе - смещаем конечную позицию на след. символ
                endPosition = pos + 1
            }
            ++pos
        }

        if (atLeastOne && prefix) {
            // Необходимо удалить хотя бы один символ,
            // но в диапазоне только статичные символы маски
            do {
                // Смещаемся в начало строки, ищем символ, который можно удалить
                --endPosition
            } while (endPosition > -1 && types[endPosition] == MaskSymbol.FIXED)

            if (endPosition > -1) {
                // Нашли символ перед диапазоном, который можно удалить
                chars[endPosition] = types[endPosition].placeholder
            } else {
                // От начала строки до start все символы фиксированные
                endPosition = start + count
            }
        }
        return validatePosition(endPosition)
    }

    override fun clear(): Int = delete(0, length)

    private fun shiftExistingTextIfNeeded(replacementStart: Int, replacedSymbols: List<Char>) {
        if (replacedSymbols.isEmpty() || hasFixedSymbols(replacedSymbols)) return
        val replacementEnd = replacementStart + replacedSymbols.size
        val leftEnd = getNextFixedSymbolPosition(replacementStart)
        val leftStart = min(replacementEnd, leftEnd)
        val leftSymbols = chars.copyOfRange(leftStart, leftEnd).toList()
        val shifted = replacedSymbols.plus(leftSymbols)
            .take(leftSymbols.size)
            .joinToString("")
        insert(replacementEnd, shifted, allowShiftText = false)
    }

    private fun hasFixedSymbols(symbols: List<Char>): Boolean {
        return symbols.any { c ->
            MaskSymbol.values()
                .filter { it != MaskSymbol.ANY }
                .none { it.matches(c) }
        }
    }

    private fun getNextFixedSymbolPosition(startingFrom: Int): Int {
        return chars.indices
            .drop(startingFrom)
            .firstOrNull { types[it] == MaskSymbol.FIXED }
            ?: length
    }
}