package ru.tensor.sbis.date_picker

/**
 * @author mb.kruglova
 */
interface SpanSizeProvider {
    fun getSpanSize(position: Int): Int
}