package ru.tensor.sbis.design.media_player

import android.content.Context
import android.view.TextureView
import androidx.annotation.VisibleForTesting
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSourceException
import androidx.media3.exoplayer.ExoPlayer
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import ru.tensor.sbis.communication_decl.communicator.media.MediaPlayer
import ru.tensor.sbis.communication_decl.communicator.media.MediaPlayer.*
import ru.tensor.sbis.communication_decl.communicator.media.data.*
import ru.tensor.sbis.communication_decl.communicator.media.data.MediaSource.*
import ru.tensor.sbis.communication_decl.communicator.media.data.State
import ru.tensor.sbis.communication_decl.videocall.bl.CallStateProvider
import ru.tensor.sbis.design.media_player.MediaPlayerPlugin.callStateFeatureProvider as providerCallState
import ru.tensor.sbis.design.media_player.helpers.ExoPlayerHelper
import ru.tensor.sbis.design.media_player.helpers.MediaPlayerInfo
import ru.tensor.sbis.design.media_player.helpers.MediaPlayerStateHelper
import ru.tensor.sbis.design.media_player.helpers.ProgressTimerHelper
import ru.tensor.sbis.design.media_player.helpers.UIThreadHelper
import ru.tensor.sbis.design.media_player.helpers.WakeLockHelper
import ru.tensor.sbis.network_native.apiservice.contract.ApiService
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import ru.tensor.sbis.verification_decl.login.LoginInterface
import java.util.*

@UnstableApi
/**
 * Реаоизация проигрывателя для аудио и видео.
 */
