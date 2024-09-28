package ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem

import ru.tensor.sbis.hallscheme.v2.business.rects.BoundsPoint
import ru.tensor.sbis.hallscheme.v2.business.rects.SchemeItemBounds
import ru.tensor.sbis.hallscheme.v2.util.unsafeLazy
import java.util.*

/**
 * Абстрактный класс, представляющий элемент схемы.
 * @author aa.gulevskiy
 */
abstract class HallSchemeItem internal constructor(
    val id: UUID?,
    val cloudId: Int?,
    val category: String?,
    val disposition: Int, // Ориентация
    val kind: String,
    val name: String?,
    val type: Int?,
    val x: Int,
    val y: Int,
    val z: Int
) {

    /**
     * Прямоугольник, в границах которого располагается вью.
     */
    internal abstract val rect: SchemeItemBounds

    /**
     * Поворот в градусах.
     */
    internal val itemRotation = this.disposition % 4 * 90

    /**
     * Не повёрнута относительно горизонтальной оси.
     */
    internal val isHorizontal: Boolean
        get() = disposition % 2 == 0

    /**
     * Границы вью в зависимости от поворота (disposition).
     */
    internal val rotatedRect: SchemeItemBounds by unsafeLazy {
        if (isHorizontal) {
            rect
        } else {
            rect.rotateTo90()
        }
    }

    /**
     * Возвращает правую нижнюю точку прямоугольника, в котором будет расположена view.
     * Используется для первоначального подсчёта размеров лэйаута, в котором будут располагаться элементы.
     */
    internal open fun getRightBottomPoint(): BoundsPoint {
        return rotatedRect.rightBottom
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as HallSchemeItem

        if (id != other.id || cloudId != other.cloudId) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}