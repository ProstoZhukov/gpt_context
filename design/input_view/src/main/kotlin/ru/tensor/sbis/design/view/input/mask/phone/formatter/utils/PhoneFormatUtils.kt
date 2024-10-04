/**
 * Функции для создания форматтеров номеров телефонов и правил для подбора маски.
 *
 * @author ps.smirnyh
 */
package ru.tensor.sbis.design.view.input.mask.phone.formatter.utils

import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.design.text_span.text.masked.formatter.Formatter
import ru.tensor.sbis.design.text_span.text.masked.formatter.factory.FormatterFactory
import ru.tensor.sbis.design.utils.hasFlag
import ru.tensor.sbis.design.view.input.mask.MaskSymbol
import ru.tensor.sbis.design.view.input.mask.phone.formatter.COMMON_FORMAT
import ru.tensor.sbis.design.view.input.mask.phone.formatter.ConditionalFormatter
import ru.tensor.sbis.design.view.input.mask.phone.formatter.DynamicFormatterFactory
import ru.tensor.sbis.design.view.input.mask.phone.formatter.FormatterPredicate
import ru.tensor.sbis.design.view.input.mask.phone.formatter.FormatterRule
import ru.tensor.sbis.design.view.input.mask.phone.formatter.MOB_FORMAT
import ru.tensor.sbis.design.view.input.mask.phone.formatter.PhoneFormatDecoration

/**
 * Создаёт [Formatter] для телефонных номеров.
 *
 * @param format флаги формата
 */
internal fun createPhoneFormatter(
    @PhoneFormatDecoration
    format: Int,
    additionalNumberString: String
): Formatter = ConditionalFormatter(
    createRules(format, DynamicFormatterFactory, additionalNumberString)
)

