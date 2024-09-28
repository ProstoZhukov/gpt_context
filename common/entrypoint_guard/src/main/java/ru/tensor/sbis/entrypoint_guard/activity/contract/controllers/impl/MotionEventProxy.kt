package ru.tensor.sbis.entrypoint_guard.activity.contract.controllers.impl

import android.view.MotionEvent
import ru.tensor.sbis.entrypoint_guard.activity.contract.controllers.MotionEventController

/**
 * Внутренняя реализация [MotionEventController] для проксирования методов активности в [interceptor] при наличии.
 */
internal class MotionEventProxy : MotionEventController() {
    private var interceptor: Interceptor? = null

    /** @SelfDocumented */
    override fun setInterceptor(value: Interceptor?) {
        interceptor = value
    }

    override fun onTouchEvent(event: MotionEvent, fallback: (MotionEvent) -> Boolean): Boolean {
        return interceptor?.onTouchEvent(event)?.takeIf { it } ?: fallback(event)
    }

    override fun dispatchTouchEvent(ev: MotionEvent, fallback: (MotionEvent) -> Boolean): Boolean {
        return interceptor?.dispatchTouchEvent(ev)?.takeIf { it } ?: fallback(ev)
    }
}