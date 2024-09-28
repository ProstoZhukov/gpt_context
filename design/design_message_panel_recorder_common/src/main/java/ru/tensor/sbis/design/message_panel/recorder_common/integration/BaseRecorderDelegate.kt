package ru.tensor.sbis.design.message_panel.recorder_common.integration

import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.tensor.sbis.common.util.storeIn
import ru.tensor.sbis.communication_decl.videocall.bl.CallStateProvider
import ru.tensor.sbis.design.message_panel.recorder_common.R
import ru.tensor.sbis.design.message_panel.recorder_common.contact.BaseRecordViewApi
import ru.tensor.sbis.design.message_panel.recorder_common.contact.RecordQuoteActionListener
import ru.tensor.sbis.design.message_panel.recorder_common.contact.RecordRecipientsActionListener
import ru.tensor.sbis.design.message_panel.recorder_common.contact.RecordResultListener
import ru.tensor.sbis.design_notification.SbisPopupNotification
import ru.tensor.sbis.message_panel.contract.MessagePanelController
import ru.tensor.sbis.message_panel.helper.media.MediaRecordData
import ru.tensor.sbis.message_panel.view.MessagePanel
import ru.tensor.sbis.modalwindows.dialogalert.PopupConfirmation
import java.lang.Exception

/**
 * Базовая реализация делегата компонентов записи [BaseRecordViewApi]
 * для подключения к панели сообщений [MessagePanel].
 *
 * @author vv.chekurda
 */
