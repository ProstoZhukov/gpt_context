package ru.tensor.sbis.business.common.ui.base

import androidx.annotation.VisibleForTesting
import androidx.annotation.VisibleForTesting.Companion.PRIVATE
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.PublishSubject
import ru.tensor.sbis.mvvm.utils.retain.LifecycleAttendant
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.TimeUnit

/**
 * Класс для обработки пагинации и получения контента реестров частями
 * - уведомляет о событии пагинации
 * - уведомляет о событии скролла
 *
 * @property receiverList список получателей событий пагинации
 * @property pagingChannel канал уведомлений о пагинации
 * @property scrollChannel канал уведомлений о скроле
 * @property isAssignedScrollListening состояние подписки на скролл [RecyclerView]
 * @property isDeactivatedPaging true если обработка пагинации деактивирована
 *
 * @author as.chadov
 */
class PagingScrollHelper private constructor() : LifecycleObserver {

    /** Конструктор для retain LifecycleOwner */
    constructor(wrapper: LifecycleAttendant<out ScrollInitiator>) : this() {
        wrapper.bind(this@PagingScrollHelper::register)
    }

    /** Конструктор для non-retain LifecycleOwner */
    constructor(scrollInitiator: ScrollInitiator) : this() {
        register(scrollInitiator)
    }

    private fun register(scrollInitiator: ScrollInitiator) {
        scrollInitiator.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                provideInitiator = scrollInitiator.provideInitiator()
                whiteList = scrollInitiator.provideWhiteList()
                val areAnyConsumer = hasPagingConsumer || hasScrollConsumer
                if (areAnyConsumer && isAssignedScrollListening.not()) {
                    addScrollListener()
                }
            }

            override fun onPause(owner: LifecycleOwner) {
                provideInitiator?.invoke()?.removeOnScrollListener(scrollListener)
                provideInitiator = null
                isAssignedScrollListening = false
            }

