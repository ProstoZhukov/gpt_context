package ru.tensor.sbis.design.selection.ui.utils

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * Вспомогательная подписка для сохраниня положения списка при вставках при выборе/отмене выбора элементов
 *
 * TODO: 1/27/2021 удалить после решения https://online.sbis.ru/opendoc.html?guid=9e5ca78a-1ab1-4710-ac59-09753dbaf7af
 *
 * @author ma.kolpakov
 */
internal class SelectionListScroller(
    private val list: RecyclerView
) : RecyclerView.AdapterDataObserver() {

    private val layoutManager = checkNotNull(list.layoutManager as? LinearLayoutManager) {
        "Unsupported layout manager ${list.layoutManager}"
    }

    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
        if (positionStart == 0 && layoutManager.itemCount != itemCount) {
            list.scrollToPosition(0)
        }
    }

    override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
        if (fromPosition == layoutManager.findFirstVisibleItemPosition()) {
            list.scrollToPosition(fromPosition)
        }
    }
}