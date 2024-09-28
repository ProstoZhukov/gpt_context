package ru.tensor.sbis.message_panel.viewModel.livedata.attachments

import io.reactivex.Observable
import ru.tensor.sbis.attachments.models.AttachmentRegisterModel
import ru.tensor.sbis.attachments.ui.view.register.contract.AttachmentsActionListener
import ru.tensor.sbis.design.message_panel.decl.attachments.AttachmentUploadingProgress
import java.util.*

/**
 * Модель данных вложений
 *
 * @author vv.chekurda
 * @since 7/16/2019
 */
@Deprecated("https://online.sbis.ru/opendoc.html?guid=bb1754f3-4936-4641-bdc2-beec53070c4b")
interface MessagePanelAttachmentsData {
    val onAttachmentsActionsListener: AttachmentsActionListener
    val attachments: Observable<List<AttachmentRegisterModel>>
    val hasAttachments: Observable<Boolean>
    val draftUuidUpdater: Observable<UUID>
    val targetMessageUuid: Observable<UUID>
    /**
     * Подписка на обновление прогресса
     */
    val locationProgressUpdater: Observable<AttachmentUploadingProgress>
    val isAttachmentsEdited: Observable<Boolean>

    fun onAttachButtonClick()
    fun setAttachments(attachments: List<AttachmentRegisterModel>)
    fun setAttachmentProgress(progress: AttachmentUploadingProgress)

    fun setDraftUuid(uuid: UUID)
    fun getDraftUuid(): UUID?
    fun resetDraftUuid()

    fun setEditMessageUuid(uuid: UUID)
    fun getEditMessageUuid(): UUID?
    fun resetEditMessageUuid()
    fun onAttachmentsEdited()

    fun getTargetMessageUuid(): UUID?
}