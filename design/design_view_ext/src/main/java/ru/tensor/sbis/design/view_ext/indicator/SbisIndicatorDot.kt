package ru.tensor.sbis.design.view_ext.indicator

import androidx.annotation.ColorInt

/**
 *  Служебная модель для точки индикатора
 *
 *  @param left расположение левой стороны точки по X
 *
 *  @author aa.prischep
 */
internal data class SbisIndicatorDot(
    var left: Float = 0f,
    var size: Float = 0f,
    var alpha: Int = 255,
    @ColorInt
    var dotColor: Int = 0,
    @ColorInt
    var borderColor: Int = 0
)
