package ru.tensor.sbis.entrypoint_guard.activity.contract.controllers.impl

import android.view.KeyEvent
import ru.tensor.sbis.entrypoint_guard.activity.contract.controllers.KeyEventController

/**
 * Внутренняя реализация [KeyEventController] для проксирования методов активности в [interceptor] при наличии.
 */
internal class KeyEventProxy : KeyEventController() {
    private var interceptor: Interceptor? = null

    /** @SelfDocumented */
    override fun setInterceptor(value: Interceptor?) {
        interceptor = value
    }

    override fun onKeyUp(
        keyCode: Int,
        event: KeyEvent?,
        fallback: (Int, KeyEvent?) -> Boolean
    ): Boolean {
        return interceptor?.onKeyUp(keyCode, event)?.takeIf { it } ?: fallback(keyCode, event)
    }

    override fun onKeyDown(
        keyCode: Int,
        event: KeyEvent?,
        fallback: (Int, KeyEvent?) -> Boolean
    ): Boolean {
        return interceptor?.onKeyDown(keyCode, event)?.takeIf { it } ?: fallback(keyCode, event)
    }

    override fun onKeyLongPress(
        keyCode: Int,
        event: KeyEvent?,
        fallback: (Int, KeyEvent?) -> Boolean
    ): Boolean {
        return interceptor?.onKeyLongPress(keyCode, event)?.takeIf { it } ?: fallback(
            keyCode,
            event
        )
    }

    override fun onKeyMultiple(
        keyCode: Int,
        repeatCount: Int,
        event: KeyEvent?,
        fallback: (Int, Int, KeyEvent?) -> Boolean
    ): Boolean {
        return interceptor?.onKeyMultiple(keyCode, repeatCount, event)?.takeIf { it } ?: fallback(
            keyCode,
            repeatCount,
            event
        )
    }

    override fun dispatchKeyEvent(event: KeyEvent?, fallback: (KeyEvent?) -> Boolean): Boolean {
        return interceptor?.dispatchKeyEvent(event)?.takeIf { it } ?: fallback(event)
    }
}