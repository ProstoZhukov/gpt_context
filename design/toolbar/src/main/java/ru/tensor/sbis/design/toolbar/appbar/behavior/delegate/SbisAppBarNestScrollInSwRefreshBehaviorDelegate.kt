package ru.tensor.sbis.design.toolbar.appbar.behavior.delegate

import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.children
import androidx.core.widget.NestedScrollView
import ru.tensor.sbis.design.toolbar.appbar.SbisAppBarLayout

/**
 * Реализация [SbisAppBarScrollingViewBehaviorDelegate], определяющая возможность сворачивания шапки, в зависимости от
 * высоты содержимого [NestedScrollView] обернутого в `SbisPullToRefresh`.
 *
 * @author us.bessonov
 */
@Suppress("unused")
class SbisAppBarNestScrollInSwRefreshBehaviorDelegate : SbisAppBarScrollingViewBehaviorDelegate() {

    override fun shouldEnableCollapsing(parent: CoordinatorLayout, header: SbisAppBarLayout, child: View): Boolean {
        val content = (child as ViewGroup).children.find { it is NestedScrollView }
        return header.measuredHeight + content!!.measuredHeight > parent.measuredHeight
    }
}