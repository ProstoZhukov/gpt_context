package ru.tensor.sbis.message_panel.interactor.attachments

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import ru.tensor.sbis.attachments.generated.DataRefreshedAttachmentCallback
import ru.tensor.sbis.attachments.generated.FileInfo
import ru.tensor.sbis.disk.decl.params.DiskDocumentParams
import ru.tensor.sbis.platform.generated.Subscription
import java.util.UUID

/**
 * @author vv.chekurda
 */
interface MessagePanelAttachmentsInteractor {

    /**
     * Подписка на события обновлений списка вложений
     */
    fun setAttachmentListRefreshCallback(refreshCallback: DataRefreshedAttachmentCallback): Observable<Subscription>

    /**
     * Добавление вложений к сообщению с идентификатором [messageUuid]
     */
    fun addAttachments(
        messageUuid: UUID,
        uriList: List<String> = emptyList(),
        diskDocumentParamsList: List<DiskDocumentParams> = emptyList(),
        compressImages: Boolean = false
    ): Completable

    /**
     * Удаление вложения
     */
    fun deleteAttachment(attachment: FileInfo): Completable

    /**
     * Удаление вложения под локальной транзакцией редактирования.
     */
    fun deleteAttachmentByTransaction(attachment: FileInfo): Completable =
        Completable.error(
            NotImplementedError(
                "Ошибка загрузки вложений при редиктировании сообщения, необходима имплементация метода"
            )
        )

    /**
     * Загрузка списка вложений для сообщения с идентификатором [messageUuid].
     */
    fun loadAttachments(messageUuid: UUID): Single<List<FileInfo>>

    /**
     * Загрузка списка вложений для сообщения с идентификатором [messageUuid]
     * для локальной транзакции редактирования.
     */
    fun loadAttachmentsByTransaction(messageUuid: UUID): Single<List<FileInfo>> =
        Single.error(
            NotImplementedError(
                "Ошибка загрузки вложений при редиктировании сообщения, необходима имплементация метода"
            )
        )

    /**
     * Запустить повторную загрузку вложения.
     */
    fun restartUploadAttachment(attachment: FileInfo): Completable
}
