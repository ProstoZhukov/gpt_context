package ru.tensor.sbis.design.text_span.text.masked.formatter

import android.text.Editable

/**
 * Модель форматтера, который можно применить к тексту. Реализации модели отвечают только за
 * форматирование
 *
 * @author us.bessonov
 */
interface StaticFormatter {

    /**
     * Добавляет необходимые символы форматирования непосредственно в строку
     */
    fun apply(s: Editable)

    /**
     * Уведомляет форматтер о том, что в строке на позиции [start] строка [old] изменилась на [new]
     */
    fun onTextBlockChanged(start: Int, old: CharSequence, new: CharSequence)
}