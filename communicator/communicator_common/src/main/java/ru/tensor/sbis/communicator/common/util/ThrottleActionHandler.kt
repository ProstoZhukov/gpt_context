package ru.tensor.sbis.communicator.common.util

import android.os.SystemClock
import ru.tensor.sbis.design.utils.DebounceActionHandler

/**
 * Обработчик повторяющихся событий, который выполняет только первое действие и дальше должен оставаться в бездействии
 * продолжительность [timeoutMs].
 * Аналог [DebounceActionHandler], только со стратегией throttle first.
 *
 * @author vv.chekurda
 */
class ThrottleActionHandler(private val timeoutMs: Long = DEFAULT_TIMEOUT) {

    companion object {

        private const val DEFAULT_TIMEOUT = 1000L

        val INSTANCE = ThrottleActionHandler()
    }

    private var lastActionTimestamp = 0L

    /**
     * Обрабатывает действие в случае, если оно доступно для выполнения в текущий момент времени.
     */
    fun handle(action: () -> Unit) {
        if (enqueue()) {
            action.invoke()
        }
    }

    private fun enqueue(): Boolean {
        val needHandle = SystemClock.elapsedRealtime() - lastActionTimestamp > timeoutMs
        lastActionTimestamp = SystemClock.elapsedRealtime()
        return needHandle
    }
}