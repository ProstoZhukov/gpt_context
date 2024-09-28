package ru.tensor.sbis.widget_player.widget.image

import com.facebook.drawee.drawable.ScalingUtils

/**
 * @author am.boldinov
 */
internal sealed interface ImageSizeConstraint {

    val width: Int
    val height: Int
    val scaleType: ScalingUtils.ScaleType

    /**
     * Рендеринг содержимого по всей ширине экрана.
     */
    data class Cover(
        override val scaleType: ScalingUtils.ScaleType
    ) : ImageSizeConstraint {
        override val width = 0
        override val height = 0
    }

    /**
     * Рендеринг содержимого в соответствии с размерами в процентах.
     */
    data class Percent(
        override val width: Int,
        override val height: Int,
        override val scaleType: ScalingUtils.ScaleType
    ) : ImageSizeConstraint

    /**
     * Рендеринг содержимого в соответствии с размерами в пикселях.
     */
    data class Pixel(
        override val width: Int,
        override val height: Int,
        override val scaleType: ScalingUtils.ScaleType
    ) : ImageSizeConstraint
}

