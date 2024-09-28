package ru.tensor.sbis.widget_player.layout.internal.scheduler

import android.view.Choreographer

/**
 * @author am.boldinov
 */
internal class ChoreographerFrameCallbackScheduler(
    private val callback: FrameCallback
) : FrameCallbackScheduler {

    private val choreographer = Choreographer.getInstance()
    private val frameCallback = Choreographer.FrameCallback {
        if (scheduleFrameCallback) {
            scheduleFrameCallback = false
            callback.run()
        }
    }
    private var scheduleFrameCallback = false

    override fun schedule() {
        if (!scheduleFrameCallback) {
            forceSchedule()
        }
    }

    override fun forceSchedule() {
        scheduleFrameCallback = true
        choreographer.postFrameCallback(frameCallback)
    }

    override fun unschedule() {
        scheduleFrameCallback = false
        choreographer.removeFrameCallback(frameCallback)
    }

}
