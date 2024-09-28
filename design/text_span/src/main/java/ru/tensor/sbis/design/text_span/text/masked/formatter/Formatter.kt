package ru.tensor.sbis.design.text_span.text.masked.formatter

import android.text.Spannable

/**
 * Модель форматтера, который можно применить к тексту. Реализации модели отвечают только за
 * форматирование
 *
 * @author ma.kolpakov
 * Создан 3/29/2019
 */
interface Formatter {

    /**
     * Добавляет необходимые символы форматирования в строку - в виде спанов
     */
    fun apply(s: Spannable)
}