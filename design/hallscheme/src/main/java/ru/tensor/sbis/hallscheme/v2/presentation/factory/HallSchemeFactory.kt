package ru.tensor.sbis.hallscheme.v2.presentation.factory

import android.content.Context
import ru.tensor.sbis.hallscheme.v2.ColorsHolder
import ru.tensor.sbis.hallscheme.v2.HallSchemeSpecHolder
import ru.tensor.sbis.hallscheme.v2.PlanTheme
import ru.tensor.sbis.hallscheme.v2.business.TableTexture
import ru.tensor.sbis.hallscheme.v2.presentation.factory.creator.Item3DCreator
import ru.tensor.sbis.hallscheme.v2.presentation.factory.creator.ItemCreator
import ru.tensor.sbis.hallscheme.v2.presentation.factory.creator.ItemFlatCreator
import ru.tensor.sbis.hallscheme.v2.presentation.factory.drawer.Drawer3D
import ru.tensor.sbis.hallscheme.v2.presentation.factory.drawer.DrawerFlat
import ru.tensor.sbis.hallscheme.v2.presentation.factory.drawer.HallSchemeDrawer

/**
 * Фабрика для создания и отображения элементов схемы.
 * @author aa.gulevskiy
 */
internal class HallSchemeFactory(planThemeString: String?, textureType: Int) {

    private val planTheme = PlanTheme.getByStringName(planThemeString)

    private val tableTexture = TableTexture.getByType(textureType)

    /**
     *  Возвращает объект класса ItemCreator, предназначенный для создания необходимых для отображения объектов.
     */
    fun getHallSchemeItemCreator(
        context: Context,
        colorsHolder: ColorsHolder,
        hallSchemeSpecHolder: HallSchemeSpecHolder
    ): ItemCreator {
        return when (planTheme) {
            PlanTheme.THEME_FLAT ->
                ItemFlatCreator(
                    context,
                    colorsHolder,
                    hallSchemeSpecHolder
                )
            PlanTheme.THEME_3D ->
                Item3DCreator(
                    context,
                    colorsHolder,
                    hallSchemeSpecHolder,
                    tableTexture
                )
        }
    }

    /**
     *  Вовзращает объект класса HallSchemeDrawer, предназначенный для отображения объектов.
     */
    fun getHallSchemeDrawer(): HallSchemeDrawer {
        return when (planTheme) {
            PlanTheme.THEME_FLAT -> DrawerFlat()
            PlanTheme.THEME_3D -> Drawer3D(tableTexture)
        }
    }
}