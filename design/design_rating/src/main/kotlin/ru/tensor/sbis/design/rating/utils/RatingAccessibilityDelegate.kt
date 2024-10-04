package ru.tensor.sbis.design.rating.utils

import android.graphics.Rect
import android.os.Bundle
import android.os.SystemClock
import android.view.MotionEvent
import android.view.View
import androidx.annotation.IdRes
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import androidx.customview.widget.ExploreByTouchHelper
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.rating.SbisRatingView
import kotlin.math.roundToInt

/**
 * Делегат для работы accessibility у [SbisRatingView].
 *
 * @param host view, для которой нужна поддержка accessibility.
 * @property layoutSet набор [TextLayout], который нужно включить для accessibility.
 *
 * @author ps.smirnyh
 */
internal class RatingAccessibilityDelegate(
    private val host: View,
    var layoutSet: Set<TextLayout> = emptySet()
) : ExploreByTouchHelper(host) {

    private val bounds = Rect()

    override fun getVirtualViewAt(x: Float, y: Float): Int {
        layoutSet.forEachIndexed { index, textLayout ->
            bounds.set(textLayout.left, textLayout.top, textLayout.right, textLayout.bottom)
            if (bounds.contains(x.roundToInt(), y.roundToInt())) {
                return index
            }
        }
        return HOST_ID
    }

    override fun getVisibleVirtualViews(virtualViewIds: MutableList<Int>) {
        layoutSet.indices.forEach { virtualViewIds.add(it) }
    }

    @Suppress("DEPRECATION")
    override fun onPopulateNodeForVirtualView(
        virtualViewId: Int,
        node: AccessibilityNodeInfoCompat
    ) {
        if (virtualViewId !in layoutSet.indices) return
        val textLayout = layoutSet.elementAtOrNull(virtualViewId) ?: return
        node.className = textLayout::class.simpleName
        node.viewIdResourceName = getResourceName(textLayout.id)
        node.isClickable = true
        node.addAction(AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_CLICK)
        bounds.set(textLayout.left, textLayout.top, textLayout.right, textLayout.bottom)
        if (!textLayout.isVisible) {
            setInvisibleElement(node)
        }
        node.text = textLayout.text
        node.setBoundsInParent(bounds)
    }

    override fun onPerformActionForVirtualView(
        virtualViewId: Int,
        action: Int,
        arguments: Bundle?
    ) = if (action == AccessibilityNodeInfoCompat.ACTION_CLICK) {
        layoutSet.elementAtOrNull(virtualViewId)?.performClick() ?: false
    } else {
        false
    }

    private fun getResourceName(@IdRes id: Int) = host.resources.getResourceName(id)

    private fun setInvisibleElement(node: AccessibilityNodeInfoCompat) {
        bounds.setEmpty()
        node.setBoundsInScreen(bounds)
        node.isVisibleToUser = false
    }

    private fun TextLayout.performClick(): Boolean {
        host.dispatchTouchEvent(
            getMotionEvent(
                MotionEvent.ACTION_DOWN,
                left.toFloat(),
                top.toFloat()
            )
        )
        return host.dispatchTouchEvent(
            getMotionEvent(
                MotionEvent.ACTION_UP,
                left.toFloat(),
                top.toFloat()
            )
        )
    }

    private fun getMotionEvent(action: Int, x: Float, y: Float) =
        MotionEvent.obtain(
            SystemClock.uptimeMillis(),
            SystemClock.uptimeMillis(),
            action,
            x,
            y,
            0
        )
}