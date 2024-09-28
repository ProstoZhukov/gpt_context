package ru.tensor.sbis.design.text_span.text.masked.phone

import android.text.InputFilter
import android.text.Spanned
import ru.tensor.sbis.design.utils.hasFlag
import ru.tensor.sbis.design.text_span.text.masked.formatter.phone.COMMON_PHONE_MASK
import ru.tensor.sbis.design.text_span.text.masked.formatter.phone.MAX_LENGTH_RUSSIAN_PHONE

/**
 * Фильтр ограничения ввода количества символов (для российских номеров и остальных номеров разное ограничение длины)
 *
 * @author ma.kolpakov
 * @since 12/6/2019
 */
internal class PhoneLengthFilter(
    @PhoneFormat
    private val decoration: Int
) : InputFilter {

    private val commonPhoneLengthFilter = InputFilter.LengthFilter(COMMON_PHONE_MASK.length)
    private val rusPhoneLengthFilter = InputFilter.LengthFilter(MAX_LENGTH_RUSSIAN_PHONE)
    /**
     * Ограничение длинны, если пользователь самостоятельно ввёл +
     */
    private val rusPlusPhoneLengthFilter = InputFilter.LengthFilter(MAX_LENGTH_RUSSIAN_PHONE + 1)

    override fun filter(
        source: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int
    ): CharSequence? = when {
        dest == null -> commonPhoneLengthFilter
        dest.isRusLenFormatWithPlus -> rusPlusPhoneLengthFilter
        dest.isRusLenFormat -> rusPhoneLengthFilter
        else -> commonPhoneLengthFilter
    }.run {
        filter(source, start, end, dest, dstart, dend)
    }

    private val CharSequence.isRusLenFormatWithPlus: Boolean get() = decoration hasFlag RU_LEN && plusSeven

    private val CharSequence.isRusLenFormat: Boolean get() = decoration hasFlag RU_LEN && (firstSeven || firstEight)
}