package ru.tensor.sbis.design.toolbar.appbar.behavior.delegate

import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.widget.NestedScrollView
import ru.tensor.sbis.design.toolbar.appbar.SbisAppBarLayout

/**
 * Реализация [SbisAppBarScrollingViewBehaviorDelegate], определяющая возможность сворачивания шапки, в зависимости от
 * высоты содержимого [NestedScrollView].
 *
 * @author us.bessonov
 */
@Suppress("unused")
class SbisAppBarNestedScrollViewBehaviorDelegate : SbisAppBarScrollingViewBehaviorDelegate() {

    override fun shouldEnableCollapsing(parent: CoordinatorLayout, header: SbisAppBarLayout, child: View): Boolean {
        val content = (child as ViewGroup).getChildAt(0)
        return header.measuredHeight + content.measuredHeight > parent.measuredHeight
    }
}