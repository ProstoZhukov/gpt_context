@file:JvmName("PhoneFormatUtils")

/**
 * @author us.bessonov
 */
package ru.tensor.sbis.design.text_span.text.masked.formatter.phone

import android.text.TextWatcher
import android.text.method.TransformationMethod
import ru.tensor.sbis.design.text_span.text.masked.formatter.ConditionalFormatter
import ru.tensor.sbis.design.text_span.text.masked.formatter.Formatter
import ru.tensor.sbis.design.text_span.text.masked.formatter.FormatterPredicate
import ru.tensor.sbis.design.text_span.text.masked.formatter.FormatterRule
import ru.tensor.sbis.design.text_span.text.masked.formatter.factory.DynamicFormatterFactory
import ru.tensor.sbis.design.text_span.text.masked.formatter.factory.FormatterFactory
import ru.tensor.sbis.design.text_span.text.masked.formatter.factory.SimpleFormatterFactory
import ru.tensor.sbis.design.text_span.text.masked.formatter.simple.ConditionalSimpleFormatter
import ru.tensor.sbis.design.text_span.text.masked.formatter.simple.SimpleFormatter
import ru.tensor.sbis.design.text_span.text.masked.phone.COMMON_FORMAT
import ru.tensor.sbis.design.text_span.text.masked.phone.PhoneFormat
import ru.tensor.sbis.design.text_span.text.masked.phone.RU_FORMAT
import ru.tensor.sbis.design.text_span.text.masked.watcher.FormatterHolder
import ru.tensor.sbis.design.utils.hasFlag

private const val SPACE = ' '
private const val NON_BREAKING_SPACE = '\u00A0'

/**
 * Применяет форматирование к телефонному номеру
 *
 * @param phone строка с телефонным номером
 * @param phoneFormatter форматтер, применяемый к строке (по умолчанию использует правила форматирования для российских
 * номеров)
 * @return новая строка с применением форматирования к [phone] и предотвращением переноса строки
 */
@JvmOverloads
fun formatPhone(phone: CharSequence, phoneFormatter: SimpleFormatter = DefaultRuPhoneFormatter): CharSequence {
    return phoneFormatter.apply(phone).toString()
        .replace(SPACE, NON_BREAKING_SPACE)
}

/**
 * Создаёт [Formatter] для телефонных номеров
 *
 * @param format флаги формата
 * @param formatShortNumbers необходимо ли применять форматирование к номерам короче пяти символов
 */
@JvmOverloads
fun createPhoneFormatter(
    @PhoneFormat
    format: Int = RU_FORMAT,
    formatShortNumbers: Boolean = false
): Formatter = ConditionalFormatter(
    createRules(format, formatShortNumbers, false, DynamicFormatterFactory)
)

/**
 * Создаёт [SimpleFormatter] для телефонных номеров.
 *
 * @param isNumberCompleted Считается ли номер однозначно завершённым, а не частично введённым.
 *
 * @see createPhoneFormatter
 */
@JvmOverloads
fun createSimplePhoneFormatter(
    @PhoneFormat
    format: Int = RU_FORMAT,
    formatShortNumbers: Boolean = false,
    isNumberCompleted: Boolean = false
): SimpleFormatter =
    ConditionalSimpleFormatter(createRules(format, formatShortNumbers, isNumberCompleted, SimpleFormatterFactory))

/**
 * Возвращает [TransformationMethod], применяющий форматирование к тексту посредством [Formatter]
 */
fun Formatter.asTransformationMethod(): TransformationMethod = FormatterHolder(this)

/**
 * Возвращает [TextWatcher], применяющий форматирование к тексту посредством [Formatter]
 */
@Suppress("unused")
fun Formatter.asTextWatcher(): TextWatcher = FormatterHolder(this)

private fun <FORMATTER> createRules(
    @PhoneFormat
    format: Int,
    formatShortNumbers: Boolean,
    isNumberCompleted: Boolean,
    formatterFactory: FormatterFactory<FORMATTER>
) = mutableListOf<FormatterRule<FORMATTER>>().apply {
    val commonFormatter = formatterFactory.createFormatter(COMMON_PHONE_MASK)
    // no mask for short work phone numbers
    if (!formatShortNumbers && format hasFlag COMMON_FORMAT) {
        add(LESS_THAN_FIVE to commonFormatter)
    }

    if (format hasFlag RU_FORMAT) {
        val mobileFullNumber = "*# (###) ###-##-##"
        if (isNumberCompleted) {
            add(START_WITH_PLUS_SEVEN_AND_11_DIGITS to formatterFactory.createFormatter(mobileFullNumber))
        } else {
            // phone numbers starts with +7
            add(START_WITH_PLUS_SEVEN_AND_LESS_THAN_4 to formatterFactory.createFormatter("*# ##"))
            add(START_WITH_PLUS_SEVEN_AND_LESS_THAN_5 to formatterFactory.createFormatter("*# (###) "))
            add(START_WITH_PLUS_SEVEN_AND_LESS_THAN_12 to formatterFactory.createFormatter(mobileFullNumber))
            // only start of string matters
            add(START_WITH_PLUS_SEVEN to formatterFactory.createFormatter("*# "))
        }
    }

    // phone numbers starts with +
    add(START_WITH_PLUS to commonFormatter)

    if (format hasFlag RU_FORMAT) {
        val mobileFullNumber = "# (###) ###-##-##"
        val mobileFullNumberWithPlus = "+# (###) ###-##-##"
        if (isNumberCompleted) {
            add(START_WITH_EIGHT_AND_11_DIGITS to formatterFactory.createFormatter(mobileFullNumber))
            add(START_WITH_SEVEN_AND_11_DIGITS to formatterFactory.createFormatter(mobileFullNumberWithPlus))
        } else {
            // phone numbers starts with 8
            add(START_WITH_EIGHT_AND_LESS_THAN_4 to formatterFactory.createFormatter("# ##"))
            add(START_WITH_EIGHT_AND_LESS_THAN_5 to formatterFactory.createFormatter("# (###) "))
            add(START_WITH_EIGHT_AND_LESS_THAN_12 to formatterFactory.createFormatter(mobileFullNumber))
            // phone numbers starts with 7
            add(START_WITH_SEVEN_AND_LESS_THAN_4 to formatterFactory.createFormatter("+# ##"))
            add(START_WITH_SEVEN_AND_LESS_THAN_5 to formatterFactory.createFormatter("+# (###) "))
            add(START_WITH_SEVEN_AND_LESS_THAN_12 to formatterFactory.createFormatter(mobileFullNumberWithPlus))
            // only start of string matters
            add(START_WITH_EIGHT to formatterFactory.createFormatter("# "))
            add(START_WITH_SEVEN to formatterFactory.createFormatter("+# "))
        }
    }

    // other phones
    if (format hasFlag COMMON_FORMAT) {
        add(LESS_THAN_FIVE to formatterFactory.createFormatter("##-##"))
        add(FIVE_DIGIT to formatterFactory.createFormatter("#-##-##"))
        add(SIX_DIGIT to formatterFactory.createFormatter("##-##-##"))
        add(SEVEN_DIGIT to formatterFactory.createFormatter("###-##-##"))
        add(UP_TO_TEN_DIGIT to formatterFactory.createFormatter("(###) ###-##-##"))
        add(MORE_THAN_TEN to commonFormatter)
    }
}

internal infix fun <FORMATTER> FormatterPredicate.to(formatter: FORMATTER) = FormatterRule(this, formatter)