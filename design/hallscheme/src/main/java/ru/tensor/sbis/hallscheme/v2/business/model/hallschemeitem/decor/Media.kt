package ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.decor

import ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.HallSchemeItem
import ru.tensor.sbis.hallscheme.v2.business.rects.SchemeItemBounds
import ru.tensor.sbis.hallscheme.v2.util.unsafeLazy
import java.util.UUID

/**
 * Произвольная картинка на схеме зала.
 */
internal class Media(
    id: UUID?,
    disposition: Int,
    kind: String,
    name: String?,
    type: Int?,
    x: Int,
    y: Int,
    z: Int,
    val width: Int,
    val height: Int,
    val opacity: Float,
    val url: String?
): HallSchemeItem(id, null, null, disposition, kind, name, type, x, y, z) {

    override val rect: SchemeItemBounds by unsafeLazy {
        SchemeItemBounds(x, y, x + width, y + height)
    }
}