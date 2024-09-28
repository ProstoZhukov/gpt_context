package ru.tensor.sbis.design.rating.api

import ru.tensor.sbis.design.rating.model.SbisRatingColorsMode
import ru.tensor.sbis.design.rating.model.SbisRatingFilledMode
import ru.tensor.sbis.design.rating.model.SbisRatingPrecision
import ru.tensor.sbis.design.theme.global_variables.IconSize

/**
 * Api рейтинга для разных типов иконок.
 *
 * @author ps.smirnyh
 */
interface SbisRatingIconTypeApi {

    /**
     * Текущий рейтинг.
     * Ограничения по значению от 0.0 до 5.0.
     *
     * @throws IllegalArgumentException при выходе за допустимый диапазон значений.
     */
    var value: Double

    /**
     * Количество отображаемых иконок рейтинга.
     * Ограничения по значению от 2 до 5.
     *
     * @throws IllegalArgumentException при выходе за допустимый диапазон значений.
     */
    var maxValue: Int

    /** Размер отображаемых иконок. */
    var iconSize: IconSize

    /** Тип цвета, которым будут раскрашиваться иконки. */
    var colorsMode: SbisRatingColorsMode

    /** Тип отображения незакрашенных (пустых) иконок. */
    var emptyIconFilledMode: SbisRatingFilledMode

    /** Режим закрашивания иконок. */
    var precision: SbisRatingPrecision
}

/** @SelfDocumented */
const val SBIS_RATING_MIN_RATING = 0.0

/** @SelfDocumented */
const val SBIS_RATING_MAX_RATING = 5.0

/** @SelfDocumented */
const val SBIS_RATING_MIN_ICON_COUNT = 2L

/** @SelfDocumented */
const val SBIS_RATING_MAX_ICON_COUNT = 5L