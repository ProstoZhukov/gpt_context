package ru.tensor.sbis.message_panel.viewModel.stateMachine

import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.apache.commons.lang3.StringUtils.EMPTY
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.*
import ru.tensor.sbis.attachments.decl.mapper.AttachmentRegisterModelMapper
import ru.tensor.sbis.common.generated.CommandStatus
import ru.tensor.sbis.common.generated.ErrorCode
import ru.tensor.sbis.common.testing.TrampolineSchedulerRule
import ru.tensor.sbis.common.util.FileUriUtil
import ru.tensor.sbis.common.util.ResourceProvider
import ru.tensor.sbis.communication_decl.selection.recipient.manager.RecipientSelectionResultManager
import ru.tensor.sbis.communicator.generated.SendMessageResult
import ru.tensor.sbis.message_panel.decl.DraftResultHelper
import ru.tensor.sbis.message_panel.decl.MessageResultHelper
import ru.tensor.sbis.message_panel.interactor.attachments.MessagePanelAttachmentsInteractor
import ru.tensor.sbis.message_panel.interactor.draft.MessagePanelDraftInteractor
import ru.tensor.sbis.message_panel.interactor.message.MessagePanelMessageInteractor
import ru.tensor.sbis.message_panel.interactor.recipients.MessagePanelRecipientsInteractor
import ru.tensor.sbis.message_panel.model.CoreConversationInfo
import ru.tensor.sbis.message_panel.model.QuoteContent
import ru.tensor.sbis.message_panel.viewModel.*
import ru.tensor.sbis.mvp.interactor.crudinterface.subscribing.SubscriptionManager
import ru.tensor.sbis.verification_decl.login.LoginInterface
import java.util.*

