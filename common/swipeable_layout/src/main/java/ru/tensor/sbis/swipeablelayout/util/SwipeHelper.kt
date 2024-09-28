package ru.tensor.sbis.swipeablelayout.util

import ru.tensor.sbis.swipeablelayout.SwipeableLayout
import ru.tensor.sbis.swipeablelayout.api.Closed
import ru.tensor.sbis.swipeablelayout.api.Dismissed
import ru.tensor.sbis.swipeablelayout.api.DismissedWithTimeout
import ru.tensor.sbis.swipeablelayout.api.DismissedWithoutMessage
import ru.tensor.sbis.swipeablelayout.api.Dismissing
import ru.tensor.sbis.swipeablelayout.api.Dragging
import ru.tensor.sbis.swipeablelayout.api.MenuOpened
import ru.tensor.sbis.swipeablelayout.api.MenuOpening
import ru.tensor.sbis.swipeablelayout.api.SwipeItemDismissType
import ru.tensor.sbis.swipeablelayout.util.swipestate.SwipeListVm

/**
 * Инструмент для операций над списком элементов со свайп-меню, отображаемым на экране.
 *
 * @author us.bessonov
 */
object SwipeHelper {

    private val attachedViews = mutableSetOf<SwipeableLayout>()

    /**
     * Закрывает все открытые или открывающиеся cвайп-меню на экране, за исключением [except]
     */
    @JvmOverloads
    fun closeAll(animated: Boolean = true, except: SwipeableLayout? = null) {
        attachedViews.filter { it != except && it.isMenuOpeningOrOpen() }.forEach { it.close(animated) }
    }

    /**
     * Сбрасывает состояния всех свайп-меню на экране, по умолчанию, за исключением помеченных как удалённые до
     * истечения таймаута.
     *
     * @param withDismissedWithTimeout сбрасывать ли состояния элементов, помеченных как удалённые
     */
    @JvmOverloads
    fun resetAll(animated: Boolean = true, withDismissedWithTimeout: Boolean = false) {
        attachedViews.filter {
            it.lastEvent !is Closed && (it.lastEvent !is DismissedWithTimeout || withDismissedWithTimeout)
        }.forEach { it.close(animated) }
    }

    /**
     * Принудительно оповестить о подтверждении удаления (событие [Dismissed]) для всех элементов с неистекшим
     * таймаутом.
     *
     * @see [SwipeItemDismissType.CANCELLABLE]
     */
    fun forceDismissItemsWithTimeout() {
        attachedViews.forEach { it.forceDismissItemsWithTimeout() }
    }

    /**
     * Найти [SwipeableLayout], ассоциируемый с указанным [uuid], если он в данный момент присутствует на экране.
     */
    fun findSwipeableLayoutByUuid(uuid: String): SwipeableLayout? = attachedViews.find { it.itemUuid == uuid }

    /** @SelfDocumented */
    internal fun onAttached(view: SwipeableLayout) {
        attachedViews.add(view)
    }

    /** @SelfDocumented */
    internal fun onDetached(view: SwipeableLayout) {
        attachedViews.remove(view)
    }

    /** @SelfDocumented */
    internal fun ensureAllDetached(swipeListVm: SwipeListVm) {
        attachedViews.removeAll { it.swipeListVm == swipeListVm }
    }

    /** @SelfDocumented */
    internal fun closeAllOthersWhenStateChangedIfNeeded(view: SwipeableLayout) {
        if (getOpenedCount() <= 1 && !view.isDismissingOrDismissed()) {
            return
        }
        closeAll(true, view)
    }

    private fun getOpenedCount() = attachedViews.count { it.isMenuOpeningOrOpen() }

    /**
     * Соответствует ли значение состояния открытому или открывающемуся свайп-меню
     */
    private fun SwipeableLayout.isMenuOpeningOrOpen() = lastEvent.let {
        it is MenuOpening || it is MenuOpened || it is Dragging
    }

    private fun SwipeableLayout.isDismissingOrDismissed() = lastEvent.let {
        it is Dismissing || it is DismissedWithoutMessage || it is DismissedWithTimeout || it is Dismissed
    }
}