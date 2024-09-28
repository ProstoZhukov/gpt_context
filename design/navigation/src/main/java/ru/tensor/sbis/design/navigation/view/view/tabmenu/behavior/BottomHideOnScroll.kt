package ru.tensor.sbis.design.navigation.view.view.tabmenu.behavior

import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout

/**
 * Интерфейс, который определяет поведение при прокрутке для [CoordinatorLayout.Behavior].
 *
 * @author ma.kolpakov
 * Создан 11/20/2018
 */
interface BottomHideOnScroll<V : View> {

    /**
     * Отметка о состоянии активности поведения [CoordinatorLayout.Behavior] при прокрутке.
     */
    var enabled: Boolean

    /**
     * Реализация метода должна определять поведение при прокрутке вверх.
     */
    fun slideUp(child: V, animated: Boolean)

    /**
     * Реализация метода должна определять поведение при прокрутке вниз.
     */
    fun slideDown(child: V, animated: Boolean)
}