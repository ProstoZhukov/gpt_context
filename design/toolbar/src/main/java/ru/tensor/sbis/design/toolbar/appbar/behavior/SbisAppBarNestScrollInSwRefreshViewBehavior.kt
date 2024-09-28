package ru.tensor.sbis.design.toolbar.appbar.behavior

import android.content.Context
import android.util.AttributeSet
import ru.tensor.sbis.design.toolbar.appbar.behavior.delegate.SbisAppBarNestScrollInSwRefreshBehaviorDelegate

/**
 * @see [SbisAppBarNestScrollInSwRefreshBehaviorDelegate]
 *
 * @author us.bessonov
 */
@Suppress("unused")

class SbisAppBarNestScrollInSwRefreshViewBehavior(context: Context, attrs: AttributeSet) :
    SbisAppBarScrollingViewBehavior(context, attrs) {

    override val delegate = SbisAppBarNestScrollInSwRefreshBehaviorDelegate()
}