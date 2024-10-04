package ru.tensor.sbis.design.retail_views.popup_menu.more_menu

import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.retail_views.R
import ru.tensor.sbis.design.retail_views.popup_menu.config.MenuItemWrapper

/** Стандартные параметры для инициализации меню действий кнопки "Ещё". */
object MoreMenuDefaultConfigurator {

    /** Метод для создания общего пункта меню "Счет". */
    fun MenuItemWrapper.Companion.createBillMenuItem(action: () -> Unit) =
        MenuItemWrapper(
            titleResId = R.string.retail_views_payment_bill_btn_text,
            imageIcon = SbisMobileIcon.Icon.smi_UnloadNew,
            action = action
        )

    /** Метод для создания общего пункта меню "Комментарий". */
    fun MenuItemWrapper.Companion.createCommentMenuItem(action: () -> Unit) =
        MenuItemWrapper(
            titleResId = R.string.retail_views_payment_comment_btn_text,
            imageIcon = SbisMobileIcon.Icon.smi_EditComment,
            action = action
        )
}