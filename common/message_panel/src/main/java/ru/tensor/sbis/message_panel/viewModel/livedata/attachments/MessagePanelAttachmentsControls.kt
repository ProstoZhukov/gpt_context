package ru.tensor.sbis.message_panel.viewModel.livedata.attachments

import io.reactivex.Observable
import ru.tensor.sbis.attachments.ui.view.register.AttachmentsViewVisibility

/**
 * Интерфейс элементов управления вложений
 *
 * @author vv.chekurda
 * @since 7/17/2019
 */
@Deprecated("https://online.sbis.ru/opendoc.html?guid=bb1754f3-4936-4641-bdc2-beec53070c4b")
interface MessagePanelAttachmentsControls {
    val isAttachmentsOnEditTransaction: Boolean
    
    val attachmentsButtonEnabled: Observable<Boolean>
    val attachmentsButtonVisible: Observable<Boolean>
    val attachmentsVisibility: Observable<AttachmentsViewVisibility>
    val attachmentsDeletable: Observable<Boolean>
    val attachmentsRestartable: Observable<Boolean>
    val attachmentsErrorVisible: Observable<Boolean>

    fun showAttachmentsButton(show: Boolean)
    fun forceChangeAttachmentsButtonVisibility(isVisible: Boolean)
    fun setAttachmentsDeletable(isRemovable: Boolean)
    fun setAttachmentsRestartable(isRestartable: Boolean)
    fun setAttachmentsErrorVisible(isErrorVisible: Boolean)
    fun setAttachmentsInEditTransaction(isEditTransaction: Boolean)
}