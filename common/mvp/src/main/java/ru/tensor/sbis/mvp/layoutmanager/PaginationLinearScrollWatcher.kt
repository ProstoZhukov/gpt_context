package ru.tensor.sbis.mvp.layoutmanager

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.SerialDisposable
import io.reactivex.subjects.PublishSubject
import ru.tensor.sbis.common.rx.consumer.FallbackErrorConsumer
import ru.tensor.sbis.common.util.isCompletelyVisibleItem
import ru.tensor.sbis.common.util.scroll.ScrollEvent
import ru.tensor.sbis.common.util.scroll.ScrollHelper
import java.util.concurrent.TimeUnit

private const val INTERVAL_DURATION = 500L

/**
 * Реализация делегата по обработке событий скролла списка с пагинацией.
 * Код перенесен из класса [PaginationLayoutManager].
 *
 * @author am.boldinov
 */
@Deprecated("Устаревший подход, переходим на mvi_extension")
class PaginationLinearScrollWatcher(
    private val layoutManager: LinearLayoutManager,
    private val scrollHelper: ScrollHelper?,
    private val progressViewType: Int,
    private val emptyViewType: Int,
    private val bottomPaddingViewType: Int,
    private val scrollToTopMediator: ScrollToTopMediator? = null
) : PaginationScrollWatcher {

    private val serialDisposable = SerialDisposable()
    private val triggerSubject = PublishSubject.create<TriggerEvent>()

    private var recyclerView: RecyclerView? = null

    override fun onAttachedToRecyclerView(view: RecyclerView?) {
        recyclerView = view
        recyclerView?.apply {
            onFlingListener = object : RecyclerView.OnFlingListener() {
                override fun onFling(velocityX: Int, velocityY: Int) = false
            }
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    // для случаев инициализации экрана (заход и поворот экрана), а также при показе/скрытии клавиатуры
                    // в этих случаях вызывается onScrolled со значением dy = 0
                    if (dy == 0) {
                        triggerSubject.onNext(TriggerEvent.ON_SCROLLED)
                    }
                }
            })
            serialDisposable.set(
                triggerSubject.throttleLast(INTERVAL_DURATION, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        { recyclerView?.let { processListItemsChangeEvents(it) } },
                        FallbackErrorConsumer()
                    )
            )
        }
    }

    override fun onDetachedFromRecyclerView(view: RecyclerView?) {
        recyclerView = null
        serialDisposable.dispose()
    }

    override fun onItemsChanged() {
        triggerSubject.onNext(TriggerEvent.ON_ITEMS_CHANGED)
    }

    override fun onItemsRemoved(positionStart: Int, itemCount: Int) {
        triggerSubject.onNext(TriggerEvent.ON_ITEMS_REMOVED)
    }

    override fun onItemsUpdated(positionStart: Int, itemCount: Int) {
        triggerSubject.onNext(TriggerEvent.ON_ITEMS_UPDATED)
    }

    override fun onItemsAdded(positionStart: Int, itemCount: Int) {
        triggerSubject.onNext(TriggerEvent.ON_ITEMS_ADDED)
    }

    override fun preventOverScroll(dy: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State): Boolean {
        preventOverscroll(dy)
        val toStart = isScrollDirectionToStart(dy)
        if (toStart) {
            val position = layoutManager.findFirstCompletelyVisibleItemPosition()
            if (position != RecyclerView.NO_POSITION) {
                val firstCompleteItemType = getItemViewType(position)
                if (firstCompleteItemType == progressViewType || firstCompleteItemType == emptyViewType) {
                    // Чтобы случайно не ограничить скролл вверх в случае, когда скролл вверх произошел сразу после
                    // попытки проскроллить список ниже с сопутствующей загрузкой следующей страницы,
                    // а на экране присутствует только один элемент данных, отображенный не целиком,
                    // проверяем, действительно-ли найденный холдер заглушки в списке располагается на верхней границе текущей страницы.
                    val contentLastItemPosition = getContentLastItemPosition(recyclerView?.adapter)
                    // Индекс последнего элемента данных больше, чем индекс видимой заглушки
                    // - заглушка располагается на границе текущей страницы сверху. Предотвращаем дальнейший скролл
                    if (contentLastItemPosition > position) {
                        return true
                    }
                    //в ином случае найденная заглушка расположена на нижней границе страницы и ограничивать скролл вверх нельзя.
                }
            }
        }
        return false
    }

    /**
     * Цель костыля - предотвратить дальнейший скролл контента после того, как мы упёрлись в естественную
     * или искуственную границу, добавили данных в список и вызвали notifyItemRangeInserted/notifyItemRangeChanged.
     * Либо лагает экран из-за переизбытка анимации.
     */
    private fun preventOverscroll(dy: Int) {
        val toStart = isScrollDirectionToStart(dy)
        if (toStart) {
            val firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
            if (firstVisiblePosition != RecyclerView.NO_POSITION) {
                val firstItemType = getItemViewType(firstVisiblePosition)
                if (firstItemType == progressViewType || firstItemType == emptyViewType) {
                    recyclerView?.apply {
                        stopScroll()
                    }
                }
            }
        } else {
            val lastVisiblePosition = layoutManager.findLastVisibleItemPosition()
            if (lastVisiblePosition != RecyclerView.NO_POSITION) {
                val lastItemType = getItemViewType(lastVisiblePosition)
                if (lastItemType == progressViewType) {
                    recyclerView?.apply {
                        stopScroll()
                    }
                }
            }
        }
    }

    private fun processListItemsChangeEvents(recyclerView: RecyclerView) {
        if (scrollHelper == null && scrollToTopMediator == null) {
            return
        }
        // Если на экране поместились все элементы списка, то необходимо отобразить нижнюю навигационную панель
        // https://online.sbis.ru/opendoc.html?guid=b4e7c4bb-8d46-4a5e-adf7-9d7a5f50b5a2
        val itemsCountInList = getContentLastItemPosition(recyclerView.adapter)
        val firstItemPosition = 0
        val lastItemPosition = if (itemsCountInList != 0) itemsCountInList - 1 else 0

        val contentCompletelyOnScreen = itemsCountInList == 0 ||
            recyclerView.isCompletelyVisibleItem(firstItemPosition) &&
            recyclerView.isCompletelyVisibleItem(lastItemPosition)

        if (contentCompletelyOnScreen) {
            scrollHelper?.sendFakeScrollEvent(ScrollEvent.SCROLL_UP_FAKE_SOFT)
        }

        scrollToTopMediator?.let { scrollToTop ->
            if (scrollToTop.isAvailable(recyclerView)) {
                if (contentCompletelyOnScreen) {
                    scrollToTop.disableScrollToTop()
                } else {
                    scrollToTop.enableScrollToTop()
                }
            }
        }
    }

    private fun getItemViewType(position: Int): Int {
        return with(layoutManager) {
            getItemViewType(findViewByPosition(position)!!)
        }
    }

    /**
     * Метод для получения индекса последнего элемента в адаптере.
     * Поскольку на многих экранах используется пустая ячейка с высотой
     * нижней панели навигации (чтобы избежать некоторых проблем), а также
     * последней ячейкой может быть прогресс при пагинации, то мы
     * учитываем это вычитаем эту ячейку из расчетов видимости контента.
     */
    private fun getContentLastItemPosition(adapter: RecyclerView.Adapter<*>?): Int {
        return when {
            adapter == null -> 0
            adapter.itemCount == 0 -> 0
            (adapter.getItemViewType(adapter.itemCount - 1) == bottomPaddingViewType ||
                adapter.getItemViewType(adapter.itemCount - 1) == progressViewType) -> adapter.itemCount - 1

            else -> adapter.itemCount
        }
    }

    private fun isScrollDirectionToStart(dy: Int): Boolean {
        return if (layoutManager.reverseLayout) dy > 0 else dy < 0
    }

    private enum class TriggerEvent {
        ON_ITEMS_CHANGED,
        ON_ITEMS_REMOVED,
        ON_ITEMS_UPDATED,
        ON_ITEMS_ADDED,
        ON_SCROLLED
    }
}