package ru.tensor.sbis.design.message_panel.audio_recorder.view.controller

import android.animation.ValueAnimator
import android.app.Activity
import android.os.Parcel
import android.os.Parcelable
import android.view.HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
import android.view.HapticFeedbackConstants.KEYBOARD_TAP
import android.view.View
import android.view.View.BaseSavedState
import androidx.core.animation.doOnEnd
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import kotlinx.coroutines.flow.MutableStateFlow
import ru.tensor.sbis.communication_decl.communicator.media.MediaPlayer
import ru.tensor.sbis.design.audio_player_view.view.message.data.AudioMessageEmotion
import ru.tensor.sbis.design.custom_view_tools.utils.safeRequestLayout
import ru.tensor.sbis.design.message_panel.audio_recorder.MessagePanelAudioRecorderPlugin
import ru.tensor.sbis.design.message_panel.audio_recorder.recorder.AudioRecordManager
import ru.tensor.sbis.design.message_panel.recorder_common.utils.DefaultMediaFileFactory
import ru.tensor.sbis.design.message_panel.audio_recorder.view.AudioRecordView
import ru.tensor.sbis.design.message_panel.audio_recorder.view.contract.AudioRecordMode
import ru.tensor.sbis.design.message_panel.audio_recorder.view.contract.AudioRecordResultData
import ru.tensor.sbis.design.message_panel.audio_recorder.view.contract.AudioRecordViewApi
import ru.tensor.sbis.design.message_panel.audio_recorder.view.contract.AudioRecordViewState
import ru.tensor.sbis.design.message_panel.audio_recorder.view.send.AudioRecordSendView
import ru.tensor.sbis.design.message_panel.audio_recorder.view.send.contract.AudioRecordControlViewEvent
import ru.tensor.sbis.design.message_panel.audio_recorder.view.send.contract.AudioRecordSendViewEvent
import ru.tensor.sbis.design.message_panel.audio_recorder.view.send.contract.AudioRecordSendViewState
import ru.tensor.sbis.design.message_panel.decl.quote.MessagePanelQuote
import ru.tensor.sbis.design.message_panel.decl.recipients.RecipientsView.RecipientsViewData
import ru.tensor.sbis.design.message_panel.decl.recipients.data.RecipientPersonItem
import ru.tensor.sbis.design.message_panel.decl.record.MediaFileFactory
import ru.tensor.sbis.design.message_panel.decl.record.RecordControlButtonPosition
import ru.tensor.sbis.design.message_panel.decl.record.RecorderDecorData
import ru.tensor.sbis.design.message_panel.recorder_common.R
import ru.tensor.sbis.design.message_panel.recorder_common.audio_record.AudioRecordError
import ru.tensor.sbis.design.message_panel.recorder_common.audio_record.AudioRecordListener
import ru.tensor.sbis.design.message_panel.recorder_common.contact.RecordQuoteActionListener
import ru.tensor.sbis.design.message_panel.recorder_common.contact.RecordRecipientsActionListener
import ru.tensor.sbis.design.message_panel.recorder_common.contact.RecordResultListener
import ru.tensor.sbis.design.message_panel.recorder_common.record_control.RecordControlView
import ru.tensor.sbis.design.message_panel.recorder_common.record_control.contract.RecordControlEvent
import ru.tensor.sbis.design.message_panel.recorder_common.record_control.contract.RecordControlQuoteActionListener
import ru.tensor.sbis.design.message_panel.recorder_common.record_control.contract.RecordControlRecipientsActionListener
import ru.tensor.sbis.persons.ContactVM
import ru.tensor.sbis.persons.PersonName
import java.io.File

/**
 * Реализует логику компонента [AudioRecordView].
 * @see AudioRecordViewApi
 *
 * @author vv.chekurda
 */
internal class AudioRecordViewController : AudioRecordViewApi {

    private lateinit var view: AudioRecordView
    private lateinit var controlView: RecordControlView
    private lateinit var sendView: AudioRecordSendView
    private var recordManager: AudioRecordManager? = null
    private var mediaPlayer: MediaPlayer? = null
    private var controlFadeOutAnimator: ValueAnimator? = null
    private var animateHiding: Boolean = true

    private val isRecording: Boolean
        get() = state.value.isRecording
    private val requireSendOnStop: Boolean
        get() = state.value.requireSendOnStop

    override val state: MutableStateFlow<AudioRecordViewState> = MutableStateFlow(AudioRecordViewState())

    override var decorData: RecorderDecorData
        get() = controlView.decorData
        set(value) { controlView.decorData = value }

    override var resultListener: RecordResultListener<AudioRecordResultData>? = null

