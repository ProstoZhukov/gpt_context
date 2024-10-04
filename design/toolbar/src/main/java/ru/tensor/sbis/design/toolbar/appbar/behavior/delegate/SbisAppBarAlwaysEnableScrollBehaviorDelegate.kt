package ru.tensor.sbis.design.toolbar.appbar.behavior.delegate

import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import ru.tensor.sbis.design.toolbar.appbar.SbisAppBarLayout

/**
 * Реализация [SbisAppBarScrollingViewBehaviorDelegate], не блокирующая прокрутку шапки.
 *
 * @author us.bessonov
 */
@Suppress("unused")
class SbisAppBarAlwaysEnableScrollBehaviorDelegate : SbisAppBarScrollingViewBehaviorDelegate() {

    override fun shouldEnableCollapsing(parent: CoordinatorLayout, header: SbisAppBarLayout, child: View) = true
}