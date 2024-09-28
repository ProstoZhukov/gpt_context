package ru.tensor.sbis.design.text_span.text.masked.mask

import android.text.InputFilter
import android.text.Spanned

/**
 * Реализация [InputFilter], которая проверяет соответствие ввода указанной последвательности
 * символов
 *
 * @author ma.kolpakov
 * Создан 4/1/2019
 */
internal open class MaskInputFilter(
    protected val types: Array<MaskSymbol>
) : InputFilter {

    override fun filter(
        source: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        /*
        Символы за пределами маски пропускаем без фильтрации
         */
        if (source == null || dstart > types.lastIndex) {
            return null
        }
        return source.filterIndexed { index, c -> types.getOrNull(dstart + index)?.matches(c) ?: true }
    }
}