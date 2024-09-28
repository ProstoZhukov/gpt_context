package ru.tensor.sbis.design.stubview.layout_strategies.icon_measuring_strategies

import android.view.View
import androidx.annotation.Px

/**
 * Стратегия измерения иконки заглушки
 *
 * @author ma.kolpakov
 */
internal interface IconMeasuringStrategy {

    /**
     * Измерение иконки заглушки
     *
     * @param icon иконка для измерения
     * @param containerWidth общая ширина контейнера заглушки
     * @param iconMinSize минимальный размер иконки
     * @param iconMaxSize максимальный размер иконки
     */
    fun measure(icon: View, @Px containerWidth: Int, @Px iconMinSize: Int, @Px iconMaxSize: Int)
}
