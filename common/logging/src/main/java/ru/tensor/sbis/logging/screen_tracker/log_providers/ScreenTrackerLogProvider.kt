package ru.tensor.sbis.logging.screen_tracker.log_providers

import ru.tensor.sbis.logging.screen_tracker.ScreenTracker

/**
 * Интерфейс для предоставления логов в [ScreenTracker].
 *
 * @author av.krymov
 */
internal fun interface ScreenTrackerLogProvider {

    /**
     * Предоставляет строку для логирования
     *
     * @param screen экран (активити, фрагмент или что-то другое)
     * @param action выполняемое действие
     * @return строка для логирования
     */
    fun getLogMessage(screen: Any, action: String): String
}
