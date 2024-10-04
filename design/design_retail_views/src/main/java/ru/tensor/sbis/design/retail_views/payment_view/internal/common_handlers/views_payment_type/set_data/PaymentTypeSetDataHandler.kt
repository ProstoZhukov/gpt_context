package ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_payment_type.set_data

import androidx.fragment.app.FragmentManager
import ru.tensor.sbis.design.container.DimType
import ru.tensor.sbis.design.container.locator.AnchorHorizontalLocator
import ru.tensor.sbis.design.container.locator.AnchorVerticalLocator
import ru.tensor.sbis.design.container.locator.HorizontalAlignment
import ru.tensor.sbis.design.container.locator.VerticalAlignment
import ru.tensor.sbis.design.context_menu.SbisMenu
import ru.tensor.sbis.design.context_menu.showMenuWithLocators
import ru.tensor.sbis.design.context_menu.utils.CheckboxIcon
import ru.tensor.sbis.design.retail_views.R
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_payment_type.action_listeners.PaymentTypeActionListenerApi
import ru.tensor.sbis.design.retail_views.popup_menu.PopupMenuConfiguration
import ru.tensor.sbis.design.retail_views.popup_menu.config.transformToDefaultItem

/** Реализация объекта для установки данных в блок "тип оплаты". */
internal class PaymentTypeSetDataHandler(
    private val actionListenerApi: PaymentTypeActionListenerApi
) : PaymentTypeSetDataApi {

    override fun configurePaymentTypeMenu(
        fragmentManager: FragmentManager,
        configuration: PopupMenuConfiguration
    ) {
        actionListenerApi.setPaymentTypeListener { view ->
            if (configuration.menuItems.isNotEmpty()) {
                SbisMenu(
                    children = configuration.menuItems
                        .map { it.transformToDefaultItem() },
                    stateOnIcon = CheckboxIcon.MARKER
                ).showMenuWithLocators(
                    fragmentManager = fragmentManager,
                    verticalLocator = AnchorVerticalLocator(
                        alignment = VerticalAlignment.BOTTOM,
                        innerPosition = false,
                        offsetRes = R.dimen.payment_custom_menu_margin
                    ).apply {
                        anchorView = view
                        rules.defaultMarginTop = false
                    },
                    horizontalLocator = AnchorHorizontalLocator(
                        alignment = HorizontalAlignment.LEFT,
                        innerPosition = true
                    ).apply { anchorView = view },
                    dimType = DimType.SHADOW
                )
            }
        }
    }
}