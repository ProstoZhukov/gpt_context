package ru.tensor.sbis.design.toolbar.appbar.behavior

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.appbar.AppBarLayout
import ru.tensor.sbis.design.toolbar.appbar.SbisAppBarLayout
import ru.tensor.sbis.design.toolbar.appbar.behavior.delegate.SbisAppBarScrollingViewBehaviorDelegate

/**
 * Расширение [AppBarLayout.ScrollingViewBehavior] для содержимого экрана с графической шапкой ([SbisAppBarLayout]),
 * позволяющее блокировать возможность сворачивания шапки, в частности, если в развернутом состоянии содержимое
 * помещается полностью
 *
 * @author us.bessonov
 */
abstract class SbisAppBarScrollingViewBehavior : AppBarLayout.ScrollingViewBehavior {

    constructor() : super()

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    protected abstract val delegate: SbisAppBarScrollingViewBehaviorDelegate

    override fun layoutChild(parent: CoordinatorLayout, child: View, layoutDirection: Int) {
        super.layoutChild(parent, child, layoutDirection)
        delegate.layoutChild(parent, child)
    }

}