package ru.tensor.sbis.mediaplayer

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.ErrorMessageProvider
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import androidx.media3.exoplayer.*
import androidx.media3.exoplayer.mediacodec.MediaCodecSelector
import androidx.media3.exoplayer.source.BehindLiveWindowException
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.UnrecognizedInputFormatException
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.ui.PlayerView
import ru.tensor.sbis.mediaplayer.datasource.MediaSourceFactory
import timber.log.Timber
import kotlin.math.max

private const val MEDIA_INFO_SAVE_KEY = "media_info_save_key"
private const val START_WINDOW_SAVE_KEY = "start_window_save_key"
private const val START_POSITION_SAVE_KEY = "start_position_save_key"
private const val AUTO_PLAY_SAVE_KEY = "auto_play_save_key"
private const val SPEED_SAVE_KEY = "speed_save_key"

private const val DEFAULT_SPEED = 1.0f
private const val MIN_SPEED = 0.1F

@UnstableApi
/**
 * Посредник, обеспечивающий взаимодействие компонентов медиаплеера
 *
 * Плеер начнёт воспроизведение только при следующих условиях (порядок не важен):
 * 1. Установлена вью, см. [setPlayerView]
 * 2. Установлено медиа, см. [setMediaInfo]
 * 3. Activity/Fragment, где используется посредник, в состоянии resumed/started (зависит от версии API).
 * См. [onResume], [onStart], [onPause], [onStop]
 * Либо вызвать этим методы вручную, либо добавить посредника как [LifecycleObserver], не забыть удалить
 *
 * Если посредник уничтожается вместе с Activity/Fragment, то имеет смысл использовать методы [saveState], [restoreState]
 * Если состояние было сохранено, то следует вызвать [restoreState] и не вызывать [setMediaInfo]
 * Посредник сам восстановит медиа, состояние (play, pause) и позицию воспроизведения (скорость не восстановится)
 *
 * При окончании использования необходимо вызвать [release]
 *
 * @param context Любой контекст
 *
 * @property mediaSourceFactory     Фабрика [MediaSource]. Требует освобождения после использования, см. [release]
 * @property errorMessageProvider   Провайдер текста ошибки
 * @property defaultAutoPlay        Состояние воспроизведения по умолчанию. Если true, то при установке медиа [setMediaInfo],
 *                                  плеер сразу начнёт воспроизведение
 * @property hasBackgroundPlay      Флаг, указывающий на возможность фонового воспроизведения. Если true, то в рамках
 *                                  onPause-onDestroy (или onStop-onDestroy, зависит от API) активити с проигрывателем
 *                                  будет происходить фоновое воспроизведение
 * @property id                     Уникальный идентификатор
 *                                  Требуется для сохранения/восстановления состояния, если на экране несколько плееров
 */
