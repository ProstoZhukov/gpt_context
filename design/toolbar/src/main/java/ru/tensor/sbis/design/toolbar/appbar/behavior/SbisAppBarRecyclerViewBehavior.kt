package ru.tensor.sbis.design.toolbar.appbar.behavior

import android.content.Context
import android.util.AttributeSet
import ru.tensor.sbis.design.toolbar.appbar.behavior.delegate.SbisAppBarRecyclerViewBehaviorDelegate

/**
 * @see [SbisAppBarRecyclerViewBehaviorDelegate]
 *
 * @author us.bessonov
 */
@Suppress("unused")
class SbisAppBarRecyclerViewBehavior : SbisAppBarScrollingViewBehavior {

    constructor() : super()

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override val delegate = SbisAppBarRecyclerViewBehaviorDelegate()
}