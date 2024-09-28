package ru.tensor.sbis.hallscheme.v2.business.rects

/**
 * Хранит данные для дуги (границы прямоугольника, начальный угол, угол смещения).
 * @author aa.gulevskiy
 */
internal data class ArcInfo(
    val schemeItemBoundsF: SchemeItemBoundsF,
    val startAngle: Float,
    val sweepAngle: Float
)