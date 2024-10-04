package ru.tensor.sbis.design.media_player.helpers

import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import java.util.Timer
import java.util.TimerTask

/**
 * Хелпер для упаравления прогрессом проигрывания медиа сообщений.
 *
 * @author da.zhukov
 */
internal class ProgressTimerHelper {

    private val progressTimerSync = Object()
    private val progressTimerTaskSync = Object()
    private var progressTimer: Timer? = null

    private var mediaPlayerInfo: MediaPlayerInfo? = null
    private var mediaPlayerStateHelper: MediaPlayerStateHelper? = null
    private var safePlayer: ExoPlayer? = null
    private var uiThreadHelper: UIThreadHelper? = null

    /**@SelfDocumented*/
    fun init(
        mediaPlayerInfo: MediaPlayerInfo,
        mediaPlayerStateHelper: MediaPlayerStateHelper,
        safePlayer: ExoPlayer,
        uiThreadHelper: UIThreadHelper
    ) {
        this.mediaPlayerInfo = mediaPlayerInfo
        this.mediaPlayerStateHelper = mediaPlayerStateHelper
        this.safePlayer = safePlayer
        this.uiThreadHelper = uiThreadHelper
    }

    /**@SelfDocumented*/
    fun startProgressTimer() = synchronized(progressTimerSync) {
        progressTimer?.cancel()
        progressTimer = Timer().also { timer ->
            var isFirstTaskExecuted = false
            timer.schedule(
                @UnstableApi object : TimerTask() {
                    override fun run(): Unit = synchronized(progressTimerTaskSync) {
                        uiThreadHelper?.runOnUIThread {
                            if (progressTimer !== timer || mediaPlayerInfo?.currentMediaInfo == null) {
                                timer.cancel()
                                return@runOnUIThread
                            }
                            // На первом считывании плеер не успевает сбросить состояние после предыдущего проигрывания.
                            if (!isFirstTaskExecuted) {
                                isFirstTaskExecuted = true
                                return@runOnUIThread
                            }
                            val player = safePlayer ?: return@runOnUIThread
                            if (mediaPlayerInfo?.seekToProgressPending != 0f) return@runOnUIThread
                            mediaPlayerStateHelper?.updateMediaState {
                                position = player.currentPosition
                                duration = player.duration.takeIf {
                                    it in (duration..duration + 1000L)
                                } ?: duration
                            }
                        }
                    }
                },
                0,
                PROGRESS_TIMER_UPDATE_PERIOD_MS
            )
        }
    }

    /**@SelfDocumented*/
    fun stopProgressTimer() = synchronized(progressTimerSync) {
        progressTimer?.cancel()
        progressTimer = null
    }

    /**@SelfDocumented*/
    fun release() {
        stopProgressTimer()
        mediaPlayerInfo = null
        mediaPlayerStateHelper = null
        safePlayer = null
        uiThreadHelper = null
    }
}

private const val PROGRESS_TIMER_UPDATE_PERIOD_MS = 17L