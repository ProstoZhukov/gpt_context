package ru.tensor.sbis.design.navigation.view.view.tabmenu.orientation

import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import ru.tensor.sbis.design.navigation.view.view.tabmenu.MenuButtonVisibility
import ru.tensor.sbis.design.navigation.view.view.tabmenu.TabNavView
import ru.tensor.sbis.design.navigation.view.view.tabmenu.behavior.BottomHideOnScroll

/**
 * Реализация интерфейса определяет способ обработки специфичных для ориентации действий.
 *
 * @see [TabNavView]
 *
 * @author ma.kolpakov
 * Создан 11/30/2018
 */
internal interface OrientationStrategy {

    /**
     * Отметка о том, что view зафиксирована и не реагирует на вызовы [hide] и [show]. Допускаются
     * блокировки других вызовов, которые управляют отображением.
     */
    fun isViewPinned(): Boolean

    /**
     * Установка состояния "зафиксировано" для [view]. Допускается блокировка других вызовов,
     * которые изменяют положение. Например [HorizontalOrientationStrategy] "отключает" реакцию на
     * прокрутку для [BottomHideOnScroll] (`BottomHideOnScroll.enabled = false`).
     */
    fun setViewPinned(view: TabNavView, pinned: Boolean)

    /**
     * Обработка запроса на скрытие [view].
     */
    fun hide(view: TabNavView, animated: Boolean)

    /**
     * Обработка запроса на отображение [view].
     */
    fun show(view: TabNavView, animated: Boolean)

    /**
     * Поведение по умолчанию для [CoordinatorLayout].
     */
    fun getDefaultBehaviour(): CoordinatorLayout.Behavior<TabNavView>

    /**
     * Обновление внешнего вида компонента.
     *
     * @param menu кнопка меню.
     * @param menuButtonVisibility состояние видимости кнопки меню в ННП.
     * @param isMenuFits да, если минимальный размер элемента, больше текущего.
     */
    fun updateMenuVisibility(menu: View, menuButtonVisibility: MenuButtonVisibility, isMenuFits: Boolean)
}