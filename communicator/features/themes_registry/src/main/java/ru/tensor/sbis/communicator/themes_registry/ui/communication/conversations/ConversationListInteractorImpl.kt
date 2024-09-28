package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.common.generated.CommandStatus
import ru.tensor.sbis.common.generated.ErrorCode
import ru.tensor.sbis.common.util.UUIDUtils
import ru.tensor.sbis.communicator.common.ControllerHelper.checkExecutionTime
import ru.tensor.sbis.communicator.common.data.theme.ConversationMapper
import ru.tensor.sbis.communicator.common.data.theme.ConversationModel
import ru.tensor.sbis.communicator.common.util.CommunicatorActivityStatusSubscriptionInitializer
import ru.tensor.sbis.communicator.contacts_declaration.controller.ContactsControllerWrapper
import ru.tensor.sbis.communicator.contacts_declaration.model.filter.ContactListFilter
import ru.tensor.sbis.communicator.contacts_declaration.model.filter.SortContacts
import ru.tensor.sbis.communicator.declaration.counter.CommunicatorCounterModel
import ru.tensor.sbis.communicator.generated.*
import ru.tensor.sbis.communicator.sbis_conversation.data.mapper.ContactVMAndProfileMapper
import ru.tensor.sbis.communicator.sbis_conversation.utils.subscribeDataRefresh
import ru.tensor.sbis.communicator.themes_registry.ThemesRegistryFacade.themesRegistryDependency
import ru.tensor.sbis.mvp.interactor.BaseInteractor
import ru.tensor.sbis.persons.ContactVM
import ru.tensor.sbis.persons.ConversationRegistryItem
import ru.tensor.sbis.profile_service.controller.employee_profile.EmployeeProfileControllerWrapper
import ru.tensor.sbis.toolbox_decl.counters.CounterProvider
import timber.log.Timber
import java.util.*

/**
 * Интерактор реестра диалогов/чатов
 *
 * @author vv.chekurda
 */
