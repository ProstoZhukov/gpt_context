package ru.tensor.sbis.design.message_panel.audio_recorder.view.send.controller

import android.animation.ValueAnimator
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.view.animation.DecelerateInterpolator
import androidx.core.view.children
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import ru.tensor.sbis.communication_decl.communicator.media.MediaPlayer
import ru.tensor.sbis.communication_decl.communicator.media.data.MediaSource
import ru.tensor.sbis.communication_decl.communicator.media.data.SourceData
import ru.tensor.sbis.design.audio_player_view.view.message.data.AudioMessageEmotion
import ru.tensor.sbis.design.custom_view_tools.utils.PAINT_MAX_ALPHA
import ru.tensor.sbis.design.custom_view_tools.utils.safeRequestLayout
import ru.tensor.sbis.design.message_panel.audio_recorder.MessagePanelAudioRecorderPlugin.audioPlayerViewDataFactory
import ru.tensor.sbis.design.message_panel.audio_recorder.view.contract.AudioRecordMode
import ru.tensor.sbis.design.message_panel.audio_recorder.view.contract.AudioRecordResultData
import ru.tensor.sbis.design.message_panel.audio_recorder.view.send.AudioRecordSendView
import ru.tensor.sbis.design.message_panel.audio_recorder.view.send.contract.AudioRecordControlViewEvent
import ru.tensor.sbis.design.message_panel.audio_recorder.view.send.contract.AudioRecordSendViewEvent.OnSendCanceled
import ru.tensor.sbis.design.message_panel.audio_recorder.view.send.contract.AudioRecordSendViewEvent.OnComplete
import ru.tensor.sbis.design.message_panel.audio_recorder.view.send.contract.AudioRecordSendViewState
import ru.tensor.sbis.design.message_panel.audio_recorder.view.send.contract.AudioRecordSendViewApi
import ru.tensor.sbis.design.message_panel.audio_recorder.view.send.contract.AudioRecordSendViewEvent
import ru.tensor.sbis.design.message_panel.audio_recorder.view.send.contract.SendEventsHandler
import ru.tensor.sbis.design.message_panel.audio_recorder.view.send.layout.AudioRecordSendLayout
import ru.tensor.sbis.design.message_panel.audio_recorder.view.send.layout.SmileSendButton
import ru.tensor.sbis.design.utils.DebounceActionHandler
import java.io.File

/**
 * Реализует логику компонента [AudioRecordSendView].
 * @see AudioRecordSendViewApi
 *
 * @author vv.chekurda
 */
internal class AudioRecordSendViewController : AudioRecordSendViewApi {

    private lateinit var layout: AudioRecordSendLayout

    private var audioFile: File? = null
    private var duration: Int = 0
    private var waveform: ByteArray? = null
    private var player: MediaPlayer? = null

    private var smilesFadeAnimator: ValueAnimator? = null
    private val smilesFadeInterpolator = DecelerateInterpolator()

    override lateinit var eventsHandler: SendEventsHandler

    override var viewState: AudioRecordSendViewState
        get() = layout.viewState
        set(value) {
            val isChanged = value != layout.viewState
            layout.viewState = value
            if (isChanged) updateViewState(value)
        }

    override var mode = AudioRecordMode.SIMPLE
        set(value) {
            field = value
            layout.mode = value
        }

    override fun attachPlayer(mediaPlayer: MediaPlayer) {
        player = mediaPlayer
        layout.playerView.setMediaPlayer(mediaPlayer)
    }

    override fun setAudioData(
        audioFile: File,
        duration: Int,
        waveform: ByteArray,
    ) {
        this.audioFile = audioFile
        this.duration = duration
        this.waveform = waveform
        layout.playerView.data = audioPlayerViewDataFactory
            .createAudioPlayerViewData(
                source = MediaSource.AudioSource(data = SourceData.UriData(Uri.fromFile(audioFile))),
                durationSeconds = duration,
                waveform = waveform
            )
    }

    override fun clear() {
        smilesFadeAnimator?.cancel()
        smilesFadeAnimator = null
        clearData()
        layout.playerView.clearState()
    }

    override fun setAmplitude(amplitude: Float) {
        layout.recordControl.setAmplitude(amplitude)
    }

    override fun show() {
        layout.apply {
            view.alpha = 1f
            recordControl.isVisible = true
            recordControl.show()
            changeSmilesVisibility(isVisible = true)
        }
    }

    override fun hide() {
        changeSmilesVisibility(isVisible = false)
        layout.recordControl.hide()
    }

    override fun startRecordAnimation() {
        layout.recordControl.startRecordAnimation()
    }

    override fun clearRecordAnimation() {
        layout.apply {
            recordControl.isVisible = false
            smilesLayout.alpha = 1f
            recordControl.clearRecordAnimation()
        }
    }

    /**
     * Прикрепить разметку.
     */
    fun attachLayout(layout: AudioRecordSendLayout) {
        this.layout = layout
        initViews()
    }

    /**
     * Изменилась видимость разметки.
     */
    fun onVisibilityChanged(isVisible: Boolean) {
        if (!isVisible) layout.resetScrollPosition()
    }

