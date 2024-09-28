package ru.tensor.sbis.event_bus

import android.annotation.SuppressLint
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

/**
 * Шина событий с реализацией на корутинах.
 *
 * @author av.krymov
 */
object EventBus {

    @Deprecated("В RxBus шина поставляется отсюда для бесшовного перехода")
    val bus = PublishSubject.create<Any>().toSerialized()

    private val flow = MutableSharedFlow<Any?>()

    /**
     * Поток держателя состояния для шины EventBus.
     */
    val publicFlow = flow.asSharedFlow()

    /**
     * [CoroutineScope] для EventBus, использует [Dispatchers.Default].
     * Рекомендуется его использовать вместо [GlobalScope].
     */
    val scope = object : CoroutineScope {
        override val coroutineContext: CoroutineContext
            get() = Dispatchers.Default
    }

    /**
     * Отправить событие.
     */
    suspend fun post(event: Any) {
        flow.emit(event)
        bus.onNext(event)
    }

    /**
     * Отправить событие только в StateFlow, минуя шину RxBus.
     */
    suspend fun postWithoutRx(event: Any) {
        flow.emit(event)
    }

    /**
     * Подписка на событие.
     */
    @Suppress("UNCHECKED_CAST")
    @SuppressLint("CheckResult")
    suspend inline fun <T> subscribe(type: Class<T>, crossinline onEvent: (T) -> Unit) {
        publicFlow
            .filter {
                type.isInstance(it)
            }.collectLatest { event ->
                if (type.isInstance(event)) {
                    coroutineContext.ensureActive()
                    onEvent(event as T)
                }
            }
    }
}