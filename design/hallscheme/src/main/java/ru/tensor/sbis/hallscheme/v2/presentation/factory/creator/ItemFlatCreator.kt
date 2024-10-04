package ru.tensor.sbis.hallscheme.v2.presentation.factory.creator

import android.content.Context
import ru.tensor.sbis.hallscheme.v2.ColorsHolder
import ru.tensor.sbis.hallscheme.v2.HallSchemeSpecHolder
import ru.tensor.sbis.hallscheme.v2.PlanTheme
import ru.tensor.sbis.hallscheme.v2.presentation.model.OrderableItemUi

/**
 * Подготавливает объекты для отображения в плоской теме.
 * @author aa.gulevskiy
 */
internal class ItemFlatCreator(
    context: Context,
    colorsHolder: ColorsHolder,
    hallSchemeSpecHolder: HallSchemeSpecHolder
) : ItemCreator(context, colorsHolder, hallSchemeSpecHolder, PlanTheme.THEME_FLAT) {

    override fun createPath(itemUi: OrderableItemUi) {
        itemUi.createFlatPath()
    }

    override fun calculateChairs(itemUi: OrderableItemUi) {
        itemUi.calculateSofasAndChairs()
    }
}