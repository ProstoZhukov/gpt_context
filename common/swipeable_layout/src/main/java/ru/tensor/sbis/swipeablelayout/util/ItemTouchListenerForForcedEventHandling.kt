package ru.tensor.sbis.swipeablelayout.util

import android.graphics.Rect
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import timber.log.Timber

private const val SCROLL_DIRECTION_UP = -1
private const val SCROLL_DIRECTION_DOWN = 1

/**
 * Обработчик событий [MotionEvent] для [view], используемого в элементе [RecyclerView], позволяющий получать события
 * непосредственно после прокрутки к началу или концу списка, когда в течение некоторого времени они не доставляются во
 * [View]. Каждому обработчику доступны только нажатия в области конретного [View], а не любого элемента списка.
 * Если этим обработчиком было зафиксировано событие нажатия, которое [view] не обработал, то это и последующие события
 * будут доступны в [onInterceptMissedDownEvent] и [onTouchEvent].
 *
 * @param onInterceptMissedDownEvent лямбда для обработки события нажатия и следующего за ним события
 * [MotionEvent.ACTION_MOVE], возвращающая true, если последующие события жеста нужно перехватить у [RecyclerView]
 * @param onTouchEvent лямбда, обрабатывающая перехваченные события [RecyclerView]
 *
 * @author us.bessonov
 */
internal class ItemTouchListenerForForcedEventHandling(
    private val view: View,
    private val onInterceptMissedDownEvent: (downEvent: MotionEvent, followingEvent: MotionEvent) -> Boolean,
    private val onTouchEvent: (event: MotionEvent) -> Unit
) : RecyclerView.SimpleOnItemTouchListener() {

    private var lastInterceptedDownEvent: MotionEvent? = null
    private var isDownEventMissed: Boolean = false

    private val rect = Rect()

    /**
     * Уведомляет обработчик о том, что клиентским [View] было получено событие [MotionEvent.ACTION_DOWN]
     */
    fun onViewDownEventReceived() {
        isDownEventMissed = false
    }

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        if (!view.readyToHandleEvent) return false

        view.obtainVisibleRectInParentCoords(rect, rv)
        if (!rect.contains(e.x.toInt(), e.y.toInt())) return false

        val localEvent = view.mapParentMotionEventCoordsToMyCoords(e, rv, rect)
        if (e.action == MotionEvent.ACTION_DOWN) {
            isDownEventMissed = true
            lastInterceptedDownEvent = localEvent
        }
        if ((!rv.canScrollVertically(SCROLL_DIRECTION_UP) || !rv.canScrollVertically(SCROLL_DIRECTION_DOWN)) && e.action == MotionEvent.ACTION_MOVE && isDownEventMissed) {
            val downEvent = lastInterceptedDownEvent ?: return false
            return onInterceptMissedDownEvent(downEvent, localEvent)
        }

        return false
    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
        if (!view.readyToHandleEvent) return

        val localEvent = view.mapParentMotionEventCoordsToMyCoords(e, rv, rect)
        runSafeWithPointerIndexAnalytics(e, localEvent) {
            onTouchEvent(localEvent)
        }
    }

    private fun runSafeWithPointerIndexAnalytics(original: MotionEvent, local: MotionEvent, action: () -> Unit) {
        try {
            action()
        } catch (e: RuntimeException) {
            Timber.e(
                e,
                "original event: ${original.getPointerIndexAnalytics()}; local event: ${local.getPointerIndexAnalytics()}"
            )
        }
    }

    private fun MotionEvent.getPointerIndexAnalytics(): String {
        val pointerId = actionIndex.takeIf { it in 0 until pointerCount }?.let { getPointerId(it) }
        return "$this. actionIndex: $actionIndex, pointerCount $pointerCount, pointerId: $pointerId, pointerIndex: ${
            findPointerIndex(
                actionIndex
            )
        }"
    }

    private val View.readyToHandleEvent: Boolean get() = isAttachedToWindow && parent != null
}