package ru.tensor.sbis.main_screen_decl.navigation

import ru.tensor.sbis.design.navigation.view.model.NavigationItem

/**
 * Пункт навигации, который может быть скрыт при сворачивании в аккордеоне.
 * Скрытие элемента таким способом следует отличать от прочих, чтобы не считать его недоступным для выбора.
 *
 * @author us.bessonov
 */
interface CollapsibleNavigationItem : NavigationItem {

    /** @SelfDocumented */
    val isCollapsed: Boolean
}