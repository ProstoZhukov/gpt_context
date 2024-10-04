package ru.tensor.sbis.design.topNavigation.internal_view

import ru.tensor.sbis.design.topNavigation.view.SbisTopNavigationView

/**
 * Контракт измерения расположения контента в шапке.
 *
 * @author da.zolotarev
 */
internal interface MeasureContract {
    /**
     * Померить контент и вернуть высоту.
     */
    fun measureAndGetHeight(
        parent: SbisTopNavigationView,
        mainContentWidthSpec: Int,
        parentHeightMeasureSpec: Int,
        isCollapsed: Boolean
    ): Int

    /**
     * Разместить контент и вернуть высоту.
     */
    fun layoutAndGetHeight(left: Int, top: Int, parent: SbisTopNavigationView): Int
}