package ru.tensor.sbis.design.navigation.view.view.tabmenu.orientation

import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import ru.tensor.sbis.design.navigation.view.view.tabmenu.MenuButtonVisibility
import ru.tensor.sbis.design.navigation.view.view.tabmenu.TabNavView
import ru.tensor.sbis.design.navigation.view.view.tabmenu.behavior.BottomHideOnScroll
import ru.tensor.sbis.design.navigation.view.view.tabmenu.behavior.BottomHideOnScrollBehavior
import timber.log.Timber
import ru.tensor.sbis.design.R as RDesign

/**
 * Стратегия поведения в горизонтальной ориентации.
 *
 * @author ma.kolpakov
 * Создан 11/30/2018
 */
internal class HorizontalOrientationStrategy : OrientationStrategy {

    private var pinned: Boolean = false

    override fun isViewPinned(): Boolean = pinned

    override fun setViewPinned(view: TabNavView, pinned: Boolean) {
        this.pinned = pinned
        runCatching { view.getVisibilityBehavior() }
            // можно безопасно игнорировать установку. Значение будет применено при получении поведения
            .onSuccess { it?.applyPinState() }
            .onFailure { Timber.w(IllegalStateException("Unable to apply pin mode to behavior", it)) }
    }

    override fun hide(view: TabNavView, animated: Boolean) {
        if (!pinned) {
            runCatching { view.getVisibilityBehavior() }
                .onSuccess { it?.slideDown(view, animated) ?: hideForced(view) }
                .onFailure { Timber.w(IllegalStateException("Unable to hide with slideDown", it)) }
        }
    }

    override fun show(view: TabNavView, animated: Boolean) {
        if (!pinned) {
            runCatching { view.getVisibilityBehavior() }
                .onSuccess { it?.slideUp(view, animated) ?: showForced(view) }
                .onFailure { Timber.w(IllegalStateException("Unable to show with slideUp", it)) }
        }
    }

    override fun getDefaultBehaviour(): CoordinatorLayout.Behavior<TabNavView> =
        BottomHideOnScrollBehavior<TabNavView>().apply { applyPinState() }

    override fun updateMenuVisibility(
        menu: View,
        menuButtonVisibility: MenuButtonVisibility,
        isMenuFits: Boolean
    ) {

        menu.visibility = when {
            menuButtonVisibility == MenuButtonVisibility.VISIBLE -> View.VISIBLE
            menuButtonVisibility == MenuButtonVisibility.HIDDEN -> View.GONE
            menuButtonVisibility == MenuButtonVisibility.AUTO && !menu.hasOnClickListeners() -> View.GONE
            // все элементы не помещаются без прокрутки - покажем кнопку меню
            menuButtonVisibility == MenuButtonVisibility.AUTO && isMenuFits -> View.VISIBLE
            else -> View.GONE
        }
    }

    private fun clearAnimation(view: TabNavView) {
        view.clearAnimation()
        view.animate().cancel()
    }

    private fun showForced(view: TabNavView) {
        clearAnimation(view)
        view.translationY = 0f
    }

    private fun hideForced(view: TabNavView) {
        clearAnimation(view)
        val measuredHeight = view.measuredHeight
        view.translationY = if (view.isAttachedToWindow && measuredHeight > 0)
            measuredHeight.toFloat()
        else
            view.resources.getDimension(RDesign.dimen.tab_navigation_menu_horizontal_height)
    }

    /**
     * Получение [BottomHideOnScroll] из [View.getLayoutParams]
     *
     * @throws IllegalStateException если [View.getParent] не [CoordinatorLayout] или поведение не реализует
     * [BottomHideOnScroll]
     */
    private fun TabNavView.getVisibilityBehavior(): BottomHideOnScroll<TabNavView>? {
        check(parent is CoordinatorLayout) {
            "View parent must be CoordinatorLayout to perform animated operations"
        }
        val behavior = (layoutParams as CoordinatorLayout.LayoutParams).behavior
        check(behavior is BottomHideOnScroll<*>?) {
            "Behavior must implement BottomHideOnScroll"
        }
        @Suppress("UNCHECKED_CAST")
        return behavior as BottomHideOnScroll<TabNavView>?
    }

    private fun BottomHideOnScroll<*>.applyPinState() {
        enabled = !pinned
    }
}