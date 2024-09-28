package ru.tensor.sbis.communication_decl.communicator.media.data

import android.util.Size
import ru.tensor.sbis.communication_decl.communicator.media.data.MediaSource

/**
 * Состояние медиа плеера.
 *
 * @author da.zhukov
 */
data class MediaInfo(
    val mediaSource: MediaSource,
    var position: Long = 0,
    var duration: Long = 0,
    var state: State = State.DEFAULT,
    var playbackSpeed: PlaybackSpeed = PlaybackSpeed.X1,

    var playbackError: Throwable? = null,
    var videoSize: Size? = null,
    var firstVideoFrameRendered: Boolean = false,
    var canSeek: Boolean? = null,
    var buffering: Boolean = false,
    var waitingActualProgress: Boolean = false
)