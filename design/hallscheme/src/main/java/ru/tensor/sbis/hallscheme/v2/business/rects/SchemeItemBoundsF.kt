package ru.tensor.sbis.hallscheme.v2.business.rects

/**
 * Класс, предоставляющий границы объекта (верхнюю левую и нижнюю правую точки).
 * Координаты точек не проверяются, поэтому клиент должен удостовериться, что
 * left <= right и top <= bottom.
 * @author aa.gulevskiy
 */
internal data class SchemeItemBoundsF(val leftTop: BoundsPointF, val rightBottom: BoundsPointF) {
    constructor(left: Float, top: Float, right: Float, bottom: Float) :
            this(BoundsPointF(left, top), BoundsPointF(right, bottom))

    val left: Float
        get() {
            return leftTop.x
        }
    val top: Float
        get() {
            return leftTop.y
        }
    val right: Float
        get() {
            return rightBottom.x
        }
    val bottom: Float
        get() {
            return rightBottom.y
        }

    val width: Float
        get() {
            return rightBottom.x - leftTop.x
        }
    val height: Float
        get() {
            return rightBottom.y - leftTop.y
        }

    fun offset(dx: Float, dy: Float) {
        leftTop.x += dx
        leftTop.y += dy
        rightBottom.x += dx
        rightBottom.y += dy
    }
}

/**
 * Класс, описывающий координаты объекта.
 */
internal data class BoundsPointF(var x: Float, var y: Float)