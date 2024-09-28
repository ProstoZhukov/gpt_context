package ru.tensor.sbis.entrypoint_guard.activity.contract.controllers

import android.view.MotionEvent

/** @SelfDocumented */
abstract class MotionEventController {

    internal abstract fun onTouchEvent(
        event: MotionEvent,
        fallback: (MotionEvent) -> Boolean
    ): Boolean

    internal abstract fun dispatchTouchEvent(
        ev: MotionEvent,
        fallback: (MotionEvent) -> Boolean
    ): Boolean

    /** @SelfDocumented */
    interface Interceptor {
        fun onTouchEvent(event: MotionEvent): Boolean = false
        fun dispatchTouchEvent(ev: MotionEvent): Boolean = false
    }

    abstract fun setInterceptor(value: Interceptor?)
}