private fun <FORMATTER> createRules(
    format: Int,
    formatterFactory: FormatterFactory<FORMATTER>,
    additionalNumberString: String
) = with(RulesCreator(format, formatterFactory, additionalNumberString)) {
    createRules {
        val commonFormatter = formatterFactory.createFormatter(COMMON_PHONE_MASK)

        if (format hasFlag MOB_FORMAT) {
            add(START_WITH_PLUS_SEVEN_AND_12_DIGITS toFormatterWithMask codeRusNumberMaskPlusSeven)
            add(START_WITH_EIGHT_AND_12_DIGITS toFormatterWithMask codeRusNumberMaskEight)
            add(
                PLUS_SEVEN_FIVE_REGION_CODE_AND_MORE_THAN_12 toFormatterWithMask getMaskForRusMobileWithAdditional(
                    amountRegionDigits = 5
                )
            )
            add(
                EIGHT_FIVE_REGION_CODE_AND_MORE_THAN_12 toFormatterWithMask getMaskForRusMobileWithAdditional(
                    amountRegionDigits = 5,
                    isUsePlusSymbol = false
                )
            )
            add(
                PLUS_SEVEN_FOUR_REGION_CODE_AND_MORE_THAN_12 toFormatterWithMask getMaskForRusMobileWithAdditional(
                    amountRegionDigits = 4
                )
            )
            add(
                EIGHT_FOUR_REGION_CODE_AND_MORE_THAN_12 toFormatterWithMask getMaskForRusMobileWithAdditional(
                    amountRegionDigits = 4,
                    isUsePlusSymbol = false
                )
            )
            add(
                PLUS_SEVEN_THREE_REGION_CODE_AND_MORE_THAN_12 toFormatterWithMask getMaskForRusMobileWithAdditional(
                    amountRegionDigits = 3
                )
            )
            add(
                EIGHT_THREE_REGION_CODE_AND_MORE_THAN_12 toFormatterWithMask getMaskForRusMobileWithAdditional(
                    amountRegionDigits = 3,
                    isUsePlusSymbol = false
                )
            )
            add(PLUS_SEVEN_FIVE_REGION_CODE_SHORT toFormatterWithMask getShortMaskForRusMobile(amountRegionDigits = 5))
            add(
                EIGHT_FIVE_REGION_CODE_SHORT toFormatterWithMask getShortMaskForRusMobile(
                    amountRegionDigits = 5,
                    isUsePlusSymbol = false
                )
            )
            add(PLUS_SEVEN_FIVE_REGION_CODE toFormatterWithMask getMaskForRusMobile(amountRegionDigits = 5))
            add(
                EIGHT_FIVE_REGION_CODE toFormatterWithMask getMaskForRusMobile(
                    amountRegionDigits = 5,
                    isUsePlusSymbol = false
                )
            )
            add(PLUS_SEVEN_FOUR_REGION_CODE_SHORT toFormatterWithMask getShortMaskForRusMobile(amountRegionDigits = 4))
            add(
                EIGHT_FOUR_REGION_CODE_SHORT toFormatterWithMask getShortMaskForRusMobile(
                    amountRegionDigits = 4,
                    isUsePlusSymbol = false
                )
            )
            add(PLUS_SEVEN_FOUR_REGION_CODE toFormatterWithMask getMaskForRusMobile(amountRegionDigits = 4))
            add(
                EIGHT_FOUR_REGION_CODE toFormatterWithMask getMaskForRusMobile(
                    amountRegionDigits = 4,
                    isUsePlusSymbol = false
                )
            )
            add(PLUS_SEVEN_THREE_REGION_CODE_SHORT toFormatterWithMask getShortMaskForRusMobile(amountRegionDigits = 3))
            add(
                EIGHT_THREE_REGION_CODE_SHORT toFormatterWithMask getShortMaskForRusMobile(
                    amountRegionDigits = 3,
                    isUsePlusSymbol = false
                )
            )
            add(PLUS_SEVEN_THREE_REGION_CODE toFormatterWithMask getMaskForRusMobile(amountRegionDigits = 3))
            add(
                EIGHT_THREE_REGION_CODE toFormatterWithMask getMaskForRusMobile(
                    amountRegionDigits = 3,
                    isUsePlusSymbol = false
                )
            )
            add(START_WITH_PLUS_SEVEN toFormatterWithMask codeRusNumberMaskPlusSeven)
            add(START_WITH_EIGHT toFormatterWithMask codeRusNumberMaskEight)

            add(
                START_PLUS_THREE_FOREIGN_CODE_AND_MORE_THAN_10 toFormatterWithMask getMaskForForeignCommon(
                    amountCountryDigits = 3
                )
            )
            add(
                START_PLUS_THREE_FOREIGN_CODE_AND_REGION toFormatterWithMask getMaskForForeignShort(
                    amountCountryDigits = 3
                )
            )
            add(
                START_PLUS_THREE_FOREIGN_CODE toFormatterWithMask getMaskForForeign(
                    amountCountryDigits = 3
                )
            )
            add(
                START_PLUS_TWO_FOREIGN_CODE_AND_MORE_THAN_10 toFormatterWithMask getMaskForForeignCommon(
                    amountCountryDigits = 2
                )
            )
            add(
                START_PLUS_TWO_FOREIGN_CODE_AND_REGION toFormatterWithMask getMaskForForeignShort(
                    amountCountryDigits = 2
                )
            )
            add(
                START_PLUS_TWO_FOREIGN_CODE toFormatterWithMask getMaskForForeign(
                    amountCountryDigits = 2
                )
            )
            add(
                START_PLUS_ONE_FOREIGN_CODE_AND_MORE_THAN_10 toFormatterWithMask getMaskForForeignCommon(
                    amountCountryDigits = 1
                )
            )
            add(
                START_PLUS_ONE_FOREIGN_CODE_AND_REGION toFormatterWithMask getMaskForForeignShort(
                    amountCountryDigits = 1
                )
            )
            add(START_PLUS_ONE_FOREIGN_CODE toFormatterWithMask getMaskForForeign(amountCountryDigits = 1))
        }

        add(LESS_THAN_FIVE to commonFormatter)

        if (format hasFlag COMMON_FORMAT) {
            add(FIVE_DIGIT toFormatterWithMask commonFiveDigitMask)
            add(SIX_DIGIT toFormatterWithMask commonSixDigitMask)
            add(SEVEN_DIGIT toFormatterWithMask commonSevenDigitMask)
            add(UP_TO_TEN_DIGIT toFormatterWithMask commonUpToTenDigitMask)
            add(MORE_THAN_TEN to commonFormatter)
        }
    }
}

