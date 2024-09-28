package ru.tensor.sbis.message_panel.recorder

import android.content.Context
import ru.tensor.sbis.message_panel.recorder.binding.bindToVm
import ru.tensor.sbis.message_panel.recorder.binding.createListener
import ru.tensor.sbis.message_panel.recorder.util.checkHasParentWithoutClippingChildren
import ru.tensor.sbis.message_panel.recorder.viewmodel.RecorderViewModel
import ru.tensor.sbis.message_panel.recorder.viewmodel.RecorderViewModelImpl
import ru.tensor.sbis.message_panel.recorder.viewmodel.listener.DEFAULT
import ru.tensor.sbis.message_panel_recorder.R
import ru.tensor.sbis.recorder.decl.*

/**
 * Реализация View панели для записи аудио
 *
 * @author vv.chekurda
 * @since 7/26/2019
 */
internal class RecorderViewImpl(context: Context) : RecorderView(context) {

    private var vm: RecorderViewModel? = null

    init {
        inflate(context, R.layout.recorder_view, this)

        clipChildren = false
    }

    override fun init(
        recorderService: RecorderService,
        permissionMediator: RecordPermissionMediator,
        recipientMediator: RecordRecipientMediator,
        recordingListener: RecorderViewListener?,
        hintListener: RecordViewHintListener?
    ) {
        if (vm != null) {
            throw IllegalStateException("Record service already attached")
        }
        val listener = createListener(hintListener ?: DEFAULT, recordingListener)
        vm = RecorderViewModelImpl(
            recorderService,
            permissionMediator,
            recipientMediator,
            listener
        ).also(::bindToVm)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        checkHasParentWithoutClippingChildren(this)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        vm?.dispose()
        vm = null
    }
}
