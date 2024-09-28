package ru.tensor.sbis.design.view.input.mask.phone.formatter.utils

import android.text.InputFilter
import android.text.Spanned
import ru.tensor.sbis.design.utils.hasFlag
import ru.tensor.sbis.design.view.input.mask.phone.formatter.MOB_LEN
import ru.tensor.sbis.design.view.input.mask.phone.formatter.PhoneFormatDecoration

/**
 * Фильтр ограничения ввода количества символов (для российских номеров и остальных номеров разное ограничение длины).
 *
 * @author ps.smirnyh
 */
internal class PhoneLengthFilter(
    @PhoneFormatDecoration var decoration: Int
) : InputFilter {

    private val commonPhoneLengthFilter = InputFilter.LengthFilter(COMMON_PHONE_MASK.length)
    private val rusPhoneLengthFilter = InputFilter.LengthFilter(MAX_LENGTH_RUSSIAN_PHONE)
    private val foreignThreePhoneLengthFilter = InputFilter.LengthFilter(MAX_LENGTH_THREE_FOREIGN_PHONE)
    private val foreignTwoPhoneLengthFilter = InputFilter.LengthFilter(MAX_LENGTH_TWO_FOREIGN_PHONE)
    private val foreignOnePhoneLengthFilter = InputFilter.LengthFilter(MAX_LENGTH_ONE_FOREIGN_PHONE)
    private val isMobile: Boolean
        get() = decoration hasFlag MOB_LEN

    override fun filter(
        source: CharSequence?,
        sourceStart: Int,
        sourceEnd: Int,
        dest: Spanned?,
        destStart: Int,
        destEnd: Int
    ): CharSequence? {
        val destString = dest ?: ""
        val sourceString = source ?: ""
        val resultString = destString.replaceRange(destStart, destEnd, sourceString.subSequence(sourceStart, sourceEnd))
        val resultLengthFilter = when {
            resultString.isRusLenFormat -> rusPhoneLengthFilter
            resultString.isForeignThreeLenFormat -> foreignThreePhoneLengthFilter
            resultString.isForeignTwoLenFormat -> foreignTwoPhoneLengthFilter
            resultString.isForeignOneLenFormat -> foreignOnePhoneLengthFilter
            else -> commonPhoneLengthFilter
        }

        if (destString.isBlank() && sourceString.isBlank()) {
            return resultLengthFilter.filter(source, sourceStart, sourceEnd, dest, destStart, destEnd)
        }

        val destStartString = destString.subSequence(0, destStart)
        val sourceRangeInsert = sourceString.subSequence(sourceStart, sourceEnd)

        // если вставляем номер, но поле ввода не пустое, то удаляем код страны +7, 8 или знак +
        val newSource = if (sourceRangeInsert.count { it.isDigit() } == 11 && !isMobile) {
            getNewSourceOrDefault(destStartString, sourceRangeInsert)
        } else {
            sourceRangeInsert
        }

        val newDestStart = if (isMobile && sourceRangeInsert.length > 1) 0 else destStart
        val newDestEnd = if (isMobile && sourceRangeInsert.length > 1) destString.length else destEnd
        return resultLengthFilter.filter(
            newSource,
            0,
            newSource.length,
            dest,
            newDestStart,
            newDestEnd
        ) ?: newSource
    }

    private fun getNewSourceOrDefault(dest: CharSequence, source: CharSequence) = when {
        dest.plusSeven && source.plusSeven -> source.drop(2)

        dest.plusSeven && (source.firstSeven || source.firstEight) -> source.drop(1)

        dest.firstEight && source.firstEight -> source.drop(1)

        (dest.plusSeven || dest.firstPlus) && (source.firstPlus) -> source.drop(1)

        else -> source
    }

    private val CharSequence.isRusLenFormat: Boolean get() = decoration hasFlag MOB_LEN && plusSeven
    private val CharSequence.isForeignOneLenFormat: Boolean
        get() = decoration hasFlag MOB_LEN && START_PLUS_ONE_FOREIGN_CODE(this)
    private val CharSequence.isForeignTwoLenFormat: Boolean
        get() = decoration hasFlag MOB_LEN && START_PLUS_TWO_FOREIGN_CODE(this)
    private val CharSequence.isForeignThreeLenFormat: Boolean
        get() = decoration hasFlag MOB_LEN && START_PLUS_THREE_FOREIGN_CODE(this)
}