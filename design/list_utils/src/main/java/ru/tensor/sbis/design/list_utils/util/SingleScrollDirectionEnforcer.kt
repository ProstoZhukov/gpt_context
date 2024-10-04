package ru.tensor.sbis.design.list_utils.util

import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnItemTouchListener
import kotlin.math.abs

/**
 * Обеспечить соблюдение ориентации [RecyclerView] при его скроле
 * См. [SingleScrollDirectionEnforcer]
 */
fun RecyclerView.enforceSingleScrollDirection() {
    val enforcer = SingleScrollDirectionEnforcer()
    addOnItemTouchListener(enforcer)
    addOnScrollListener(enforcer)
}

/**
 * Класс, обеспечивающий соблюдение ориентации [RecyclerView] при его скроле
 * Для вертикального [RecyclerView] скрол осуществится при жесте с углом > 45 градусов, для горизонтального - < 45 градусов.
 *
 * Если в вертикальном [RecyclerView] есть вложенная вью с горизонтальной прокруткой, например, другой [RecyclerView],
 * то объект этого класса обеспечит горизонтальный скрол вложенной вью, если угол жеста < 45 градусов
 *
 * Установить на [RecyclerView] можно с помощью [enforceSingleScrollDirection]
 *
 * @author sa.nikitin
 */
private class SingleScrollDirectionEnforcer : RecyclerView.OnScrollListener(), OnItemTouchListener {

    private var scrollState = RecyclerView.SCROLL_STATE_IDLE
    private var scrollPointerId = -1
    private var initialTouchX = 0
    private var initialTouchY = 0
    private var dx = 0
    private var dy = 0

    override fun onInterceptTouchEvent(rv: RecyclerView, event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN         -> {
                scrollPointerId = event.getPointerId(0)
                initialTouchX = (event.x + 0.5f).toInt()
                initialTouchY = (event.y + 0.5f).toInt()
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                val actionIndex = event.actionIndex
                scrollPointerId = event.getPointerId(actionIndex)
                initialTouchX = (event.getX(actionIndex) + 0.5f).toInt()
                initialTouchY = (event.getY(actionIndex) + 0.5f).toInt()
            }
            MotionEvent.ACTION_MOVE         -> {
                val index = event.findPointerIndex(scrollPointerId)
                if (index >= 0 && scrollState != RecyclerView.SCROLL_STATE_DRAGGING) {
                    val x = (event.getX(index) + 0.5f).toInt()
                    val y = (event.getY(index) + 0.5f).toInt()
                    dx = x - initialTouchX
                    dy = y - initialTouchY
                }
            }
        }
        return false
    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) = Unit

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) = Unit

    override fun onScrollStateChanged(recyclerView: RecyclerView, newScrollState: Int) {
        val oldScrollState = scrollState
        //Важно присвоить scrollState до stopScroll, т.к. внутри него onScrollStateChanged может быть вызван снова
        scrollState = newScrollState
        recyclerView.layoutManager?.apply {
            if (oldScrollState == RecyclerView.SCROLL_STATE_IDLE && newScrollState == RecyclerView.SCROLL_STATE_DRAGGING &&
                (canScrollVertically() && isHorizontalDirectionPrevails() || canScrollHorizontally() && isVerticalDirectionPrevails())
            ) {
                //Останавливаем скрол, если жест, который его стартовал, не соответствует ориентации RecyclerView
                recyclerView.stopScroll()
            }
        }

    }

    /** @SelfDocumented */
    fun isVerticalDirectionPrevails(): Boolean = abs(dy) > abs(dx)

    /** @SelfDocumented */
    fun isHorizontalDirectionPrevails(): Boolean = abs(dx) > abs(dy)
}