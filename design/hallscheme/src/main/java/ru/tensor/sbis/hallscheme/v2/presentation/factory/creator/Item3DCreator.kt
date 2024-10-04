package ru.tensor.sbis.hallscheme.v2.presentation.factory.creator

import android.content.Context
import ru.tensor.sbis.hallscheme.v2.ColorsHolder
import ru.tensor.sbis.hallscheme.v2.HallSchemeSpecHolder
import ru.tensor.sbis.hallscheme.v2.PlanTheme
import ru.tensor.sbis.hallscheme.v2.business.TableTexture
import ru.tensor.sbis.hallscheme.v2.presentation.model.OrderableItemUi

/**
 * Подготавливает объекты для отображения в объёмной теме.
 * @author aa.gulevskiy
 */
internal class Item3DCreator(
    context: Context,
    colorsHolder: ColorsHolder,
    hallSchemeSpecHolder: HallSchemeSpecHolder,
    private val tableTexture: TableTexture
) : ItemCreator(context, colorsHolder, hallSchemeSpecHolder, PlanTheme.THEME_3D) {

    override fun createPath(itemUi: OrderableItemUi) {
        itemUi.create3dPath(tableTexture)
    }

    override fun calculateChairs(itemUi: OrderableItemUi) {
        itemUi.calculateSofasAndChairs()
    }
}