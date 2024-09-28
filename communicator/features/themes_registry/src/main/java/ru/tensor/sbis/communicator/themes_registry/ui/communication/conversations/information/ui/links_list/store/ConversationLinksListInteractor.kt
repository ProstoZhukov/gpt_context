package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.links_list.store

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.tensor.sbis.common.generated.CommandStatus
import ru.tensor.sbis.communicator.generated.LinkController
import java.util.UUID

/**
 * Интерактор экрана списка ссылок для информации о диалоге/канале.
 *
 * @author dv.baranov
 */
internal interface ConversationLinksListInteractor {

    /** Закрепить ссылку. */
    suspend fun pin(linkId: UUID): CommandStatus

    /** Закрепить ссылку. */
    suspend fun unpin(linkId: UUID): CommandStatus

    /** Удалить ссылку. */
    suspend fun delete(linkId: UUID): CommandStatus

    /** Добавить ссылку. */
    suspend fun add(link: String): CommandStatus
}

/**
 * Реализация интерактора экрана списка ссылок для информации о диалоге/канале.
 */
internal class ConversationLinksListInteractorImpl(
    private val themeUUID: UUID
) : ConversationLinksListInteractor {

    private val linkController by lazy { LinkController.instance() }
    private val dispatcherIO = Dispatchers.IO

    override suspend fun pin(linkId: UUID) = withContext(dispatcherIO) {
        linkController.pin(themeUUID, linkId)
    }

    override suspend fun unpin(linkId: UUID) = withContext(dispatcherIO) {
        linkController.unpin(themeUUID, linkId)
    }

    override suspend fun delete(linkId: UUID) = withContext(dispatcherIO) {
        linkController.detach(themeUUID, linkId)
    }

    override suspend fun add(link: String) = withContext(dispatcherIO) {
        linkController.attach(themeUUID, link)
    }
}