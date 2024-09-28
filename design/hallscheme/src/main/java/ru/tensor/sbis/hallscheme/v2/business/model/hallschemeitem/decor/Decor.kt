package ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.decor

import ru.tensor.sbis.hallscheme.v2.HallSchemeSpecHolder
import ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.HallSchemeItem
import ru.tensor.sbis.hallscheme.v2.business.rects.SchemeItemBounds
import ru.tensor.sbis.hallscheme.v2.util.unsafeLazy
import java.util.*

/**
 * Класс, описывающий элементы декора (камины, растения, окна, лестницы и т.п.).
 * @author aa.gulevskiy
 */
internal class Decor(
    id: UUID?,
    disposition: Int,
    kind: String,
    name: String?,
    type: Int?,
    x: Int,
    y: Int,
    z: Int,
    private val decorSpec: HallSchemeSpecHolder.DecorSpec,
    val opacity: Float
) : HallSchemeItem(id, null,  null, disposition, kind, name, type, x, y, z) {

    override val rect: SchemeItemBounds by unsafeLazy {
        SchemeItemBounds(x, y, x + decorSpec.width, y + decorSpec.height)
    }
}