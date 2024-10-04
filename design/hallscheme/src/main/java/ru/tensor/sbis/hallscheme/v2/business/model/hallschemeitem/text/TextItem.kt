package ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.text

import ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.HallSchemeItem
import ru.tensor.sbis.hallscheme.v2.business.rects.SchemeItemBounds
import ru.tensor.sbis.hallscheme.v2.util.unsafeLazy
import java.util.*

/**
 * Произвольный текст на схеме зала.
 * id = 800
 * @author aa.gulevskiy
 */
internal class TextItem private constructor(
    id: UUID?,
    disposition: Int,
    kind: String,
    name: String?,
    type: Int?,
    x: Int,
    y: Int,
    z: Int,
    val size: Int,
    val color: String?,
    val width: Int,
    val height: Int,
    val opacity: Float,
    val margin: Int
) : HallSchemeItem(id, null, null, disposition, kind, name, type, x, y, z) {

    constructor(
            id: UUID?,
            disposition: Int,
            kind: String,
            name: String?,
            type: Int?,
            x: Int,
            y: Int,
            z: Int,
            size: Int?,
            color: String?,
            width: Int,
            height: Int,
            opacity: Float,
            margin: Int
    ) : this(id, disposition, kind, name, type, x, y, z,
            size ?: 20,
            color, width, height, opacity, margin)

    override val rect: SchemeItemBounds by unsafeLazy {
        SchemeItemBounds(x, y, x + width, y + size)
    }
}