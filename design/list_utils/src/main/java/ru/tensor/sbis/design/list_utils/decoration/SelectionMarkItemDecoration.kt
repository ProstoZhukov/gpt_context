package ru.tensor.sbis.design.list_utils.decoration

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.AnyThread
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.list_utils.R
import ru.tensor.sbis.design.theme.global_variables.MarkerColor
import kotlin.math.roundToInt

/**
 * Декоратор для реализации выделения нажатого элемента по спецификации
 * http://axure.tensor.ru/MobileStandart8/#p=компоновка_под_планшет&g=1
 * Для выделенного элемента добавляется ораньжевая вертикальная полоска у левого края.
 */
class SelectionMarkItemDecoration(
    context: Context
) : RecyclerView.ItemDecoration() {

    private val drawableMark: Drawable = ColorDrawable(MarkerColor.DEFAULT.getValue(context))
    private val drawableForeground: Drawable =
        ColorDrawable(ContextCompat.getColor(context, R.color.list_selection_mask))
    private val markWidth: Float = context.resources.getDimension(R.dimen.list_utils_selection_mark_width)

    private var positionToMark = NO_POSITION_TO_MARK

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        if (positionToMark == NO_POSITION_TO_MARK) return
        drawSelectionHighlightIfNeeded(parent, c)
        drawMarkIfNeeded(parent, c)
    }

    @AnyThread
    fun setPosition(position: Int) {
        positionToMark = position
    }

    private fun drawMarkIfNeeded(parent: RecyclerView, c: Canvas) {
        val childCount = parent.childCount

        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val position = parent.getChildAdapterPosition(child)
            if (position == RecyclerView.NO_POSITION || position != positionToMark) continue

            if (position > positionToMark) return

            drawMark(child, c)
        }
    }

    private fun drawSelectionHighlightIfNeeded(parent: RecyclerView, c: Canvas) {
        val childCount = parent.childCount

        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val position = parent.getChildAdapterPosition(child)
            if (position == RecyclerView.NO_POSITION) continue

            if (position > positionToMark) return

            if (position != positionToMark) continue

            drawForeground(child, c)
        }
    }

    private fun drawMark(
        child: View,
        c: Canvas
    ) {
        val top = child.top + child.translationY.roundToInt()
        val bottom = child.bottom + child.translationY.roundToInt()
        val right = (child.left + markWidth).toInt()
        drawableMark.setBounds(
            child.left,
            top,
            right,
            bottom
        )
        drawableMark.draw(c)
    }

    @AnyThread
    fun cleanSelection() {
        positionToMark = NO_POSITION_TO_MARK
    }

    private fun drawForeground(
        child: View,
        c: Canvas
    ) {
        val top = child.top + child.translationY.roundToInt()
        val bottom = child.bottom + child.translationY.roundToInt()
        drawableForeground.setBounds(
            child.left,
            top,
            child.right,
            bottom
        )
        drawableForeground.draw(c)
    }
}

private const val NO_POSITION_TO_MARK = Int.MIN_VALUE