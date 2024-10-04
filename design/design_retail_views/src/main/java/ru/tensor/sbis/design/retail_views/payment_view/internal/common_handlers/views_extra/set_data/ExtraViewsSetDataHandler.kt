package ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_extra.set_data

import androidx.fragment.app.FragmentManager
import ru.tensor.sbis.design.container.DimType
import ru.tensor.sbis.design.context_menu.SbisMenu
import ru.tensor.sbis.design.context_menu.showMenu
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_extra.action_listeners.ExtraViewsActionListenerApi
import ru.tensor.sbis.design.retail_views.popup_menu.PopupMenuConfiguration
import ru.tensor.sbis.design.retail_views.popup_menu.config.transformToDefaultItem

/** Реализация объекта для установки данных в блок "блок дополнительных действий". */
internal class ExtraViewsSetDataHandler(
    private val actionListenerApi: ExtraViewsActionListenerApi
) : ExtraViewsSetDataApi {

    override fun configureMoreMenu(
        fragmentManager: FragmentManager,
        configuration: PopupMenuConfiguration
    ) {
        actionListenerApi.setMoreClickListener { view ->
            SbisMenu(
                children = configuration.menuItems
                    .map { it.transformToDefaultItem() }
            ).showMenu(
                fragmentManager,
                anchor = view,
                dimType = DimType.SHADOW,
                customWidth = configuration.displayOptions.width
            )
        }
    }
}