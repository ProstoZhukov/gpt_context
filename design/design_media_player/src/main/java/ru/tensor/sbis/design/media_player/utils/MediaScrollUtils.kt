package ru.tensor.sbis.design.media_player.utils

import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.recyclerview.widget.RecyclerView

/**
 * Скролл списка относительно видимой позиции View,
 * где скролл происходит только при выходе view за нижние видимые границы контейнера,
 * но не выше верхней видимой границы контейнера.
 *
 * @author vv.chekurda
 */
fun ViewGroup.scrollByViewPosition(child: View, dy: Int) {
    child.doOnPreDraw {
        val rect = getRectDescendantParent()
        val childRect = child.getRectDescendantParent(this)
        val top = rect.top
        val bottom = rect.bottom - paddingBottom
        val childTop = childRect.top
        val maxScrollDy = (childRect.bottom - bottom)
            .coerceAtLeast(0)
        val availableDyByTop = if (this is RecyclerView) {
            childTop - top
        } else {
            childTop - scrollY
        }
        val availableDy = availableDyByTop
            .coerceAtMost(maxScrollDy)
            .coerceAtLeast(0)
        val scrollDy = when {
            dy >= 0 -> dy.coerceAtMost(availableDy)
            availableDyByTop > 0 -> 0
            else -> dy.coerceAtLeast(availableDyByTop)
        }

        scrollBy(0, scrollDy)
    }
}

/**
 * Получить высоту [view], которая скрыта за пределами видимых границ контейнера.
 */
fun ViewGroup.getPartiallyInvisibleHeight(view: View): Int {
    val rect = getRectDescendantParent()
    val viewRect = view.getRectDescendantParent(this)
    val additionalScrollY = if (this is RecyclerView) 0 else - scrollY
    val viewTop = viewRect.top + additionalScrollY
    val viewBottom = viewRect.bottom + additionalScrollY
    val topPartially = (viewTop).coerceAtMost(0)
    val bottomPartially = (viewBottom - (rect.bottom - paddingBottom)).coerceAtLeast(0)
    return if (bottomPartially != 0) {
        bottomPartially
    } else {
        topPartially
    }
}

private fun View.getRectDescendantParent(targetParent: ViewGroup? = parent as? ViewGroup): Rect {
    val anchorRect = Rect()
    getDrawingRect(anchorRect)
    targetParent?.offsetDescendantRectToMyCoords(this, anchorRect)
    return anchorRect
}