package ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_extra.access_safety

import android.content.Context
import androidx.core.view.isVisible
import ru.tensor.sbis.design.container.DimType
import ru.tensor.sbis.design.context_menu.SbisMenu
import ru.tensor.sbis.design.context_menu.showMenu
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.tryFindFragmentManager
import ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_extra.access_dangerous.ExtraViewsAccessDangerousApi
import ru.tensor.sbis.design.retail_views.popup_menu.PopupMenuConfiguration
import ru.tensor.sbis.design.retail_views.popup_menu.config.transformToDefaultItem

/** Реализация объекта для безопасного доступа к View элементам "блок дополнительных действий". */
internal class ExtraViewsAccessSafetyHandler(
    private val viewAccessApi: ExtraViewsAccessDangerousApi
) : ExtraViewsAccessSafetyApi {

    private val context: Context
        get() = viewAccessApi.moreButton.context

    override fun setQrCodeButtonVisibility(isVisible: Boolean) {
        viewAccessApi.qrCodeButton.isVisible = isVisible
    }

    override fun setMoreButtonVisibility(isVisible: Boolean) {
        viewAccessApi.moreButton.isVisible = isVisible
    }

    override fun setSendButtonVisibility(isVisible: Boolean) {
        viewAccessApi.sendButton.isVisible = isVisible
    }

    override fun setQrCodeButtonEnabled(isEnabled: Boolean) {
        viewAccessApi.qrCodeButton.isEnabled = isEnabled
    }

    override fun setMoreButtonEnabled(isEnabled: Boolean) {
        viewAccessApi.moreButton.isEnabled = isEnabled
    }

    override fun setSendButtonEnabled(isEnabled: Boolean) {
        viewAccessApi.sendButton.isEnabled = isEnabled
    }

    override fun configureMoreMenu(configuration: PopupMenuConfiguration) {
        viewAccessApi.moreButton.setOnClickListener { view ->
            SbisMenu(
                children = configuration.menuItems
                    .map { it.transformToDefaultItem() }
            )
                .showMenu(
                    view.tryFindFragmentManager(),
                    anchor = view,
                    dimType = DimType.SHADOW,
                    customWidth = configuration.displayOptions.width
                )
        }
    }
}