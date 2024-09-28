package ru.tensor.sbis.design.skeleton_view

import androidx.annotation.ColorInt
import ru.tensor.sbis.design.skeleton_view.mask.SkeletonShimmerDirection

/**
 * Интерфейс настроек Skeleton для отображения в UI
 *
 * @author us.merzlikina
 */
interface SkeletonStyle {

    /**
     * цвет маски для заполнения view
     */
    @get:ColorInt
    var maskColor: Int

    /**
     * радиус скругления углов у view маски
     */
    var maskCornerRadius: Float

    /**
     * показывать или нет анимацию мерцания
     */
    var showShimmer: Boolean

    /**
     * цвет анимации мерцания
     */
    @get:ColorInt
    var shimmerColor: Int

    /**
     * длительность интервала анимации мерцания в миллисекундах
     */
    var shimmerDuration: Long

    /**
     * направление анимации мерцания
     */
    var shimmerDirection: SkeletonShimmerDirection
}