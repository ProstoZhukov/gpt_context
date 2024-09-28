package ru.tensor.sbis.appdesign.combined_multiselection.datasource.dialog

import ru.tensor.sbis.appdesign.combined_multiselection.data.dialog.DemoDialogServiceResult
import ru.tensor.sbis.design.selection.ui.contract.list.ListMapper
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.design.selection.ui.model.share.dialog.DefaultDialogSelectorItemModel

/**
 * @author ma.kolpakov
 */
class DemoDialogDataMapper : ListMapper<DemoDialogServiceResult, SelectorItemModel> {

    override fun invoke(serviceData: DemoDialogServiceResult): List<SelectorItemModel> =
        serviceData.data.map {
            DefaultDialogSelectorItemModel(
                    id = it.id,
                    title = it.title,
                    subtitle = it.subtitle,
                    timestamp = it.timestamp,
                    syncStatus = it.syncStatus,
                    participantsCollage = it.participantsCollage,
                    participantsCount = it.participantsCount,
                    messageUuid = it.messageUuid,
                    messageType = it.messageType,
                    messagePersonCompany = it.messagePersonCompany,
                    messageText = it.messageText,
                    isOutgoing = it.isOutgoing,
                    isRead = it.isRead,
                    isReadByMe = it.isReadByMe,
                    isForMe = it.isForMe,
                    serviceText = it.serviceText,
                    unreadCount = it.unreadCount,
                    documentUuid = it.documentUuid,
                    documentType = it.documentType,
                    externalEntityTitle = it.externalEntityTitle,
                    attachments = null,
                    attachmentCount = it.attachmentCount,
                    isChatForOperations = it.isChatForOperations,
                    isPrivateChat = it.isPrivateChat,
                    isSocnetEvent = it.isSocnetEvent,
            )
        }
}
