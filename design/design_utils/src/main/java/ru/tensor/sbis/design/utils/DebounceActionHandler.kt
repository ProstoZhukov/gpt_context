package ru.tensor.sbis.design.utils

import android.os.SystemClock

/**
 * Обработчик повторяющихся событий, который выполняет только первое действие за определенный промежуток времени
 *
 * @author am.boldinov
 */
class DebounceActionHandler(private val debounce: Long = DEFAULT_TIMEOUT) {

    companion object {

        private const val DEFAULT_TIMEOUT = 800L

        val INSTANCE = DebounceActionHandler()
    }

    private var lastActionTimestamp = 0L

    /**
     * Посылает сигнал о том, что начинается выполнение повторяющегося действия.
     *
     * @return true если действие доступно для выполнения, false иначе
     */
    fun enqueue(): Boolean {
        return if (SystemClock.elapsedRealtime() - lastActionTimestamp > debounce) {
            lastActionTimestamp = SystemClock.elapsedRealtime()
            true
        } else {
            false
        }
    }

    /**
     * Обрабатывает действие в случае если оно доступно для выполнения в текущий момент времени
     */
    fun handle(action: () -> Unit) {
        if (enqueue()) {
            action.invoke()
        }
    }
}