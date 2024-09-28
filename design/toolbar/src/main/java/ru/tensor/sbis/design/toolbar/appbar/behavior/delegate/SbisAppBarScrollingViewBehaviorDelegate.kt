package ru.tensor.sbis.design.toolbar.appbar.behavior.delegate

import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.appbar.AppBarLayout
import ru.tensor.sbis.design.toolbar.appbar.SbisAppBarLayout
import ru.tensor.sbis.design.toolbar.appbar.behavior.SbisAppBarScrollingViewBehavior
import ru.tensor.sbis.design.utils.checkNotNullSafe

/**
 * Блокирует возможность сворачивания [SbisAppBarLayout], в зависимости от содержимого.
 * Необходимо делегировать вызов [AppBarLayout.ScrollingViewBehavior.layoutChild], либо унаследоваться от
 * [SbisAppBarScrollingViewBehavior].
 *
 * @author us.bessonov
 */
abstract class SbisAppBarScrollingViewBehaviorDelegate {

    /**
     * Должна ли шапка иметь возможность сворачиваться.
     */
    abstract fun shouldEnableCollapsing(parent: CoordinatorLayout, header: SbisAppBarLayout, child: View): Boolean

    /** @SelfDocumented */
    fun layoutChild(parent: CoordinatorLayout, child: View) {
        val header = checkNotNullSafe(findSbisAppBarLayoutDependency(parent, child)) {
            "Cannot find SbisAppBarLayout as dependency of $child"
        } ?: return
        val isCollapsingEnabled = shouldEnableCollapsing(parent, header, child)
        header.setCollapsible(isCollapsingEnabled)
    }

    private fun findSbisAppBarLayoutDependency(parent: CoordinatorLayout, child: View): SbisAppBarLayout? {
        val dependencies = parent.getDependencies(child)
        return dependencies.find { it is SbisAppBarLayout } as SbisAppBarLayout?
    }
}
