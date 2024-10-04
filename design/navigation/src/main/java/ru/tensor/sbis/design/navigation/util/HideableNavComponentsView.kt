package ru.tensor.sbis.design.navigation.util

import ru.tensor.sbis.design.navigation.view.view.HideableNavigationView
import ru.tensor.sbis.design.navigation.view.view.tabmenu.TabNavView

/**
 * Класс для совместного управления компонентами навигации.
 * Делегирует методы [hide] и [show] кнопке создания [com.google.android.material.floatingactionbutton.FloatingActionButton]
 * и ННП [TabNavView], обеспечивая наличие/отсутствие на экране обоих компонентов сразу.
 * Так как класс реализует интерфейс [HideableNavigationView], его можно использовать совместно с TabNavScrollHelper.
 *
 * @property fab wrapper для кнопки создания (плавающей кнопки)
 * @property bottomNavigation нижняя навигационная панель (ННП)
 *
 * @author ma.kolpakov
 */
class HideableNavComponentsView(
    private val fab: HideableNavigationView,
    private val bottomNavigation: TabNavView
) : HideableNavigationView {

    private var _pinned: Boolean = false
    override var pinned: Boolean
        get() = _pinned
        set(value) {
            _pinned = value
            fab.pinned = value
            bottomNavigation.pinned = value
        }

    override fun hide(animated: Boolean) {
        fab.hide(animated)
        bottomNavigation.hide(animated)
    }

    override fun show(animated: Boolean) {
        fab.show(animated)
        bottomNavigation.show(animated)
    }
}