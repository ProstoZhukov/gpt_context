package ru.tensor.sbis.list.view.decorator

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import ru.tensor.sbis.design.theme.global_variables.BorderThickness
import ru.tensor.sbis.list.view.ListDataHolder
import ru.tensor.sbis.list.view.background.ColorProvider
import kotlin.math.roundToInt

/**
 * Декоратор для реализации разделителя между ячейками посредством добавления отступа и отрисовки горизонтальной линии.
 *
 * @property sectionsHolder SectionsHold @SelDocumented
 */
internal class DividerItemDecoration(
    private val sectionsHolder: ListDataHolder,
    context: Context,
    colorProvider: ColorProvider
) : RecyclerView.ItemDecoration() {

    private val divider = ColorDrawable(colorProvider.separatorColor)
    private val dividerSize = BorderThickness.S.getDimenPx(context)

    override fun onDraw(
        canvas: Canvas,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val childCount = parent.childCount

        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val position = parent.getChildAdapterPosition(child)

            if (position == NO_POSITION || !needDrawDivider(position)) continue

            val params = child.layoutParams as RecyclerView.LayoutParams
            val top = child.bottom + params.bottomMargin + child.translationY.roundToInt()
            val bottom = top + dividerSize
            divider.setBounds(child.left, top, child.right, bottom)
            divider.alpha = child.alpha.toInt() * 255
            divider.draw(canvas)
        }
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
            .let { if (it == NO_POSITION) return else it }

        if (needDrawDivider(position)) {
            outRect.set(0, 0, 0, dividerSize)
        } else {
            outRect.setEmpty()
        }
    }

    private fun needDrawDivider(position: Int) = sectionsHolder.hasDividers(position)
        && !excludeDividerUnderFirst(position)
        && !excludeDividerUpperLast(position)

    private fun excludeDividerUnderFirst(position: Int) =
        sectionsHolder.isFirstInSection(position) && !sectionsHolder.needDrawDividerUnderFirst(position)

    private fun excludeDividerUpperLast(position: Int) =
        sectionsHolder.isLastItemInSection(position + 1)
            && !sectionsHolder.needDrawDividerUpperLast(position + 1)
}