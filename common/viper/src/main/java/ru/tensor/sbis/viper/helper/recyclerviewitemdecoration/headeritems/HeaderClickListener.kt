package ru.tensor.sbis.viper.helper.recyclerviewitemdecoration.headeritems

import android.graphics.Rect
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.utils.extentions.getMargins

/**
 * SimpleOnItemTouchListener для обработки кликов по stickyHeader
 */
class HeaderClickListener(
    private val headerItemDecoration: HeaderItemDecoration,
    private val clickEventHook: HeaderClickEventHook
) :
    RecyclerView.SimpleOnItemTouchListener() {

    override fun onInterceptTouchEvent(recyclerView: RecyclerView, motionEvent: MotionEvent): Boolean {
        val currentHeader = headerItemDecoration.currentHeader ?: return false

        return if (motionEvent.action == MotionEvent.ACTION_DOWN) {
            if (detectViewsClick(motionEvent, currentHeader.second.itemView, currentHeader.first))
                true
            else detectHeaderClick(motionEvent, currentHeader.second.itemView, currentHeader.first)
        } else false
    }

    private fun detectViewsClick(e: MotionEvent, headerView: View, pos: Int): Boolean {
        val headerRect = getHeaderRect(headerView)

        var isViewClicked = false

        for (view in clickEventHook.onBindHeaderViews(headerView)) {
            val viewRect = getViewRect(view, headerRect)

            if (viewRect.contains(e.x.toInt(), e.y.toInt()) && headerRect.contains(viewRect)) {
                clickEventHook.onHeaderViewClick(headerView, view, pos)

                isViewClicked = true

                break
            }
        }

        return isViewClicked
    }

    private fun getViewRect(view: View, headerRect: Rect) =
        Rect(
            view.left, headerRect.top + view.top,
            view.right, headerRect.top + view.bottom
        )

    private fun getHeaderRect(header: View) =
        Rect(
            header.left, header.top + header.getMargins().top,
            header.right, header.bottom + header.getMargins().top
        )

    private fun detectHeaderClick(e: MotionEvent, header: View, pos: Int): Boolean {
        return if (getHeaderRect(header).contains(e.x.toInt(), e.y.toInt())) {
            clickEventHook.onHeaderClick(header, pos)
            true
        } else false
    }
}
