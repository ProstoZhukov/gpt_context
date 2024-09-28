package ru.tensor.sbis.communicator.sbis_conversation.interactor

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.tensor.sbis.attachments.generated.AttachmentController
import ru.tensor.sbis.attachments.generated.ConfigOptions
import ru.tensor.sbis.attachments.generated.FileInfoViewModel
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.common.generated.CommandStatus
import ru.tensor.sbis.common.generated.ErrorCode
import ru.tensor.sbis.common.util.UUIDUtils
import ru.tensor.sbis.common.util.asArrayList
import ru.tensor.sbis.communicator.common.conversation.data.Message
import ru.tensor.sbis.communicator.common.crud.ThemeRepository
import ru.tensor.sbis.communicator.generated.*
import ru.tensor.sbis.communicator.sbis_conversation.data.mapper.MessageMapper
import ru.tensor.sbis.communicator.sbis_conversation.data.model.ConversationMessage
import ru.tensor.sbis.communicator.core.utils.subscribeDataRefresh
import ru.tensor.sbis.communicator.core.utils.subscribeTypingUsers
import ru.tensor.sbis.communicator.sbis_conversation.interactor.data.ConversationDataInteractor
import ru.tensor.sbis.communicator.sbis_conversation.ui.crud.ConversationPrefetchManagerImpl
import ru.tensor.sbis.communicator.sbis_conversation.ui.crud.ConversationRepository
import ru.tensor.sbis.communicator.common.conversation.crud.MessageControllerBinaryMapper
import ru.tensor.sbis.communicator.common.ControllerHelper.checkExecutionTime
import ru.tensor.sbis.communicator.common.data.ThreadInfo
import ru.tensor.sbis.communicator.generated.ThreadInfo as CppThreadInfo
import ru.tensor.sbis.communicator.common.util.CommunicatorActivityStatusSubscriptionInitializer
import ru.tensor.sbis.communicator.sbis_conversation.CommunicatorSbisConversationPlugin.singletonComponent
import ru.tensor.sbis.design.profile_decl.person.InitialsStubData
import ru.tensor.sbis.design.profile_decl.person.PersonData
import ru.tensor.sbis.edo_decl.document.DocumentType
import ru.tensor.sbis.message_panel.decl.AttachmentControllerProvider
import ru.tensor.sbis.person_decl.profile.model.ProfileActivityStatus
import ru.tensor.sbis.mvp.interactor.BaseInteractor
import ru.tensor.sbis.platform.generated.Subscription
import ru.tensor.sbis.profiles.generated.PersonController
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList

/**
 * Реализация интерактора экрана сообщений сбис
 *
 * @author vv.chekurda
 */
