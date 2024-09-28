package ru.tensor.sbis.design.retail_views.numberic_keyboard.internal

import android.text.InputFilter
import android.text.Spanned
import android.widget.EditText

/**
 * LengthFilter фильтр, делающий исключения для разделителя и символов после него.
 * Используется, чтобы поддержать работоспобность логики ввода с физической клавиатуры для случая,
 * когда в поле ввода введено максимальное количество символов:
 * https://online.sbis.ru/opendoc.html?guid=a0b7d0ad-aab9-460d-89b5-36d13d2e8357
 */
internal class LengthFilterWithPointAndDecimalPartExclusion(
    max: Int,
    private val separator: String,
    private val inputField: EditText
) : InputFilter.LengthFilter(max) {

    override fun filter(
        source: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        return if (source?.equals(separator) == true || isInputAfterSeparator()) {
            source
        } else {
            // обрезаем введенные символы с начала при превышении максимальной длины
            // https://online.sbis.ru/opendoc.html?guid=590a3bde-c718-44b1-931c-2366957f3a61
            if (end > max) {
                super.filter(
                    source?.subSequence(end - max, end),
                    start,
                    end,
                    dest,
                    dstart,
                    dend
                )
            } else {
                super.filter(source, start, end, dest, dstart, dend)
            }
        }
    }

    private fun isInputAfterSeparator(): Boolean {
        val selectionIdx = inputField.selectionEnd
        val separatorIdx = inputField.text.indexOf(separator)
        return separatorIdx != -1 && selectionIdx > separatorIdx
    }
}