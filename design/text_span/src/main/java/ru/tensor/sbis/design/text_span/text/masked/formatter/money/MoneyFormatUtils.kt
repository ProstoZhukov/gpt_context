/**
 * @author ar.leschev
 */
package ru.tensor.sbis.design.text_span.text.masked.formatter.money

import ru.tensor.sbis.design.text_span.text.masked.formatter.ConditionalFormatter
import ru.tensor.sbis.design.text_span.text.masked.formatter.DynamicFormatter
import ru.tensor.sbis.design.text_span.text.masked.formatter.Formatter
import ru.tensor.sbis.design.text_span.text.masked.formatter.FormatterRule
import ru.tensor.sbis.design.text_span.text.masked.formatter.factory.DynamicFormatterFactory
import ru.tensor.sbis.design.text_span.text.masked.formatter.factory.FormatterFactory
import ru.tensor.sbis.design.text_span.text.masked.formatter.factory.MoneyFormatterFactory

/**
 * Создает условный форматтер для поля ввода денег
 *
 * @param ignoreSymbol символ отделения копеек от общей суммы.
 */
@Suppress("unused")
fun createMoneyFormatter(ignoreSymbol: Char): Formatter =
    ConditionalFormatter(createMoneyRules(ignoreSymbol, MoneyFormatterFactory))

/**
 * Создает условный форматтер для поля ввода процентов
 *
 * @param ignoreSymbol символ отделения копеек от общей суммы.
 */
@Suppress("unused")
fun createPercentsFormatter(ignoreSymbol: Char): Formatter =
    ConditionalFormatter(createMoneyRules(ignoreSymbol, DynamicFormatterFactory))

/**
 * Создает динамический форматтер для маски [mask]
 */
@Suppress("unused")
fun createDynamicFormatter(mask: CharSequence): Formatter =
    DynamicFormatter(mask)

/**
 * Правила форматирования для форматтера денег.
 */
private fun <FORMATTER> createMoneyRules(
    ignoreSymbol: Char,
    formatterFactory: FormatterFactory<FORMATTER>
) = listOf(
    // Правила включающие [ignoreSymbol]
    FormatterRule({ s -> s.endsWith(ignoreSymbol) }, formatterFactory.createFormatter("#############")),
    FormatterRule(
        { s -> s.length <= 6 && s.contains(ignoreSymbol) },
        formatterFactory.createFormatter("######")
    ),
    FormatterRule(
        { s -> s.length == 7 && s.contains(ignoreSymbol) },
        formatterFactory.createFormatter("# ######")
    ),
    FormatterRule(
        { s -> s.length == 8 && s.contains(ignoreSymbol) },
        formatterFactory.createFormatter("## ######")
    ),
    FormatterRule(
        { s -> s.length == 9 && s.contains(ignoreSymbol) },
        formatterFactory.createFormatter("### ######")
    ),
    FormatterRule(
        { s -> s.length == 10 && s.contains(ignoreSymbol) },
        formatterFactory.createFormatter("# ### ######")
    ),
    FormatterRule(
        { s -> s.length == 11 && s.contains(ignoreSymbol) },
        formatterFactory.createFormatter("## ### ######")
    ),
    FormatterRule(
        { s -> s.length == 12 && s.contains(ignoreSymbol) },
        formatterFactory.createFormatter("### ### ######")
    ),
    FormatterRule(
        { s -> s.length == 13 && s.contains(ignoreSymbol) },
        formatterFactory.createFormatter("# ### ### ######")
    ),
    FormatterRule(
        { s -> s.length == 14 && s.contains(ignoreSymbol) },
        formatterFactory.createFormatter("## ### ### ######")
    ),
    FormatterRule(
        { s -> s.length == 15 && s.contains(ignoreSymbol) },
        formatterFactory.createFormatter("### ### ### ######")
    ),

    // Правила без [ignoreSymbol]
    FormatterRule(
        { s -> s.length <= 3 && s.contains(ignoreSymbol).not() },
        formatterFactory.createFormatter("###")
    ),
    FormatterRule(
        { s -> s.length == 4 && s.contains(ignoreSymbol).not() },
        formatterFactory.createFormatter("# ###")
    ),
    FormatterRule(
        { s -> s.length == 5 && s.contains(ignoreSymbol).not() },
        formatterFactory.createFormatter("## ###")
    ),
    FormatterRule(
        { s -> s.length <= 6 && s.contains(ignoreSymbol).not() },
        formatterFactory.createFormatter("### ###")
    ),
    FormatterRule(
        { s -> s.length == 7 && s.contains(ignoreSymbol).not() },
        formatterFactory.createFormatter("# ### ###")
    ),
    FormatterRule(
        { s -> s.length == 8 && s.contains(ignoreSymbol).not() },
        formatterFactory.createFormatter("## ### ###")
    ),
    FormatterRule(
        { s -> s.length == 9 && s.contains(ignoreSymbol).not() },
        formatterFactory.createFormatter("### ### ###")
    ),
    FormatterRule(
        { s -> s.length == 10 && s.contains(ignoreSymbol).not() },
        formatterFactory.createFormatter("# ### ### ###")
    ),
    FormatterRule(
        { s -> s.length == 11 && s.contains(ignoreSymbol).not() },
        formatterFactory.createFormatter("## ### ### ###")
    ),
    FormatterRule(
        { s -> s.length == 12 && s.contains(ignoreSymbol).not() },
        formatterFactory.createFormatter("### ### ### ###")
    ),
    FormatterRule(
        { s -> s.length > 12 && s.contains(ignoreSymbol).not() },
        formatterFactory.createFormatter("### ### ### ###")
    )
)