internal class MediaPlayerImpl(
    private val appContext: Context,
    loginInterface: LoginInterface,
    apiService: ApiService,
) : MediaPlayer {

    internal constructor(
        appContext: Context,
        loginInterface: LoginInterface,
        apiService: ApiService,
        exoPlayerHelper: ExoPlayerHelper,
        mediaPlayerStateHelper: MediaPlayerStateHelper,
        mediaPlayerInfo: MediaPlayerInfo,
        wakeLockHelper: WakeLockHelper,
        uiThreadHelper: UIThreadHelper,
        progressTimerHelper: ProgressTimerHelper,
        callStateProvider: FeatureProvider<CallStateProvider>?
    ) : this(appContext, loginInterface, apiService) {
        this.exoPlayerHelper = exoPlayerHelper
        this.mediaPlayerStateHelper = mediaPlayerStateHelper
        this.mediaPlayerInfo = mediaPlayerInfo
        this.wakeLockHelper = wakeLockHelper
        this.uiThreadHelper = uiThreadHelper
        this.progressTimerHelper = progressTimerHelper
        this.callStateProvider = callStateProvider
    }

    private var mediaPlayerStateHelper: MediaPlayerStateHelper = MediaPlayerStateHelper()
    private var exoPlayerHelper: ExoPlayerHelper = ExoPlayerHelper(
        mediaPlayerStateHelper,
        loginInterface,
        apiService,
        appContext
    )
    private var mediaPlayerInfo: MediaPlayerInfo = MediaPlayerInfo()
    private var wakeLockHelper: WakeLockHelper = WakeLockHelper(appContext)
    private var uiThreadHelper: UIThreadHelper = UIThreadHelper(appContext)
    private var progressTimerHelper: ProgressTimerHelper = ProgressTimerHelper()
    private var callStateProvider: FeatureProvider<CallStateProvider>? = providerCallState

    /**@SelfDocumented*/
    private val currentSubscription: BehaviorSubject<MediaInfo>?
        get() = mediaPlayerStateHelper.currentSubscription

    private val player: ExoPlayer
        get() = exoPlayerHelper.player

    override var audioRouteChangeListener: AudioRouteChangeListener? = null

    override var isEnabled: Boolean = true
        set(value) {
            field = value
            if (!value) stop()
        }

    override var isPreparing: Boolean = false

    private var needInitHelpers: Boolean = true
    private val isCallRunning
        get() = callStateProvider?.get()?.isCallRunning() ?: false

    override fun setUriResolver(resolver: UriResolver?) {
        exoPlayerHelper.setUriResolver(resolver)
    }

    override fun setMediaInfo(mediaInfo: MediaInfo, playWhenReady: Boolean) {
        uiThreadHelper.ensureMainThread()
        if (needInitHelpers) initHelpers()
        val currentInfo = mediaPlayerInfo.currentMediaInfo
        if (currentInfo != null) {
            if (mediaInfo.mediaSource == mediaPlayerInfo.currentMediaInfo?.mediaSource) {
                // уже проигрывается или проигрывание приостановлено / завершено
                return
            } else {
                player.stop()
                stop()
            }
        }

        mediaPlayerStateHelper.currentSubscription?.onComplete()
        if (!isCallRunning) {
            mediaPlayerInfo.currentMediaInfo = mediaInfo
            mediaPlayerStateHelper.currentSubscription = BehaviorSubject.createDefault(mediaInfo)
            exoPlayerHelper.prepare(
                mediaInfo,
                playWhenReady,
                ::setPlaybackSpeed
            )
        }
    }

    override fun playingState(): Observable<MediaInfo>? =
        currentSubscription

    override fun getMediaInfo(): MediaInfo? =
        mediaPlayerInfo.currentMediaInfo

    override fun play() {
        isPreparing = false
        if (!checkPlayingAvailability()) return
        uiThreadHelper.ensureMainThread()
        mediaPlayerStateHelper.updateMediaState { state = State.PLAYING }
        player.playWhenReady = true
        progressTimerHelper.startProgressTimer()
        wakeLockHelper.requestWakeLock(request = true)
    }

    override fun pause() {
        isPreparing = false
        getMediaInfo() ?: return
        uiThreadHelper.ensureMainThread()
        mediaPlayerStateHelper.updateMediaState {
            state = State.PAUSED
            waitingActualProgress = false
        }
        player.playWhenReady = false
        progressTimerHelper.stopProgressTimer()
        wakeLockHelper.requestWakeLock(request = false)
    }

    override fun stop() {
        isPreparing = false
        getMediaInfo() ?: return
        uiThreadHelper.ensureMainThread()
        progressTimerHelper.stopProgressTimer()
        mediaPlayerStateHelper.updateMediaState {
            position = 0
            state = State.DEFAULT
            waitingActualProgress = false
        }
        player.playWhenReady = false
        player.seekTo(0)
        wakeLockHelper.requestWakeLock(request = false)
    }

    override fun setPosition(position: Long) {
        mediaPlayerStateHelper.updateMediaState { this.position = position }
        player.seekTo(position)
    }

    override fun seekToProgress(progress: Float, isSourceChanged: Boolean) {
        val duration = player.duration
        if (!isSourceChanged && duration != C.TIME_UNSET) {
            setPosition((duration * progress).toLong())
        } else {
            mediaPlayerInfo.seekToProgressPending = progress
            mediaPlayerInfo.currentMediaInfo?.waitingActualProgress = progress > 0
        }
    }

    override fun setVideoTextureView(texture: TextureView?) {
        player.setVideoTextureView(null)
        player.setVideoTextureView(texture)
    }

    override fun setPlaybackSpeed(speed: PlaybackSpeed) {
        player.playbackParameters = PlaybackParameters(speed.value)
        mediaPlayerStateHelper.updateMediaState { playbackSpeed = speed }
    }

    override fun checkPlayingAvailability(mediaInfo: MediaInfo?): Boolean {
        val currentMediaInfo = mediaInfo ?: getMediaInfo()
        return currentMediaInfo != null && isEnabled && !checkCallRunning(currentMediaInfo)
    }

    override fun release() {
        needInitHelpers = true
        if (mediaPlayerInfo.isPlayingActive) stop()
        progressTimerHelper.release()
        mediaPlayerInfo.clear()
        exoPlayerHelper.release()
        mediaPlayerStateHelper.release()
        isPreparing = false
        wakeLockHelper.requestWakeLock(request = false)
    }

    override fun isPlayingActive(): Boolean {
        return mediaPlayerInfo.isPlayingActive
    }

    override fun setPlayingListener(listener: PlayingStateListener?) {
        mediaPlayerStateHelper.setPlayingListener(listener)
    }

    override fun changeAudioRoute(useFrontSpeaker: Boolean) {
        player.audioAttributes.let {
            player.setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(C.USAGE_VOICE_COMMUNICATION)
                    .setFlags(it.flags)
                    .setAllowedCapturePolicy(it.allowedCapturePolicy)
                    .setSpatializationBehavior(it.spatializationBehavior)
                    .setContentType(if (useFrontSpeaker) C.AUDIO_CONTENT_TYPE_SPEECH else C.AUDIO_CONTENT_TYPE_MUSIC)
                    .build(),
                false
            )
        }
        audioRouteChangeListener?.onAudioRouteChanged(useFrontSpeaker)
    }

    private fun initHelpers() {
        needInitHelpers = false
        exoPlayerHelper.init(::onError)
        mediaPlayerStateHelper.init(player, this, mediaPlayerInfo, ::onError)
        progressTimerHelper.init(mediaPlayerInfo, mediaPlayerStateHelper, player, uiThreadHelper)
    }

    private fun checkCallRunning(mediaInfo: MediaInfo): Boolean {
        if (isCallRunning) {
            val isVideo = mediaInfo.mediaSource is VideoSource
            val message = appContext.getString(
                if (isVideo) R.string.design_media_player_play_video_error
                else R.string.design_media_player_play_audio_error
            )
            mediaInfo.playbackError = Throwable(message)
            return true
        }
        return false
    }

    @VisibleForTesting
    internal fun onError(error: Throwable?) {
        val currentInfo: MediaInfo = mediaPlayerInfo.currentMediaInfo ?: return
        // Не показываем ошибку когда аудиодорожка закончилось раньше видео (актуально для видеосообщений с веба)
        @Suppress("DEPRECATION")
        val skipError = (error?.cause as? DataSourceException)?.reason == DataSourceException.POSITION_OUT_OF_RANGE
        if (!skipError) {
            error?.let { currentInfo.playbackError = error }
        }
        stop()
        currentSubscription?.onComplete()
        mediaPlayerInfo.currentMediaInfo = null
    }
}