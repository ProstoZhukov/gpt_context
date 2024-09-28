/**
 * Расширения для маппинга контроллеровских моделей в модели компонента селектора
 *
 * @author vv.chekurda
 */
package ru.tensor.sbis.communicator.dialog_selection.data.mapper

import ru.tensor.sbis.common.generated.SyncStatus
import ru.tensor.sbis.communicator.generated.DocumentType as ControllerDocumentType
import ru.tensor.sbis.communicator.generated.RelevantMessageType
import ru.tensor.sbis.edo_decl.document.DocumentType
import ru.tensor.sbis.design.selection.ui.model.share.dialog.message.SelectionDialogMessageSyncStatus
import ru.tensor.sbis.design.selection.ui.model.share.dialog.message.SelectionDialogRelevantMessageType
import ru.tensor.sbis.communicator.generated.ProfilesFoldersResult

internal val SyncStatus.asNative: SelectionDialogMessageSyncStatus
    get() = when (this) {
        SyncStatus.SUCCEEDED -> SelectionDialogMessageSyncStatus.SUCCEEDED
        SyncStatus.ERROR     -> SelectionDialogMessageSyncStatus.ERROR
        else                 -> SelectionDialogMessageSyncStatus.OTHER
    }

internal val RelevantMessageType.asNative: SelectionDialogRelevantMessageType
    get() = when (this) {
        RelevantMessageType.MESSAGE -> SelectionDialogRelevantMessageType.MESSAGE
        RelevantMessageType.DRAFT   -> SelectionDialogRelevantMessageType.DRAFT
        RelevantMessageType.SENDING -> SelectionDialogRelevantMessageType.SENDING
        else                        -> SelectionDialogRelevantMessageType.OTHER
    }

internal val ControllerDocumentType.asNative: DocumentType get() =
    when (this) {
        ControllerDocumentType.DISK_FOLDER        -> DocumentType.DISC_FOLDER
        ControllerDocumentType.NEWS               -> DocumentType.NEWS
        ControllerDocumentType.SOCNET_NEWS        -> DocumentType.SOCNET_NEWS
        ControllerDocumentType.SOCNET_NEWS_REPOST -> DocumentType.NEWS_REPOST
        ControllerDocumentType.TASK               -> DocumentType.TASK
        ControllerDocumentType.GROUP_DISCUSSION_TOPIC -> DocumentType.GROUP_DISCUSSION_TOPIC
        else                                      -> DocumentType.UNSUPPORTED
    }

internal val ProfilesFoldersResult.size
    get() = profiles.size + folders.size

internal val ProfilesFoldersResult.isEmpty
    get() = size == 0

internal val ProfilesFoldersResult.uuidList
    get() = profiles.map { it.person.uuid }.plus(folders.map { it.uuid })