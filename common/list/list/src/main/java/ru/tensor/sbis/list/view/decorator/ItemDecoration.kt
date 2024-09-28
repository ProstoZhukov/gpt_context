package ru.tensor.sbis.list.view.decorator

import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * Интрфейч для пользовательской декорации элементов списка.
 *
 * @author ma.kolpakov
 */
interface ItemDecoration {

    /**
     * Устанавливает смещение для декорируемой ячейки.
     * @see RecyclerView.ItemDecoration.getItemOffsets
     */
    fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) = Unit

    /**
     * Отрисовка декорации перед отрисовкой самой ячейки.
     * @see RecyclerView.ItemDecoration.onDraw
     */
    fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State, view: View) = Unit

    /**
     * Отрисовка декорации после отрисовкой самой ячейки.
     * @see RecyclerView.ItemDecoration.onDrawOver
     */
    fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State, view: View) = Unit

}