package ru.tensor.sbis.main_screen.widget.view

import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import io.reactivex.functions.Consumer
import ru.tensor.sbis.design.navigation.view.model.ItemSelected
import ru.tensor.sbis.design.navigation.view.model.ItemSelectedByUser
import ru.tensor.sbis.design.navigation.view.model.NavigationEvent
import ru.tensor.sbis.design.navigation.view.model.NavigationItem
import ru.tensor.sbis.design.navigation.view.model.SelectedSameItem

/**
 * Вспомогательная реализация обработчика выбранного элемента, учитывающая особенность работы с [DrawerLayout].
 *
 * @author kv.martyshenko
 */
internal class MenuItemSelectionConsumer(
    private val drawerLayout: DrawerLayout,
    private val onNavigationTabSelected: (NavigationEvent<NavigationItem>?) -> Unit
) : Consumer<NavigationEvent<NavigationItem>?> {

    override fun accept(event: NavigationEvent<NavigationItem>?) {
        val navigationTab = getSelected(event)
        if (event != null && navigationTab != null) {
            onNavigationTabSelected(event)
        }
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers()
        }
    }

    private fun getSelected(event: NavigationEvent<NavigationItem>?): NavigationItem? {
        return when (event) {
            is ItemSelectedByUser -> event.selectedItem
            is ItemSelected -> event.selectedItem
            is SelectedSameItem -> event.selectedItem
            else -> null
        }
    }
}