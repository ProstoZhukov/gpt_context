/**
 * Инструменты, используемые в SwipeBackLayout
 *
 * @author us.bessonov
 */
package ru.tensor.sbis.design.swipeback

import android.graphics.Rect
import android.view.View
import android.view.ViewGroup

/**
 * Выполняет поиск [View], в область которого входит указанная точка, и который удовлетворяет заданному критерию
 */
@JvmOverloads
internal fun tryFindViewAt(
    view: View?,
    x: Float,
    y: Float,
    tempRect: Rect = Rect(),
    isDesiredView: (view: View) -> Boolean
): View? {
    if (view == null || view.visibility != View.VISIBLE || !contains(view, x, y, tempRect)) {
        return null
    }

    if (isDesiredView(view)) return view

    if (view !is ViewGroup) return null

    for (i in 0 until view.childCount) {
        val possiblyDesiredView = tryFindViewAt(view.getChildAt(i), x, y, tempRect, isDesiredView)
        possiblyDesiredView?.let { return it }
    }

    return null
}


private fun contains(mView: View, x: Float, y: Float, tempRect: Rect): Boolean {
    mView.getGlobalVisibleRect(tempRect)
    return tempRect.contains(x.toInt(), y.toInt())
}