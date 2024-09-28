package ru.tensor.sbis.swipeablelayout.api

/**
 * События изменения состояния свайп-меню.
 *
 * @author us.bessonov
 */
sealed interface SwipeEvent {
    /**
     * Идентификатор элемента, к которому относится событие.
     */
    val uuid: String?
}

/**
 * Меню закрыто.
 */
data class Closed(override val uuid: String?) : SwipeEvent

/**
 * Меню в процессе открытия.
 */
data class MenuOpening(override val uuid: String?, val side: SwipeMenuSide) : SwipeEvent

/**
 * Меню полностью видимо.
 */
data class MenuOpened(override val uuid: String?, val side: SwipeMenuSide) : SwipeEvent

/**
 * Пользователь коснулся элемента, и совершает жест.
 */
data class Dragging(override val uuid: String?) : SwipeEvent

/**
 * Элемент в процессе смахивания (удаления элемента жестом).
 */
data class Dismissing(override val uuid: String?) : SwipeEvent

/**
 * Смахивание завершено, элемент можно считать удалённым.
 */
data class Dismissed(override val uuid: String?) : SwipeEvent

/**
 * Смахивание частично завершено - содержимое элемента скрыто, но сообщение об удалении не отображено.
 */
data class DismissedWithoutMessage(override val uuid: String?) : SwipeEvent

/**
 * Смахивание завершено, но по клику, либо путём вызова [SwipeableLayoutApi.close], может быть отменено.
 */
data class DismissedWithTimeout(override val uuid: String?) : SwipeEvent