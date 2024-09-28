package ru.tensor.sbis.design.audio_player_view.view.player

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.annotation.AttrRes
import androidx.annotation.Px
import androidx.annotation.StyleRes
import androidx.core.view.children
import androidx.core.view.isVisible
import io.reactivex.disposables.Disposable
import ru.tensor.sbis.communication_decl.communicator.media.MediaPlaybackListener
import ru.tensor.sbis.communication_decl.communicator.media.MediaPlayer
import ru.tensor.sbis.communication_decl.communicator.media.data.MediaInfo
import ru.tensor.sbis.communication_decl.communicator.media.data.MediaSource.AudioSource
import ru.tensor.sbis.communication_decl.communicator.media.data.State
import ru.tensor.sbis.communication_decl.communicator.media.waveform.WAVEFORM_SIZE
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.audio_player_view.AudioPlayerViewPlugin
import ru.tensor.sbis.design.audio_player_view.R
import ru.tensor.sbis.design.audio_player_view.view.player.children.SeekBarDelegate
import ru.tensor.sbis.design.audio_player_view.view.player.children.WaveformView
import ru.tensor.sbis.design.audio_player_view.view.player.contact.AudioPlayerViewApi
import ru.tensor.sbis.design.audio_player_view.view.player.data.AudioPlayerViewData
import ru.tensor.sbis.design.buttons.SbisRoundButton
import ru.tensor.sbis.design.buttons.base.models.icon.SbisButtonTextIcon
import ru.tensor.sbis.design.buttons.base.models.style.InfoButtonStyle
import ru.tensor.sbis.design.buttons.round.model.SbisRoundButtonSize
import ru.tensor.sbis.design.custom_view_tools.utils.dp
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.theme.global_variables.StyleColor
import ru.tensor.sbis.design.utils.getDimen
import ru.tensor.sbis.design.utils.getDimenPx
import ru.tensor.sbis.design.utils.getThemeColorInt
import java.util.concurrent.TimeUnit
import ru.tensor.sbis.design.R as RDesign

/**
 * Компонент для проигрывания аудио.
 * @see AudioPlayerViewApi
 *
 * @author vv.chekurda
 */
class AudioPlayerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes),
    AudioPlayerViewApi,
    SeekBarDelegate {

    /**
     * Варианты отображения компонента.
     */
    enum class ViewMode {
        DEFAULT,
        RECORDER
    }

    private var mediaPlayer: MediaPlayer? = null

    private val playButton = SbisRoundButton(context)
    private val speedButton = SbisTextView(context)
    private val waveformAndDurationLayout: LinearLayout = LinearLayout(context)
    private val waveformView = WaveformView(context).apply {
        setAvailabilityCondition { checkPlayingAvailability(mediaInfo) }
    }
    private val durationTextView = SbisTextView(context)

    private var mediaInfo: MediaInfo? = null
    private var playingSubscription: Disposable? = null
    private var listener: MediaPlaybackListener? = null

    private var pendingAudioSource: AudioSource? = null
    private var isSourceOnDragChanged: Boolean = false

    @get:Px
    private val defaultHeight: Int by lazy {
        context.getDimenPx(RDesign.attr.inlineHeight_xs)
    }

    @get:Px
    private val contentHeight: Int
        get() = layoutParams?.height?.takeIf { it >= 0 } ?: defaultHeight

    override var data: AudioPlayerViewData? = null
        set(value) {
            field = value
            value?.apply {
                setAudioSource(audioSource)
                setDuration(durationSeconds)
                setWaveform(waveform)
            }
        }

    override var viewMode: ViewMode = ViewMode.DEFAULT
        set(value) {
            val isChanged = field != value
            field = value
            if (isChanged) updateLayoutParams()
        }

    init {
        initButtons()
        initWaveformAndDurationLayout()
        updateLayoutParams()
        waveformView.setSeekBarDelegate(this)
        setMediaPlayer(AudioPlayerViewPlugin.defaultMediaPlayer)
    }

    override fun setMediaPlayer(mediaPlayer: MediaPlayer) {
        if (mediaPlayer === this.mediaPlayer) return
        this.mediaPlayer = mediaPlayer
        // Плеер задан, устанавливаем данные для проигрывания
        pendingAudioSource?.let { setAudioSource(it) }
        updateClickable()
    }

    /**
     * Задать слушателя событий воспроизведения.
     */
    override fun setListener(listener: MediaPlaybackListener?) {
        this.listener = listener
    }

    override fun clearState() {
        if (mediaInfo != null && mediaPlayer?.getMediaInfo()?.mediaSource == mediaInfo?.mediaSource) {
            mediaPlayer?.stop()
        }
        mediaInfo = null
        clearSubscription()
        waveformView.setProgress(0f)
    }

    override fun recycle() {
        data = null
        listener = null
        mediaInfo = null
        clearSubscription()
        waveformView.setProgress(0f)
        waveformView.setActive(false)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        clearSubscription()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (playingSubscription == null && mediaInfo != null) {
            checkAlreadyPlaying(mediaInfo!!)
        }
    }

    override fun onSeekBarDragged(progress: Float) {
        mediaPlayer?.apply {
            val mediaInfo = mediaInfo ?: return
            var isSourceChanged = false
            val playWhenReady = mediaInfo.state == State.DEFAULT
            if (getMediaInfo()?.mediaSource != mediaInfo.mediaSource) {
                isSourceOnDragChanged = true
                updatePlayerMediaInfo(playWhenReady = playWhenReady)
                isSourceChanged = true
            }
            seekToProgress(progress, isSourceChanged = isSourceChanged)
            if (playWhenReady) {
                play()
            }
        }
    }

    override fun onSeekBarDragging(progress: Float) {
        val mediaInfo = mediaInfo ?: return
        val countDown = TimeUnit.MILLISECONDS.toSeconds((mediaInfo.duration * (1f - progress)).toLong())
        setDuration(countDown.toInt())
    }

    override fun getBaseline(): Int {
        return (measuredHeight - durationTextView.measuredHeight) / 2 + durationTextView.baseline
    }

    private fun updateClickable() {
        children.forEach { it.isClickable = mediaPlayer != null }
        waveformAndDurationLayout.children.forEach { it.isClickable = mediaPlayer != null }
    }

    private fun setAudioSource(audioSource: AudioSource) {
        if (mediaPlayer == null) {
            // Откладываем установку данных до момента пока не будет установлен плеер
            pendingAudioSource = audioSource
            return
        }
        pendingAudioSource = null

        if (mediaInfo?.mediaSource != audioSource) {
            clearSubscription()
        }
        mediaInfo = MediaInfo(mediaSource = audioSource, duration = (data?.durationSeconds ?: 0) * 1000L)
        speedButton.text = mediaInfo!!.playbackSpeed.text
        updatePlayButtonState(mediaInfo!!.state)
        checkAlreadyPlaying(mediaInfo!!)
    }

    private fun clearSubscription() {
        playingSubscription?.dispose()
        playingSubscription = null
    }

    private fun initButtons() {
        playButton.apply {
            id = R.id.design_audio_message_view_audio_play_button_id
            size = SbisRoundButtonSize.S
            style = InfoButtonStyle
            updatePlayButtonState(State.DEFAULT)
        }

        addView(playButton)
        playButton.setOnClickListener { onPlayButtonClick() }

        speedButton.apply {
            id = R.id.design_audio_message_view_audio_speed_button_id
            isSingleLine = true
            includeFontPadding = false
            setTextColor(StyleColor.INFO.getColor(context))
            setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                context.getDimen(RDesign.attr.fontSize_xs_scaleOff)
            )
            gravity = Gravity.CENTER
        }
        addView(speedButton)
        speedButton.setOnClickListener { onSpeedButtonClick() }
    }

    private fun initWaveformAndDurationLayout() {
        waveformAndDurationLayout.apply {
            id = R.id.design_audio_message_view_audio_container_view_id
            orientation = LinearLayout.HORIZONTAL
        }
        addView(waveformAndDurationLayout)

        updateWaveformLayoutParams()
        val durationLayoutParams = LinearLayout.LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT,
            0f
        ).apply { gravity = Gravity.CENTER_VERTICAL }

        durationTextView.apply {
            id = R.id.design_audio_message_view_audio_duration_view_id
            isSingleLine = true
            includeFontPadding = false
            setTextColor(context.getThemeColorInt(RDesign.attr.labelContrastTextColor))
            setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                context.getDimen(RDesign.attr.fontSize_xs_scaleOff)
            )
            layoutParams = durationLayoutParams
        }
        waveformView.id = R.id.design_audio_message_view_audio_waveform_view_id

        waveformAndDurationLayout.addView(durationTextView)
        waveformAndDurationLayout.addView(waveformView)
    }

    private fun updateWaveformLayoutParams() {
        waveformView.layoutParams = LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1f).apply {
            gravity = Gravity.CENTER_VERTICAL
            marginStart = context.getDimenPx(RDesign.attr.offset_xs)
        }
    }

    private fun updateLayoutParams() {
        val playButtonSize = contentHeight
        val buttonGravity = Gravity.END
        val playButtonParams = LayoutParams(
            playButtonSize,
            playButtonSize,
            buttonGravity or Gravity.CENTER_VERTICAL
        )
        playButton.layoutParams = playButtonParams

        // Кнопка изменения скорости не показывается при прослушивании записанного сообщения в поле ввода
        if (viewMode == ViewMode.DEFAULT) {
            val speedButtonParams = LayoutParams(
                dp(PLAY_BUTTON_TO_WAVEFORM_PADDING_DP),
                contentHeight,
                Gravity.END or Gravity.CENTER_VERTICAL
            ).apply {
                rightMargin = playButtonSize
            }
            speedButton.layoutParams = speedButtonParams
            speedButton.isVisible = true
        } else {
            speedButton.isVisible = false
        }

        val waveformAndDurationLayoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            contentHeight,
            Gravity.CENTER_VERTICAL or Gravity.START
        )
        val buttonMarginDp = if (viewMode == ViewMode.DEFAULT) {
            PLAY_BUTTON_TO_WAVEFORM_PADDING_DP
        } else {
            PLAY_BUTTON_TO_WAVEFORM_RECORDER_PADDING_DP
        }
        waveformAndDurationLayoutParams.marginEnd = playButtonSize + dp(buttonMarginDp)
        waveformAndDurationLayout.layoutParams = waveformAndDurationLayoutParams
        updateWaveformLayoutParams()
    }

    private fun onPlayButtonClick() {
        mediaInfo ?: return
        val mediaPlayer = this.mediaPlayer ?: return
        when {
            playingSubscription == null -> {
                if (!checkPlayingAvailability(mediaInfo)) return
                updatePlayerMediaInfo()
                mediaPlayer.play()
            }
            mediaInfo!!.state == State.PLAYING -> {
                mediaPlayer.pause()
            }
            mediaInfo!!.state != State.PLAYING -> {
                mediaPlayer.play()
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

    private fun onSpeedButtonClick() {
        val mediaInfo = this.mediaInfo ?: return

        mediaInfo.playbackSpeed = mediaInfo.playbackSpeed.nextGradation()
        speedButton.text = mediaInfo.playbackSpeed.text
        if (playingSubscription != null) {
            mediaPlayer?.setPlaybackSpeed(mediaInfo.playbackSpeed)
        }
    }

    private fun updatePlayerMediaInfo(playWhenReady: Boolean = true) {
        mediaPlayer?.setMediaInfo(mediaInfo!!, playWhenReady) ?: return
        subscribeToMediaPlayer()
    }

    private fun checkAlreadyPlaying(mediaInfo: MediaInfo) {
        val mediaPlayer = this.mediaPlayer ?: return
        val currentPlaying = mediaPlayer.getMediaInfo() ?: return
        // Если медиа уже проигрывается подписываемся на состояние проигрывания
        if (playingSubscription == null && mediaInfo.mediaSource == currentPlaying.mediaSource) {
            this.mediaInfo = currentPlaying
            subscribeToMediaPlayer()
        }
    }

    private fun subscribeToMediaPlayer() {
        val playingState = mediaPlayer?.playingState() ?: return
        clearSubscription()
        playingSubscription = playingState
            .doAfterTerminate { clearSubscription() }
            .subscribe { updateState(it) }
    }

    private fun updateState(mediaInfo: MediaInfo) {
        if (mediaInfo.duration > 0 && !mediaInfo.waitingActualProgress) {
            val progress = mediaInfo.position / mediaInfo.duration.toFloat()
            waveformView.setProgress(progress)
        }
        waveformView.setActive(mediaInfo.state != State.DEFAULT)
        waveformView.setPaused(mediaInfo.state == State.PAUSED)

        if (!mediaInfo.waitingActualProgress) {
            if (mediaInfo.state != State.DEFAULT) {
                if (mediaInfo.duration > 0) {
                    val countDown = TimeUnit.MILLISECONDS.toSeconds(mediaInfo.duration - mediaInfo.position)
                    setDuration(countDown.toInt())
                }
            } else {
                // Фикс смаргивания продолжительности при обновлении состояния после onSeekBarDragged.
                if (isSourceOnDragChanged) {
                    isSourceOnDragChanged = false
                } else {
                    setDuration(data!!.durationSeconds)
                }
            }
        }

        updatePlayButtonState(mediaInfo.state)

        val speedText = mediaInfo.playbackSpeed.text
        if (speedButton.isVisible && speedButton.text != speedText) speedButton.text = speedText

        checkPlaybackError(mediaInfo)
    }

    private fun checkPlaybackError(mediaInfo: MediaInfo) {
        mediaInfo.playbackError?.let {
            listener?.onMediaPlaybackError(it)
            mediaInfo.playbackError = null
        }
    }

    private fun updatePlayButtonState(state: State) {
        val currentIcon = (playButton.icon as? SbisButtonTextIcon)?.icon
        val newIcon = if (state == State.PLAYING) {
            SbisMobileIcon.Icon.smi_pauseAudioMessage.character.toString()
        } else {
            SbisMobileIcon.Icon.smi_playAudioMessage.character.toString()
        }
        if (currentIcon != newIcon) playButton.icon = SbisButtonTextIcon(newIcon)
    }

    override fun setLayoutParams(params: ViewGroup.LayoutParams) {
        super.setLayoutParams(params)
        updateLayoutParams()
    }

    /**
     * Отобразить осциллограмму при проигрывании. Ожидаемый размер [WAVEFORM_SIZE].
     */
    private fun setWaveform(waveform: ByteArray?) {
        waveformView.setWaveform(waveform ?: ByteArray(WAVEFORM_SIZE))
    }

    /**
     * Отобразить длительность аудио.
     */
    private fun setDuration(durationSeconds: Int) {
        val minutes = durationSeconds / MINUTE_TO_SECONDS
        val seconds = durationSeconds % MINUTE_TO_SECONDS
        val durationText = DURATION_FORMAT.format(minutes, seconds)
        if (durationTextView.text != durationText) {
            durationTextView.text = durationText
        }
    }
}

private const val PLAY_BUTTON_TO_WAVEFORM_PADDING_DP = 42
private const val PLAY_BUTTON_TO_WAVEFORM_RECORDER_PADDING_DP = 6
private const val MINUTE_TO_SECONDS = 60
private const val DURATION_FORMAT = "%2d:%02d"