            override fun onDestroy(owner: LifecycleOwner) {
                scrollInitiator.lifecycle.removeObserver(this)
            }
        })
    }

    /**
     * Предоставляет [Observable] события пагинации, достижения порогового значения для получения следующего разворота
     *
     * @param receiverId идентификатор получателя событий
     *
     * @return [Observable] событие пагинации
     */
    fun observePaging(receiverId: String = DEFAULT_RECEIVER): Observable<Unit> =
        observePagingChannel(receiverId)
            .observeOn(AndroidSchedulers.mainThread())
            .filter { isActiveReceiver(receiverId) }
            .doOnNext { block(receiverId) }

    /**
     * Предоставляет [Observable] события скролла
     *
     * @return [Observable] событие скролла
     */
    fun observeScroll(): Observable<ScrollConsumerEvent> = observeScrollChannel()

    /**
     * Блокировать дальнейшую обработку скролла на списке, получение событий пагинации
     *
     * @param receiverId идентификатор получателя событий пагинации
     */
    fun block(receiverId: String = DEFAULT_RECEIVER) {
        getReceiver(receiverId)?.processScroll = false
    }

    /**
     * Деактивировать обработку пагинации [PagingScrollHelper]
     * Например если новые развороты не должны быть получены
     */
    fun deactivate(): PagingScrollHelper {
        isDeactivatedPaging = true
        return this
    }

    /**
     * Активировать обработку пагинации [PagingScrollHelper]
     * только для списка, предоставляемого [ScrollInitiator.provideWhiteList]
     */
    fun activateByWhiteList(): PagingScrollHelper {
        hasWhiteList = true
        return this
    }

    /**
     * Деблокировать дальнейшую обработку скролла на списке, отправка событий пагинации для [receiverId]
     *
     * @param receiverId идентификатор получателя событий пагинации
     */
    fun relieve(receiverId: String = DEFAULT_RECEIVER) {
        getReceiver(receiverId)?.processScroll = true
    }

    fun setThreshold(threshold: Int) {
        scrollingThreshold = threshold
    }

    private fun observePagingChannel(receiverId: String): Observable<Unit> =
        pagingChannel
            .doOnSubscribe { processSubscription(receiverId) }
            .doOnDispose { processUnsubscription(receiverId) }
            .throttleLatest(MIN_PAGING_TIMEOUT, TimeUnit.MILLISECONDS)

    private fun observeScrollChannel(): Observable<ScrollConsumerEvent> = scrollChannel

    private fun processSubscription(receiverId: String) {
        if (isAssignedScrollListening.not()) {
            addScrollListener()
        }
        if (getReceiver(receiverId) == null) {
            receiverList.add(PagingConsumer(receiverId))
        }
    }

    private fun processUnsubscription(receiverId: String) {
        getReceiver(receiverId)?.let(receiverList::remove)
    }

    private fun addScrollListener() {
        provideInitiator?.invoke()?.apply {
            addOnScrollListener(scrollListener)
            isAssignedScrollListening = true
        }
    }

    private val hasAnyActiveReceiver: Boolean
        get() = receiverList.any(PagingConsumer::processScroll)

    private fun isActiveReceiver(receiverId: String): Boolean = if (hasWhiteList) {
        getReceiver(receiverId)?.processScroll == true && whiteList.contains(receiverId)
    } else {
        getReceiver(receiverId)?.processScroll == true
    }

    private fun getReceiver(receiverId: String): PagingConsumer? =
        receiverList.find { receiverId == it.id }

    private val hasPagingConsumer: Boolean
        get() = pagingChannel.hasObservers()

    private val hasScrollConsumer: Boolean
        get() = scrollChannel.hasObservers()

    /**
     *  @param processScroll состояние обработки скролла на списке
     */
    @VisibleForTesting(otherwise = PRIVATE)
    inner class PagingConsumer(
        val id: String,
        var processScroll: Boolean = true,
    )

    data class ScrollConsumerEvent(
        val computedVerticalOffset: Int,
        val scrollDown: Boolean,
        val scrollUp: Boolean,
    )

    private inner class ScrollListener : RecyclerView.OnScrollListener() {

        override fun onScrolled(
            recyclerView: RecyclerView,
            dx: Int,
            dy: Int,
        ) {
            super.onScrolled(recyclerView, dx, dy)
            processPaging(recyclerView, dy)
            processScroll(recyclerView, dy)
        }

        private fun processPaging(recyclerView: RecyclerView, dy: Int) {
            if (isDeactivatedPaging) {
                return
            }
            if (dy > 0 && hasAnyActiveReceiver && isNeedLoadNextPage(recyclerView)) {
                pagingChannel.onNext(Unit)
            }
        }

        private fun processScroll(recyclerView: RecyclerView, dy: Int) {
            if (hasScrollConsumer.not()) {
                return
            }
            val computedVerticalOffset = recyclerView.computeVerticalScrollOffset()
            val isScrollUp = dy > TRASH_SCROLL_THRESHOLD
            val isScrollDown = dy < -TRASH_SCROLL_THRESHOLD
            val event = ScrollConsumerEvent(computedVerticalOffset, isScrollUp, isScrollDown)
            scrollChannel.onNext(event)
        }

        private fun isNeedLoadNextPage(recyclerView: RecyclerView): Boolean {
            val (lastPosition, totalCount) = (recyclerView.layoutManager as LinearLayoutManager).let {
                Pair(it.findLastVisibleItemPosition(), it.itemCount)
            }
            return lastPosition + scrollingThreshold >= totalCount
        }
    }

    interface ScrollInitiator : LifecycleOwner {
        fun provideInitiator(): () -> RecyclerView?

        /**
         * Предоставляет "белый" список id вьюмоделей, для которых нужна пагинация
         */
        fun provideWhiteList(): List<String> = listOf()
    }

    private var whiteList: List<String> = listOf()
    private var isDeactivatedPaging = false
    private var hasWhiteList = false
    private val scrollListener: ScrollListener by lazy { ScrollListener() }
    private var scrollingThreshold: Int = DEFAULT_THRESHOLD

    @VisibleForTesting(otherwise = PRIVATE)
    val pagingChannel = PublishSubject.create<Unit>()

    @VisibleForTesting(otherwise = PRIVATE)
    val scrollChannel = PublishSubject.create<ScrollConsumerEvent>()

    @VisibleForTesting(otherwise = PRIVATE)
    val receiverList: MutableList<PagingConsumer> = CopyOnWriteArrayList()

    @VisibleForTesting(otherwise = PRIVATE)
    var isAssignedScrollListening: Boolean = false

    @VisibleForTesting(otherwise = PRIVATE)
    var provideInitiator: (() -> RecyclerView?)? = null

    private companion object {
        const val MIN_PAGING_TIMEOUT: Long = 500
        const val DEFAULT_THRESHOLD: Int = 10
        const val DEFAULT_RECEIVER: String = "DEFAULT_RECEIVER"
        const val TRASH_SCROLL_THRESHOLD = 1
    }
}