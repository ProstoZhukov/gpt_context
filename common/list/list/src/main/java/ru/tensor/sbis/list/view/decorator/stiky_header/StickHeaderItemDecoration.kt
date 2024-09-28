package ru.tensor.sbis.list.view.decorator.stiky_header

import android.graphics.Canvas
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import ru.tensor.sbis.list.view.ListDataHolder
import ru.tensor.sbis.list.view.background.ColorProvider
import ru.tensor.sbis.list.view.decorator.configureItemBackground
import ru.tensor.sbis.list.view.decorator.configureItemPadding

/**
 * Декоратор для реализации стики-заголовка.
 * @property stickyHeaderInterface StickyHeaderInterface
 */
internal class StickHeaderItemDecoration(
    private val stickyHeaderInterface: StickyHeaderInterface,
    private val colorProvider: ColorProvider,
    private val sectionsHolder: ListDataHolder
) : RecyclerView.ItemDecoration() {

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
        val topChild = parent.getChildAt(0) ?: return

        val topChildPosition = parent.getChildAdapterPosition(topChild)
            .let { if (it == NO_POSITION) return else it }

        stickyHeaderInterface.runWithHeaderPosition(
            topChildPosition,
            parent
        ) { headerPos, currentHeader ->

            configureItemBackground(currentHeader, headerPos, colorProvider, sectionsHolder)
            configureItemPadding(currentHeader, headerPos, sectionsHolder)

            val height = layoutHeaderOnTopAndGetHeight(parent, currentHeader)
            val childInContact = getChildInContact(
                parent,
                currentHeader.bottom,
                headerPos,
                height
            )

            childInContact?.let {
                val position = parent.getChildAdapterPosition(childInContact)
                    .let { if (it == NO_POSITION) return@runWithHeaderPosition else it }

                if (stickyHeaderInterface.isSticky(position)) {
                    moveHeader(c, currentHeader, childInContact)
                } else drawHeader(c, currentHeader)

                return@runWithHeaderPosition
            }

            drawHeader(c, currentHeader)
        }
    }

    private fun drawHeader(c: Canvas, header: View) {
        c.save()
        c.translate(0f, 0f)
        header.draw(c)
        c.restore()
    }

    private fun moveHeader(c: Canvas, currentHeader: View, nextHeader: View) {
        c.save()
        c.translate(0f, (nextHeader.top - currentHeader.height).toFloat())
        currentHeader.draw(c)
        c.restore()
    }

    private fun getChildInContact(
        parent: RecyclerView,
        contactPoint: Int,
        currentHeaderPos: Int,
        stickyHeaderHeight: Int
    ): View? {
        var childInContact: View? = null
        for (i in 0 until parent.childCount) {
            var heightTolerance = 0
            val child = parent.getChildAt(i)

            //measure height tolerance with child if child is another header
            if (currentHeaderPos != i) {
                val position = parent.getChildAdapterPosition(child)
                if (position == NO_POSITION) continue

                val isChildHeader = stickyHeaderInterface.isSticky(position)
                if (isChildHeader) {
                    heightTolerance = stickyHeaderHeight - child.height
                }
            }

            //add heightTolerance if child top be in display area
            val childBottomPosition = if (child.top > 0) {
                child.bottom + heightTolerance
            } else {
                child.bottom
            }

            if (childBottomPosition > contactPoint) {
                if (child.top <= contactPoint) {
                    // This child overlaps the contactPoint
                    childInContact = child
                    break
                }
            }
        }
        return childInContact
    }

    /**
     * Properly measures and layouts the top sticky header.
     * @param parent ViewGroup: RecyclerView in this case.
     */
    private fun layoutHeaderOnTopAndGetHeight(parent: ViewGroup, view: View): Int {

        // Specs for parent (RecyclerView)
        val widthSpec = View.MeasureSpec.makeMeasureSpec(parent.width, View.MeasureSpec.EXACTLY)
        val heightSpec =
            View.MeasureSpec.makeMeasureSpec(parent.height, View.MeasureSpec.UNSPECIFIED)

        // Specs for children (headers)
        val childWidthSpec = ViewGroup.getChildMeasureSpec(
            widthSpec,
            parent.paddingLeft + parent.paddingRight,
            view.layoutParams.width
        )
        val childHeightSpec = ViewGroup.getChildMeasureSpec(
            heightSpec,
            parent.paddingTop + parent.paddingBottom,
            view.layoutParams.height
        )

        view.measure(childWidthSpec, childHeightSpec)
        val stickyHeaderHeight = view.measuredHeight
        view.layout(0, 0, view.measuredWidth, stickyHeaderHeight)
        return stickyHeaderHeight
    }
}