internal class ConversationListInteractorImpl(
    private val dialogController: DependencyProvider<DialogController>,
    private val messageController: DependencyProvider<MessageController>,
    private val chatControllerProvider: DependencyProvider<ChatController>,
    private val contactsControllerWrapper: ContactsControllerWrapper,
    private val employeeProfileControllerWrapperProvider: DependencyProvider<EmployeeProfileControllerWrapper>,
    private val conversationMapper: ConversationMapper,
    private val counterProvider: CounterProvider<CommunicatorCounterModel>,
    private val activityStatusSubscriptionsInitializer: CommunicatorActivityStatusSubscriptionInitializer,
) : BaseInteractor(), ConversationListInteractor {

    override fun deleteDialogs(uuids: Collection<UUID>, forAll: Boolean): Completable =
        Completable.fromRunnable {
            checkExecutionTime("DialogController.delete") {
                dialogController.get().delete(ArrayList(uuids), forAll)
            }
        }.compose(completableBackgroundSchedulers)

    override fun undeleteDialogs(uuids: Collection<UUID>): Completable =
        Completable.fromRunnable {
            checkExecutionTime("DialogController.undelete") {
                dialogController.get().undelete(ArrayList(uuids))
            }
        }.compose(completableBackgroundSchedulers)

    override fun markDialogs(uuids: Collection<UUID>, asRead: Boolean): Observable<List<ConversationRegistryItem>> =
        Observable.fromCallable {
            checkExecutionTime("DialogController.mark") {
                dialogController.get().mark(ArrayList(uuids), asRead)
            }
        }
            .compose(getObservableComputationScheduler())
            .map { dialogListResult: DialogListResult ->
                conversationMapper.applyToList(
                    dialogListResult.data,
                    forChats = false,
                    isConversationHiddenOrArchived = false
                )
            }
            .compose(getObservableBackgroundSchedulers())

    override fun moveDialogsToFolder(uuids: Collection<UUID>, folderUuid: UUID): Single<DialogListResult> {
        val folderUuidToMove = if (UUIDUtils.equals(UUIDUtils.NIL_UUID, folderUuid)) null else folderUuid
        return Single
            .fromCallable {
                checkExecutionTime("DialogController.moveToFolder") {
                    dialogController.get().moveToFolder(ArrayList(uuids), folderUuidToMove)
                }
            }
            .compose(getSingleBackgroundSchedulers())
    }

    override fun deleteArchiveMessage(archivedItem: ConversationModel): Completable =
        Completable.fromRunnable {
            checkExecutionTime("MessageController.deleteMessagesForEveryone") {
                cancelSendMessageWorkAndRemovePush(archivedItem.messageUuid)
                messageController.get().deleteMessagesForEveryone(
                    archivedItem.uuid,
                    archivedItem.messageUuid?.let { arrayListOf(it) } ?: arrayListOf())
            }
        }.compose(completableBackgroundSchedulers)

    override fun deleteArchiveMessageForMe(archivedItem: ConversationModel): Completable =
        Completable.fromRunnable {
            checkExecutionTime("MessageController.deleteMessagesForMeOnly") {
                cancelSendMessageWorkAndRemovePush(archivedItem.messageUuid)
                messageController.get().deleteMessagesForMeOnly(
                    archivedItem.uuid,
                    archivedItem.messageUuid?.let { arrayListOf(it) } ?: arrayListOf())
            }
        }.compose(completableBackgroundSchedulers)

    private fun cancelSendMessageWorkAndRemovePush(messageUuid: UUID?) {
        if (messageUuid == null) return
        themesRegistryDependency.sendMessageManagerProvider
            ?.getSendMessageManager()
            ?.cancelSendMessageByMessageActionDelete(messageUuid)
    }

    override fun hideConversation(uuid: UUID): Completable =
        Completable.fromRunnable { chatControllerProvider.get().hide(uuid) }
            .compose(completableBackgroundSchedulers)

    override fun formatUnreadCount(count: Int): String =
        conversationMapper.formatUnreadCount(count)

    override fun observeContactsUpdates(): Observable<Unit> =
        Observable.fromCallable { contactsControllerWrapper }
            .flatMap { it.subscribeDataRefresh() }
            .compose(getObservableBackgroundSchedulers())

    override fun syncContacts(): Completable =
        Completable.fromAction { contactsControllerWrapper.list(ContactListFilter()) }
            .subscribeOn(Schedulers.io())

    override fun getRecipientList(searchString: String?, count: Int, refresh: Boolean): Observable<List<ContactVM>> =
        Observable.fromCallable {
            if (refresh) {
                contactsControllerWrapper.refresh(
                    ContactListFilter(
                        searchQuery = searchString,
                        sort = SortContacts.BY_DATE,
                        folderUuid = null,
                        hasMessages = true,
                        excludeCurrentUserIfNoSearch = false,
                        from = 0,
                        count = count
                    )
                )
            } else {
                contactsControllerWrapper.list(
                    ContactListFilter(
                        searchQuery = searchString,
                        sort = SortContacts.BY_DATE,
                        folderUuid = null,
                        hasMessages = true,
                        excludeCurrentUserIfNoSearch = false,
                        from = 0,
                        count = count
                    )
                )
            }
        }
            .doOnNext {
                activityStatusSubscriptionsInitializer.initialize(
                    it.contacts.map { contact ->
                        contact.uuid
                    }
                )
            }
            .map { ContactVMAndProfileMapper.mapContactToContactVM(it.contacts) }
            .compose(getObservableBackgroundSchedulers())

    override fun getPersonUuidsByDepartments(departmentUuids: List<UUID>): Single<List<UUID>> =
        Single.fromCallable {
            val resultPersonUuids = arrayListOf<UUID>()
            for (departmentUuid in departmentUuids) {
                val departmentPersonUuids = employeeProfileControllerWrapperProvider.get().getProfilesByGroupUuid(departmentUuid).map { it.uuid }
                    resultPersonUuids.addAll(departmentPersonUuids)
            }
            return@fromCallable resultPersonUuids
        }.compose(getSingleBackgroundSchedulers())

    override fun markReadPush(dialogUuid: UUID, messageId: UUID) {
        Completable.fromRunnable {
            checkExecutionTime("MessageController.markReadOnPushNotification") {
                messageController.get().markReadOnPushNotification(dialogUuid, messageId)
            }
        }
            .compose(completableBackgroundSchedulers)
            .doOnError { error: Throwable? ->
                Timber.e(error, "Couldn't mark push")
            }
            .subscribe()
    }

    override fun pinChat(uuid: UUID): Completable {
        return Completable.fromRunnable {
            checkExecutionTime("ChatController.pin") {
                chatControllerProvider.get().pin(uuid)
            }
        }
            .compose(completableBackgroundSchedulers)
    }

    override fun unpinChat(uuid: UUID): Completable {
        return Completable.fromRunnable {
            checkExecutionTime("ChatController.unpin") {
                chatControllerProvider.get().unpin(uuid)
            }
        }
            .compose(completableBackgroundSchedulers)
    }

    override fun unhideChat(uuid: UUID): Completable {
        return Completable.fromRunnable { chatControllerProvider.get().unhide(uuid) }
            .compose(completableBackgroundSchedulers)
    }

    override fun markDialogRegistryAsViewed(): Completable {
        return Completable.fromRunnable {
            checkExecutionTime("DialogController.markDialogRegistryAsViewed") {
                dialogController.get().markDialogRegistryAsViewed()
            }
        }
            .compose(completableBackgroundSchedulers)
    }

    override fun markDialogAsViewed(uuids: UUID): Completable {
        return Completable.fromRunnable {
            checkExecutionTime("DialogController.markDialogAsViewed") {
                dialogController.get().markDialogAsViewed(uuids)
            }
        }
            .compose(completableBackgroundSchedulers)
    }

    override fun markChatRegistryAsViewed(): Completable =
        Completable.fromRunnable {
            checkExecutionTime("ChatController.markChatRegistryAsViewed") {
                chatControllerProvider.get().markChatRegistryAsViewed()
            }
        }
            .compose(completableBackgroundSchedulers)

    override fun markChannelAsViewed(uuid: UUID): Completable =
        Completable.fromRunnable {
            checkExecutionTime("ChatController.markChatAsViewed") {
                chatControllerProvider.get().markChatAsViewed(uuid)
            }
        }
            .compose(completableBackgroundSchedulers)

    override fun communicatorCounter(): Observable<CommunicatorCounterModel> =
        counterProvider.counterEventObservable
            .compose(getObservableBackgroundSchedulers())

    override fun cancelThemeSynchronizations() {
        ThemeController.instance().cancelAll()
    }

    override fun onConversationMessageButtonClick(messageId: UUID, buttonId: String): Single<CommandStatus> =
        Single.fromCallable { messageController.get().doMessageButtonAction(messageId, buttonId) }
            .compose(getSingleBackgroundSchedulers())

    override suspend fun getConversationModel(noticeType: Int): ConversationModel? =
        withContext(Dispatchers.IO) {
            val result = dialogController.get().getConversationViewDataByNoticeType(noticeType)
            val conversation = result.data
            if (result.status.errorCode == ErrorCode.SUCCESS && conversation != null) {
                conversationMapper.apply(conversation)
            } else {
                null
            }
        }
}
