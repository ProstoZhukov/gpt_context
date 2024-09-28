package ru.tensor.sbis.base_components.adapter.sectioned.visibility.dispatcher

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * Реализация диспетчера для отслеживания диапазона полностью видимых элементов в [RecyclerView].
 *
 * @author am.boldinov
 */
@Suppress("unused")
class CompletelyVisibleRangeDispatcher(
        recyclerView: RecyclerView? = null
) : AbstractVisibleRangeDispatcher(recyclerView) {

    override fun getFirstVisible(layoutManager: RecyclerView.LayoutManager): Int {
        return when (layoutManager) {
            is LinearLayoutManager -> layoutManager.findFirstCompletelyVisibleItemPosition()
            else                                                -> throw NotImplementedError("Case for ${layoutManager.javaClass.name} not implemented.")
        }
    }

    override fun getLastVisible(layoutManager: RecyclerView.LayoutManager): Int {
        return when (layoutManager) {
            is LinearLayoutManager -> layoutManager.findLastCompletelyVisibleItemPosition()
            else                                                -> throw NotImplementedError("Case for ${layoutManager.javaClass.name} not implemented.")
        }
    }

}