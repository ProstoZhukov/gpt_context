package ru.tensor.sbis.design.text_span.text.masked.formatter.factory

import ru.tensor.sbis.design.text_span.text.masked.formatter.Formatter
import ru.tensor.sbis.design.text_span.text.masked.formatter.money.MoneyDynamicFormatter

/**
 * Фабрика для создания [MoneyDynamicFormatter]
 *
 * @author ar.leschev
 */
internal object MoneyFormatterFactory : FormatterFactory<Formatter> {

    override fun createFormatter(mask: CharSequence) = MoneyDynamicFormatter(mask)
}