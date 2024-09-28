package ru.tensor.sbis.message_panel.viewModel.livedata.attachments

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import ru.tensor.sbis.attachments.models.AttachmentRegisterModel
import ru.tensor.sbis.attachments.ui.view.register.contract.AttachmentsActionListener
import ru.tensor.sbis.common.util.ResourceProvider
import ru.tensor.sbis.common.util.UUIDUtils
import ru.tensor.sbis.common_attachments.AttachmentPresenterHelper
import ru.tensor.sbis.design.message_panel.decl.attachments.AttachmentUploadingProgress
import ru.tensor.sbis.message_panel.R
import ru.tensor.sbis.message_panel.attachments.MessagePanelAttachmentHelper
import ru.tensor.sbis.message_panel.viewModel.livedata.MessagePanelNotifications
import ru.tensor.sbis.message_panel.viewModel.livedata.keyboard.ClosedByRequest
import ru.tensor.sbis.message_panel.viewModel.livedata.keyboard.KeyboardEventMediator
import java.util.*

/**
 * @author vv.chekurda
 */
internal class MessagePanelAttachmentsDataImpl(
    attachmentHelper: MessagePanelAttachmentHelper,
    private val keyboardEventMediator: KeyboardEventMediator,
    private val resourceProvider: ResourceProvider,
    private val notifications: MessagePanelNotifications
) : MessagePanelAttachmentsData {

    override val onAttachmentsActionsListener: AttachmentsActionListener = attachmentHelper
    override val attachments = BehaviorSubject.createDefault(emptyList<AttachmentRegisterModel>())
    override val hasAttachments: Observable<Boolean> = attachments.map(List<*>::isNotEmpty).distinctUntilChanged()
    override val locationProgressUpdater = PublishSubject.create<AttachmentUploadingProgress>()
    private val draftUuidBehaviorSubject = BehaviorSubject.create<UUID>()
    override val draftUuidUpdater: Observable<UUID> = draftUuidBehaviorSubject.filter { it != UUIDUtils.NIL_UUID }
    private val editMessageUuidBehaviorSubject = BehaviorSubject.create<UUID>()
    override val targetMessageUuid: Observable<UUID>
        get() = getEditMessageUuid()?.let { Observable.just(it) }
            ?: draftUuidUpdater
    override val isAttachmentsEdited = BehaviorSubject.createDefault(false)

    override fun onAttachButtonClick() {
        if (attachments.value!!.size >= AttachmentPresenterHelper.MAX_ATTACHMENTS_COUNT) {
            notifications.showToast(resourceProvider.getString(R.string.message_panel_to_many_attachments))
        }

        keyboardEventMediator.postKeyboardEvent(ClosedByRequest)
    }

    override fun setAttachments(attachments: List<AttachmentRegisterModel>) {
        this.attachments.onNext(attachments)
    }

    override fun setAttachmentProgress(progress: AttachmentUploadingProgress) {
        locationProgressUpdater.onNext(progress)
    }

    override fun setDraftUuid(uuid: UUID) {
        draftUuidBehaviorSubject.onNext(uuid)
    }

    override fun getDraftUuid(): UUID? =
        draftUuidBehaviorSubject.value?.let { if (it == UUIDUtils.NIL_UUID) null else it }

    override fun resetDraftUuid() {
        draftUuidBehaviorSubject.onNext(UUIDUtils.NIL_UUID)
    }

    override fun setEditMessageUuid(uuid: UUID) {
        editMessageUuidBehaviorSubject.onNext(uuid)
    }

    override fun getEditMessageUuid(): UUID? =
        editMessageUuidBehaviorSubject.value?.let { if (it == UUIDUtils.NIL_UUID) null else it }

    override fun resetEditMessageUuid() {
        editMessageUuidBehaviorSubject.onNext(UUIDUtils.NIL_UUID)
        isAttachmentsEdited.onNext(false)
    }

    override fun onAttachmentsEdited() {
        if (getEditMessageUuid() == null) return
        isAttachmentsEdited.onNext(true)
    }

    override fun getTargetMessageUuid(): UUID? =
        getEditMessageUuid() ?: getDraftUuid()
}