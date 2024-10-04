package ru.tensor.sbis.design.text_span.text.masked.formatter.factory

import ru.tensor.sbis.design.text_span.text.masked.formatter.simple.SimpleFormatter
import ru.tensor.sbis.design.text_span.text.masked.formatter.simple.SimpleFormatterImpl

/**
 * Фабрика для создания [SimpleFormatterImpl]
 *
 * @author us.bessonov
 */
internal object SimpleFormatterFactory : FormatterFactory<SimpleFormatter> {

    override fun createFormatter(mask: CharSequence) = SimpleFormatterImpl(mask)
}