    private fun initViews() {
        layout.apply {
            cancelSendButton.setOnClickListener { _, _ -> onCancelClicked() }
            sendButton.setOnClickListener { onSendClicked(AudioMessageEmotion.DEFAULT) }
            smilesLayout.children.forEach { (it as? SmileSendButton)?.smileClickListener = ::onSendClicked }
            recordControl.eventsHandler = { event: AudioRecordControlViewEvent ->
                when (event) {
                    AudioRecordControlViewEvent.OnRecordCanceled,
                    AudioRecordControlViewEvent.OnRecordStopped -> eventsHandler.invoke(event)
                    AudioRecordControlViewEvent.OnHidingEnd -> {
                        layout.view.alpha = 0f
                        recordControl.isVisible = false
                        eventsHandler.invoke(event)
                    }
                }
            }
        }
    }

    private fun onSendClicked(sendType: AudioMessageEmotion) {
        when (viewState) {
            AudioRecordSendViewState.PLAYER -> onPlayerSendClicked(sendType)
            AudioRecordSendViewState.RECORDER -> onRecorderSendClicked(sendType)
        }
    }

    private fun onPlayerSendClicked(sendType: AudioMessageEmotion) {
        DebounceActionHandler.INSTANCE.handle {
            val file = audioFile ?: return@handle
            val waveform = waveform ?: return@handle
            stopFilePlaying(file)
            val resultData = AudioRecordResultData(
                file,
                duration,
                waveform,
                sendType
            )
            eventsHandler.invoke(OnComplete(resultData))
            clearData()
        }
    }

    private fun onRecorderSendClicked(sendType: AudioMessageEmotion) {
        DebounceActionHandler.INSTANCE.handle {
            eventsHandler.invoke(AudioRecordSendViewEvent.OnSendClicked(sendType))
        }
    }

    private fun onCancelClicked() {
        DebounceActionHandler.INSTANCE.handle {
            audioFile?.delete()
            clear()
            eventsHandler.invoke(OnSendCanceled)
        }
    }

    private fun stopFilePlaying(file: File) {
        player?.also {
            if ((it.getMediaInfo()?.mediaSource?.data as? SourceData.UriData)?.uri == Uri.fromFile(file)) {
                it.stop()
            }
        }
    }

    private fun clearData() {
        audioFile = null
        waveform = null
        duration = 0
    }

    private fun changeSmilesVisibility(isVisible: Boolean) {
        if (mode == AudioRecordMode.MESSAGE_PANEL) {
            animateSmilesFade(isShowing = isVisible)
        } else {
            layout.smilesLayout.alpha = if (isVisible) 1f else 0f
        }
    }

    private fun animateSmilesFade(isShowing: Boolean) {
        smilesFadeAnimator?.cancel()
        smilesFadeAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = SMILES_FADE_DURATION_MS
            if (isShowing) {
                addUpdateListener {
                    layout.smilesLayout.alpha = smilesFadeInterpolator.getInterpolation(it.animatedFraction)
                }
            } else {
                addUpdateListener {
                    layout.smilesLayout.alpha = 1f - smilesFadeInterpolator.getInterpolation(it.animatedFraction)
                }
            }
            start()
            pause()
            layout.view.doOnPreDraw { resume() }
        }
    }

    private fun updateViewState(viewState: AudioRecordSendViewState) {
        layout.apply {
            if (viewState == AudioRecordSendViewState.PLAYER) {
                view.alpha = 1f
                smilesLayout.alpha = 1f
                cancelSendButton.alpha = 1f
                fieldDrawable.alpha = PAINT_MAX_ALPHA
                playerView.isVisible = true
                recordControl.isVisible = false
            } else {
                view.alpha = 0f
                smilesLayout.alpha = 0f
                cancelSendButton.alpha = 0f
                fieldDrawable.alpha = 0
                playerView.isVisible = false
                recordControl.isVisible = true
            }
            view.safeRequestLayout()
        }
    }

    /**
     * Сохранение состояния состояние.
     */
    fun onSaveInstanceState(superState: Parcelable?): Parcelable =
        Bundle().apply {
            putParcelable(SUPER_STATE_KEY, superState)
            putInt(VISIBILITY_KEY, layout.view.visibility)
            if (audioFile == null) return@apply
            putString(FILE_PATH_KEY, audioFile?.absolutePath)
            putByteArray(WAVEFORM_KEY, waveform)
            putInt(DURATION_KEY, duration)
        }

    /**
     * Восстановление состояния.
     */
    fun onRestoreInstanceState(state: Parcelable): Parcelable? =
        if (state is Bundle) {
            with(state) {
                val path = getString(FILE_PATH_KEY)
                if (path != null) {
                    setAudioData(
                        audioFile = File(path),
                        duration = getInt(DURATION_KEY),
                        waveform = getByteArray(WAVEFORM_KEY)!!
                    )
                }
                layout.view.visibility = getInt(VISIBILITY_KEY)
                getParcelable(SUPER_STATE_KEY)
            }
        } else null
}

private const val SMILES_FADE_DURATION_MS = 240L

private const val SUPER_STATE_KEY = "super_state"
private const val FILE_PATH_KEY = "file_path"
private const val VISIBILITY_KEY = "visibility"
private const val WAVEFORM_KEY = "waveform"
private const val DURATION_KEY = "duration"