private infix fun <FORMATTER> FormatterPredicate.to(formatter: FORMATTER) =
    FormatterRule(this, formatter)

private class RulesCreator<FORMATTER>(
    @PhoneFormatDecoration private val format: Int,
    private val formatterFactory: FormatterFactory<FORMATTER>,
    private val additionalNumberString: String
) {

    private val amountDigitsRusMobile = 10
    private val tailRusMobileNumber = "00-00"
    private val tailForeignNumber = "000 00 00"
    private val amountDigitsTailRusMobile = tailRusMobileNumber.count(MaskSymbol.DIGIT::matches)

    val codeRusNumberMaskPlusSeven = buildString {
        append(MaskSymbol.ANY.symbol)
        append(MaskSymbol.DIGIT.symbol)
        append(StringUtils.SPACE)
    }
    val codeRusNumberMaskEight = buildString {
        append(MaskSymbol.DIGIT.symbol)
        append(StringUtils.SPACE)
    }
    val commonFiveDigitMask = "0-00-00"
    val commonSixDigitMask = "00-00-00"
    val commonSevenDigitMask = "000-00-00"
    val commonUpToTenDigitMask = "(000)-000-00-00"

    fun createRules(init: MutableList<FormatterRule<FORMATTER>>.() -> Unit): MutableList<FormatterRule<FORMATTER>> =
        mutableListOf<FormatterRule<FORMATTER>>().apply {
            init()
        }

    infix fun FormatterPredicate.toFormatterWithMask(mask: String) =
        FormatterRule(this, formatterFactory.createFormatter(mask))

    fun getMaskForRusMobileWithAdditional(amountRegionDigits: Int, isUsePlusSymbol: Boolean = true): String =
        "${getMaskForRusMobile(amountRegionDigits, isUsePlusSymbol)} $additionalNumberString "

    fun getMaskForRusMobile(amountRegionDigits: Int, isUsePlusSymbol: Boolean = true): String {
        val codeAndRegion = getShortMaskForRusMobile(amountRegionDigits, isUsePlusSymbol)
        val needToAddMore = amountDigitsRusMobile - amountRegionDigits - amountDigitsTailRusMobile
        return StringBuilder(codeAndRegion).apply {
            append(StringUtils.SPACE)
            repeat(needToAddMore) {
                append(MaskSymbol.DIGIT.symbol)
            }
            append('-')
            append(tailRusMobileNumber)
        }.toString()
    }

    fun getShortMaskForRusMobile(amountRegionDigits: Int, isUsePlusSymbol: Boolean = true): String =
        buildString {
            if (isUsePlusSymbol) {
                append(MaskSymbol.ANY.symbol)
            }
            append(MaskSymbol.DIGIT.symbol)
            append(StringUtils.SPACE)
            append('(')
            repeat(amountRegionDigits) {
                append(MaskSymbol.DIGIT.symbol)
            }
            append(')')
        }

    fun getMaskForForeign(amountCountryDigits: Int): String =
        buildString {
            append(getMaskForForeignShort(amountCountryDigits))
            append(StringUtils.SPACE)
            append(tailForeignNumber)
        }

    fun getMaskForForeignShort(amountCountryDigits: Int): String =
        buildString {
            append(getMaskForForeignCommon(amountCountryDigits))
            appendMaskRegionForForeign()
        }

    fun getMaskForForeignCommon(amountCountryDigits: Int): String =
        buildString {
            append(MaskSymbol.ANY.symbol)
            repeat(amountCountryDigits) {
                append(MaskSymbol.DIGIT.symbol)
            }
            append(StringUtils.SPACE)
        }

    private fun StringBuilder.appendMaskRegionForForeign() {
        if (format hasFlag COMMON_FORMAT) {
            appendMaskRegionForForeignCommon()
        } else {
            appendMaskRegionForForeignMobile()
        }
    }

    private fun StringBuilder.appendMaskRegionForForeignMobile() {
        append('(')
        appendMaskRegionForForeignCommon()
        append(')')
    }

    private fun StringBuilder.appendMaskRegionForForeignCommon() {
        repeat(3) {
            append(MaskSymbol.DIGIT.symbol)
        }
    }
}
