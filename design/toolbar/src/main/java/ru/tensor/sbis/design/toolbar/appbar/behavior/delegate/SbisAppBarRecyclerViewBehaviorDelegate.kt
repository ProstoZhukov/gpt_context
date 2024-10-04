package ru.tensor.sbis.design.toolbar.appbar.behavior.delegate

import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.toolbar.appbar.SbisAppBarLayout
import ru.tensor.sbis.design.utils.checkNotNullSafe

internal const val SCROLL_DIRECTION_DOWN = 1
internal const val SCROLL_DIRECTION_UP = -1

/**
 * Реализация [SbisAppBarScrollingViewBehaviorDelegate], определяющая возможность сворачивания шапки, в зависимости от
 * возможности прокрутки дочернего [RecyclerView].
 *
 * @author us.bessonov
 */
class SbisAppBarRecyclerViewBehaviorDelegate : SbisAppBarScrollingViewBehaviorDelegate() {

    override fun shouldEnableCollapsing(parent: CoordinatorLayout, header: SbisAppBarLayout, child: View): Boolean {
        checkNotNullSafe(child as? RecyclerView) {
            "You should use SbisAppBarRecyclerViewBehavior only with RecyclerView"
        }?.let {
            return it.canScrollVertically(SCROLL_DIRECTION_DOWN) || it.canScrollVertically(SCROLL_DIRECTION_UP)
        }
        return true
    }
}