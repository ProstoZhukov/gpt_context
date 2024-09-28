/**
 * Набор реализованных [MeasureContract] для разных контентов шапки.
 *
 * @author da.zolotarev
 */
package ru.tensor.sbis.design.topNavigation.internal_view

import android.view.View
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils.makeUnspecifiedSpec
import ru.tensor.sbis.design.custom_view_tools.utils.MeasureSpecUtils.measureDirection
import ru.tensor.sbis.design.topNavigation.util.layoutContentAndGetHeight
import ru.tensor.sbis.design.topNavigation.util.measureAndGetHeight
import ru.tensor.sbis.design.topNavigation.view.SbisTopNavigationView

/**
 * Стандартный  [MeasureContract].
 */
internal class DefaultMeasureContract(
    private val getView: (SbisTopNavigationView) -> View
) : MeasureContract {

    override fun measureAndGetHeight(
        parent: SbisTopNavigationView,
        mainContentWidthSpec: Int,
        parentHeightMeasureSpec: Int,
        isCollapsed: Boolean
    ): Int {
        val mainContentView = getView(parent)
        val mainContentHeightSpec = if (isCollapsed) {
            MeasureSpecUtils.makeExactlySpec(parent.styleHolder.collapsedTopNavHeight)
        } else {
            measureDirection(parentHeightMeasureSpec) { mainContentView.minimumHeight }
        }
        return mainContentView.measureAndGetHeight(mainContentWidthSpec, mainContentHeightSpec)
    }

    override fun layoutAndGetHeight(left: Int, top: Int, parent: SbisTopNavigationView): Int =
        getView(parent).layoutContentAndGetHeight(left, top)
}

/** @SelfDocumented */
internal val largeTitleMeasurer = DefaultMeasureContract { it.largeTitleContainerView }

/** @SelfDocumented */
internal val searchInputMeasurer = DefaultMeasureContract { it.searchContainerView }

/** @SelfDocumented */
internal val tabsMeasurer = DefaultMeasureContract { it.tabsContainerView }

/** @SelfDocumented */
internal val emptyMeasurer = DefaultMeasureContract { it.emptyContainerView }

/** @SelfDocumented */
internal val logoMeasurer = DefaultMeasureContract { it.logoContainerView }

/** @SelfDocumented */
internal val smallTitleMeasurer = object : MeasureContract {
    override fun measureAndGetHeight(
        parent: SbisTopNavigationView,
        mainContentWidthSpec: Int,
        parentHeightMeasureSpec: Int,
        isCollapsed: Boolean
    ): Int {
        parent.smallTitleContainerView.measure(
            mainContentWidthSpec,
            getHeightSpec(parent, isCollapsed)
        )
        return parent.smallTitleContainerView.measuredHeight
    }

    override fun layoutAndGetHeight(left: Int, top: Int, parent: SbisTopNavigationView): Int {
        return parent.smallTitleContainerView.layoutContentAndGetHeight(left, top)
    }

    /** @SelfDocumented */
    private fun getHeightSpec(parent: SbisTopNavigationView, isCollapsed: Boolean) =
        if (isCollapsed) {
            MeasureSpecUtils.makeExactlySpec(parent.styleHolder.collapsedTopNavHeight)
        } else {
            makeUnspecifiedSpec()
        }
}