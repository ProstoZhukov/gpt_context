package ru.tensor.sbis.communicator.crm.conversation.interactor

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.common.generated.CommandStatus
import ru.tensor.sbis.common.generated.ErrorCode
import ru.tensor.sbis.communicator.common.ControllerHelper
import ru.tensor.sbis.communicator.common.analytics.CRMChatWorkEvent
import ru.tensor.sbis.communicator.common.analytics.ReassignConsultationTarget
import ru.tensor.sbis.communicator.common.conversation.crud.MessageControllerBinaryMapper
import ru.tensor.sbis.communicator.common.crud.ThemeRepository
import ru.tensor.sbis.communicator.core.utils.subscribeDataRefresh
import ru.tensor.sbis.communicator.crm.conversation.CRMConversationPlugin.crmConversationDependency
import ru.tensor.sbis.communicator.crm.conversation.data.CRMConversationData
import ru.tensor.sbis.communicator.crm.conversation.data.mapper.CRMConversationDataMapper
import ru.tensor.sbis.communicator.crm.conversation.data.mapper.CRMMessageMapper
import ru.tensor.sbis.communicator.crm.conversation.data.model.CRMConversationMessage
import ru.tensor.sbis.communicator.crm.conversation.interactor.contract.CRMConversationInteractor
import ru.tensor.sbis.communicator.crm.conversation.interactor.contract.CRMConversationInteractor.*
import ru.tensor.sbis.communicator.crm.conversation.utils.toChannelHeirarchyItemType
import ru.tensor.sbis.communication_decl.crm.CRMConsultationCase
import ru.tensor.sbis.communicator.generated.ConsultationChatType
import ru.tensor.sbis.communicator.generated.DataRefreshedMessageControllerCallback
import ru.tensor.sbis.communicator.generated.DialogController
import ru.tensor.sbis.communicator.generated.DialogListResult
import ru.tensor.sbis.communicator.generated.MarkMessagesResult
import ru.tensor.sbis.communicator.generated.MessageController
import ru.tensor.sbis.communicator.generated.MessageErrorResult
import ru.tensor.sbis.communicator.generated.MessageFilter
import ru.tensor.sbis.communicator.generated.MessageListResult
import ru.tensor.sbis.communicator.generated.MessageTextWithMentions
import ru.tensor.sbis.communicator.generated.ServiceMessageGroup
import ru.tensor.sbis.consultations.generated.ConsultationService
import ru.tensor.sbis.consultations.generated.OnConsultationChangedCallback
import ru.tensor.sbis.consultations.generated.OnSummaryUnreadCountersChangeCallback
import ru.tensor.sbis.consultations.generated.QuickReplyCollectionProvider
import ru.tensor.sbis.mvp.interactor.BaseInteractor
import ru.tensor.sbis.platform.generated.Subscription
import java.util.ArrayList
import java.util.HashMap
import java.util.UUID
import javax.inject.Inject
import timber.log.Timber

/**
 * Реализация интерактора экрана сообщений CRM
 *
 * @author da.zhukov
 */
