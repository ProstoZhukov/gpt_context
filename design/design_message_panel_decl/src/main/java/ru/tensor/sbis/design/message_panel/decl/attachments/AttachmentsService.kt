package ru.tensor.sbis.design.message_panel.decl.attachments

import ru.tensor.sbis.attachments.generated.DataRefreshedAttachmentCallback
import ru.tensor.sbis.attachments.generated.FileInfo
import ru.tensor.sbis.disk.decl.params.DiskDocumentParams
import ru.tensor.sbis.platform.generated.Subscription
import ru.tensor.sbis.plugin_struct.feature.Feature
import java.util.UUID

/**
 * @author ma.kolpakov
 */
interface AttachmentsService : AttachmentsServiceEvents, Feature {

    /**
     * Подписка на события обновлений списка вложений
     */
    suspend fun setAttachmentListRefreshCallback(
        refreshCallback: DataRefreshedAttachmentCallback
    ): Subscription

    /**
     * Добавление вложений к сообщению с идентификатором [messageUuid]
     */
    suspend fun addAttachments(
        messageUuid: UUID,
        uriList: List<String>,
        diskDocumentParamsList: List<DiskDocumentParams>
    )

    /**
     * Удаление вложения
     */
    suspend fun deleteAttachment(attachment: FileInfo)

    /**
     * Загрузка списка вложений для сообщения с идентификатором [messageUuid]
     */
    suspend fun loadAttachments(messageUuid: UUID): List<FileInfo>
}
