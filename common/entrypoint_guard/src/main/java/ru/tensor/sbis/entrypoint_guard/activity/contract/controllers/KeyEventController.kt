package ru.tensor.sbis.entrypoint_guard.activity.contract.controllers

import android.view.KeyEvent

/** @SelfDocumented */
abstract class KeyEventController {

    internal abstract fun onKeyUp(
        keyCode: Int,
        event: KeyEvent?,
        fallback: (Int, KeyEvent?) -> Boolean
    ): Boolean

    internal abstract fun onKeyLongPress(
        keyCode: Int,
        event: KeyEvent?,
        fallback: (Int, KeyEvent?) -> Boolean
    ): Boolean

    internal abstract fun onKeyMultiple(
        keyCode: Int,
        repeatCount: Int,
        event: KeyEvent?,
        fallback: (Int, Int, KeyEvent?) -> Boolean
    ): Boolean

    internal abstract fun onKeyDown(
        keyCode: Int,
        event: KeyEvent?,
        fallback: (Int, KeyEvent?) -> Boolean
    ): Boolean

    internal abstract fun dispatchKeyEvent(
        event: KeyEvent?,
        fallback: (KeyEvent?) -> Boolean
    ): Boolean

    /** @SelfDocumented */
    interface Interceptor {
        fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean = false
        fun onKeyLongPress(keyCode: Int, event: KeyEvent?): Boolean = false
        fun onKeyMultiple(keyCode: Int, repeatCount: Int, event: KeyEvent?): Boolean = false
        fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean = false
        fun dispatchKeyEvent(event: KeyEvent?): Boolean = false
    }

    abstract fun setInterceptor(value: Interceptor?)
}