internal class CRMConversationInteractorImpl @Inject constructor(
    private val messageControllerProvider: DependencyProvider<MessageController>,
    private val consultationService: DependencyProvider<ConsultationService>,
    private val dialogControllerProvider: DependencyProvider<DialogController>,
    private val themeRepository: ThemeRepository,
    private val conversationDataMapper: CRMConversationDataMapper,
    private val messageMapper: CRMMessageMapper,
    private val messageControllerBinaryMapper: MessageControllerBinaryMapper,
    private val quickReplyCollectionProvider: DependencyProvider<QuickReplyCollectionProvider>,
) : BaseInteractor(), CRMConversationInteractor {

    private val analyticsUtil = crmConversationDependency?.analyticsUtilProvider?.getAnalyticsUtil()

    override fun loadConversationData(
        viewId: UUID?,
        consultation: UUID,
        refresh: Boolean,
        chatType: ConsultationChatType
    ): Observable<CRMConversationDataResult> = Observable.fromCallable {
        consultationService.get().getConversationData(viewId, consultation, refresh, chatType)
    }.flatMap {
        if (it.status.errorCode == ErrorCode.SYNC_IN_PROGRESS && it.consultation == null) {
            Observable.empty()
        } else {
            Observable.just(it)
        }
    }.map {
        CRMConversationDataResultImpl(
            conversationDataMapper.map(it.consultation, it.data, it.next),
            it.status
        )
    }.compose(getObservableBackgroundSchedulers())

    override fun createConsultation(
        case: CRMConsultationCase
    ): Observable<CRMConversationDataResult> = Observable.fromCallable {
        val result = when {
            case is CRMConsultationCase.Client -> {
                consultationService.get().createByClient(connectionId = case.originUuid)
            }
            case is CRMConsultationCase.SalePoint -> {
                consultationService.get().createForSalePoint(salePointId = case.originUuid)
            }
            case is CRMConsultationCase.Operator && !case.isForReclamation -> {
                consultationService.get().createByOperator(
                    baseConsultaionId = case.originUuid,
                    contactId = case.contactId!!,
                    contactType = case.channelType!!.toChannelHeirarchyItemType()
                )
            }
            case is CRMConsultationCase.Operator && case.isForReclamation -> {
                consultationService.get().createForReclamation(
                    contactId = case.contactId!!,
                    contactType = case.channelType!!.toChannelHeirarchyItemType(),
                    reclamationId = case.originUuid
                )
            }
            else ->  consultationService.get().createByClient(case.originUuid)
        }

        result
    }.flatMap {
        if (it.status.errorCode == ErrorCode.SYNC_IN_PROGRESS && it.consultation == null) {
            Observable.empty()
        } else {
            Observable.just(it)
        }
    }.map {
        CRMConversationDataResultImpl(
            conversationDataMapper.map(it.consultation, it.data, it.next),
            it.status
        )
    }.compose(getObservableBackgroundSchedulers())

    override fun getUrlByUuid(consultationUUID: UUID): Observable<String> {
        return Observable.fromCallable { themeRepository.getUrlByUuid(consultationUUID) }
            .compose(getObservableBackgroundSchedulers())
    }

    override fun reassignToQueue(consultationUUID: UUID): Completable =
        Completable.fromRunnable {
            analyticsUtil?.sendAnalytics(
                CRMChatWorkEvent.ReassignConsultation(
                    ReassignConsultationTarget.QUEUE,
                ),
            )
            consultationService.get().changeOperator(
                consultationUUID,
                null,
                null,
                null,
                null
            )
        }.compose(completableBackgroundSchedulers)

    override fun takeConsultation(consultationUUID: UUID): Completable =
        Completable.fromRunnable {
            analyticsUtil?.sendAnalytics(
                CRMChatWorkEvent.TakeConsultation,
            )
            consultationService.get().take(consultationUUID)
        }
            .compose(completableBackgroundSchedulers)

    override fun reopenConsultation(consultationUUID: UUID): Completable =
        Completable.fromRunnable {
            consultationService.get().reopen(consultationUUID)
        }
            .compose(completableBackgroundSchedulers)

    override fun closeConsultation(consultationUUID: UUID, documentUUID: UUID?): Completable =
        Completable.fromRunnable {
            analyticsUtil?.sendAnalytics(
                CRMChatWorkEvent.CompleteConsultation,
            )
            consultationService.get().close(consultationUUID, documentUUID)
        }
            .compose(completableBackgroundSchedulers)

    override fun deleteConsultation(consultationUUID: UUID): Completable =
        Completable.fromRunnable { consultationService.get().delete(consultationUUID) }
            .compose(completableBackgroundSchedulers)

    override fun subscribeConsultationChangedCallback(): Observable<UUID> =
        Observable.create<UUID> { emitter ->
            var subscription: Subscription? = consultationService.get().onConsultationChanged()
                .subscribe(object : OnConsultationChangedCallback() {
                    override fun onEvent(chatId: UUID) {
                        Timber.i("event OnConsultationChangedCallback chatId $chatId")
                        emitter.onNext(chatId)
                    }
                }).also {
                    it.enable()
                    Timber.i("subscribed OnConsultationChangedCallback")
                }
            emitter.setCancellable {
                subscription?.disable()
                subscription = null
                Timber.i("disposed OnConsultationChangedCallback")
            }
        }.compose(getObservableBackgroundSchedulers())

    override suspend fun getGreetings(consultationUUID: UUID): List<String> = withContext(Dispatchers.IO) {
        quickReplyCollectionProvider.get().getGreetings(consultationUUID)
    }

    override suspend fun sendGreetingMessage(consultationUUID: UUID?, text: String) {
        sendMessage(
            conversationUuid = consultationUUID,
            text = text
        )
    }

    override fun requestContacts(consultationUUID: UUID): Completable =
        Completable.fromRunnable { consultationService.get().requestContactData(consultationUUID) }
            .compose(completableBackgroundSchedulers)

    private suspend fun sendMessage(
        conversationUuid: UUID?,
        text: String,
        answeredMessageUuid: UUID? = null
    ) = withContext(Dispatchers.IO) {
        messageControllerProvider.get().enqueueMessage2(
            conversationUuid,
            null,
            text,
            null,
            null,
            null,
            null,
            null,
            answeredMessageUuid,
            null,
        )
    }

    override fun subscribeOnCounterUpdates(): Observable<Pair<UUID, Int>> =
        Observable.create<Pair<UUID, Int>> { emitter ->
            var subscription: Subscription? = consultationService.get().onSummaryUnreadCountersChange()
                .subscribe(object : OnSummaryUnreadCountersChangeCallback() {
                    override fun onEvent(chatId: UUID, msgCounter: Int) {
                        emitter.onNext(chatId to msgCounter)
                    }
                }).also {
                    it.enable()
                }
            emitter.setCancellable {
                subscription?.disable()
                subscription = null
            }
        }.compose(getObservableBackgroundSchedulers())

    override fun observeThemeControllerUpdates(): Observable<HashMap<String, String>> =
        Observable.fromCallable { themeRepository }
            .flatMap { it.subscribeDataRefresh() }
            .compose(getObservableBackgroundSchedulers())

    override fun observeMessageControllerCallbackSubscription(callback: DataRefreshedMessageControllerCallback): Observable<Subscription> {
        return Observable.fromCallable { messageControllerProvider.get().dataRefreshed().subscribe(callback)}
            .compose(getObservableBackgroundSchedulers())
    }

    override fun forceResendMessage(messageUuid: UUID): Single<CommandStatus> =
        Single.fromCallable {
            messageControllerProvider.get().forceResendMessage(messageUuid)
        }.compose(getSingleBackgroundSchedulers())

    override fun deleteMessageForMe(conversationUuid: UUID, messageUuid: UUID): Single<MessageListResult> =
        Single.fromCallable { messageControllerProvider.get().deleteMessagesForMeOnly(conversationUuid, arrayListOf(messageUuid)) }
            .compose(getSingleBackgroundSchedulers())

    override fun onThemeBeforeOpened(themeUUID: UUID) = themeRepository.onThemeBeforeOpened(themeUUID)

    override fun onThemeAfterOpened(themeUUID: UUID, filter: MessageFilter?, isChat: Boolean): Completable =
        Completable.fromRunnable { themeRepository.onThemeAfterOpened(themeUUID, filter, isChat) }
            .compose(completableBackgroundSchedulers)

    override fun onThemeClosed(themeUUID: UUID): Completable =
        Completable.fromRunnable { themeRepository.onThemeClosed(themeUUID) }
            .compose(completableBackgroundSchedulers)

    override fun getMessageText(conversationUuid: UUID, messageUuid: UUID): Single<MessageTextWithMentions> =
        Single.fromCallable {
            messageControllerProvider.get().getMessageText(messageUuid, conversationUuid)
        }.compose(getSingleBackgroundSchedulers())

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

    override fun getMessageByUuid(uuid: UUID): Maybe<CRMConversationMessage> =
        Maybe.fromCallable {
            messageControllerProvider.get().read(uuid)
        }
            .map { messageControllerBinaryMapper.map(it) }
            .map(messageMapper::apply)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    override fun markMessagesAsRead(messagesUuid: ArrayList<UUID>): Single<MarkMessagesResult> {
        return Single.fromCallable { messageControllerProvider.get().markMessagesAsRead(messagesUuid) }
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
                        messageControllerProvider.get()
                            .markMessageGroupAsRead(conversationUuid, group.firstMessageUuid)
                    }
                }
            ) { a: ServiceMessageGroup?, b: CommandStatus? -> Pair(a!!, b!!) }
            .compose(getObservableBackgroundSchedulers())
    }

    override fun deleteDialog(conversationUuid: UUID): Single<CommandStatus> =
        Single.fromCallable {
            ControllerHelper.checkExecutionTime("DialogController.delete") {
                dialogControllerProvider.get().delete(arrayListOf(conversationUuid), false)
            }
        }
            .map { obj: DialogListResult -> obj.status }
            .compose(getSingleBackgroundSchedulers())

    override fun deleteDialogFromArchive(conversationUuid: UUID, deleteFromArchive: Boolean): Single<CommandStatus> =
        Single.fromCallable {
            ControllerHelper.checkExecutionTime("DialogController.delete") {
                dialogControllerProvider.get().delete(arrayListOf(conversationUuid), deleteFromArchive)
            }.status
        }
            .compose(getSingleBackgroundSchedulers())

    override fun deleteMessageForEveryone(conversationUuid: UUID, messageUuid: UUID): Completable =
        Single.fromCallable {
            val result = ControllerHelper.checkExecutionTime("MessageController.deleteMessagesForEveryone") {
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
        sendMessage(
            conversationUuid = conversationUuid,
            text = title,
            answeredMessageUuid = serviceMessageUUID
        )
    }

    private data class CRMConversationDataResultImpl(
        override val conversationData: CRMConversationData,
        override val commandStatus: CommandStatus
    ) : CRMConversationDataResult {
        init {
            conversationData.conversationAccess.isAvailable =
                ErrorCode.NOT_AVAILABLE != commandStatus.errorCode
        }
    }
}
