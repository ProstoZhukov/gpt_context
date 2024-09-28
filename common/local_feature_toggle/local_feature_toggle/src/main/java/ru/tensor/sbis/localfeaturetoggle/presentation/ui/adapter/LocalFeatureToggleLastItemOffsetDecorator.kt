package ru.tensor.sbis.localfeaturetoggle.presentation.ui.adapter

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.R as RDesign

/**
 * Декоратор для отступа последнего элемента списка, чтобы он не перекрывался ННП.
 *
 * @author ps.smirnyh
 */
internal class LocalFeatureToggleLastItemOffsetDecorator : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val viewPosition = parent.getChildLayoutPosition(view).takeIf { it > RecyclerView.NO_POSITION } ?: return
        if (viewPosition != state.itemCount - 1) {
            return
        }
        outRect.bottom = parent.resources.getDimensionPixelOffset(RDesign.dimen.bottom_navigation_height)
    }
}