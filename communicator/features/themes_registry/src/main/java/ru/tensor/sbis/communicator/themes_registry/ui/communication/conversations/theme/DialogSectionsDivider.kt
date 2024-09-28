package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.theme

import android.graphics.Canvas
import android.graphics.Rect
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.tensor.sbis.communicator.common.util.castTo
import ru.tensor.sbis.communicator.themes_registry.R
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.ConversationListAdapter

/**
 * Итем декоратор для разделения секции контактов и диалогов в реестре диалогов -
 * надпись "Диалоги" плюс серый разделитель (см. [segmentDividerResource])
 */
internal class DialogSectionsDivider : RecyclerView.ItemDecoration() {

    private val segmentDividerResource: Int = R.layout.communicator_dialogs_section_header

    private var segmentDividerView: View? = null

    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val lastContactPosition = lastContactPosition(parent)
        if (lastContactPosition < 0) return

        var lastContactView: View? = null
        for (i in 0 until parent.childCount) {
            val view = parent.getChildAt(i)
            val position = parent.getChildAdapterPosition(view)
            if (position == lastContactPosition) {
                lastContactView = view
                break
            }
        }
        lastContactView ?: return

        createSegmentDividerViewIfNeeded(parent)

        canvas.save()
        val translateY = lastContactView.bottom.toFloat()
        canvas.translate(0f, translateY)
        segmentDividerView!!.draw(canvas)
        canvas.restore()
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        val lastContactPosition = lastContactPosition(parent)

        if (position == lastContactPosition) {
            createSegmentDividerViewIfNeeded(parent)
            outRect.set(0, 0, 0, segmentDividerView!!.height)
        } else {
            outRect.setEmpty()
        }
    }

    private fun fixLayoutSize(view: View, parent: ViewGroup) {
        val widthSpec = View.MeasureSpec.makeMeasureSpec(parent.width, View.MeasureSpec.EXACTLY)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(parent.height, View.MeasureSpec.UNSPECIFIED)

        val childWidth =
            ViewGroup.getChildMeasureSpec(widthSpec, parent.paddingLeft + parent.paddingRight, view.layoutParams.width)
        val childHeight = ViewGroup.getChildMeasureSpec(
            heightSpec,
            parent.paddingTop + parent.paddingBottom,
            view.layoutParams.height
        )

        view.measure(childWidth, childHeight)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
    }

    private fun lastContactPosition(recyclerView: RecyclerView): Int {
        val themeAdapter = recyclerView.adapter?.castTo<ConversationListAdapter>() ?: return -1
        return themeAdapter.lastContactPositionBeforeDialogs()
    }

    private fun createSegmentDividerViewIfNeeded(parent: RecyclerView) {
        if (segmentDividerView == null) {
            segmentDividerView = LayoutInflater.from(parent.context).inflate(segmentDividerResource, parent, false)
            fixLayoutSize(segmentDividerView!!, parent)
        }
    }
}