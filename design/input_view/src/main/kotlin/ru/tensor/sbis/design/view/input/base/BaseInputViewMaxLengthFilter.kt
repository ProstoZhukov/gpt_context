package ru.tensor.sbis.design.view.input.base

import android.text.InputFilter
import android.text.Spanned
import androidx.annotation.IntRange

/**
 * Фильтр длины текста для поля ввода.
 *
 * @author ps.smirnyh
 */
internal class BaseInputViewMaxLengthFilter : InputFilter {

    /**
     * Максимальная длина вводимого текста, количество символов.
     */
    @get:IntRange(from = 0L)
    @setparam:IntRange(from = 0L)
    var maxLength: Int = NO_MAX_LENGTH
        set(newValue) {
            require(newValue >= 0) { "maxLength must be greater or equals zero" }
            val lastValue = field
            field = newValue
            if (lastValue != newValue) {
                lengthFilter =
                    if (newValue > NO_MAX_LENGTH) InputFilter.LengthFilter(newValue) else null
            }
        }

    /**
     * Текущий фильтр длины.
     */
    private var lengthFilter: InputFilter.LengthFilter? = null

    override fun filter(
        source: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int
    ): CharSequence? =
        lengthFilter?.filter(source, start, end, dest, dstart, dend)

    internal companion object {
        internal const val NO_MAX_LENGTH = 0
    }
}