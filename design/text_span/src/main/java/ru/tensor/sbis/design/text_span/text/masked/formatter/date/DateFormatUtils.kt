package ru.tensor.sbis.design.text_span.text.masked.formatter.date

import ru.tensor.sbis.design.text_span.text.masked.formatter.Formatter
import ru.tensor.sbis.design.text_span.text.masked.formatter.factory.DynamicFormatterFactory

/**
 * Создает форматтер для поля ввода даты в формате ДД.ММ.ГГГГ
 *
 * @author ve.arefev
 */
@Suppress("unused")
fun createSimpleDateFormatter(): Formatter = DynamicFormatterFactory.createFormatter("##.##.####")