package ru.tensor.sbis.list.view.decorator

import android.content.res.Resources
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import ru.tensor.sbis.design.R
import ru.tensor.sbis.list.view.utils.layout_manager.SbisGridLayoutManager

/**
 * Декоратор для реализации отступа снизу для элемента/ов расположенных на последней строке в GridLayoutManager.
 * Особенность GridLayoutManager является то, что он выставляет одинаковую высоту для всех элементов в строке, поэтому
 * если изменить отступ через getItemOffsets размер одной ячейки, то он изменит РАЗМЕР остальных ячеек.
 *
 * @property layoutManager SectionsHolder @SelDocumented.
 */
internal class LastItemBottomPaddingDecoration(
    private val layoutManager: SbisGridLayoutManager,
    resources: Resources,
    private val navigationHeight: Int = resources.getDimensionPixelSize(R.dimen.tab_navigation_menu_horizontal_height),
    private val fabHeight: Int = resources.getDimensionPixelSize(R.dimen.floating_panel_height)
) : RecyclerView.ItemDecoration() {

    var hasNav: Boolean = false
    var hasFab: Boolean = false

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val position = parent.getChildAdapterPosition(view)
            .let { if (it == NO_POSITION) return else it }

        var padding = 0
        if (hasFab) padding += fabHeight
        if (hasNav) padding += navigationHeight

        if (layoutManager.isInLastGroup(position)) {
            outRect.bottom = padding
        }
    }
}
