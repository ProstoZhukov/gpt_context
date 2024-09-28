package ru.tensor.sbis.list.view.decorator

import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import ru.tensor.sbis.list.view.adapter.SbisAdapter

/**
 * Внутренний декоратор списка для добавления пользовательских декораций к элементам.
 *
 * @author ma.kolpakov
 */
internal class CustomDecorator : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        val position = parent.getChildAdapterPosition(view)
        if (position == NO_POSITION) return
        val adapter = parent.adapter as? SbisAdapter ?: return
        adapter.getItem(position).itemDecoration?.getItemOffsets(outRect, view, parent, state)
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        draw(parent) { itemDecoration, view ->
            itemDecoration.onDraw(c, parent, state, view)
        }
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
        draw(parent) { itemDecoration, view ->
            itemDecoration.onDrawOver(c, parent, state, view)
        }
    }

    private fun draw(parent: RecyclerView, block: (ItemDecoration, View) -> Unit) {
        val adapter = parent.adapter as? SbisAdapter ?: return
        for (i in 0 until parent.childCount) {
            val view = parent.getChildAt(i)
            val pos = parent.getChildAdapterPosition(view)
            if (pos < 0 || pos >= adapter.itemCount) break
            adapter.getItem(pos).itemDecoration?.let {
                block.invoke(it, view)
            }
        }
    }
}

