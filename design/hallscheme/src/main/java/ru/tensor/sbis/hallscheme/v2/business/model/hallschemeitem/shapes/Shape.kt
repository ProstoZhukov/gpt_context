package ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.shapes

import ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.HallSchemeItem
import ru.tensor.sbis.hallscheme.v2.business.rects.SchemeItemBounds
import ru.tensor.sbis.hallscheme.v2.util.unsafeLazy
import java.util.*

/**
 * Класс, описывающий фигуры (прямоугольник, овал, линия).
 * @author aa.gulevskiy
 */
internal class Shape private constructor(
    id: UUID?,
    kind: String,
    name: String?,
    type: Int?,
    x: Int,
    y: Int,
    z: Int,
    val size: Int, // толщина линии
    val color: String?,
    val fillColor: String?,
    val width: Int,
    val height: Int,
    val opacity: Float
) : HallSchemeItem(id, null, null, 0, kind, name, type, x, y, z) {

    constructor(
        id: UUID?,
        kind: String,
        name: String?,
        type: Int?,
        x: Int,
        y: Int,
        z: Int,
        size: Int?,
        color: String?,
        fillColor: String?,
        width: Int?,
        height: Int?,
        opacity: Float
    ) : this(
        id, kind, name, type, x, y, z,
        if (size == null || size == 0) 1 else size,
        color,
        fillColor,
        width ?: 0,
        height ?: 0,
        opacity
    )

    override val rect: SchemeItemBounds by unsafeLazy {
        SchemeItemBounds(x, y, x + width, y + height)
    }
}