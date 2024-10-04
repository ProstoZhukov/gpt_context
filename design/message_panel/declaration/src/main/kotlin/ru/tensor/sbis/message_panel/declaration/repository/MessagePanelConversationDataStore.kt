package ru.tensor.sbis.message_panel.declaration.repository

import io.reactivex.Observable
import java.util.*

/**
 * TODO: 11/13/2020 Добавить документацию
 *
 * @author ma.kolpakov
 */
interface MessagePanelConversationDataStore {

    val conversationUuid: Observable<Result<UUID?>>
    val documentUuid: Observable<Result<UUID?>>
    val answeredMessageUuid: Observable<Result<UUID>>
    val folderUuid: Observable<Result<UUID?>>
    val messageUuid: Observable<Result<UUID?>>

    fun setConversationUuid(conversationUuid: UUID?)
    fun setDocumentUuid(documentUuid: UUID?)
    fun setAnsweredMessageUuid(answeredMessageUuid: UUID?)
    fun setFolderUuid(folderUuid: UUID?)
    fun setMessageUuid(messageUuid: UUID?)
}