    override var mode = AudioRecordMode.SIMPLE
        set(value) {
            val isChanged = field != value
            field = value
            if (isChanged) {
                sendView.mode = value
                view.safeRequestLayout()
            }
        }

    override var pressedButtonPanelPosition: RecordControlButtonPosition
        get() = controlView.recordButtonPosition
        set(value) {
            controlView.recordButtonPosition = value
        }

    override fun init(
        fragment: Fragment,
        fileFactory: MediaFileFactory?,
        customPlayer: MediaPlayer?
    ) {
        val player = customPlayer ?: MessagePanelAudioRecorderPlugin.mediaPlayerFeature.getMediaPlayer()
        attachPlayer(player)

        val mediaFileFactory = fileFactory
            ?: DefaultMediaFileFactory(
                fragment.getString(R.string.design_message_panel_recorder_audio_file_name_template),
                fragment.requireContext().cacheDir.path,
                DefaultMediaFileFactory.Formats.FLAC.value
            )
        val manager = AudioRecordManager(fragment.requireActivity() as Activity, mediaFileFactory)
        recordManager = manager
        manager.processListener = object : AudioRecordListener {
            override fun onRecordStarted() {
                when {
                    !isRecording -> return
                    state.value.isLockedViewState -> sendView.startRecordAnimation()
                    else -> controlView.startRecordAnimation()
                }
            }

            override fun onRecordCompleted(audioFile: File, duration: Int, waveform: ByteArray) {
                this@AudioRecordViewController.onRecordCompleted(audioFile, duration, waveform)
            }

            override fun onRecordCanceled() {
                this@AudioRecordViewController.cancelRecord()
            }

            override fun onError(error: AudioRecordError) {
                this@AudioRecordViewController.onRecordError(error.exception)
            }

            override fun onVolumeAmplitudeChanged(amplitude: Float) {
                when {
                    !isRecording -> return
                    state.value.isLockedViewState -> sendView.setAmplitude(amplitude)
                    else -> controlView.setAmplitude(amplitude)
                }
            }
        }
    }

    override fun setRecipientsActionListener(listener: RecordRecipientsActionListener?) {
        controlView.recipientsActionListener = listener?.let {
            object : RecordControlRecipientsActionListener {
                override fun onRecipientsClicked() {
                    it.onRecipientsClicked()
                }

                override fun onClearButtonClicked() {
                    it.onClearButtonClicked()
                }
            }
        }
    }

    override fun setQuoteActionListener(listener: RecordQuoteActionListener?) {
        controlView.quoteActionListener = listener?.let {
            object : RecordControlQuoteActionListener {
                override fun onClearButtonClicked() {
                    it.onClearButtonClicked()
                }
            }
        }
    }

    override fun startRecording(
        lockRecord: Boolean,
        animateHiding: Boolean
    ) {
        if (isRecording) return
        this.animateHiding = animateHiding

        recordManager?.withPermissions {
            mediaPlayer?.isEnabled = false
            resultListener?.onRecordStarted()
            startRecorder()
            view.apply {
                performHapticFeedback(KEYBOARD_TAP, FLAG_IGNORE_GLOBAL_SETTING)
                isVisible = true
            }
            showRecordView(withLock = lockRecord)
        }
    }

    override fun stopRecording() {
        stopRecord()
    }

    override fun cancelRecording() {
        if (!(state.value.isRecording || state.value.isSendPreparing)) return
        clearState()
        recordManager?.cancelRecording()
        view.isVisible = false
        sendView.isVisible = false
        sendView.clear()
        mediaPlayer?.isEnabled = true
        controlView.clearRecordAnimation()
        sendView.clearRecordAnimation()
        recordManager?.clearDeviceRecordConfig()
        resultListener?.onRecordCanceled()
    }

    override fun release() {
        cancelRecording()
        recordManager?.release()
        recordManager = null
        resultListener = null
        mediaPlayer = null
        setRecipientsActionListener(null)
        setQuoteActionListener(null)
    }

    override fun onKeyboardOpenMeasure(keyboardHeight: Int): Boolean {
        view.updatePadding(bottom = keyboardHeight)
        return true
    }

    override fun onKeyboardCloseMeasure(keyboardHeight: Int): Boolean {
        view.updatePadding(bottom = 0)
        return true
    }

    /**
     * Прикрепить вью элементы.
     */
    fun attachViews(
        view: AudioRecordView,
        controlView: RecordControlView,
        sendView: AudioRecordSendView
    ) {
        this.view = view
        this.controlView = controlView
        this.sendView = sendView
        sendView.mode = AudioRecordMode.SIMPLE
        subscribeOnControlEvents()
    }

