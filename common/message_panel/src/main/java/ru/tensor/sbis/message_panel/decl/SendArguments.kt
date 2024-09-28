package ru.tensor.sbis.message_panel.decl

import ru.tensor.sbis.attachments.generated.FileInfo
import ru.tensor.sbis.communicator.generated.SignActions
import java.util.*

/**
 * Параметры для отправки
 *
 * @author vv.chekurda
 */
data class SendArguments(
    /**
     * Текст для отправки
     */
    val text: String?,
    /**
     * Список вложений для отправки
     */
    val attachments: List<FileInfo>,
    /**
     * Список идентификаторов получателей
     */
    val recipientUuidList: List<UUID>,
    /**
     * Идентификатор документа
     */
    val documentUuid: UUID?,
    /**
     * Идентификатор обсуждения
     */
    val conversationUuid: UUID?,
    /**
     * Идентификатор папки
     */
    val folderUuid: UUID?,
    /**
     * Действия подписи
     */
    @Deprecated("TODO: 11/3/2020 https://online.sbis.ru/opendoc.html?guid=1088a9cf-77de-479e-8247-bea1e810380b")
    val signActions: SignActions?,
    /**
     * Идентификатор цитируемого сообщения
     */
    val quotedMessageUuid: UUID?,
    /**
     * Идентификатор сообщения, на которое отправляется ответ
     */
    val answeredMessageUuid: UUID?,
    /**
     * Дополнительная мета-информация для отправки особых сообщений
     */
    val metaData: String?
)