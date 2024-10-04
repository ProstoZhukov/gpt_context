package ru.tensor.sbis.design.selection.ui.utils

import androidx.recyclerview.widget.RecyclerView

/**
 * Выполняет прокрутку списка при добавлении/перемещении элементов наверх, в связи с изменением поискового запроса
 *
 * @author us.bessonov
 */
internal class SingleSelectionListScroller(
    private val list: RecyclerView
) : RecyclerView.AdapterDataObserver() {

    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
        if (positionStart == 0) list.scrollToPosition(0)
    }

    override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
        if (toPosition == 0) list.scrollToPosition(0)
    }
}