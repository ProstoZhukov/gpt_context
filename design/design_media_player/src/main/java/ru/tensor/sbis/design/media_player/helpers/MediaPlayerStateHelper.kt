package ru.tensor.sbis.design.media_player.helpers

import android.media.MediaFormat
import android.util.Size
import androidx.media3.common.C
import androidx.media3.common.Format
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.video.VideoFrameMetadataListener
import io.reactivex.subjects.BehaviorSubject
import ru.tensor.sbis.communication_decl.communicator.media.MediaPlayer
import ru.tensor.sbis.communication_decl.communicator.media.MediaPlayerController
import ru.tensor.sbis.communication_decl.communicator.media.data.MediaInfo
import ru.tensor.sbis.communication_decl.communicator.media.data.State

@UnstableApi
/**
 * Хелпер для реагирования на изменение состояния проигрывателя.
 *
 * @author da.zhukov
 */
internal class MediaPlayerStateHelper :
    Player.Listener,
    VideoFrameMetadataListener {

    private var playingStateListener: MediaPlayer.PlayingStateListener? = null
    private var mediaPlayerController: MediaPlayerController? = null
    private var player: ExoPlayer? = null
    private var mediaPlayerInfo: MediaPlayerInfo? = null
    private var onErrorListener: (error: Throwable?) -> Unit = {}

    /**@SelfDocumented*/
    var currentSubscription: BehaviorSubject<MediaInfo>? = null

    /**@SelfDocumented*/
    fun init(
        player: ExoPlayer,
        mediaPlayerController: MediaPlayerController,
        mediaPlayerInfo: MediaPlayerInfo,
        onErrorListener: (error: Throwable?) -> Unit
    ) {
        this.player = player
        this.mediaPlayerController = mediaPlayerController
        this.mediaPlayerInfo = mediaPlayerInfo
        this.onErrorListener = onErrorListener
    }

    /**@SelfDocumented*/
    fun setPlayingListener(listener: MediaPlayer.PlayingStateListener?) {
        playingStateListener = listener
    }

    /**@SelfDocumented*/
    fun updateMediaState(block: MediaInfo.() -> Unit): MediaInfo {
        val mediaInfo = mediaPlayerInfo?.currentMediaInfo!!
        mediaInfo.block()
        currentSubscription?.onNext(mediaInfo)
        updatePlayingState(mediaInfo)
        return mediaInfo
    }

    /**@SelfDocumented*/
    fun release() {
        currentSubscription?.onComplete()
        currentSubscription = null
        playingStateListener = null
        player = null
        mediaPlayerController = null
        mediaPlayerInfo = null
    }

    @Deprecated("Deprecated in Java")
    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        if (playbackState == Player.STATE_ENDED) {
            mediaPlayerController?.stop()
        } else {
            val isBuffering = playbackState == Player.STATE_BUFFERING
            if (mediaPlayerInfo?.currentMediaInfo?.buffering?.let { it != isBuffering } == true) {
                updateMediaState { buffering = isBuffering }
            }
        }
        val seekToProgressPending = mediaPlayerInfo?.seekToProgressPending
        val duration = mediaPlayerInfo?.currentMediaInfo?.duration
        if (seekToProgressPending != null && seekToProgressPending != 0f &&
            duration != null && duration > 0 &&
            player?.duration != C.TIME_UNSET &&
            playbackState == Player.STATE_READY
        ) {
            val newPosition = (duration * seekToProgressPending).toLong()
            mediaPlayerController?.setPosition(newPosition)
            mediaPlayerInfo?.seekToProgressPending = 0f
            mediaPlayerInfo?.currentMediaInfo?.waitingActualProgress = false
        }
    }

    override fun onPlayerError(error: PlaybackException) {
        onErrorListener(error)
    }

    override fun onVideoSizeChanged(videoSize: VideoSize) {
        updateMediaState { this.videoSize = Size(videoSize.width, videoSize.height) }
    }

    override fun onRenderedFirstFrame() {
        updateMediaState { firstVideoFrameRendered = true }
    }

    override fun onVideoFrameAboutToBeRendered(
        presentationTimeUs: Long,
        releaseTimeNs: Long,
        format: Format,
        mediaFormat: MediaFormat?
    ) {
        val mediaInfo = mediaPlayerInfo?.currentMediaInfo ?: return
        if (mediaInfo.canSeek == null && format.sampleMimeType != null) {
            updateMediaState {
                canSeek = format.sampleMimeType != MediaFormat.MIMETYPE_VIDEO_VP8
            }
        }
    }

    private fun updatePlayingState(mediaInfo: MediaInfo) {
        val isActive = mediaInfo.state == State.PLAYING || mediaInfo.state == State.PAUSED
        if (mediaPlayerInfo?.isPlayingActive != isActive) {
            mediaPlayerInfo?.isPlayingActive = isActive
            playingStateListener?.playingStateChanged(isActive)
        }
    }
}