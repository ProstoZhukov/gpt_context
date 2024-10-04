package ru.tensor.sbis.design.rating.model

import ru.tensor.sbis.design.rating.utils.RatingStyleHolder

/**
 * Тип цвета закрашенных иконок.
 *
 * @author ps.smirnyh
 */
enum class SbisRatingColorsMode {

    /** Все иконки закрашиваются одним цветом. */
    STATIC {
        override fun getFilledIconColor(countFilledIcons: Int, styleHolder: RatingStyleHolder): Int =
            styleHolder.filledIconColor
    },

    /** В зависимости от количества закрашенных иконок используется нужных цвет. */
    DYNAMIC {
        override fun getFilledIconColor(countFilledIcons: Int, styleHolder: RatingStyleHolder): Int =
            styleHolder.iconColors.getOrNull(countFilledIcons - 1) ?: styleHolder.iconColors.first()
    };

    /** @SelfDocumented */
    internal abstract fun getFilledIconColor(countFilledIcons: Int, styleHolder: RatingStyleHolder): Int
}
