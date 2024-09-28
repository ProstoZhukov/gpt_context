package ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.places

import ru.tensor.sbis.hallscheme.v2.HallSchemeSpecHolder
import ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.HallSchemeItem
import ru.tensor.sbis.hallscheme.v2.business.rects.SchemeItemBounds
import ru.tensor.sbis.hallscheme.v2.util.unsafeLazy
import java.util.UUID

/**
 * Модель, описывающая отдельно стоящее место.
 * @author aa.gulevskiy
 */
internal class Place(
    id: UUID?,
    category: String?,
    disposition: Int,
    kind: String,
    name: String?,
    type: Int?,
    x: Int,
    y: Int,
    z: Int,
    itemSpec: HallSchemeSpecHolder.PlaceSpec,
    val padding: Int
) : HallSchemeItem(id, null, category, disposition, kind, name, type, x, y, z) {

    override val rect: SchemeItemBounds by unsafeLazy {
        SchemeItemBounds(
            x, y,
            x + itemSpec.width + padding * 2,
            y + itemSpec.height + padding * 2
        )
    }
}