abstract class BaseRecorderDelegate<STATE, RESULT>(
    protected val fragment: Fragment,
    protected val messagePanel: MessagePanel,
    protected val messagePanelController: MessagePanelController<*, *, *>,
    protected val recorder: BaseRecordViewApi<STATE, RESULT>,
    private val callStateProvider: CallStateProvider? = null,
    private val isVideo: Boolean = false
) : RecorderDelegate<STATE, RESULT>,
    RecordResultListener<RESULT> {

    private var resultListener: RecordResultListener<RESULT>? = null
    private var stateListener: ((STATE) -> Unit)? = null
    private var recordClickListener: (() -> Unit)? = null
    private var recordState: STATE? = null

    protected val disposer = CompositeDisposable()
    private val lifecycleObserver =
        object : DefaultLifecycleObserver {

            override fun onPause(owner: LifecycleOwner) {
                this@BaseRecorderDelegate.onPause()
            }

            override fun onStop(owner: LifecycleOwner) {
                stopRecording()
            }

            override fun onDestroy(owner: LifecycleOwner) {
                disposer.dispose()
                release()
                fragment.viewLifecycleOwner.lifecycle.removeObserver(this)
            }
        }

    private val isCallRunning: Boolean
        get() = callStateProvider?.isCallRunning() != true

    @get:StringRes
    private val callRunningErrorStringRes: Int by lazy {
        if (isVideo) {
            R.string.design_message_panel_recorder_video_record_info
        } else {
            R.string.design_message_panel_recorder_audio_record_info
        }
    }

    override var isEnabled: Boolean = true
        set(value) {
            val isChanged = field != value
            field = value
            if (isChanged) updatePressedListener()
        }

    override val state: STATE?
        get() = recordState

    init {
        fragment.viewLifecycleOwner.lifecycle.addObserver(lifecycleObserver)
        messagePanel.addKeyboardEventDelegate(recorder)
        recorder.init(fragment)
        initListeners()
    }

    protected abstract fun getMediaRecordData(resultData: RESULT): MediaRecordData

    protected open fun onBeforeStartRecording() = Unit

    protected open fun onPause() = Unit

    override fun setRecordResultListener(listener: RecordResultListener<RESULT>?) {
        resultListener = listener
    }

    override fun setRecordStateChangedListener(listener: (STATE) -> Unit) {
        stateListener = listener
    }

    override fun setOnRecordClickListener(listener: () -> Unit) {
        recordClickListener = listener
    }

    override fun stopRecording() {
        recorder.stopRecording()
    }

    override fun cancelRecording() {
        recorder.cancelRecording()
    }

    override fun release() {
        recorder.onKeyboardCloseMeasure(0)
        recorder.release()
    }

    override fun onRecordCompleted(resultData: RESULT) {
        resultListener?.onRecordCompleted(resultData)
        changeMessagePanelAlpha(isVisible = true)
        messagePanelController.sendMediaMessage(getMediaRecordData(resultData))
    }

    override fun onRecordStarted() {
        resultListener?.onRecordStarted()
        changeMessagePanelAlpha(isVisible = false)
    }

    override fun onRecordCanceled() {
        resultListener?.onRecordCanceled()
        changeMessagePanelAlpha(isVisible = true)
    }

    override fun onRecordError(error: Exception) {
        resultListener?.onRecordError(error)
        changeMessagePanelAlpha(isVisible = true)
        showRecordError(R.string.design_message_panel_recorder_error_message)
    }

    private fun changeMessagePanelAlpha(isVisible: Boolean) {
        messagePanel.apply {
            alpha = if (isVisible) 1f else 0f
            isInputLocked = !isVisible
        }
    }

    private fun onRecordClicked(isLongPressed: Boolean) {
        if (!isCallRunning) {
            showRecordInfoPopup(callRunningErrorStringRes)
            return
        }
        recordClickListener?.invoke()
        onBeforeStartRecording()
        recorder.startRecording(
            lockRecord = !isLongPressed,
            animateHiding = !messagePanelController.viewModel.conversationInfo.isNewDialogModeEnabled
        )
    }

    private fun initListeners() {
        recorder.resultListener = this
        updatePressedListener()
        initDecorDataListener()
        initRecordStateListener()
        initQuoteListener()
        initRecipientsListener()
    }

    private fun updatePressedListener() {
        val setter = if (isVideo) {
            messagePanel::setOnVideoRecordPressedListener
        } else {
            messagePanel::setOnAudioRecordPressedListener
        }
        setter(if (isEnabled) ::onRecordClicked else null)
    }

    private fun initDecorDataListener() {
        messagePanelController.viewModel
            .recorderDecorData
            .subscribe {
                recorder.decorData = it
            }.storeIn(disposer)
    }

    private fun initRecordStateListener() {
        fragment.viewLifecycleOwner.lifecycleScope.launch {
            launch(Dispatchers.Main) {
                recorder.state.collect(::onRecordStateChanged)
            }
        }
    }

    private fun onRecordStateChanged(state: STATE) {
        this.recordState = state
        stateListener?.invoke(state)
    }

    private fun initRecipientsListener() {
        recorder.setRecipientsActionListener(
            object : RecordRecipientsActionListener {
                override fun onRecipientsClicked() {
                    messagePanelController.requestRecipientsSelection()
                }

                override fun onClearButtonClicked() {
                    messagePanelController.setRecipients(emptyList(), isUserSelected = true)
                }
            }
        )
    }

    private fun initQuoteListener() {
        recorder.setQuoteActionListener(
            object : RecordQuoteActionListener {
                override fun onClearButtonClicked() {
                    messagePanelController.cancelEdit()
                }
            }
        )
    }

    private fun showRecordInfoPopup(@StringRes infoRes: Int) {
        val context = messagePanel.context
        PopupConfirmation.newSimpleInstance(DIALOG_CODE_RECORD_MEDIA_INFO_POPUP).also {
            it.requestTitle(context.getString(infoRes))
            it.requestPositiveButton(context.getString(ru.tensor.sbis.common.R.string.dialog_button_ok))
            it.setEventProcessingRequired(false)
        }.show(fragment.childFragmentManager, PopupConfirmation::class.simpleName)
    }

    private fun showRecordError(errorRes: Int) {
        val context = messagePanel.context
        val message = context.getString(errorRes)
        SbisPopupNotification.pushToast(context, message)
    }
}

private const val DIALOG_CODE_RECORD_MEDIA_INFO_POPUP = 499999