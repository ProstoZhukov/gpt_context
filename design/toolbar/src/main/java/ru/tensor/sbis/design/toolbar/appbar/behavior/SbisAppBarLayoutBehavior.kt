package ru.tensor.sbis.design.toolbar.appbar.behavior

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.core.view.children
import com.google.android.material.appbar.AppBarLayout
import ru.tensor.sbis.design.toolbar.multilinecollapsingtoolbar.CollapsingToolbarLayout
import ru.tensor.sbis.design.utils.hasFlag
import kotlin.math.abs
import kotlin.math.roundToInt

private const val MAX_OFFSET_ANIMATION_DURATION = 600
internal const val APP_BAR_MEDIATE_SNAP_OFFSET_POSITION = 0.3F

/**
 * Behavior для графической шапки для поддержки фиксации в промежуточном и свёрнутом состоянии.
 *
 * @author us.bessonov
 */
internal class SbisAppBarLayoutBehavior : AppBarLayout.Behavior {
    constructor() : super()

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    @ViewCompat.NestedScrollType
    private var lastStartedType = 0

    private var offsetAnimator: ValueAnimator? = null

    override fun onStartNestedScroll(
        parent: CoordinatorLayout,
        child: AppBarLayout,
        directTargetChild: View,
        target: View,
        nestedScrollAxes: Int,
        type: Int
    ): Boolean {
        lastStartedType = type
        return super.onStartNestedScroll(parent, child, directTargetChild, target, nestedScrollAxes, type)
    }

    override fun onStopNestedScroll(coordinatorLayout: CoordinatorLayout, abl: AppBarLayout, target: View, type: Int) {
        val collapsingToolbar = abl.children.filterIsInstance<CollapsingToolbarLayout>().singleOrNull()
        val snapEnabled = (collapsingToolbar?.layoutParams as AppBarLayout.LayoutParams?)?.scrollFlags
            ?.hasFlag(AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP)
            ?: false
        if (snapEnabled && (lastStartedType == ViewCompat.TYPE_TOUCH || type == ViewCompat.TYPE_NON_TOUCH)) {
            val absOffset = abs(topAndBottomOffset)
            val mediateStateOffset = (1f - APP_BAR_MEDIATE_SNAP_OFFSET_POSITION) * abl.totalScrollRange
            collapsingToolbar?.let {
                it.minimumHeight =
                    if (absOffset > mediateStateOffset / 2f &&
                        absOffset < (abl.totalScrollRange + mediateStateOffset) / 2f
                    ) {
                        it.defaultMinimumHeight + abl.totalScrollRange - mediateStateOffset.roundToInt()
                    } else {
                        it.defaultMinimumHeight
                    }
            }
        }
        super.onStopNestedScroll(coordinatorLayout, abl, target, type)
    }

    private fun getChildIndexOnOffset(abl: AppBarLayout, offset: Int): Int? {
        var i = 0
        val count: Int = abl.childCount
        while (i < count) {
            val child = abl.getChildAt(i)
            val top = child.top
            val bottom = child.bottom
            if (-offset in top..bottom) {
                return i
            }
            i++
        }
        return null
    }
}