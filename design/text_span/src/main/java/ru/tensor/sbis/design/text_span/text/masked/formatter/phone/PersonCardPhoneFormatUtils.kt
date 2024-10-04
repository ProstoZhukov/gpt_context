package ru.tensor.sbis.design.text_span.text.masked.formatter.phone

import ru.tensor.sbis.design.text_span.text.masked.formatter.FormatterRule
import ru.tensor.sbis.design.text_span.text.masked.formatter.factory.FormatterFactory
import ru.tensor.sbis.design.text_span.text.masked.formatter.factory.SimpleFormatterFactory
import ru.tensor.sbis.design.text_span.text.masked.formatter.simple.ConditionalSimpleFormatter
import ru.tensor.sbis.design.text_span.text.masked.formatter.simple.SimpleFormatter

/**
 * Создание отдельных правил для карточки сотрудника, необходима поддержка до 15 знаков с маской как на вебе
 *
 * @author ra.temnikov
 */
fun createPersonCardPhoneFormatter(): SimpleFormatter =
    ConditionalSimpleFormatter(
        createRulesForPersonCard(
            formatterFactory = SimpleFormatterFactory
        )
    )

/**
 * Правила масок телефона для карточки сотрудника
 */
private fun <FORMATTER> createRulesForPersonCard(
    formatterFactory: FormatterFactory<FORMATTER>
) = mutableListOf<FormatterRule<FORMATTER>>().apply {
    add(FIVE_DIGITS to formatterFactory.createFormatter("#-##-##"))
    add(SIX_DIGITS to formatterFactory.createFormatter("##-##-##"))
    add(SEVEN_DIGITS to formatterFactory.createFormatter("###-##-##"))
    add(EIGHT_DIGITS to formatterFactory.createFormatter("####-##-##"))
    add(NINE_DIGITS to formatterFactory.createFormatter("#####-##-##"))
    add(TEN_DIGITS to formatterFactory.createFormatter("(###) ###-##-##"))
    add(ELEVEN_DIGITS_AND_CONTAINS_EIGHT to formatterFactory.createFormatter("# (###) ###-##-##"))
    add(ELEVEN_DIGITS to formatterFactory.createFormatter("+# (###) ###-##-##"))
    add(TWELVE_DIGITS to formatterFactory.createFormatter("+# (###) ####-##-##"))
    add(THIRTEEN_DIGITS to formatterFactory.createFormatter("+# (###) #####-##-##"))
    add(FOURTEEN_DIGITS to formatterFactory.createFormatter("+# (###) ######-##-##"))
    add(FIFTEEN_DIGITS to formatterFactory.createFormatter("+# (###) #######-##-##"))
}