internal class ConversationInteractorImpl @JvmOverloads constructor(
    private val themeRepository: ThemeRepository,
    private val dialogControllerProvider: DependencyProvider<DialogController>,
    private val dialogDocumentControllerProvider: DependencyProvider<DialogDocumentController>,
    private val chatControllerProvider: DependencyProvider<ChatController>,
    private val messageControllerProvider: DependencyProvider<MessageController>,
    private val themeParticipantsControllerProvider: DependencyProvider<ThemeParticipantsController>,
    private val conversationDataInteractor: ConversationDataInteractor,
    private val messageMapper: MessageMapper,
    private val activityStatusSubscriptionInitializer: CommunicatorActivityStatusSubscriptionInitializer,
    private val prefetchManager: ConversationPrefetchManagerImpl? = null,
    private val conversationRepository: ConversationRepository,
    private val messageControllerBinaryMapper: MessageControllerBinaryMapper,
    private val attachmentControllerProvider: AttachmentControllerProvider?,
    private val personControllerProvider: DependencyProvider<PersonController>
) : BaseInteractor(),
    ConversationInteractor,
    ConversationDataInteractor by conversationDataInteractor {

    override fun clearReferences() {
        messageMapper.clearReferences()
    }

    override fun loadConversationData(
        conversationUuid: UUID?,
        documentUuid: UUID?,
        startMessage: ConversationMessage?,
        messagesCount: Int,
        isChat: Boolean,
        isConsultation: Boolean
    ): Observable<ConversationDataInteractor.ConversationDataResult> =
        prefetchManager?.alterLoadConversationData(
            conversationUuid,
            documentUuid,
            isChat
        ) ?: conversationDataInteractor.loadConversationData(
            conversationUuid,
            documentUuid,
            startMessage,
            messagesCount,
            isChat,
            isConsultation
        )

    override fun backgroundLoadConversationData(
        conversationUuid: UUID?,
        documentUuid: UUID?,
        startMessage: ConversationMessage?,
        messagesCount: Int,
        isChat: Boolean,
        isConsultation: Boolean
    ): Observable<ConversationDataInteractor.ConversationDataResult> =
        prefetchManager?.alterLoadConversationData(
            conversationUuid,
            documentUuid,
            isChat
        ) ?: conversationDataInteractor.backgroundLoadConversationData(
            conversationUuid,
            documentUuid,
            startMessage,
            messagesCount,
            isChat,
            isConsultation
        )

    override fun onMessageSigningSuccess(message: Message): Single<CommandStatus> {
        return Single.fromCallable { messageControllerProvider.get().acceptMessageSignRequest(message.uuid) }
            .compose(getSingleBackgroundSchedulers())
    }

    override fun loadServiceMessages(serviceGroupUuid: UUID): Observable<List<ConversationMessage>> =
        Observable.fromCallable {
            messageControllerProvider.get().getServiceGroupContent(serviceGroupUuid)
        }
            .map { messageControllerBinaryMapper.map(it) }
            .map { messageMapper.apply(it.apply { reverse() }) }
            .compose(getObservableBackgroundSchedulers())

    override fun createDraftDialogIfNotExists(
        dialogUuid: UUID?,
        documentUuid: UUID?,
        documentType: DocumentType?,
        folderUuid: UUID?,
        newMessageUuid: UUID?,
        participantUuids: List<UUID>,
        threadInfo: ThreadInfo?
    ): Single<Pair<CreateDraftDialogResult, List<UUID>>> =
        when {
            documentUuid != null -> Single.fromCallable {
                // Для переписки по документу диалог уже возможно существует. Возвращаем его UUID в таком случае
                val checkForExistence = dialogControllerProvider.get().checkDocumentDialogForExistence(documentUuid)
                if (checkForExistence.status.errorCode == ErrorCode.SUCCESS) {
                    checkForExistence.data?.let {
                        return@fromCallable CreateDraftDialogResult(
                            Dialog(it),
                            null,
                            checkForExistence.status
                        ) to participantUuids
                    }
                }

                documentType?.toDialogDocumentType()?.let {
                    dialogDocumentControllerProvider.get()
                        .saveDocumentInfo(documentUuid, it, null, null, null, null)
                } ?: Timber.w("Unexpected documentType $documentType")

                checkExecutionTime("DialogController.createDraftDialog") {
                    dialogControllerProvider.get().createDraftDialog(
                        documentUuid,
                        documentUuid,
                        folderUuid,
                        newMessageUuid,
                        participantUuids.asArrayList(),
                        null
                    ) to participantUuids
                }
            }
            else -> Single.fromCallable {
                checkExecutionTime("DialogController.createDraftDialog") {
                    dialogControllerProvider.get().createDraftDialog(
                        dialogUuid,
                        null,
                        folderUuid,
                        newMessageUuid,
                        participantUuids.asArrayList(),
                        threadInfo?.let {
                            CppThreadInfo(
                                it.parentConversationUuid,
                                it.isChat,
                                it.parentConversationMessageUuid
                            )
                        }
                    ) to participantUuids
                }
            }
        }
            .flatMap { saveRecipientsInDraftMessage(it) }
            .compose(getSingleBackgroundSchedulers())

    override fun deleteDraftDialog(dialogUuid: UUID): Completable =
        Completable.fromAction {
            dialogControllerProvider.get().deleteDraft(dialogUuid)
        }.compose(completableBackgroundSchedulers)

    private fun saveRecipientsInDraftMessage(
        result: Pair<CreateDraftDialogResult, List<UUID>>
    ): Single<Pair<CreateDraftDialogResult, List<UUID>>> =
        if (result.second.isNullOrEmpty()) {
            Single.just(result)
        } else {
            Single.fromCallable {
                val dialogUuid = result.first.dialog!!.uuid
                // сохраняем список участников диалога в драфтовое сообщение в этом диалоге
                messageControllerProvider.get().apply {
                    val draftMessage = checkExecutionTime("MessageController.getDraft") { getDraft(dialogUuid) }
                    draftMessage.recipients = result.second.asArrayList()
                    checkExecutionTime("MessageController.saveDraft") { saveDraft(dialogUuid, draftMessage) }
                }
                result
            }
        }

    @Deprecated("")
    override fun deleteMessageForMe(conversationUuid: UUID, messageUuid: UUID): Single<MessageListResult> =
        Single.fromCallable {
            checkExecutionTime("MessageController.deleteMessagesForMeOnly") {
                cancelSendMessageWorkAndRemovePush(messageUuid)
                messageControllerProvider.get().deleteMessagesForMeOnly(
                    conversationUuid, arrayListOf(
                        messageUuid
                    )
                )
            }
        }
            .compose(getSingleBackgroundSchedulers())

    override fun deleteMessageForEveryone(conversationUuid: UUID, messageUuid: UUID): Completable =
        Single.fromCallable {
            val result = checkExecutionTime("MessageController.deleteMessagesForEveryone") {
                messageControllerProvider.get()
                    .deleteMessagesForEveryone(
                        conversationUuid,
                        arrayListOf(messageUuid)
                    )
            }
            val status = result.status
            if (status.errorCode != ErrorCode.SUCCESS) {
                throw RuntimeException("Failed to remove message status: $status, messageUuid: $messageUuid")
            }
            cancelSendMessageWorkAndRemovePush(messageUuid)
        }
            .ignoreElement()
            .compose(completableBackgroundSchedulers)

    override suspend fun checkMessagesArea(filter: MessageFilter): CommandStatus =
        withContext(Dispatchers.IO) {
            messageControllerProvider.get().checkMessagesArea(filter)
        }

    override suspend fun onChatBotButtonClick(
        conversationUuid: UUID,
        serviceMessageUUID: UUID,
        title: String,
    ): Unit = withContext(Dispatchers.IO) {
        messageControllerProvider.get().enqueueMessage2(
            conversationUuid,
            null,
            title,
            null,
            null,
            null,
            null,
            null,
            serviceMessageUUID,
            null,
        )
    }

    override fun updateDialog(conversationUuid: UUID, participantUuids: Collection<UUID>): Single<CommandStatus> {
        val participants = ArrayList(participantUuids)
        return Single.fromCallable {
            checkExecutionTime("DialogController.updateDialog") {
                dialogControllerProvider.get().updateDialog(conversationUuid, participants)
            }
        }
            .compose(getSingleBackgroundSchedulers())
    }

    override fun deleteDialog(conversationUuid: UUID): Single<CommandStatus> =
        Single.fromCallable {
            checkExecutionTime("DialogController.delete") {
                dialogControllerProvider.get().delete(arrayListOf(conversationUuid), false)
            }
        }
            .map { obj: DialogListResult -> obj.status }
            .compose(getSingleBackgroundSchedulers())

    override fun deleteDialogFromArchive(conversationUuid: UUID, deleteFromArchive: Boolean): Single<CommandStatus> =
        Single.fromCallable {
            checkExecutionTime("DialogController.delete") {
                dialogControllerProvider.get().delete(arrayListOf(conversationUuid), deleteFromArchive)
            }.status
        }
            .compose(getSingleBackgroundSchedulers())

    override fun restoreDialog(dialogUuid: UUID): Completable =
        Completable.fromRunnable {
            checkExecutionTime("DialogController.undelete") {
                dialogControllerProvider.get().undelete(arrayListOf(dialogUuid))
            }
        }
            .compose(completableBackgroundSchedulers)

    override fun forceResendMessage(messageUuid: UUID): Single<CommandStatus> =
        Single.fromCallable {
            messageControllerProvider.get().forceResendMessage(messageUuid)
        }.compose(getSingleBackgroundSchedulers())

    override fun markMessagesAsRead(messagesUuid: ArrayList<UUID>): Single<MarkMessagesResult> {
        return Single.fromCallable {
            checkExecutionTime("MessageController.markMessagesAsRead") {
                messageControllerProvider.get().markMessagesAsRead(messagesUuid)
            }
        }
            .compose(getSingleBackgroundSchedulers())
    }

    override fun markGroupServiceMessageAsRead(
        conversationUuid: UUID,
        serviceMessageGroup: List<ServiceMessageGroup>
    ): Observable<Pair<ServiceMessageGroup, CommandStatus>> {
        return Observable
            .fromIterable(serviceMessageGroup)
            .flatMap<CommandStatus, Pair<ServiceMessageGroup, CommandStatus>>(
                { group ->
                    Observable.fromCallable {
                        checkExecutionTime("MessageController.markMessageGroupAsRead") {
                            messageControllerProvider.get()
                                .markMessageGroupAsRead(conversationUuid, group.firstMessageUuid)
                        }
                    }
                })
            { a: ServiceMessageGroup?, b: CommandStatus? -> Pair(a!!, b!!) }
            .compose(getObservableBackgroundSchedulers())
    }

    override fun markAllMessages(conversationUuid: UUID): Single<CommandStatus> =
        Single.fromCallable {
            checkExecutionTime("MessageController.markAllMessages") {
                messageControllerProvider.get().markAllMessagesAsRead(conversationUuid)
            }
        }
            .compose(getSingleBackgroundSchedulers())

    override fun rejectSignature(message: Message): Single<CommandStatus> =
        Single.fromCallable { messageControllerProvider.get().declineMessageSignRequest(message.uuid) }
            .compose(getSingleBackgroundSchedulers())

    override fun subscribeActivityObserver(personUuid: UUID): Observable<ProfileActivityStatus> =
        Observable.create { emitter ->
            val scope = CoroutineScope(Dispatchers.IO)
            scope.launch {
                activityStatusSubscriptionInitializer.observe(personUuid).collect {
                    emitter.onNext(it)
                }
            }
            emitter.setCancellable {
                scope.cancel()
            }
        }

    override fun forceUpdateActivityStatus(personUuid: UUID): Completable =
        Completable.fromAction {
            activityStatusSubscriptionInitializer.forceUpdateActivityStatus(personUuid)
        }.subscribeOn(Schedulers.io())

    override fun quitAndHideChat(chatUuid: UUID): Single<CommandStatus> =
        Single.fromCallable { chatControllerProvider.get().quitAndHide(chatUuid) }
            .compose(getSingleBackgroundSchedulers())

    override fun quitChat(chatUuid: UUID): Single<CommandStatus> =
        Single.fromCallable { chatControllerProvider.get().quit(chatUuid) }
            .compose(getSingleBackgroundSchedulers())

    override fun hideChat(chatUuid: UUID): Single<CommandStatus> =
        Single.fromCallable { chatControllerProvider.get().hide(chatUuid) }
            .compose(getSingleBackgroundSchedulers())

    override fun restoreChat(chatUuid: UUID): Single<CommandStatus> =
        Single.fromCallable { chatControllerProvider.get().restore(chatUuid) }
            .compose(getSingleBackgroundSchedulers())

    override fun loadServiceMessageNames(serviceMessageUuid: UUID, newPersonListCount: Int): Single<ConversationMessage> =
        Single.fromCallable { messageControllerProvider.get().getMessageWithPersonList(
            serviceMessageUuid,
            newPersonListCount
        ) }
            .map { messageMapper.apply(it.data!!) }
            .compose(getSingleBackgroundSchedulers())

    override fun changeNotificationType(chatUuid: UUID, onlyPersonal: Boolean): Single<ChatResult> =
        Single.fromCallable {
            val chatNotificationOption =
                if (onlyPersonal) {
                    ChatNotificationOptions(
                        notificationsTurnedOff = false,
                        notificationsPrivateEvents = true,
                        notificationsAdminEvents = false
                    )
                } else {
                    ChatNotificationOptions(
                        notificationsTurnedOff = false,
                        notificationsPrivateEvents = true,
                        notificationsAdminEvents = true
                    )
                }
            chatControllerProvider.get().update(
                chatUuid,
                null,
                chatNotificationOption,
                null,
                null,
                null,
                null
            )
        }.compose(getSingleBackgroundSchedulers())

    override fun addChatParticipants(chatUuid: UUID, participantUuids: List<UUID>): Completable =
        Completable.fromAction {
            checkExecutionTime("ThemeParticipantsController.addParticipants") {
                themeParticipantsControllerProvider.get().addParticipants(
                    chatUuid, ArrayList(
                        participantUuids
                    )
                )
            }
        }
            .compose(completableBackgroundSchedulers)

    override fun getMessageText(conversationUuid: UUID, messageUuid: UUID): Single<MessageTextWithMentions> =
        Single.fromCallable {
            checkExecutionTime("MessageController.getMessageText") {
                messageControllerProvider.get().getMessageText(messageUuid, conversationUuid)
            }
        }
            .compose(getSingleBackgroundSchedulers())

    override fun getMessageError(messageUuid: UUID): Maybe<MessageErrorResult> =
        Maybe.create { emitter ->
            val result = messageControllerProvider.get().getMessageError(messageUuid)
            if (result == null) {
                emitter.onComplete()
            } else {
                emitter.onSuccess(result)
            }
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    override fun getMessageByUuid(uuid: UUID): Maybe<ConversationMessage> =
        Maybe.fromCallable {
            checkExecutionTime("MessageController.read") {
                messageControllerProvider.get().read(uuid)
            }
        }
            .map { messageControllerBinaryMapper.map(it) }
            .map(messageMapper::apply)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    override fun getPrivateChatUUID(participantUUID: UUID): Single<UUID> =
        chatControllerProvider.async.firstOrError()
            .map { it.createPrivate(null, participantUUID).data!!.uuid }
            .compose(getSingleBackgroundSchedulers())

    override fun observeThemeControllerUpdates(): Observable<HashMap<String, String>> =
        Observable.fromCallable { themeRepository }
            .flatMap { it.subscribeDataRefresh() }
            .compose(getObservableBackgroundSchedulers())

    override fun observeMessageControllerCallbackSubscription(callback: DataRefreshedMessageControllerCallback): Observable<Subscription> {
        return Observable.fromCallable { messageControllerProvider.get().dataRefreshed().subscribe(callback) }
            .compose(getObservableBackgroundSchedulers())
    }

    override fun observeTypingUsers(): Observable<List<String>> =
        Observable.fromCallable { themeRepository }
            .flatMap { it.subscribeTypingUsers() }
            .compose(getObservableBackgroundSchedulers())

    override fun onThemeBeforeOpened(themeUUID: UUID) = themeRepository.onThemeBeforeOpened(themeUUID)

    override fun onThemeAfterOpened(themeUUID: UUID, filter: MessageFilter?, isChat: Boolean): Completable =
        Completable.fromRunnable { themeRepository.onThemeAfterOpened(themeUUID, filter, isChat) }
            .compose(completableBackgroundSchedulers)

    override fun onThemeClosed(themeUUID: UUID): Completable =
        Completable.fromRunnable { themeRepository.onThemeClosed(themeUUID) }
            .compose(completableBackgroundSchedulers)

    override fun getUrlById(themeUUID: UUID): Observable<String> =
        Observable.fromCallable { themeRepository.getUrlByUuid(themeUUID) }
            .compose(getObservableBackgroundSchedulers())

    override fun unhideChat(chatUuid: UUID): Completable =
        Completable.fromRunnable { chatControllerProvider.get().unhide(chatUuid) }
            .compose(completableBackgroundSchedulers)

    override fun pinMessage(chatUuid: UUID, messageUuid: UUID): Completable =
        Completable.fromRunnable { chatControllerProvider.get().pinMessage(chatUuid, messageUuid) }
            .compose(completableBackgroundSchedulers)

    override fun unpinMessage(chatUuid: UUID): Completable =
        Completable.fromRunnable { chatControllerProvider.get().unpinMessage(chatUuid) }
            .compose(completableBackgroundSchedulers)

    override fun deserializeMessage(data: ByteArray): ConversationMessage {
        return conversationRepository.deserializeMessage(data).let { controllerMessage ->
            messageMapper.apply(controllerMessage)
        }
    }

    override fun acceptAccessRequest(message: Message, accessType: DocumentAccessType): Single<CommandStatus> {
        return Single.fromCallable { messageControllerProvider.get().acceptAccessRequest(message.uuid, accessType) }
            .compose(getSingleBackgroundSchedulers())
    }

    override fun declineAccessRequest(message: Message): Single<CommandStatus> {
        return Single.fromCallable { messageControllerProvider.get().declineAccessRequest(message.uuid) }
            .compose(getSingleBackgroundSchedulers())
    }

    override fun setDialogTitle(dialogUuid: UUID, newTitle: String): Single<CommandStatus> =
        Single.fromCallable {
            dialogControllerProvider.get().setDialogTitle(
                dialogUuid,
                newTitle
            )
        }.compose(getSingleBackgroundSchedulers())

    override fun getAttachmentsUuidsToSign(fileInfoViewModels: ArrayList<FileInfoViewModel>): Single<List<UUID>> {
        return Single.fromCallable {
            fileInfoViewModels.mapNotNull {
                val fileInfo = attachmentControllerProvider?.getAttachmentController()?.get()
                    ?.readByIdFromCache(it.attachId, it.redId)
                fileInfo
            }.map {
                val id = it.cloudAttachId ?: if (!it.isUploading) {
                    it.diskFileId
                } else {
                    null
                }
                val result = UUIDUtils.fromString(id)
                result
            }
        }.compose(getSingleBackgroundSchedulers())
    }

    override fun cancelUploadAttachment(messageUuid: UUID, attachmentLocalId: Long): Single<CommandStatus> =
        Single.fromCallable {
            messageControllerProvider.get().cancelAttachmentUpload(messageUuid, attachmentLocalId)
        }.compose(getSingleBackgroundSchedulers())

    override fun setAttachmentsUploadErrors(activated: Boolean): Completable =
        Completable.fromAction {
            AttachmentController.instance().setOption(ConfigOptions.TEST_UPLOAD_ERROR, activated.toString())
        }.compose(completableBackgroundSchedulers)

    override fun getMessageRecipients(message: Message): Single<List<PersonData>> =
        Single.fromCallable {
            val threadParticipants = messageControllerProvider.get().getThreadParticipants(message.uuid)
            val receiverCount = message.receiverCount
            if (threadParticipants.isEmpty()
                || receiverCount == 0
                || receiverCount > DEFAULT_THREAD_MAX_PARTICIPANT_COUNT
            ) {
                emptyList()
            } else {
                personControllerProvider.get().getPersonsFromCacheKeepOrder(threadParticipants.asArrayList())
                    .map {
                        PersonData(
                            it.uuid,
                            it.photoUrl,
                            it.photoDecoration?.let { decoration ->
                                InitialsStubData(
                                    decoration.initials,
                                    decoration.backgroundColorHex
                                )
                            }
                        )
                    }
            }
        }.compose(getSingleBackgroundSchedulers())

    private fun DocumentType?.toDialogDocumentType(): ru.tensor.sbis.communicator.generated.DocumentType? {
        return when (this) {
            DocumentType.SHARED_FILE -> ru.tensor.sbis.communicator.generated.DocumentType.DISK
            DocumentType.DISC_FOLDER -> ru.tensor.sbis.communicator.generated.DocumentType.DISK_FOLDER
            else                     -> null
        }
    }

    private fun cancelSendMessageWorkAndRemovePush(messageUuid: UUID) {
        singletonComponent.dependency.sendMessageManagerProvider
            ?.getSendMessageManager()
            ?.cancelSendMessageByMessageActionDelete(messageUuid)
    }
}

private const val DEFAULT_THREAD_MAX_PARTICIPANT_COUNT = 30