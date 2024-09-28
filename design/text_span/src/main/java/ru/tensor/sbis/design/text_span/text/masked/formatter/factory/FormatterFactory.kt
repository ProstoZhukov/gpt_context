package ru.tensor.sbis.design.text_span.text.masked.formatter.factory

/**
 * Фабрика реализаций форматтера
 *
 * @author us.bessonov
 */
interface FormatterFactory<out FORMATTER> {

    /** @SelfDocumented */
    fun createFormatter(mask: CharSequence): FORMATTER
}