    private fun subscribeOnControlEvents() {
        controlView.eventsHandler = { event: RecordControlEvent ->
            when (event) {
                RecordControlEvent.OnRecordStopped -> stopRecord(withSend = !controlView.isLocked)
                RecordControlEvent.OnSendClicked -> stopRecord(withSend = true)
                RecordControlEvent.OnRecordCanceled -> cancelRecord()
                RecordControlEvent.OnHidingEnd -> fadeOutControlView()
                RecordControlEvent.OnReady -> Unit
            }
        }
        sendView.eventsHandler = { event: AudioRecordSendViewEvent ->
            when (event) {
                is AudioRecordSendViewEvent.OnComplete -> onSendViewCompleted(event.resultData)
                is AudioRecordSendViewEvent.OnSendClicked -> {
                    updateState(pickedEmotion = event.emotion)
                    stopRecord(withSend = true)
                }
                AudioRecordSendViewEvent.OnSendCanceled -> cancelSending()
                AudioRecordControlViewEvent.OnRecordCanceled -> cancelRecord()
                AudioRecordControlViewEvent.OnHidingEnd -> fadeOutControlView()
                AudioRecordControlViewEvent.OnRecordStopped -> stopRecord(withSend = false)
            }
        }
    }

    private fun attachPlayer(player: MediaPlayer) {
        this.mediaPlayer = player
        sendView.attachPlayer(player)
    }

    private fun startRecorder() {
        recordManager?.apply {
            prepareRecorder()
            startRecording()
        }
    }

    private fun showRecordView(withLock: Boolean) {
        updateState(isRecording = true)
        controlFadeOutAnimator?.cancel()
        controlFadeOutAnimator = null
        when (mode) {
            AudioRecordMode.SIMPLE -> {
                showLockedRecord()
            }

            AudioRecordMode.MESSAGE_PANEL -> {
                if (withLock) {
                    showPanelLockedRecord()
                } else {
                    showPanelUnlockedRecord()
                }
            }
        }
    }

    private fun showLockedRecord() {
        updateState(isLockedViewState = true)
        controlView.isVisible = false
        showRecordSendView()
    }

    private fun showPanelLockedRecord() {
        updateState(isLockedViewState = true)
        controlView.apply {
            alpha = 1f
            clearRecordAnimation()
            isSendButtonEnabled = true
        }
        showRecordSendView()
    }

    private fun showRecordSendView() {
        sendView.apply {
            viewState = AudioRecordSendViewState.RECORDER
            isVisible = true
            show()
        }
    }

    private fun showPanelUnlockedRecord() {
        sendView.isVisible = false
        controlView.apply {
            isSendButtonEnabled = controlView.recordButtonPosition == RecordControlButtonPosition.FIRST_ALIGN_END
            animateShowing(withLock = false)
        }
    }

    private fun stopRecord(withSend: Boolean = false) {
        if (!isRecording) return
        updateState(requireSendOnStop = withSend)
        recordManager?.stopRecording(false)
    }

    private fun cancelRecord() {
        mediaPlayer?.isEnabled = true
        if (!isRecording) return
        updateState(isRecording = false)
        recordManager?.cancelRecording()
        resultListener?.onRecordCanceled()
        hideView()
    }

    private fun hideView() {
        when {
            !animateHiding -> onHideAnimationEnd()
            state.value.isLockedViewState -> sendView.hide()
            else -> controlView.animateHiding()
        }
    }

