package ru.tensor.sbis.list.view.utils.layout_manager

import androidx.recyclerview.widget.GridLayoutManager

/**
 * Вычисляет количество span в группе [GridLayoutManager], в зависимости от размера элемента на экране.
 * [density] используется для перевода px в dp, по которых уже и подбирает количество.
 */
class SpanCountsCalculator(private val density: Float) {

    /**
     * Посчитать количество span.
     */
    fun calculate(screenWidthPx: Int): Int {
        val widthDp = screenWidthPx / density

        return when {
            widthDp >= 800 -> maxSpanCount
            widthDp >= 450 -> 4
            else -> 1
        }
    }
}

/**
 * Удобное число для подбора количества span для вывода одной, двух или трех ячеек в группе.
 */
private const val maxSpanCount = 6