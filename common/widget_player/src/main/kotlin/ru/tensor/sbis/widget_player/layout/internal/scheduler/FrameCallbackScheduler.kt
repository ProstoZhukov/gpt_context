package ru.tensor.sbis.widget_player.layout.internal.scheduler

/**
 * @author am.boldinov
 */
internal interface FrameCallbackScheduler {

    fun schedule()

    fun forceSchedule()

    fun unschedule()
}

internal fun interface FrameCallback : Runnable