package ru.tensor.sbis.design.view.input.mask.phone.formatter

import android.content.res.Resources
import android.text.Editable
import android.text.Selection
import android.text.Spannable
import android.text.SpannableStringBuilder
import ru.tensor.sbis.design.text_span.span.CharSequenceSpan
import ru.tensor.sbis.design.text_span.text.masked.formatter.Formatter
import ru.tensor.sbis.design.utils.extentions.clearSpans
import ru.tensor.sbis.design.view.input.R
import ru.tensor.sbis.design.view.input.mask.phone.PhoneFormat
import ru.tensor.sbis.design.view.input.mask.phone.formatter.utils.PhoneLengthFilter
import ru.tensor.sbis.design.view.input.mask.phone.formatter.utils.createPhoneFormatter
import ru.tensor.sbis.design.view.input.mask.phone.formatter.utils.firstEight
import ru.tensor.sbis.design.view.input.mask.phone.formatter.utils.firstPlus
import ru.tensor.sbis.design.view.input.utils.safeSetSelection

/**
 * Логика форматирования символов по маске для ввода номера телефона.
 * Умеет заменять 8 на +7 и вставлять + перед 7 и +7 перед 9.
 *
 * @author ps.smirnyh
 */
class PhoneInputViewFormatter internal constructor(
    private var phoneFormat: PhoneFormat,
    internal val lengthFilter: PhoneLengthFilter,
    private var formatter: Formatter
) {

    /** @SelfDocumented */
    constructor(
        phoneFormat: PhoneFormat,
        resources: Resources
    ) :
        this(
            phoneFormat = phoneFormat,
            lengthFilter = PhoneLengthFilter(decoration = phoneFormat.format),
            formatter = createPhoneFormatter(
                format = phoneFormat.format,
                resources.getString(R.string.phone_input_view_ext_number)
            )
        )

    /**
     * Отформатировать строку по правилам ввода номера телефона.
     * Правила форматирования зависят от [phoneFormat].
     */
    fun format(text: CharSequence): CharSequence {
        val formattedText = text.filterForbiddenCharacters()
        val filterResult = lengthFilter.filter(
            formattedText,
            0,
            formattedText.length,
            formattedText,
            0,
            formattedText.length
        )?.let { (it as? Editable) ?: SpannableStringBuilder(it) }
        val resultText = filterResult ?: formattedText
        resultText.copySelection(text)
        return formatWithoutFilter(resultText)
    }

    /** Форматирование номера телефона без ограничений по длине. */
    internal fun formatWithoutFilter(text: Editable): Editable {
        var selection = 0
        val isMobile = phoneFormat == PhoneFormat.MOBILE
        val isExistSelection = Selection.getSelectionEnd(text).also { selection = it } > -1
        if (isMobile) {
            val changeSelection = text.mobileFormat()
            selection += changeSelection
        }
        // очистим прошлое форматирование
        text.clearSpans<CharSequenceSpan>()
        // применим новое
        formatter.apply(text)
        Selection.setSelection(text, text.length)
        if (isExistSelection) {
            text.safeSetSelection(selection)
        }
        return text
    }

    internal fun updatePhoneFormat(phoneFormat: PhoneFormat, resources: Resources) {
        this.phoneFormat = phoneFormat
        formatter = createPhoneFormatter(
            format = phoneFormat.format,
            additionalNumberString = resources.getString(R.string.phone_input_view_ext_number)
        )
        lengthFilter.decoration = phoneFormat.format
    }

    /**
     * Отформатировать телефон по правилам мобильного формата.
     * Возвращает количество, на которое нужно изменить [Selection].
     */
    private fun Editable.mobileFormat(): Int {
        if (firstEight) {
            delete(0, 1)
            insert(0, "+7")
            return 1
        }
        if (!firstPlus) {
            insert(0, "+")
            return 1
        }
        return 0
    }

    private fun Spannable.copySelection(source: CharSequence) {
        safeSetSelection(Selection.getSelectionEnd(source).coerceIn(0, length))
    }

    private fun CharSequence.filterForbiddenCharacters() =
        SpannableStringBuilder(filterIndexed { index, char -> char.isDigit() || (char == '+' && index == 0) })
}