/**
 * Инструменты для работы с пунктами меню приложения
 *
 * @author us.bessonov
 */
package ru.tensor.sbis.common.navigation

import androidx.annotation.StringRes
import ru.tensor.sbis.common.R

/**
 * Позволяет получить название пункта раздела "Сотрудники", в зависимости от того, является ли аккаунт пользователя
 * аккаунтом физического лица
 */
@StringRes
fun getEmployeesMenuItemLabel(isPhysical: Boolean) = if (isPhysical) {
    R.string.common_navigation_menu_item_contacts
} else {
    R.string.common_navigation_menu_item_employees
}

/**
 * Позволяет получить название пункта раздела "Сотрудники", в зависимости от того, есть ли доступ к данному разделу
 */
@StringRes
fun getEmployeesNavMenuItemLabel(hasPermissions: Boolean) = if (hasPermissions) {
    R.string.common_navigation_menu_item_employees
} else {
    R.string.common_navigation_menu_item_contacts
}