package ru.tensor.sbis.main_screen.widget.view

import ru.tensor.sbis.design.navigation.view.view.HideableNavigationView
import ru.tensor.sbis.design.navigation.view.view.tabmenu.TabNavView
import kotlin.properties.Delegates

/**
 * Реализация [HideableNavigationView], работающая как компоновщик.
 *
 * @author kv.martyshenko
 */
internal class HidableNavBunchView(
    private val bottomNavigation: TabNavView,
    private vararg val views: HideableNavigationView
) : HideableNavigationView {

    override var pinned: Boolean by Delegates.observable(bottomNavigation.pinned) { _, _, value ->
        delegateToView { pinned = value }
        bottomNavigation.pinned = value
    }

    override fun hide(animated: Boolean) {
        delegateToView { hide(animated) }
        bottomNavigation.hide(animated)
    }

    override fun show(animated: Boolean) {
        delegateToView { show(animated) }
        bottomNavigation.show(animated)
    }

    private fun delegateToView(action: HideableNavigationView.() -> Unit) {
        views.forEach(action)
    }

}