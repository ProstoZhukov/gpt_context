package ru.tensor.sbis.viper.helper.recyclerviewitemdecoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * Разделитель элементов списка с GridLayoutManager для двух столбцов
 * без декораторов для крайних элементов слева и справа
 */
class GridSpaceItemDecorationTwoRows(
    private val horizontalSpace: Int,
    private val verticalSpace: Int
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        if (parent.layoutManager is GridLayoutManager) {
            val layoutManager = parent.layoutManager as GridLayoutManager
            val itemPosition = parent.getChildAdapterPosition(view)
            val spanCount = layoutManager.spanCount
            if ((itemPosition + 1) % spanCount != 0) {
                outRect.right = horizontalSpace / 2
            }
            if ((itemPosition) % spanCount != 0) {
                outRect.left = horizontalSpace / 2
            }
            if (itemPosition >= spanCount) {
                outRect.top = verticalSpace
            }
        } else throw IllegalStateException("Invalid LayoutManager")
    }

}