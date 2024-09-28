package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import ru.tensor.sbis.common.generated.CommandStatus
import ru.tensor.sbis.persons.ConversationRegistryItem
import ru.tensor.sbis.persons.ContactVM
import ru.tensor.sbis.communicator.common.data.theme.ConversationModel
import ru.tensor.sbis.communicator.declaration.counter.CommunicatorCounterModel
import ru.tensor.sbis.communicator.generated.DialogListResult
import java.util.*

/**
 * Интерфейс для взатмодействия с данными о диалогах и контактах
 *
 * @author rv.krohalev
 */
internal interface ConversationListInteractor {
    /**@SelfDocumented */
    fun markDialogs(uuids: Collection<UUID>, asRead: Boolean): Observable<List<ConversationRegistryItem>>

    /**@SelfDocumented */
    fun moveDialogsToFolder(uuids: Collection<UUID>, folderUuid: UUID): Single<DialogListResult>

    /**@SelfDocumented */
    fun deleteDialogs(uuids: Collection<UUID>, forAll: Boolean): Completable

    /**@SelfDocumented */
    fun undeleteDialogs(uuids: Collection<UUID>): Completable

    /**@SelfDocumented */
    fun deleteArchiveMessage(archivedItem: ConversationModel): Completable

    /**@SelfDocumented */
    fun deleteArchiveMessageForMe(archivedItem: ConversationModel): Completable

    /**@SelfDocumented */
    fun hideConversation(uuid: UUID): Completable

    /**@SelfDocumented */
    fun formatUnreadCount(count: Int): String

    /**@SelfDocumented */
    fun observeContactsUpdates(): Observable<Unit>

    /**@SelfDocumented */
    fun syncContacts(): Completable

    /**@SelfDocumented */
    fun getRecipientList(searchString: String?, count: Int, refresh: Boolean = false): Observable<List<ContactVM>>

    /**@SelfDocumented */
    fun getPersonUuidsByDepartments(departmentUuids: List<UUID>): Single<List<UUID>>

    /**@SelfDocumented */
    fun markReadPush(dialogUuid: UUID, messageId: UUID)

    /**@SelfDocumented */
    fun pinChat(uuid: UUID): Completable

    /**@SelfDocumented */
    fun unpinChat(uuid: UUID): Completable

    /**@SelfDocumented */
    fun unhideChat(uuid: UUID): Completable

    /**@SelfDocumented */
    fun markDialogRegistryAsViewed(): Completable

    /**@SelfDocumented */
    fun markDialogAsViewed(uuids: UUID): Completable

    /**@SelfDocumented */
    fun markChatRegistryAsViewed(): Completable

    /**@SelfDocumented */
    fun markChannelAsViewed(uuid: UUID): Completable

    /**@SelfDocumented */
    fun communicatorCounter(): Observable<CommunicatorCounterModel>

    /** @SelfDocumented */
    fun cancelThemeSynchronizations()

    /** @SelfDocumented */
    fun onConversationMessageButtonClick(messageId: UUID, buttonId: String): Single<CommandStatus>

    /** @SelfDocumented */
    suspend fun getConversationModel(noticeType: Int): ConversationModel?
}