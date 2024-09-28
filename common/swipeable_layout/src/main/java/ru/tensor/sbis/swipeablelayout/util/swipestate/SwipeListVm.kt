package ru.tensor.sbis.swipeablelayout.util.swipestate

import android.util.ArrayMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.tensor.sbis.swipeablelayout.DismissListener
import ru.tensor.sbis.swipeablelayout.SwipeableLayout
import ru.tensor.sbis.swipeablelayout.api.DismissedWithTimeout
import ru.tensor.sbis.swipeablelayout.api.ItemInListChecker
import ru.tensor.sbis.swipeablelayout.api.MenuOpened
import ru.tensor.sbis.swipeablelayout.api.MenuOpening
import ru.tensor.sbis.swipeablelayout.api.SwipeEvent
import ru.tensor.sbis.swipeablelayout.api.SwipeEventListener
import ru.tensor.sbis.swipeablelayout.api.SwipeMenuSide
import ru.tensor.sbis.swipeablelayout.util.SwipeHelper

private const val INITIAL_CAPACITY = 32
private const val DISMISSAL_TIMEOUT_MS = 4000L

/**
 * Предназначен для сохранения и восстановления состояния элементов экрана со свайп-меню.
 *
 * @author us.bessonov
 */
internal class SwipeListVm : ViewModel() {

    private val lastEvents: MutableMap<SwipeItemId, SwipeEvent> = mutableMapOf()
    private val pendingDismissals = ArrayMap<SwipeItemId, PendingDismissal>(INITIAL_CAPACITY)
    private var isItemInList: ((String) -> Boolean)? = null

    /** @SelfDocumented */
    fun onSwipeEvent(view: SwipeableLayout) {
        val itemId = view.getItemId()
        if (itemId != NoId) {
            lastEvents[itemId] = view.lastEvent
            if (view.lastEvent is DismissedWithTimeout) {
                onItemDismissedWithTimeout(itemId, view.eventListeners, view.onDismissListener)
            }
        }
        SwipeHelper.closeAllOthersWhenStateChangedIfNeeded(view)
    }

    /** @SelfDocumented */
    fun restoreState(view: SwipeableLayout) {
        val itemId = view.getItemId()
        if (itemId == NoId) {
            view.close(false)
            return
        }
        lastEvents[itemId]?.let { event ->
            // Восстанавливаем состояние, если оно есть
            when (event) {
                is MenuOpening -> view.openMenu(event.side)
                is MenuOpened -> view.openMenu(event.side)
                is DismissedWithTimeout -> {
                    view.setDismissedWithTimeout()
                    pendingDismissals[view.getItemId()]?.let {
                        it.eventListeners = view.eventListeners
                        it.dismissListener = view.onDismissListener
                    }
                }
                else -> view.close(false)
            }
        } ?: view.close(false)
    }

    /** @SelfDocumented */
    fun onItemDismissCancelled(layout: SwipeableLayout) {
        val id = layout.getItemId()
        pendingDismissals.remove(id)?.cancel()
    }

    /**
     * Принудительно уведомить слушателей о подтверждении удаления элементов с неистекшим таймаутом.
     */
    fun forceDismissItemsWithTimeout() {
        getPendingItemIds().forEach(::onItemTimeoutExpired)
    }

    /**
     * Задать лябмду для проверки наличия элемента в списке данных.
     */
    fun setItemInListChecker(checker: ItemInListChecker?) {
        isItemInList = checker
    }

    /** @SelfDocumented */
    fun onItemsChangedOrRemoved() {
        if (pendingDismissals.isEmpty()) {
            return
        }
        isItemInList?.let { isItemInList ->
            getPendingItemIds().filterIsInstance<Uuid>().forEach { id ->
                if (!isItemInList(id.uuid)) onItemTimeoutExpired(id)
            }
        }
    }

    private fun SwipeableLayout.openMenu(side: SwipeMenuSide) = when(side) {
        SwipeMenuSide.LEFT -> openLeftMenu(false)
        SwipeMenuSide.RIGHT -> openMenu(false)
    }

    private fun onItemTimeoutExpired(id: SwipeItemId) {
        pendingDismissals[id]?.let {
            if (it.isInDismissalProcess) return
            it.isInDismissalProcess = true
            it.notifyDismissed(id)
            it.cancel()
            if (lastEvents[id] is DismissedWithTimeout) {
                lastEvents.remove(id)
            }
            pendingDismissals.remove(id)
        }
    }

    private fun onItemDismissedWithTimeout(
        id: SwipeItemId,
        eventListeners: Map<String, SwipeEventListener>,
        onDismissListener: DismissListener?
    ) {
        if (pendingDismissals.contains(id)) return
        val job = viewModelScope.launch {
            delay(DISMISSAL_TIMEOUT_MS)
            onItemTimeoutExpired(id)
        }
        pendingDismissals[id] = PendingDismissal(job, eventListeners, onDismissListener)
    }

    private fun getPendingItemIds() = pendingDismissals.keys.toList()

    override fun onCleared() {
        super.onCleared()
        lastEvents.clear()
        SwipeHelper.ensureAllDetached(this)
        isItemInList = null
        pendingDismissals.clear()
    }
}