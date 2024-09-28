package ru.tensor.sbis.list.view.utils

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.list.view.calback.ListViewListener

/**
 * Логика добавления в [LinearLayoutManager] слушателя о необходимости запроса постраничной подгрузки данных.
 * @property layoutManager LinearLayoutManager
 * @property createNeedLoadMoreNotifier Function1<InteractionCallback, NeedLoadMoreNotifier>
 * @property needLoadMoreNotifier NeedLoadMoreNotifier?
 * @constructor
 */
internal class NeedLoadMoreNotifierCallbackHandler(
    private val layoutManager: LinearLayoutManager,
    private val createNeedLoadMoreNotifier: (ListViewListener) -> NeedLoadMoreNotifier = {
        NeedLoadMoreNotifier(
            it,
            layoutManager
        )
    }
) {
    private var needLoadMoreNotifier: NeedLoadMoreNotifier? = null

    /**
     * Добавить слушателя о необходимости запроса постраничной подгрузки данных. При этом предыдущий слушатель,
     * добавленный с использованием этого метода, будет удален.
     * @param recyclerView RecyclerView
     * @param listViewListener InteractionCallback
     */
    fun handle(recyclerView: RecyclerView, listViewListener: ListViewListener) {
        val newNeedLoadMoreNotifier = createNeedLoadMoreNotifier(listViewListener)
        needLoadMoreNotifier?.apply { recyclerView.removeOnScrollListener(this) }
        needLoadMoreNotifier = newNeedLoadMoreNotifier
        recyclerView.addOnScrollListener(newNeedLoadMoreNotifier)
    }

    fun shouldNotifyNext(value: Boolean) {
        needLoadMoreNotifier?.shouldNotifyNext(value)
    }

    fun shouldNotifyPrevious(value: Boolean) {
        needLoadMoreNotifier?.shouldNotifyPrevious(value)
    }
}