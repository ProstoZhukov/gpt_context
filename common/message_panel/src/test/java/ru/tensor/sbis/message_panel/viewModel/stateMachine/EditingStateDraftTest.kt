package ru.tensor.sbis.message_panel.viewModel.stateMachine

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.apache.commons.lang3.StringUtils.EMPTY
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.*
import ru.tensor.sbis.attachments.decl.mapper.AttachmentRegisterModelMapper
import ru.tensor.sbis.common.generated.CommandStatus
import ru.tensor.sbis.common.rx.livedata.value
import ru.tensor.sbis.common.testing.TrampolineSchedulerRule
import ru.tensor.sbis.common.util.FileUriUtil
import ru.tensor.sbis.common.util.ResourceProvider
import ru.tensor.sbis.communication_decl.selection.recipient.manager.RecipientSelectionResultManager
import ru.tensor.sbis.communicator.generated.MessageTextWithMentions
import ru.tensor.sbis.message_panel.decl.DraftResultHelper
import ru.tensor.sbis.message_panel.decl.MessageResultHelper
import ru.tensor.sbis.message_panel.interactor.attachments.MessagePanelAttachmentsInteractor
import ru.tensor.sbis.message_panel.interactor.draft.MessagePanelDraftInteractor
import ru.tensor.sbis.message_panel.interactor.message.MessagePanelMessageInteractor
import ru.tensor.sbis.message_panel.interactor.recipients.MessagePanelRecipientsInteractor
import ru.tensor.sbis.message_panel.model.CoreConversationInfo
import ru.tensor.sbis.message_panel.model.EditContent
import ru.tensor.sbis.message_panel.viewModel.MessagePanelViewModel
import ru.tensor.sbis.message_panel.viewModel.MessagePanelViewModelImpl
import ru.tensor.sbis.mvp.interactor.crudinterface.subscribing.SubscriptionManager
import ru.tensor.sbis.verification_decl.login.LoginInterface
import java.util.*

