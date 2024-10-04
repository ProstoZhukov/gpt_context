package ru.tensor.sbis.design.retail_views.payment_view.internal.common_handlers.views_extra.set_data

import androidx.fragment.app.FragmentManager
import ru.tensor.sbis.design.retail_views.popup_menu.PopupMenuConfiguration

/** Обобщение API для установки данных в "блок дополнительных действий". */
interface ExtraViewsSetDataApi {

    /** Конфигурирование выпадающего списка меню для кнопки "Еще". */
    fun configureMoreMenu(
        fragmentManager: FragmentManager,
        configuration: PopupMenuConfiguration
    )
}