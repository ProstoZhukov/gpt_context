package ru.tensor.sbis.list.view.utils

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.list.view.calback.ListViewListener

/**
 * Логика необходимости постраничной подгрузки данных по событию скрола списка и текущим показанным позициям элементов.
 * Вызывает запрос на подгрузки следующей страницы, когда осуществляется скрол в направлении конца списка
 * и видим последний элемент.
 * Вызывает запрос на подгрузки предыдущей страницы, когда осуществляется скрол в направлении начало списка
 * и видим первый элемент.
 * @property listViewListener InteractionCallback запрос постраничной подгрузки.
 * @property layoutManager LinearLayoutManager
 * @constructor
 */
internal class NeedLoadMoreNotifier(
    private val listViewListener: ListViewListener,
    private val layoutManager: LinearLayoutManager
) : RecyclerView.OnScrollListener() {

    private var shouldNotifyNext = true
    private var shouldNotifyPrevious = false
    private var lastUpdateTime = 0L

    @Synchronized
    fun shouldNotifyNext(value: Boolean) {
        shouldNotifyNext = value
    }

    @Synchronized
    fun shouldNotifyPrevious(value: Boolean) {
        shouldNotifyPrevious = value
    }

    @Synchronized
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        if (System.currentTimeMillis() - lastUpdateTime < TIME_TO_THROTTLE) return

        if (shouldNotifyNext && (dy > 0 || dx > 0)
            && layoutManager.findLastVisibleItemPosition() >= layoutManager.itemCount - ITEM_TO_END_BEFORE_CALL_MORE
        ) {
            notifyNextAndStopNotifying()
        }

        if (shouldNotifyPrevious && (dy < 0 || dx < 0) &&
            layoutManager.findFirstVisibleItemPosition() < ITEM_TO_END_BEFORE_CALL_MORE) {
            notifyPreviousAndStopNotifying()
        }
    }

    private fun notifyNextAndStopNotifying() {
        listViewListener.loadNext()

        lastUpdateTime = System.currentTimeMillis()
    }

    private fun notifyPreviousAndStopNotifying() {
        listViewListener.loadPrevious()

        lastUpdateTime = System.currentTimeMillis()
    }
}

private const val ITEM_TO_END_BEFORE_CALL_MORE = 10
private const val TIME_TO_THROTTLE = 100