    private fun fadeOutControlView() {
        controlFadeOutAnimator?.cancel()
        controlFadeOutAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            controlFadeOutAnimator = this
            duration = FADE_OUT_ANIMATION_DURATION_MS
            addUpdateListener { controlView.alpha = 1f - it.animatedFraction }
            doOnEnd { onHideAnimationEnd() }
            start()
            pause()
            view.invalidate()
            view.doOnPreDraw { resume() }
        }
    }

    private fun onHideAnimationEnd() {
        view.isVisible = false
        sendView.isVisible = false
        recordManager?.clearDeviceRecordConfig()
        clearState()
    }

    private fun onRecordError(error: Exception) {
        mediaPlayer?.isEnabled = true
        if (!isRecording) return
        view.isVisible = false
        controlView.clearRecordAnimation()
        sendView.clearRecordAnimation()
        resultListener?.onRecordError(error)
        recordManager?.clearDeviceRecordConfig()
        clearState()
    }

    private fun onRecordCompleted(audioFile: File, duration: Int, waveform: ByteArray) {
        mediaPlayer?.isEnabled = true
        if (!isRecording) return
        if (requireSendOnStop || mode == AudioRecordMode.SIMPLE) {
            complete(audioFile, duration, waveform)
        } else {
            showPrepareSendView(audioFile, duration, waveform)
        }
    }

    private fun complete(audioFile: File, duration: Int, waveform: ByteArray) {
        val resultData = AudioRecordResultData(
            audioFile = audioFile,
            duration = duration,
            waveform = waveform,
            emotion = state.value.pickedEmotion
        )
        updateState(pickedEmotion = null)
        checkNotNull(resultListener).onRecordCompleted(resultData)
        hideView()
    }

    private fun showPrepareSendView(audioFile: File, duration: Int, waveform: ByteArray) {
        updateState(isRecording = false, isLockedViewState = false, isSendPreparing = true)
        controlView.clearRecordAnimation()
        sendView.apply {
            setAudioData(audioFile, duration, waveform)
            viewState = AudioRecordSendViewState.PLAYER
            isVisible = true
        }
    }

    private fun onSendViewCompleted(resultData: AudioRecordResultData) {
        view.isVisible = false
        sendView.isVisible = false
        recordManager?.clearDeviceRecordConfig()
        checkNotNull(resultListener).onRecordCompleted(resultData)
        clearState()
    }

    private fun cancelSending() {
        view.isVisible = false
        sendView.isVisible = false
        resultListener?.onRecordCanceled()
        recordManager?.clearDeviceRecordConfig()
        clearState()
    }

    private fun updateState(
        isRecording: Boolean = state.value.isRecording,
        isLockedViewState: Boolean = state.value.isLockedViewState,
        isSendPreparing: Boolean = state.value.isSendPreparing,
        requireSendOnStop: Boolean = state.value.requireSendOnStop,
        pickedEmotion: AudioMessageEmotion? = state.value.pickedEmotion
    ) {
        state.tryEmit(
            state.value.copy(
                isRecording = isRecording,
                isLockedViewState = isLockedViewState,
                isSendPreparing = isSendPreparing,
                requireSendOnStop = requireSendOnStop,
                pickedEmotion = pickedEmotion
            )
        )
    }

    private fun clearState() {
        updateState(
            isRecording = false,
            isLockedViewState = false,
            isSendPreparing = false,
            requireSendOnStop = false,
            pickedEmotion = null
        )
    }

    /**
     * Сохранение состояния состояние.
     */
    fun onSaveInstanceState(superState: Parcelable?): Parcelable =
        SavedState(superState).also {
            it.visibility = view.visibility
            it.decorData = decorData
        }

    /**
     * Восстановление состояния.
     */
    fun onRestoreInstanceState(state: Parcelable): Parcelable? =
        if (state is SavedState) {
            state.let {
                view.visibility = it.visibility
                decorData = it.decorData
                it.superState
            }
        } else null

    private class SavedState : BaseSavedState {

        var visibility: Int = View.GONE
        lateinit var decorData: RecorderDecorData

        constructor(superState: Parcelable?) : super(superState)

        private constructor(source: Parcel?) : super(source) {
            visibility = source?.readInt() ?: return
            val recipientsData = if (source.readInt() == 1) {
                val recipients = mutableListOf<PersonName>().also {
                    source.readList(it, PersonName::class.java.classLoader)
                }.map { ContactVM().apply { name = it } }
                val isHintEnabled = source.readInt() == 1
                RecipientsViewData(
                    recipients.map(::RecipientPersonItem),
                    isHintEnabled
                )
            } else null
            val quoteData = if (source.readInt() == 1) {
                MessagePanelQuote(
                    title = source.readString().orEmpty(),
                    text = source.readString().orEmpty()
                )
            } else {
                null
            }
            decorData = RecorderDecorData(recipientsData, quoteData)
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.apply {
                writeInt(visibility)
                val recipientsData = decorData.recipientsData
                if (recipientsData != null) {
                    writeInt(1)
                    writeList(
                        recipientsData.recipients
                            .filterIsInstance(RecipientPersonItem::class.java)
                            .map { it.name }
                    )
                    writeInt(if (recipientsData.isHintEnabled) 1 else 0)
                } else {
                    writeInt(0)
                }
                val quoteData = decorData.quoteData
                if (quoteData != null) {
                    writeInt(1)
                    writeString(quoteData.title)
                    writeString(quoteData.text)
                } else {
                    writeInt(0)
                }
            }
        }

        override fun describeContents(): Int = 0

        companion object CREATOR : Parcelable.Creator<SavedState> {
            override fun createFromParcel(source: Parcel): SavedState = SavedState(source)
            override fun newArray(size: Int): Array<SavedState?> = arrayOfNulls(size)
        }
    }
}

/**
 * Продолжительность анимации скрытия view контрола.
 */
private const val FADE_OUT_ANIMATION_DURATION_MS = 100L