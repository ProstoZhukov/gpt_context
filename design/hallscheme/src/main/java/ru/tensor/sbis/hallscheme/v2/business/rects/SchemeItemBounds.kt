package ru.tensor.sbis.hallscheme.v2.business.rects

import android.graphics.Rect

/**
 * Класс, предоставляющий границы объекта (верхнюю левую и нижнюю правую точки).
 * Координаты точек не проверяются, поэтому клиент должен удостовериться, что
 * left <= right и top <= bottom.
 * @author aa.gulevskiy
 */
internal data class SchemeItemBounds(val leftTop: BoundsPoint, val rightBottom: BoundsPoint) {

    /**
     * Создаёт новый пустой SchemeItemBounds. Все координаты инициализированы 0.
     */
    constructor() : this(BoundsPoint(0, 0), BoundsPoint(0, 0))

    constructor(left: Int, top: Int, right: Int, bottom: Int)
            : this(BoundsPoint(left, top), BoundsPoint(right, bottom))

    val width: Int
        get() {
            return rightBottom.x - leftTop.x
        }
    val height: Int
        get() {
            return rightBottom.y - leftTop.y
        }

    val left: Int
        get() {
            return leftTop.x
        }
    val top: Int
        get() {
            return leftTop.y
        }
    val right: Int
        get() {
            return rightBottom.x
        }
    val bottom: Int
        get() {
            return rightBottom.y
        }

    val boundsRect: Rect
        get() = Rect(left, top, right, bottom)

    fun rotateTo90(): SchemeItemBounds {
        val rightBottom = BoundsPoint(left + height, top + width)
        return SchemeItemBounds(this.leftTop, rightBottom)
    }
}

/**
 * Класс, описывающий координаты объекта.
 */
internal data class BoundsPoint(var x: Int, var y: Int)