package ru.tensor.sbis.design.video_message_view.player.children

import android.content.Context
import android.graphics.Matrix
import android.os.SystemClock
import android.util.AttributeSet
import android.util.Size
import android.view.Gravity.CENTER
import android.view.MotionEvent
import android.view.TextureView
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.media3.common.C
import androidx.media3.ui.TimeBar
import io.reactivex.disposables.Disposable
import ru.tensor.sbis.communication_decl.communicator.media.MediaPlayer
import ru.tensor.sbis.communication_decl.communicator.media.data.MediaInfo
import ru.tensor.sbis.communication_decl.communicator.media.data.MediaSource
import ru.tensor.sbis.communication_decl.communicator.media.data.State
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.video_message_view.R
import ru.tensor.sbis.design.video_message_view.VideoPlayerViewPlugin
import ru.tensor.sbis.design.video_message_view.player.data.VideoPlayerViewData
import java.util.concurrent.TimeUnit
import kotlin.math.max

/**
 * Вью для отображения контроллера управления круглого проигрывателя.
 *
 * @author da.zhukov
 */
internal class VideoPlayerControlView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes),
    View.OnClickListener,
    TimeBar.OnScrubListener {

    /**
     * Текущая вью для проигрывания видео видео
     */
    private lateinit var textureView: TextureView

    /**
     * Проигрыватель видео
     */
    private var mediaPlayer: MediaPlayer? = null

    /**
     * Слушатель изменения состояния проигрывания.
     */
    private lateinit var stateListener: StateListener

    /**
     * Текущее состояние проигрывания.
     */
    private var currentState = State.DEFAULT

    /**
     * Иконка начала проигрывания
     */
    private val playIcon = SbisTextView(context, null, 0, R.style.VideoMessagePlayIcon).apply {
        alpha = 0.6f
    }

    /**
     * Затемнение до начала проигрывания и во время паузы
     */
    private val blackout = View(context).apply {
        alpha = 0.2f
        setBackgroundColor(ContextCompat.getColor(context, ru.tensor.sbis.design.R.color.palette_color_black1))
    }

    /**
     * Таймбар для отображения прогресса проигрывания видео
     */
    private val timeBar = RoundTimeBar(context, attrs, defStyleAttr, defStyleRes)

    /**
     * Текущее состояние медиа плеера.
     */
    private var mediaInfo: MediaInfo? = null

    /**
     * Размер видео.
     */
    private var videoSize: Size? = null

    /**
     * Отрисован ли первый кадр.
     */
    private var firstFrameRendered = false

    /**
     * Требуется первый кадр.
     */
    private var requiredFirstFrame: Boolean = false

    /**
     * Подписка на изменение состояния воспроизведения видео.
     */
    private var playingSubscription: Disposable? = null

    /**
     * Приклеплен к окну.
     */
    private var attachedToWindow = false

    /**
     * Перемотка видеосообщения.
     */
    private var scrubbing = false

    /**
     * Вермя через которое автоматически скрывается управление видеосообщения.
     */
    private var hideAtMs: Long = C.TIME_UNSET

    /**
     * Действие скрытия управления видеосообщения.
     */
    private var hideAction = Runnable(::hide)

    /**
     * Слушатель нажатие на сообщение(проигрывание не должно начинаться)
     * Используется в ленте по задаче.
     */
    private var messageClickListener: OnClickListener? = null

    /**
     * Задать данные для проигрывания.
     * @see VideoPlayerViewData
     */
    var data: VideoPlayerViewData? = null
        set(value) {
            field = value
            value?.apply {
                setVideoSource(videoSource)
                setDuration(durationSeconds)
            } ?: clearState()
        }

    init {
        addView(blackout, LayoutParams(MATCH_PARENT, MATCH_PARENT, CENTER))
        addView(playIcon, LayoutParams(WRAP_CONTENT, WRAP_CONTENT, CENTER))
        addView(timeBar, LayoutParams(WRAP_CONTENT, WRAP_CONTENT, CENTER))
        initListeners()
        setMediaPlayer(VideoPlayerViewPlugin.defaultMediaPlayer)
    }

    /**
     * Подготовить первый кадр.
     */
    fun prepareFirstFrame() {
        mediaPlayer?.setVideoTextureView(textureView)
        updatePlayerMediaInfo(playWhenReady = true)
        requiredFirstFrame = true
    }

    /**
     * Инициализация слушателей.
     */
    private fun initListeners() {
        setOnClickListener(this)
        timeBar.addListener(this)
    }

    /**
     * Установить данные видео.
     */
    private fun setVideoSource(videoSource: MediaSource.VideoSource) {
        if (mediaInfo?.mediaSource != videoSource) {
            clearSubscription()
        }
        mediaInfo = MediaInfo(mediaSource = videoSource, duration = (data?.durationSeconds ?: 0) * 1000L)
        checkAlreadyPlaying(mediaInfo!!)
        videoSize = mediaInfo!!.videoSize
        firstFrameRendered = mediaInfo!!.firstVideoFrameRendered
        timeBar.seekEnabled = mediaInfo!!.canSeek ?: true
        timeBar.changeState(currentState, animated = false)
    }

    /**
     * Очистить подписки.
     */
    private fun clearSubscription() {
        playingSubscription?.dispose()
        playingSubscription = null
    }

    /**
     * Обновить медиа источник проигрывателя.
     */
    private fun updatePlayerMediaInfo(playWhenReady: Boolean = false) {
        mediaPlayer?.setMediaInfo(mediaInfo!!, playWhenReady = playWhenReady) ?: return
        subscribeToMediaPlayer()
        updateAll()
    }

    /**
     * Является ли тикущее видео проигрываевым.
     */
    private fun checkAlreadyPlaying(mediaInfo: MediaInfo) {
        val mediaPlayer = this.mediaPlayer ?: return
        val currentPlaying = mediaPlayer.getMediaInfo()?.takeIf { mediaInfo.mediaSource == it.mediaSource }
        val resultMediaInfo = if (currentPlaying != null) {
            subscribeToMediaPlayer()
            mediaPlayer.setVideoTextureView(textureView)
            currentPlaying.also(this::mediaInfo::set)
        } else {
            currentState = mediaInfo.state
            mediaInfo
        }
        updateState(resultMediaInfo)
    }

    /**
     * Подписаться на события проигрывателя видео.
     */
    private fun subscribeToMediaPlayer() {
        val playingState = mediaPlayer?.playingState() ?: return
        clearSubscription()
        playingSubscription = playingState
            .doAfterTerminate { clearSubscription() }
            .subscribe { updateState(it) }
    }

    /**
     * Установить длительность видеосообщения.
     */
    private fun setDuration(durationSeconds: Int) {
        stateListener.onDurationChange(durationSeconds)
    }

    /**
     * Установить оставшуюся длительность видеосообщения.
     */
    private fun setRemainingDuration(currentPosition: Long) {
        val duration = data?.durationSeconds?.let { duration ->
            duration - TimeUnit.MILLISECONDS.toSeconds(currentPosition).toInt()
        } ?: 0
        setDuration(duration.coerceAtLeast(0))
    }

    /**
     * Задать плеер, который будет использоваться для проигрывания
     */
    fun setMediaPlayer(mediaPlayer: MediaPlayer) {
        if (mediaPlayer === this.mediaPlayer) return
        this.mediaPlayer = mediaPlayer
        mediaInfo?.let { checkAlreadyPlaying(it) }
        updateBlurAndPlayButtonVisibility()
    }

    /**
     * Установить текущую вью для воспроизведения видео.
     */
    fun setTextureView(textureView: TextureView) {
        this.textureView = textureView
    }

    /**
     * Установить слушатель изменения состояния проигрывания.
     */
    fun setStateListener(listener: StateListener) {
        stateListener = listener
    }

    /**
     * Задаёт обработчик нажатий на сообщение
     */
    fun setOnMessageClickListener(onMessageClickListener: OnClickListener?) {
        messageClickListener = onMessageClickListener
    }

    /**
     * Показывает элементы управления воспроизведением.
     */
    fun show() {
        if (!isVisible()) {
            visibility = VISIBLE
            updateAll()
        }
    }

    /**
     * Скрывает контроллер.
     */
    fun hide() {
        if (isVisible()) {
            visibility = INVISIBLE
            removeCallbacks(hideAction)
            hideAtMs = C.TIME_UNSET
        }
    }

    /**
     * Видимость управления видеосообщением.
     */
    fun isVisible(): Boolean {
        return visibility == VISIBLE
    }

    private fun updateAll() {
        updateBlurAndPlayButtonVisibility()
        updateTimeline()
    }

    /**
     * Обновить видимость блюра и кнопки воспроизведения видеосообщения.
     */
    private fun updateBlurAndPlayButtonVisibility() {
        if (!isVisible() || !attachedToWindow) return
        val shouldHidePlayIcon = shouldHidePlayIcon()
        playIcon.visibility = if (shouldHidePlayIcon) GONE else VISIBLE
        blackout.visibility = if (playIcon.isVisible) VISIBLE else GONE
    }

    /**
     * Нужно ли скрыть кнопку начала проигрывания.
     */
    private fun shouldHidePlayIcon(): Boolean {
        mediaInfo ?: return false
        return mediaInfo?.state == State.PLAYING
    }

    /**
     * Обновить длительность видеосообщения.
     */
    private fun updateTimeline() {
        mediaInfo ?: return
        timeBar.setDuration(mediaInfo!!.duration)
    }

    /**
     * Обновить состояние прогресса видеосообщения.
     */
    private fun updateState(mediaInfo: MediaInfo) {
        if (!isVisible()) return
        timeBar.setDuration(mediaInfo.duration)
        timeBar.setPosition(mediaInfo.position)
        this.mediaInfo!!.state = mediaInfo.state
        if (videoSize != mediaInfo.videoSize) {
            mediaInfo.videoSize?.let {
                if (it.width != 0 && it.height != 0 && updateScale(it)) { videoSize = it }
            }
        }
        if (currentState != mediaInfo.state) {
            currentState = mediaInfo.state
            stateListener.onStateChange(mediaInfo.state, firstFrameRendered)
            timeBar.changeState(currentState, animated = false)
            updateBlurAndPlayButtonVisibility()
        }
        if (!firstFrameRendered && mediaInfo.firstVideoFrameRendered) {
            firstFrameRendered = true
            if (requiredFirstFrame) {
                postDelayed({ mediaPlayer?.stop() }, 50)
                // Задержка необходима, чтобы exoplayer успел избавиться от последнего кадра предыдущего видео.
                postDelayed({ stateListener.onFirstVideoFrameRendered(currentState) }, 150)
                requiredFirstFrame = false
            } else {
                // Чтобы успел отработать updateScale
                postDelayed({ stateListener.onFirstVideoFrameRendered(currentState) }, 100)
            }
        }
        mediaInfo.canSeek?.let { canSeek -> if (timeBar.seekEnabled != canSeek) timeBar.seekEnabled = canSeek }
        if (mediaInfo.state != State.DEFAULT) {
            if (mediaInfo.duration > 0) {
                setRemainingDuration(mediaInfo.position)
            }
        } else {
            setDuration(data!!.durationSeconds)
        }
        checkPlaybackError(mediaInfo)

        val buffering = mediaInfo.state == State.PLAYING && mediaInfo.buffering
        stateListener.updateBufferingState(buffering)
    }

    private fun checkPlaybackError(mediaInfo: MediaInfo) {
        mediaInfo.playbackError?.let {
            stateListener.onVideoPlaybackError(it)
            mediaInfo.playbackError = null
        }
    }

    /**
     * Обновить матрицу видеосообщения.
     */
    private fun updateScale(size: Size): Boolean {
        val textureWidth = textureView.measuredWidth.toFloat()
        val textureHeight = textureView.measuredHeight.toFloat()
        if (textureWidth == 0f || textureHeight == 0f) return false

        var scaleX = textureWidth / size.width
        var scaleY = textureHeight / size.height
        val maxScale = max(scaleX, scaleY)
        scaleX = maxScale / scaleX
        scaleY = maxScale / scaleY
        val pivotX = textureWidth / 2f
        val pivotY = textureHeight / 2f
        val matrix = Matrix().apply { setScale(scaleX, scaleY, pivotX, pivotY) }
        textureView.setTransform(matrix)
        return true
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        attachedToWindow = true
        if (hideAtMs != C.TIME_UNSET) {
            val delayMs = hideAtMs - SystemClock.uptimeMillis()
            if (delayMs <= 0) {
                hide()
            } else {
                postDelayed(hideAction, delayMs)
            }
        }
        updateAll()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        attachedToWindow = false
        removeCallbacks(hideAction)
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) removeCallbacks(hideAction)
        return super.dispatchTouchEvent(ev)
    }

    /**
     * Очистить состояние.
     */
    fun clearState() {
        if (mediaInfo != null && mediaPlayer?.getMediaInfo()?.mediaSource == mediaInfo?.mediaSource) {
            mediaPlayer?.stop()
        }
        mediaInfo = null
        clearSubscription()
    }

    override fun onClick(view: View) {
        val mediaPlayer = this.mediaPlayer
        if (mediaPlayer == null) {
            messageClickListener?.onClick(view)
            return
        }
        when {
            playingSubscription == null -> {
                if (!checkPlayingAvailability(mediaInfo)) return
                mediaPlayer.setVideoTextureView(textureView)
                updatePlayerMediaInfo(playWhenReady = true)
                mediaPlayer.isPreparing = true
                stateListener.showPlayClickAnimation {
                    mediaPlayer.play()
                    updateBlurAndPlayButtonVisibility()
                }
            }
            mediaInfo!!.state == State.PLAYING -> {
                timeBar.changeState(State.PAUSED)
                mediaPlayer.pause()
                updateBlurAndPlayButtonVisibility()
            }
            mediaInfo!!.state == State.PAUSED -> {
                timeBar.changeState(State.PLAYING)
                mediaPlayer.play()
                updateBlurAndPlayButtonVisibility()
            }
            mediaInfo!!.state == State.DEFAULT -> {
                if (!checkPlayingAvailability(mediaInfo)) return
                mediaPlayer.isPreparing = true
                stateListener.showPlayClickAnimation {
                    mediaPlayer.play()
                    updateBlurAndPlayButtonVisibility()
                }
            }
        }
    }

    private fun checkPlayingAvailability(info: MediaInfo?): Boolean {
        val mediaPlayer = mediaPlayer ?: return false
        val mediaInfo = info ?: return false

        val isAvailable = mediaPlayer.checkPlayingAvailability(mediaInfo)
        if (!isAvailable && mediaInfo.playbackError != null) {
            checkPlaybackError(mediaInfo)
        }
        return isAvailable
    }

    override fun onScrubStart(timeBar: TimeBar, position: Long) {
        scrubbing = true
    }

    override fun onScrubMove(timeBar: TimeBar, position: Long) {
        setRemainingDuration(position)
    }

    override fun onScrubStop(timeBar: TimeBar, position: Long, canceled: Boolean) {
        scrubbing = false
        if (!canceled && mediaPlayer != null) {
            mediaPlayer?.setPosition(position)
            this.timeBar.changeState(state = State.PLAYING)
            mediaPlayer?.play()
        }
    }
}

/**
 * Слушатель изменения состояния проигрывания.
 *
 * @author da.zhukov
 */
interface StateListener {
    /**
     * Состояние воспроизведения изменилось.
     */
    fun onStateChange(state: State, firstFrameRendered: Boolean) = Unit

    /**
     * Отрисовался первый кадр.
     */
    fun onFirstVideoFrameRendered(state: State) = Unit

    /**
     * Продолжительность видеосообщения изменилось.
     */
    fun onDurationChange(duration: Int) = Unit

    /**
     * Ошибка воспроизведения видеосообщения.
     */
    fun onVideoPlaybackError(error: Throwable) = Unit

    /**
     * Проанимировать изменение размера видеосообщение.
     */
    fun showPlayClickAnimation(callback: () -> Unit) { callback() }

    /**
     * Обновить состояние загрузки видеосообщения.
     */
    fun updateBufferingState(show: Boolean) = Unit
}