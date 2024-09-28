package ru.tensor.sbis.design.view.input.searchinput.util

import android.graphics.Rect
import android.view.MotionEvent
import android.view.TouchDelegate
import android.view.View
import android.view.ViewConfiguration

/**
 * [TouchDelegate] с возможностью обновления границ касания. Копия UpdatableTouchDelegate из [android.widget.SearchView].
 *
 * @param targetBounds необходимая область нажатия View в локальных координатах контейнера
 * @param actualBounds оригинальная область нажатия View в локальных координатах контейнера
 * @param delegateView View которой должны делегироваться нажатия
 *
 * @author ps.smirnyh
 */
internal class UpdatableTouchDelegate(
    private val targetBounds: Rect,
    private val actualBounds: Rect,
    private val delegateView: View
) : TouchDelegate(targetBounds, delegateView) {

    /**
     * [targetBounds] будет увеличен для включения дополнительной области, на которую может уйти event.
     * Этот rect предназначен для отслеживания того, следует ли считать события в области действия делегата.
     *
     * @see ViewConfiguration.getScaledTouchSlop
     */
    private val slopBounds = Rect()
    private val slop: Int = ViewConfiguration.get(delegateView.context).scaledTouchSlop

    /**
     * True если нужно делегировать событие делегату.
     */
    private var delegateTargeted = false

    init {
        setBounds(targetBounds, actualBounds)
    }

    /**
     * Установить область нажатия [delegateView].
     *
     * @param desiredBounds необходимая область нажатия View в локальных координатах контейнера
     * @param actualBounds оригинальная область нажатия View в локальных координатах контейнера
     */
    fun setBounds(desiredBounds: Rect, actualBounds: Rect) {
        targetBounds.set(desiredBounds)
        slopBounds.set(desiredBounds)
        slopBounds.inset(-slop, -slop)
        this.actualBounds.set(actualBounds)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x.toInt()
        val y = event.y.toInt()
        var sendToDelegate = false
        var hit = true
        var handled = false
        when (event.action) {
            MotionEvent.ACTION_DOWN -> if (targetBounds.contains(x, y)) {
                delegateTargeted = true
                sendToDelegate = true
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_MOVE -> {
                sendToDelegate = delegateTargeted
                if (sendToDelegate && !slopBounds.contains(x, y)) {
                    hit = false
                }
            }

            MotionEvent.ACTION_CANCEL -> {
                sendToDelegate = delegateTargeted
                delegateTargeted = false
            }
        }
        if (sendToDelegate) {
            if (hit && !actualBounds.contains(x, y)) {
                // Смещение координат в центр delegateView поскольку мы находимся в пределах targetBounds,
                // но не в пределах actualBounds
                event.setLocation(
                    (delegateView.width / 2).toFloat(),
                    (delegateView.height / 2).toFloat()
                )
            } else {
                // Смещение координат события к координатам delegateView
                event.setLocation(
                    (x - actualBounds.left).toFloat(),
                    (y - actualBounds.top).toFloat()
                )
            }
            handled = delegateView.dispatchTouchEvent(event)
        }
        return handled
    }
}