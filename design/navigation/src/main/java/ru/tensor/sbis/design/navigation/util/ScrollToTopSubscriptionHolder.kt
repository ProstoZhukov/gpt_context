package ru.tensor.sbis.design.navigation.util

import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Объект хранящий горячий флоу для получения событий о необходимости вызова скрола на экране.
 * Является глобальным, так как прокидывание данных из навигации, потребует изменений множества прикладного кода.
 * Ликов и т.п. быть не должно так как подписчики управляют подпиской/отпиской.
 *
 * @author da.zolotarev
 */
@Deprecated(
    "Вместо ScrollToTopSubscriptionHolder объекта пользоваться " +
        "методом ContentController.activeTabNavViewClicked в аддонах"
)
// TODO Убрать когда все откажутся от ScrollToTopSubscriptionHolder
object ScrollToTopSubscriptionHolder {

    private val internalEvent: MutableSharedFlow<String> = MutableSharedFlow(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    /**
     * Флоу излучающий события нажатия на активный таб
     */
    val event = internalEvent.asSharedFlow()

    @Suppress("unused")
    @JvmOverloads
    fun getEventLiveData(context: CoroutineContext = EmptyCoroutineContext) = event.asLiveData(context)

    /**
     * Публикация события в флоу
     *
     * [value] - Уникальный идентификатор элемента ННП.
     */
    @Suppress("unused")
    suspend fun emitEvent(value: String) = internalEvent.emit(value)
}

/**
 * Скролит recycler к первому элементу
 */
@Suppress("unused")
fun RecyclerView.scrollToTop() {
    scrollToPosition(0)
}