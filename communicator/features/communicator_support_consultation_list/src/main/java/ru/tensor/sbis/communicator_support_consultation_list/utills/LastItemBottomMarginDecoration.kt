package ru.tensor.sbis.communicator_support_consultation_list.utills

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * Вспомогательный класс для нижнего отсупа последнего элемента списка
 * @param lastItemMargin необходимый отступ
 */
internal class LastItemBottomMarginDecoration(
    private val lastItemMargin: Int
) : RecyclerView.ItemDecoration() {

    private fun isLastItem(parent: RecyclerView, view: View, state: RecyclerView.State): Boolean {
        return parent.getChildAdapterPosition(view) == state.itemCount - 1
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        with(outRect) {
            bottom = if (isLastItem(parent, view, state)) {
                lastItemMargin
            } else {
                0
            }
        }
    }
}