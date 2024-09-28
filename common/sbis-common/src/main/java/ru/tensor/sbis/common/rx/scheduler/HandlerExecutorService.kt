package ru.tensor.sbis.common.rx.scheduler

import java.util.concurrent.ExecutorService
import java.util.concurrent.ScheduledExecutorService

/** An [ExecutorService] that is backed by a handler.
 *
 * Копия из facebook core (fresco)
 */
interface HandlerExecutorService : ScheduledExecutorService {
    /** Quit the handler  */
    fun quit()

    /** Check if we are currently in the handler thread of this HandlerExecutorService.  */
    fun isHandlerThread(): Boolean
}