open class MediaPlayerMediator<PV : PlayerView>(
    context: Context,
    protected val mediaSourceFactory: MediaSourceFactory,
    protected val errorMessageProvider: ErrorMessageProvider<PlaybackException>,
    protected val defaultAutoPlay: Boolean = true,
    val hasBackgroundPlay: Boolean = true,
    private val id: String = ""
) :
    LifecycleObserver,
    Player.Listener {

    var playerView: PV? = null
        private set
    var player: ExoPlayer? = null
        private set
    var mediaInfo: MediaInfo? = null
        private set
    var autoPlay: Boolean = defaultAutoPlay
        set(value) {
            if (field != value) {
                field = value
                player?.playWhenReady = value
            }
        }
    var handleAudioFocus: Boolean = true
        set(value) {
            if (field != value) {
                field = value
                updateAudioAttributes()
            }
        }

    private val appContext: Context = context.applicationContext

    //Активность/фрагмент находятся на переднем плане
    //Определяется как отработавший onStart/onResume (зависит от версии api)
    //Если на переднем плане, значит можно начать воспроизведение
    private var isForeground: Boolean = false

    private var startWindowIndex: Int = 0
    private var startPositionMs: Long = 0
    private var speed: Float = DEFAULT_SPEED

    /**
     * Установить вью плеера
     * Если передать null, то вью будет сброшена, ресурсы освобождены
     * Если посредник переживает Activity/Fragment, то вью следует сбросить
     * Вью сбрасывается в методе [release]
     */
    open fun setPlayerView(playerView: PV?) {
        val currentPlayerView: PV? = this.playerView
        val player: Player? = this.player
        if (playerView != null) {
            playerView.setErrorMessageProvider(errorMessageProvider)
            playerView.requestFocus()
            if (player != null) {
                if (currentPlayerView != null) {
                    PlayerView.switchTargetView(player, currentPlayerView, playerView)
                } else {
                    playerView.player = player
                }
            } else {
                initializePlayer()
            }
        } else {
            //Меняем playWhenReady только у плеера, свойство autoPlay не обновляем
            //Так, при следующей установке playerView воспроизведение продолжится, если было активно
            player?.playWhenReady = false
            currentPlayerView?.player = null
        }
        this.playerView = playerView
    }

    /**
     * Установить медиа
     * Если передать null, то плеер будет освобождён, однако повторное использование возможно, следует передать не null медиа
     */
    fun setMediaInfo(mediaInfo: MediaInfo?) {
        if (this.mediaInfo != mediaInfo) {
            this.mediaInfo = mediaInfo
            resetPlaybackParams()
            setInitialPlaybackParams()
            if (mediaInfo != null) {
                initializePlayer()
            } else {
                releasePlayer()
            }
        }
    }

    /**
     * Установить скорость воспроизведения
     */
    open fun setPlaybackSpeed(speed: Float) {
        player?.setPlaybackSpeed(speed)
    }

    /**
     * Восстановить состояние посредника
     */
    open fun restoreState(savedState: Bundle) {
        //Восстановление медиа обязательно до восстановления позиций, т.к. внутри они сбрасываются
        val mediaInfo: MediaInfo? = savedState.getParcelable(saveKey(MEDIA_INFO_SAVE_KEY))
        if (mediaInfo != null) {
            setMediaInfo(mediaInfo)
        }
        startWindowIndex = savedState.getInt(saveKey(START_WINDOW_SAVE_KEY), 0)
        startPositionMs = savedState.getLong(saveKey(START_POSITION_SAVE_KEY), 0)
        speed = savedState.getFloat(saveKey(SPEED_SAVE_KEY), DEFAULT_SPEED)
        autoPlay = savedState.getBoolean(saveKey(AUTO_PLAY_SAVE_KEY), defaultAutoPlay)
        player?.let { player ->
            player.seekTo(startWindowIndex, startPositionMs)
            player.setPlaybackSpeed(speed)
            player.playWhenReady = autoPlay
        }
    }

    /**
     * Сохранить состояние
     */
    open fun saveState(outState: Bundle) {
        outState.putParcelable(saveKey(MEDIA_INFO_SAVE_KEY), mediaInfo)
        updatePlaybackParams()
        outState.putInt(saveKey(START_WINDOW_SAVE_KEY), startWindowIndex)
        outState.putLong(saveKey(START_POSITION_SAVE_KEY), startPositionMs)
        outState.putFloat(saveKey(SPEED_SAVE_KEY), speed)
        outState.putBoolean(saveKey(AUTO_PLAY_SAVE_KEY), autoPlay)
    }

    private fun saveKey(suffix: String): String = id + suffix

    /**
     * Освободить посредника
     * Вызывать в конце жизненного цикла объекта, в котором используется посредник, например, onDestroy у Activity
     * После вызова не следует использовать посредник
     */
    open fun release() {
        setPlayerView(null)
        mediaSourceFactory.release()
        releasePlayer()
    }

    /**
     * Метод позволяет переопределить переключателя, который возвращает предпочтительные декодеры для входного формата.
     */
    open fun getMediaCodecSelector() = MediaCodecSelector.DEFAULT

    @Suppress("MemberVisibilityCanBePrivate")
    protected fun initializePlayer() {
        if (!isForeground) return
        val playerView: PV = playerView ?: return
        val mediaSource: MediaSource = createMediaSource(playerView) ?: return
        val player: ExoPlayer = getOrCreatePlayer()
        val haveStartPosition = startWindowIndex != C.INDEX_UNSET
        player.setMediaSource(mediaSource, !haveStartPosition)
        player.prepare()
        if (haveStartPosition) {
            player.seekTo(startWindowIndex, startPositionMs)
        } else {
            player.seekToDefaultPosition()
        }
        playerView.setCustomErrorMessage(null)
        playerView.player = player
    }

    private fun createMediaSource(playerView: PV): MediaSource? = mediaInfo?.let {
        val mediaType: Int = if (it.isHls) {
            C.CONTENT_TYPE_HLS
        } else {
            Util.inferContentType(it.uri)
        }
        if (mediaType in mediaSourceFactory.supportedTypes) {
            mediaSourceFactory.createMediaSource(it)
        } else {
            val rawErrorMessage = "Unsupported media type $mediaType. Info: $it"
            val exception = UnrecognizedInputFormatException(rawErrorMessage, it.uri)
            val playbackException = ExoPlaybackException.createForSource(exception, 0)
            Timber.e(rawErrorMessage)
            playerView.setCustomErrorMessage(errorMessageProvider.getErrorMessage(playbackException).second)
            null
        }
    }

    private fun getOrCreatePlayer(): ExoPlayer =
        player.let { player ->
            if (player == null) {
                val newPlayer: ExoPlayer = createPlayer()
                this.player = newPlayer
                newPlayer
            } else {
                player
            }
        }

    private fun createPlayer(): ExoPlayer =
        ExoPlayer.Builder(
            appContext,
            DefaultRenderersFactory(appContext)
                .setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER)
                .setMediaCodecSelector(getMediaCodecSelector())
        ).apply {
            val trackSelector = DefaultTrackSelector(appContext)
            val isSamsung = Build.MANUFACTURER.equals("samsung", true)
            // Возможное решение ошибки: https://github.com/google/ExoPlayer/issues/10684
            // попробовать убрать после миграции на compileSdkVersion = 33
            if (isSamsung && Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
                trackSelector.parameters = trackSelector.buildUponParameters()
                    .setConstrainAudioChannelCountToDeviceCapabilities(false).build()
            }
            setTrackSelector(trackSelector)
            preBuildPlayer(this)
        }
            .build()
            .also { player ->
                player.setPlaybackSpeed(speed)
                player.playWhenReady = autoPlay
                addListeners(player)
                updateAudioAttributes()
            }

    @CallSuper
    open fun addListeners(player: ExoPlayer) {
        player.addListener(this)
    }

    @CallSuper
    open fun removeListeners(player: ExoPlayer) {
        player.removeListener(this)
    }

    /** Метод донастройки создаваемого экземпляра плеера [ExoPlayer] через [initializePlayer] */
    protected open fun preBuildPlayer(builder: ExoPlayer.Builder) = Unit

    @Suppress("MemberVisibilityCanBePrivate")
    protected fun updateAudioAttributes() {
        player?.setAudioAttributes(buildAudioAttributes(), handleAudioFocus)
    }

    protected open fun buildAudioAttributes(): AudioAttributes =
        AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
            .build()

    /** Сбросить параметры воспроизведения. */
    private fun resetPlaybackParams() {
        startWindowIndex = C.INDEX_UNSET
        startPositionMs = C.TIME_UNSET
        autoPlay = defaultAutoPlay
    }

    /** Установить начальные параметры воспроизведения для нового источника медиа. */
    private fun setInitialPlaybackParams() = mediaInfo?.let { info ->
        if (info.startPositionMs != C.TIME_UNSET) {
            startPositionMs = info.startPositionMs
        }
        if (startPositionMs > 0) {
            startWindowIndex = 0
        }
    }

    /** Обновить/сохранить параметры воспроизведения по текущему состоянию плеера. */
    private fun updatePlaybackParams() {
        player?.let { player ->
            startWindowIndex = player.currentMediaItemIndex
            startPositionMs = max(0, player.contentPosition)
            speed = player.playbackParameters.speed
            autoPlay = player.playWhenReady
        }
    }

    @Suppress("MemberVisibilityCanBePrivate")
    protected fun releasePlayer() {
        player?.let { player ->
            updatePlaybackParams()
            player.playWhenReady = false
            removeListeners(player)
            player.release()
            playerView?.player = null
            this.player = null
        }
    }

    /*
     * Инициализация плеера и воспроизведение медиа в onStart для API версии 24 и выше
     * В 24 версии API добавлена многооконность, поэтому инициализация плеера происходит в onStart
     * Подробнее: https://developer.android.com/guide/topics/ui/multi-window.html
     */
    @Suppress("unused")
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        if (isAtLeastNougatAPI()) {
            onGetFocus()
        }
    }

    /*
     * Инициализация плеера и воспроизведение медиа в onResume для API версии 23 и ниже
     * До 24 версии API необходимо ждать инициализации системных ресурсов для инициализации плеера
     */
    @Suppress("unused")
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        if (isBelowNougatAPI() || player == null) {
            onGetFocus()
        }
    }

    /*
     * Пауза воспроизведения и освобождение ресурсов плеера в onPause для API версии 23 и ниже
     * До 24 версии API нет гарантии вызова onStop, плюс, вызов onPause до 24 версии API гарантирует,
     * что активность частично скрыта чем-то другим, поэтому необходимо остановить воспроизведение и
     * освободить ресурсы плеера именно в onPause
     */
    @Suppress("unused")
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        if (isBelowNougatAPI()) {
            onLostFocus()
        }
    }

    /*
     * Пауза воспроизведения и освобождение ресурсов плеера в onStop для API версии 24 и выше
     * В 24 версии API добавлена многооконность, onPause не гарантирует, что активность больше не видна,
     * поэтому пауза воспроизведения и освобождение ресурсов плеера происходит именно в onStop
     * Подробнее: https://developer.android.com/guide/topics/ui/multi-window.html
     */
    @Suppress("unused")
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        if (isAtLeastNougatAPI()) {
            onLostFocus()
        }
    }

    private fun isAtLeastNougatAPI(): Boolean = Util.SDK_INT >= Build.VERSION_CODES.N

    private fun isBelowNougatAPI(): Boolean = !isAtLeastNougatAPI()

    private fun onGetFocus() {
        isForeground = true
        if (hasBackgroundPlay && playerView?.player != null) return
        initializePlayer()
        playerView?.onResume()
    }

    private fun onLostFocus() {
        isForeground = false
        if (hasBackgroundPlay && player?.isPlaying == true) return
        releasePlayer()
        playerView?.onPause()
    }

    //region Player.EventListener
    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        playerView?.keepScreenOn =
            playbackState != Player.STATE_IDLE && playbackState != Player.STATE_ENDED && playWhenReady
    }

    override fun onPositionDiscontinuity(@Player.DiscontinuityReason reason: Int) {
        if (player?.playerError != null) {
            //Пользователь совершил перемотку в состоянии ошибки
            //Требуется сохранить позицию, чтобы воспроизведение продолжилось с нужной позиции
            updatePlaybackParams()
        }
    }

    override fun onPlayerError(error: PlaybackException) {
        if (error is ExoPlaybackException && isBehindLiveWindow(error)) {
            resetPlaybackParams()
            initializePlayer()
        } else {
            updatePlaybackParams()
        }
    }

    //Побробнее: https://github.com/google/ExoPlayer/issues/1074
    private fun isBehindLiveWindow(e: ExoPlaybackException): Boolean {
        if (e.type != ExoPlaybackException.TYPE_SOURCE) {
            return false
        }
        var cause: Throwable? = e.sourceException
        while (cause != null) {
            if (cause is BehindLiveWindowException) {
                return true
            }
            cause = cause.cause
        }
        return false
    }
    //endregion
}