package ru.tensor.sbis.swipeablelayout.swipeablevm

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import ru.tensor.sbis.swipeablelayout.SwipeMenu
import ru.tensor.sbis.swipeablelayout.SwipeableLayout
import ru.tensor.sbis.swipeablelayout.api.Closed
import ru.tensor.sbis.swipeablelayout.api.Dismissed
import ru.tensor.sbis.swipeablelayout.api.MenuOpened
import ru.tensor.sbis.swipeablelayout.api.SwipeEvent
import ru.tensor.sbis.swipeablelayout.api.SwipeEventListener
import ru.tensor.sbis.swipeablelayout.api.SwipeItemDismissType
import ru.tensor.sbis.swipeablelayout.api.SwipeMenuSide
import ru.tensor.sbis.swipeablelayout.api.menu.SwipeMenuItem

/**
 * Интерфейс носителя вьюмодели с поддержкой свайпа/смахивания
 *
 * @author us.bessonov
 */
interface SwipeableVmHolder {
    /** @SelfDocumented */
    val swipeableVm: SwipeableVm
}

/**
 * Расширение [SwipeableVmHolder], предусматривающее возможность переустановки [SwipeableVm]
 *
 * @author us.bessonov
 */
interface MutableSwipeableVmHolder : SwipeableVmHolder {
    /** @SelfDocumented */
    override var swipeableVm: SwipeableVm
}

/**
 * Вьюмодель, используемая для конфигурации [SwipeableLayout] посредством [setSwipeableViewModel]
 *
 * @property uuid идентификатор
 * @property menu список элементов свайп-меню
 * @property itemDismissType тип смахивания элемента для удаления
 * @property isDragLocked заблокировано ли любое движение содержимого или свайп меню
 * @property shouldSwipeContentToRight должно ли содержимое смахиваться вправо, а не влево
 * @property dismissMessage сообщение об удалении (по умолчанию "Удалено")
 * @property eventListeners контейнер дополнительных слушателей событий, если [singleEventListener] недостаточно
 *
 * @author us.bessonov
 */
data class SwipeableVm @JvmOverloads constructor(
    var uuid: String,

    // region Deprecated
    @Deprecated("Есть более актуальный параметр", ReplaceWith("menu")) @JvmField var swipeMenu: SwipeMenu<*>? = null,
    @Deprecated(
        "Используйте методы для управления",
        ReplaceWith("openMenu(), close(), dismiss()")
    ) var state: ObservableField<SwipeState> = ObservableField(SwipeState(SwipeableLayout.CLOSED, false)),
    @Deprecated(
        "Используйте обработчики SwipeEvent",
        ReplaceWith("SwipeEventListener")
    ) var stateChangeListener: SwipeableLayout.StateChangeListener? = null,
    @Deprecated(
        "Используйте обработчики SwipeEvent",
        ReplaceWith("SwipeEventListener")
    ) var onDismissed: Runnable = Runnable { },
    @Deprecated(
        "Используйте обработчики SwipeEvent",
        ReplaceWith("SwipeEventListener")
    ) var onDismissedWithoutMessage: Runnable = Runnable { },
    @Deprecated(
        "Есть более актуальный параметр",
        ReplaceWith("itemDismissType")
    ) var isSwipeToDismissLocked: Boolean = false,
    @Deprecated(
        "Есть более актуальный параметр",
        ReplaceWith("itemDismissType")
    ) var shouldDismissWithoutMessageAtFirst: Boolean = false,
    // endregion

    var menu: List<SwipeMenuItem> = emptyList(),
    var itemDismissType: SwipeItemDismissType = getItemDismissType(
        shouldDismissWithoutMessageAtFirst,
        isSwipeToDismissLocked
    ),
    var isDragLocked: Boolean = false,
    var shouldSwipeContentToRight: Boolean = false,
    var dismissMessage: String? = null,
    internal val event: MutableLiveData<SwipeEvent> = MutableLiveData<SwipeEvent>(),
    val eventListeners: MutableList<SwipeEventListener> = mutableListOf(),
    val singleEventListener: SwipeEventListener? = null,
) {

    init {
        singleEventListener?.let(eventListeners::add)
    }

    val stateValue: Int
        get() = state.get()!!.state

    /**
     * Последнее событие, изменившее состояние свайп-меню.
     */
    val lastEvent: SwipeEvent
        get() = event.value ?: Closed(uuid)

    /** @SelfDocumented */
    fun openMenu() {
        event.value = MenuOpened(uuid, SwipeMenuSide.RIGHT)
    }

    /** @SelfDocumented */
    fun close() {
        event.value = Closed(uuid)
    }

    /** @SelfDocumented */
    fun dismiss() {
        event.value = Dismissed(uuid)
    }
}

/**
 * Определяет состояние [SwipeableLayout], с указанием, требуется ли анимировать переход в него
 * @property state целевое состояние
 * @property animated требуется ли менять состояние анимированно
 */
@Deprecated("Используйте механику событий и методы для управления состоянием свайп-меню")
data class SwipeState(val state: Int, val animated: Boolean = true)

private fun getItemDismissType(
    shouldDismissWithoutMessageAtFirst: Boolean, isSwipeToDismissLocked: Boolean
) = when {
    shouldDismissWithoutMessageAtFirst -> SwipeItemDismissType.DISMISS_WITHOUT_MESSAGE
    isSwipeToDismissLocked -> SwipeItemDismissType.LOCKED
    else -> SwipeItemDismissType.NONE
}