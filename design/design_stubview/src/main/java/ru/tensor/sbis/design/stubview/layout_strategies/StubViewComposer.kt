package ru.tensor.sbis.design.stubview.layout_strategies

import android.view.View
import androidx.annotation.Px

/**
 * Стратегия позицизионирования элементов заглушки
 *
 * @author ma.kolpakov
 */
internal interface StubViewComposer {

    /**
     * Измерение размеров элементов заглушки
     *
     * @param containerWidth вся ширина контейнера, в котором находится заглушка
     * @param containerHeight вся высота контейнера, в котором находится заглушка
     */
    fun measure(@Px containerWidth: Int, @Px containerHeight: Int)

    /**
     * Установка положения элементов заглушки.
     * По аналогии с [View.layout]
     */
    fun layout(@Px left: Int, @Px top: Int, @Px right: Int, @Px bottom: Int)

    /**
     * Максимальная высота заглушки.
     * Высота, которую может занять контент + отступы, если высота контейнера не определена.
     * Вызывать метод нужно только после вызова [measure], иначе размеры элементов будут нулевыми.
     *
     * @return высота в пикселях
     */
    @Px
    fun maxHeight(): Int
}
