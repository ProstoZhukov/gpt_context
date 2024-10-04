package ru.tensor.sbis.design.text_span.text.masked.formatter.factory

import ru.tensor.sbis.design.text_span.text.masked.formatter.DynamicFormatter
import ru.tensor.sbis.design.text_span.text.masked.formatter.Formatter

/**
 * Фабрика для создания [DynamicFormatter]
 *
 * @author us.bessonov
 */
internal object DynamicFormatterFactory : FormatterFactory<Formatter> {

    override fun createFormatter(mask: CharSequence) = DynamicFormatter(mask)
}