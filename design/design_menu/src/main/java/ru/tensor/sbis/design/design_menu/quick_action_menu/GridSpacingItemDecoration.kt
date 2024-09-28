package ru.tensor.sbis.design.design_menu.quick_action_menu

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * Декоратор для установки вертикальных и горизонтальных отступов между элементами списка.
 *  @param spanCount количество столбцов в списке
 *  @param spacing отступ между элементами списка
 *
 * @author ra.geraskin
 */

class GridSpacingItemDecoration(private val spanCount: Int, private val spacing: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        val column = position % spanCount
        outRect.right = column * spacing / spanCount
        outRect.left = spacing - (column + 1) * spacing / spanCount
        if (position >= spanCount) {
            outRect.top = spacing
        }
    }

}
