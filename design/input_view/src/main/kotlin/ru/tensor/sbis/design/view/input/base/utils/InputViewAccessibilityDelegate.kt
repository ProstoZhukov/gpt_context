package ru.tensor.sbis.design.view.input.base.utils

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.core.view.ViewCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import androidx.core.view.isVisible
import androidx.customview.widget.ExploreByTouchHelper
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.view.input.base.BaseInputView
import kotlin.math.roundToInt

/**
 * Делегат для работы accessibility у [BaseInputView].
 *
 * @param host любой класс полей ввода.
 * @property inputView view для ввода текста.
 * @property layoutSet набор [TextLayout], который нужно включить для accessibility.
 *
 * @author ps.smirnyh
 */
internal class InputViewAccessibilityDelegate(
    host: BaseInputView,
    private val inputView: View,
    private val layoutSet: Set<TextLayout>
) : ExploreByTouchHelper(host) {

    constructor(host: BaseInputView, inputView: View, vararg textLayouts: TextLayout) : this(
        host,
        inputView,
        textLayouts.toSet()
    )

    private val bounds = Rect()
    private val resources = host.resources

    @IdRes
    private val hostId = host.id
    private val hostClassName = host::class.qualifiedName

    override fun onPopulateNodeForHost(node: AccessibilityNodeInfoCompat) {
        super.onPopulateNodeForHost(node)
        node.className = hostClassName
    }

    override fun getVirtualViewAt(x: Float, y: Float): Int {
        bounds.set(inputView.left, inputView.top, inputView.right, inputView.bottom)
        if (bounds.contains(x.roundToInt(), y.roundToInt())) {
            return 0
        }
        layoutSet.forEachIndexed { index, textLayout ->
            bounds.set(textLayout.left, textLayout.top, textLayout.right, textLayout.bottom)
            if (bounds.contains(x.roundToInt(), y.roundToInt())) {
                return index + 1
            }
        }
        return HOST_ID
    }

    override fun getVisibleVirtualViews(virtualViewIds: MutableList<Int>) {
        virtualViewIds.add(0)
        layoutSet.indices.forEach { virtualViewIds.add(it + 1) }
    }

    override fun onPopulateNodeForVirtualView(
        virtualViewId: Int,
        node: AccessibilityNodeInfoCompat
    ) {
        if (virtualViewId !in 0..layoutSet.size) return
        if (virtualViewId == 0) {
            bounds.set(inputView.left, inputView.top, inputView.right, inputView.bottom)
            ViewCompat.onInitializeAccessibilityNodeInfo(inputView, node)
            // Удаляем actions, ими управляет родительский класс. Иначе будет exception
            node.removeAction(AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_ACCESSIBILITY_FOCUS)
            node.removeAction(AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_CLEAR_ACCESSIBILITY_FOCUS)
            if (!inputView.isVisible) {
                setInvisibleElement(node)
            }
            if (node.text.isNullOrEmpty()) node.text = ""
            node.className = inputView::class.qualifiedName
            if (hostId != View.NO_ID) {
                node.contentDescription = "${getResourceName(inputView.id)}_${getResourceEntryName(hostId)}"
            }
        }
        if (virtualViewId > 0) {
            val textLayout = layoutSet.elementAtOrNull(virtualViewId - 1) ?: return
            node.className = textLayout::class.qualifiedName
            node.viewIdResourceName = getResourceName(textLayout.id)
            bounds.set(textLayout.left, textLayout.top, textLayout.right, textLayout.bottom)
            if (!textLayout.isVisible) {
                setInvisibleElement(node)
            }
            node.text = textLayout.text
        }
        node.setBoundsInParent(bounds)
    }

    override fun onPerformActionForVirtualView(
        virtualViewId: Int,
        action: Int,
        arguments: Bundle?
    ) =
        if (virtualViewId == 0) {
            inputView.performAccessibilityAction(action, arguments)
        } else {
            false
        }

    private fun getResourceName(@IdRes id: Int) = resources.getResourceName(id)

    private fun getResourceEntryName(@IdRes id: Int) = resources.getResourceEntryName(id)

    private fun setInvisibleElement(node: AccessibilityNodeInfoCompat) {
        bounds.setEmpty()
        node.setBoundsInScreen(bounds)
        node.isVisibleToUser = false
    }
}