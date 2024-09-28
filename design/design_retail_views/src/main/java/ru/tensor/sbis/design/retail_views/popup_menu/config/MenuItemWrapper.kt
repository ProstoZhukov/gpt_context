package ru.tensor.sbis.design.retail_views.popup_menu.config

import androidx.annotation.StringRes
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.context_menu.MenuItem
import ru.tensor.sbis.design.context_menu.MenuItemState
import ru.tensor.sbis.design.theme.res.PlatformSbisString

/**
 * Элемент списка всплывающего меню.
 *
 * @param titleResId текст отображаемый элементом меню.
 * @param isSelected выбран ли данный элемент выпадающего списка ранее (если поддерживается).
 * Используется для обозначения текущего режима оплаты.
 * @param imageIcon иконка в формате [SbisMobileIcon.Icon].
 * @param action действие по нажатию на элемент списка.
 */
class MenuItemWrapper internal constructor(
    @StringRes val titleResId: Int,
    val isSelected: Boolean? = null,
    val imageIcon: SbisMobileIcon.Icon? = null,
    val action: () -> Unit
) {
    companion object {
        /**
         * Метод для создания кастомного пункта меню.
         *
         * @param titleResId текст отображаемый элементом меню.
         * @param isSelected выбран ли данный элемент выпадающего списка ранее (если поддерживается).
         * Используется для обозначения текущего режима оплаты.
         * @param imageIcon иконка в формате [SbisMobileIcon.Icon].
         * @param action действие по нажатию на элемент списка.
         * */
        fun createCustom(
            @StringRes titleResId: Int,
            isSelected: Boolean? = null,
            imageIcon: SbisMobileIcon.Icon? = null,
            action: () -> Unit
        ) = MenuItemWrapper(titleResId, isSelected, imageIcon, action)
    }
}

/** Метод для транформации [MenuItemWrapper] в [MenuItem]. */
internal fun MenuItemWrapper.transformToDefaultItem() =
    MenuItem(
        title = PlatformSbisString.Res(titleResId),
        image = imageIcon,
        state = if (isSelected == true) MenuItemState.ON else MenuItemState.MIXED,
        handler = action
    )