/**
 * Тестирование сохранения и восстановления драфта цитирования для [QuotingState]
 *
 * @author vv.chekurda
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class QuotingStateDraftTest {

    @get:Rule
    val rxSchedulerRule = TrampolineSchedulerRule()

    private val senderName = "Test sender name"
    private val quoteText = "Test quote text"

    @Mock
    private lateinit var conversationUuid: UUID
    @Mock
    private lateinit var draftUuid: UUID
    @Mock
    private lateinit var documentUuid: UUID
    @Mock
    private lateinit var quoteMessageUuid: UUID
    @Mock
    private lateinit var draftMessage: CommunicatorDraftMessage

    @Mock
    private lateinit var recipientsInteractor: MessagePanelRecipientsInteractor
    @Mock
    private lateinit var attachmentsInteractor: MessagePanelAttachmentsInteractor
    @Mock
    private lateinit var messageInteractor: MessagePanelMessageInteractor<MessageResult, MessageSentResult>
    @Mock
    private lateinit var messageResultHelper: MessageResultHelper<MessageResult, MessageSentResult>
    @Mock
    private lateinit var draftResultHelper: DraftResultHelper<CommunicatorDraftMessage>
    @Mock
    private lateinit var draftInteractor: MessagePanelDraftInteractor<CommunicatorDraftMessage>
    @Mock
    private lateinit var fileUriUtil: FileUriUtil
    @Mock
    private lateinit var resourceProvider: ResourceProvider
    @Mock
    private lateinit var recipientsManager: RecipientSelectionResultManager
    @Mock
    private lateinit var attachemtnsModelMapper: AttachmentRegisterModelMapper
    @Mock
    private lateinit var subscriptionManager: SubscriptionManager
    @Mock
    private lateinit var loginInterface: LoginInterface

    private lateinit var vm: MessagePanelViewModel<MessageResult, MessageSentResult, CommunicatorDraftMessage>

    @Before
    fun setUp() {
        whenever(attachmentsInteractor.setAttachmentListRefreshCallback(any())).thenReturn(Observable.empty())
        whenever(subscriptionManager.batch()).thenReturn(mock())

        vm = MessagePanelViewModelImpl(
            recipientsInteractor,
            attachmentsInteractor,
            messageInteractor,
            messageResultHelper,
            draftInteractor,
            draftResultHelper,
            fileUriUtil,
            resourceProvider,
            recipientsManager,
            attachemtnsModelMapper,
            subscriptionManager,
            loginInterface,
            true,
            Schedulers.newThread(),
        ).apply {
            setConversationInfo(
                CoreConversationInfo(
                    conversationUuid = conversationUuid,
                    document = documentUuid
                )
            )
            resetConversationInfo()
            liveData.setDraftUuid(draftUuid)
            stateMachine.start()
        }
    }

    @After
    fun tearDown() {
        vm.stateMachine.stop()
    }

    @Test
    fun `When quote state activated, then save draft with quoted message uuid on close screen`() {
        val quoteContent = QuoteContent(quoteMessageUuid, senderName, quoteText)

        vm.stateMachine.setState(QuotingState(vm, EventQuote(quoteContent)))
        vm.onCleared()

        verify(draftInteractor).saveDraft(
            draftUuid,
            conversationUuid,
            documentUuid,
            listOf(),
            EMPTY,
            listOf(),
            quoteMessageUuid,
            null,
            null
        )
    }

    @Test
    fun `Don't save quote uuid in draft on destroy after cancel quoting`() {
        val quoteContent = QuoteContent(quoteMessageUuid, senderName, quoteText)

        val stateObserver = vm.stateMachine.currentStateObservable.test()

        vm.stateMachine.setState(QuotingState(vm, EventQuote(quoteContent)))
        vm.cancelEdit(null)
        vm.onCleared()

        // ожидаемые состояния техпроцесса
        stateObserver
            .awaitCount(4)
            .assertValueAt(0) { it is DisabledState }
            .assertValueAt(1) { it is QuotingState<*, *, *> }
            .assertValueAt(2) { it is SimpleSendState<*, *, *> }
            .assertValueAt(3) { it is CleanSendState<*, *, *> }

        verify(draftInteractor).saveDraft(
            draftUuid,
            conversationUuid,
            documentUuid,
            listOf(),
            EMPTY,
            listOf(),
            null,
            null,
            null
        )
    }

    @Test
    fun `Don't save quote uuid in draft on destroy after sent message`() {
        val quoteContent = QuoteContent(quoteMessageUuid, senderName, quoteText)
        whenever(draftInteractor.loadDraft(conversationUuid, documentUuid)).thenReturn(Single.just(draftMessage))
        whenever(draftResultHelper.isEmpty(draftMessage)).thenReturn(true)
        whenever(draftResultHelper.getId(draftMessage)).thenReturn(draftUuid)
        messageInteractor.mockSendMessageAction()

        val stateObserver = vm.stateMachine.currentStateObservable.test()

        vm.stateMachine.setState(QuotingState(vm, EventQuote(quoteContent)))
        vm.stateMachine.fire(SendingQuoteMessageEvent())
        vm.onCleared()

        // ожидаемые состояния техпроцесса
        stateObserver
            .awaitCount(5)
            .assertValueAt(0) { it is DisabledState }
            .assertValueAt(1) { it is QuotingState<*, *, *> }
            .assertValueAt(2) { it is SendingQuoteMessageState<*, *, *> }
            .assertValueAt(3) { it is DraftLoadingState<*, *, *> }
            .assertValueAt(4) { it is CleanSendState<*, *, *> }

        verify(draftInteractor).saveDraft(
            draftUuid,
            conversationUuid,
            documentUuid,
            listOf(),
            EMPTY,
            listOf(),
            null,
            null,
            null
        )
    }

    @Test
    fun `When view model load draft with quote content, then state machine should switch to quoting state`() {
        val quoteContent = QuoteContent(quoteMessageUuid, senderName, quoteText)
        whenever(draftResultHelper.getQuoteContent(draftMessage)).thenReturn(quoteContent)
        whenever(draftResultHelper.isEmpty(draftMessage)).thenReturn(false)
        whenever(draftResultHelper.getId(draftMessage)).thenReturn(draftUuid)
        whenever(draftInteractor.loadDraft(conversationUuid, documentUuid)).thenReturn(Single.just(draftMessage))
        whenever(recipientsInteractor.loadRecipientModels(emptyList())).thenReturn(Maybe.just(emptyList()))

        val stateObserver = vm.stateMachine.currentStateObservable.test()

        vm.loadDraft()

        stateObserver
            .awaitCount(3)
            .assertValueAt(0) { it is DisabledState }
            .assertValueAt(1) { it is DraftLoadingState<*, *, *> }
            .assertValueAt(2) { it is QuotingState<*, *, *> }
            .assertValueCount(3)
    }

    private fun MessagePanelMessageInteractor<*, *>.mockSendMessageAction() {
        whenever(sendMessage(
            text = EMPTY,
            attachments = emptyList(),
            recipientUuids = emptyList(),
            documentUuid = documentUuid,
            conversationUuid = conversationUuid,
            folderUuid = null,
            signActions = null,
            quotedMessageUuid = quoteMessageUuid,
            answeredMessageUuid = null,
            metaData = null
        )).thenReturn(
            Single.just(
                SendMessageResult(null, null, CommandStatus(ErrorCode.SUCCESS, ""))
            )
        )
    }
}

/**
 * Тестовая модель результата запроса сообщения
 */
internal interface MessageResult

/**
 * Тестовая модель результата отправки сообщения
 */
internal interface MessageSentResult

/**
 * Тестовая модель черновика сообщения
 */
internal interface CommunicatorDraftMessage