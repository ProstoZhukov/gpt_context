package ru.tensor.sbis.design.view.input.mask.phone.formatter

import ru.tensor.sbis.design.text_span.text.masked.formatter.Formatter
import ru.tensor.sbis.design.text_span.text.masked.formatter.factory.FormatterFactory

/**
 * Фабрика для создания [DynamicFormatter].
 *
 * @author ps.smirnyh
 */
internal object DynamicFormatterFactory : FormatterFactory<Formatter> {

    override fun createFormatter(mask: CharSequence) = DynamicFormatter(mask)
}