/**
 * Тестирование сохранения и восстановления драфта в сценариях редактирования [EditingState]
 *
 * @author vv.chekurda
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class EditingStateDraftTest {

    @get:Rule
    val rxSchedulerRule = TrampolineSchedulerRule()

    private val userInputText = "Current input text"
    private val editedMessageText = "Test edited message text"
    private val editedMessageUuid = UUID.randomUUID()
    private val editedTextWithMentions = MessageTextWithMentions(editedMessageText, EMPTY)
    private val editEvent = EventEdit(editedMessageText, EditContent(editedMessageUuid))

    @Mock
    private lateinit var conversationUuid: UUID
    @Mock
    private lateinit var draftUuid: UUID
    @Mock
    private lateinit var documentUuid: UUID
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
        val mockCompletable = mock<Completable> {
            on { subscribe() } doReturn mock()
        }
        whenever(messageInteractor.notifyUserTyping(any())).thenReturn(mockCompletable)
        whenever(messageInteractor.beginEditMessage(any())).thenReturn(Single.just(CommandStatus()))
        whenever(messageInteractor.cancelEditMessage(any())).thenReturn(Single.just(CommandStatus()))
        whenever(attachmentsInteractor.loadAttachments(any())).thenReturn(Single.just(emptyList()))
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

    @Before
    fun mockEditStateInitialization() {
        whenever(messageInteractor.getMessageText(editedMessageUuid, conversationUuid))
            .thenReturn(Single.just(editedTextWithMentions))
        whenever(
            draftInteractor.saveDraft(
                draftUuid,
                conversationUuid,
                documentUuid,
                listOf(),
                userInputText,
                listOf(),
                null,
                null,
                null
            )
        ).thenReturn(Completable.complete())
    }

    @After
    fun tearDown() {
        vm.stateMachine.stop()
    }

    @Test
    fun `Save current text in draft before text changed on edit state activation`() {
        vm.liveData.setMessageText(userInputText)
        val messageTextObserver = vm.liveData.messageText.test()

        vm.stateMachine.fire(EditingStateEvent(editEvent))

        messageTextObserver
            .awaitCount(2)
            .assertValueAt(0) { it.value == userInputText }
            .assertValueAt(1) { it.value == editedMessageText }

        verify(draftInteractor).saveDraft(
            draftUuid,
            conversationUuid,
            documentUuid,
            listOf(),
            userInputText,
            listOf(),
            null,
            null,
            null
        )
    }

    @Test
    fun `Don't save draft on destroy, when edit state activated`() {
        vm.liveData.setMessageText(userInputText)

        vm.stateMachine.setState(EditingState(vm, editEvent))
        vm.onCleared()

        verify(draftInteractor, never()).saveDraft(
            draftUuid,
            conversationUuid,
            documentUuid,
            listOf(),
            editedMessageText,
            listOf(),
            null,
            null,
            null
        )
    }

    @Test
    fun `Load draft text on cancel editing state`() {
        whenever(recipientsInteractor.loadRecipientModels(emptyList())).thenReturn(Maybe.just(emptyList()))
        whenever(draftInteractor.loadDraft(conversationUuid, documentUuid)).thenReturn(Single.just(draftMessage))
        whenever(draftResultHelper.isEmpty(draftMessage)).thenReturn(false)
        whenever(draftResultHelper.getId(draftMessage)).thenReturn(draftUuid)
        whenever(draftResultHelper.getText(draftMessage)).thenReturn(userInputText)

        vm.liveData.setMessageText(userInputText)
        val stateObserver = vm.stateMachine.currentStateObservable.test()

        vm.stateMachine.setState(EditingState(vm, editEvent))
        vm.cancelEdit()

        // ожидаемые состояния техпроцесса
        stateObserver
            .awaitCount(4)
            .assertValueAt(0) { it is DisabledState }
            .assertValueAt(1) { it is EditingState<*, *, *> }
            .assertValueAt(2) { it is DraftLoadingState<*, *, *> }
            .assertValueAt(3) { it is SimpleSendState<*, *, *> }

        verify(draftInteractor).loadDraft(conversationUuid, documentUuid)
        assertEquals(vm.liveData.messageText.value, userInputText)
    }

    @Test
    fun `Load and insert draft text on edit message success`() {
        whenever(messageResultHelper.isResultError(any())).thenReturn(false)
        whenever(messageInteractor.editMessage(editedMessageUuid, editedMessageText))
            .thenReturn(Single.just(mock()))

        whenever(recipientsInteractor.loadRecipientModels(emptyList())).thenReturn(Maybe.just(emptyList()))
        whenever(draftInteractor.loadDraft(conversationUuid, documentUuid)).thenReturn(Single.just(draftMessage))
        whenever(draftResultHelper.isEmpty(draftMessage)).thenReturn(false)
        whenever(draftResultHelper.getId(draftMessage)).thenReturn(draftUuid)
        whenever(draftResultHelper.getText(draftMessage)).thenReturn(userInputText)

        vm.liveData.setMessageText(userInputText)
        val stateObserver = vm.stateMachine.currentStateObservable.test()

        vm.stateMachine.setState(EditingState(vm, editEvent))
        vm.sendMessage()

        // ожидаемые состояния техпроцесса
        stateObserver
            .awaitCount(5)
            .assertValueAt(0) { it is DisabledState }
            .assertValueAt(1) { it is EditingState<*, *, *> }
            .assertValueAt(2) { it is SendingEditMessageState<*, *, *> }
            .assertValueAt(3) { it is DraftLoadingState<*, *, *> }
            .assertValueAt(4) { it is SimpleSendState<*, *, *> }

        verify(draftInteractor).loadDraft(conversationUuid, documentUuid)
        assertEquals(vm.liveData.messageText.value, userInputText)
    }

    @Test
    fun `Load and insert draft text on edit message failed`() {
        whenever(messageResultHelper.isResultError(any())).thenReturn(true)
        whenever(messageInteractor.editMessage(editedMessageUuid, editedMessageText))
            .thenReturn(Single.just(mock()))
        whenever(resourceProvider.getString(any())).thenReturn(EMPTY)

        whenever(recipientsInteractor.loadRecipientModels(emptyList())).thenReturn(Maybe.just(emptyList()))
        whenever(draftInteractor.loadDraft(conversationUuid, documentUuid)).thenReturn(Single.just(draftMessage))
        whenever(draftResultHelper.isEmpty(draftMessage)).thenReturn(false)
        whenever(draftResultHelper.getId(draftMessage)).thenReturn(draftUuid)
        whenever(draftResultHelper.getText(draftMessage)).thenReturn(userInputText)

        vm.liveData.setMessageText(userInputText)
        val stateObserver = vm.stateMachine.currentStateObservable.test()

        vm.stateMachine.setState(EditingState(vm, editEvent))
        vm.sendMessage()

        // ожидаемые состояния техпроцесса
        stateObserver
            .awaitCount(5)
            .assertValueAt(0) { it is DisabledState }
            .assertValueAt(1) { it is EditingState<*, *, *> }
            .assertValueAt(2) { it is SendingEditMessageState<*, *, *> }
            .assertValueAt(3) { it is DraftLoadingState<*, *, *> }
            .assertValueAt(4) { it is SimpleSendState<*, *, *> }

        verify(draftInteractor).loadDraft(conversationUuid, documentUuid)
        assertEquals(vm.liveData.messageText.value, userInputText)
    }
}