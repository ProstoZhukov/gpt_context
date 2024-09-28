package ru.tensor.sbis.main_screen_decl.navigation

import ru.tensor.sbis.design.navigation.view.model.NavigationItem

/**
 * Предназначен для проверки видимости элемента навигации.
 *
 * @author us.bessonov
 */
interface NavigationVisibilityProvider {

    /** @SelfDocumented */
    fun isItemVisible(item: NavigationItem): Boolean
}