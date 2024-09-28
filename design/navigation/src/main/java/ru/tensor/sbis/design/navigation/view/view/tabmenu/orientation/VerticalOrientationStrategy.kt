package ru.tensor.sbis.design.navigation.view.view.tabmenu.orientation

import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import ru.tensor.sbis.design.navigation.R
import ru.tensor.sbis.design.navigation.view.view.tabmenu.MenuButtonVisibility
import ru.tensor.sbis.design.navigation.view.view.tabmenu.TabNavView

/**
 * Стратегия поведения в вертикальной ориентации
 *
 * @author ma.kolpakov
 * Создан 11/30/2018
 */
internal class VerticalOrientationStrategy : OrientationStrategy {

    /**
     * Property can be safely changed, but (on use side) call of [hide] and [show] will thrown exception
     */
    private var pinned: Boolean = false

    override fun isViewPinned(): Boolean = pinned

    override fun setViewPinned(view: TabNavView, pinned: Boolean) {
        this.pinned = pinned
    }

    override fun hide(view: TabNavView, animated: Boolean) {
        throw UnsupportedOperationException("Hide operation is not supported for vertical orientation")
    }

    override fun show(view: TabNavView, animated: Boolean) {
        throw UnsupportedOperationException("Show operation is not supported for vertical orientation")
    }

    override fun getDefaultBehaviour(): CoordinatorLayout.Behavior<TabNavView> {
        return object : CoordinatorLayout.Behavior<TabNavView>() {}
    }

    override fun updateMenuVisibility(menu: View, menuButtonVisibility: MenuButtonVisibility, isMenuFits: Boolean) {
        // TODO https://dev.sbis.ru/opendoc.html?guid=e59d2881-4e48-4786-9609-c3e1c1bf78ea&client=3
        menu.findViewById<View>(R.id.tab_icon_text)?.visibility = when {
            menuButtonVisibility == MenuButtonVisibility.VISIBLE -> View.VISIBLE
            menuButtonVisibility == MenuButtonVisibility.HIDDEN -> View.GONE
            menuButtonVisibility == MenuButtonVisibility.AUTO && menu.hasOnClickListeners() -> View.VISIBLE
            else -> View.GONE
        }
    }
}