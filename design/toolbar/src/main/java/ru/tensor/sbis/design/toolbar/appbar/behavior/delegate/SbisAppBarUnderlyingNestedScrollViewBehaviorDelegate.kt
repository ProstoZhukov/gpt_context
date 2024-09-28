package ru.tensor.sbis.design.toolbar.appbar.behavior.delegate

import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.toolbar.appbar.SbisAppBarLayout

/**
 * Реализация [SbisAppBarScrollingViewBehaviorDelegate], определяющая возможность сворачивания шапки, в зависимости от
 * прокручиваемости первого найденного [View], для которого `[View.isNestedScrollingEnabled] == true`.
 * Частный случай применения - [RecyclerView], вложенный в один или более непрокручиваемых контейнеров.
 *
 * @author us.bessonov
 */
@Suppress("unused")
class SbisAppBarUnderlyingNestedScrollViewBehaviorDelegate : SbisAppBarScrollingViewBehaviorDelegate() {

    override fun shouldEnableCollapsing(parent: CoordinatorLayout, header: SbisAppBarLayout, child: View): Boolean {
        val view = findNestedScrollingView(child)
            ?: return true

        val availableHeight = parent.measuredHeight - header.measuredHeight
        return view.getChildViewsTotalHeight() > availableHeight
    }

    private fun View.getChildViewsTotalHeight(): Int {
        val viewGroup = this as? ViewGroup ?: return 0
        val viewsHeight = viewGroup.children.sumOf { view ->
            val margins = (view.layoutParams as? MarginLayoutParams?)
                ?.let { it.bottomMargin + it.topMargin }
                ?: 0
            view.measuredHeight + margins
        }
        return if (!viewGroup.clipToPadding) {
            viewsHeight + paddingBottom + paddingTop
        } else {
            viewsHeight
        }
    }

    private fun findNestedScrollingView(view: View?): View? {
        if (view == null) return null

        if (ViewCompat.isNestedScrollingEnabled(view)) {
            return view
        }

        (view as? ViewGroup)?.let { parent ->
            parent.children.forEach { child ->
                findNestedScrollingView(child)
                    ?.let { return it }
            }
        }

        return null
    }

}