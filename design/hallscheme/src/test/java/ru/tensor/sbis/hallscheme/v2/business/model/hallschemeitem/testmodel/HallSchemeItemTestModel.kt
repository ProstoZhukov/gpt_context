package ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.testmodel

import ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.HallSchemeItem
import ru.tensor.sbis.hallscheme.v2.business.rects.SchemeItemBounds
import java.util.UUID

/**
 * @author aa.gulevskiy
 */
internal class HallSchemeItemTestModel(
    id: UUID?,
    cloudId: Int?,
    disposition: Int,
    kind: String,
    name: String?,
    type: Int?,
    x: Int,
    y: Int,
    z: Int,
    override val rect: SchemeItemBounds = SchemeItemBounds()
) : HallSchemeItem(id, cloudId, null, disposition, kind, name, type, x, y, z)