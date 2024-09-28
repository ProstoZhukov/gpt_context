package ru.tensor.sbis.message_panel.viewModel.stateMachine

import org.mockito.kotlin.*
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import junitparams.JUnitParamsRunner
import org.apache.commons.lang3.StringUtils
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.quality.Strictness
import ru.tensor.sbis.common.generated.CommandStatus
import ru.tensor.sbis.common.generated.ErrorCode
import ru.tensor.sbis.common.rx.RxContainer
import ru.tensor.sbis.common.testing.TrampolineSchedulerRule
import ru.tensor.sbis.common.testing.stringParamMapper
import ru.tensor.sbis.common.util.ResourceProvider
import ru.tensor.sbis.communicator.generated.SendMessageResult
import ru.tensor.sbis.message_panel.attachments.MessagePanelAttachmentHelper
import ru.tensor.sbis.message_panel.decl.MessageResultHelper
import ru.tensor.sbis.message_panel.interactor.draft.MessagePanelDraftInteractor
import ru.tensor.sbis.message_panel.interactor.message.MessagePanelMessageInteractor
import ru.tensor.sbis.message_panel.model.CoreConversationInfo
import ru.tensor.sbis.message_panel.setUpDefaultSubjects
import ru.tensor.sbis.message_panel.view.AlertDialogData
import ru.tensor.sbis.message_panel.viewModel.MessagePanelViewModel
import ru.tensor.sbis.message_panel.viewModel.livedata.MessagePanelLiveData
import java.util.*

/**
 * Тестирование состояния отправки сообщения
 *
 * @author vv.chekurda
 */
@RunWith(JUnitParamsRunner::class)
class SendingSimpleMessageStateTest {

    private val messageText = "Test message text"
    private val documentUuid = UUID.randomUUID()
    private val conversationUuid = UUID.randomUUID()
    private val folderUuid = UUID.randomUUID()
    private val quotedMessageUuid = UUID.randomUUID()
    private val answeredMessageUuid = UUID.randomUUID()
    private val sentErrorTest = "Sent result error"

    @get:Rule
    val rxSchedulerRule = TrampolineSchedulerRule()

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS)

    @Mock
    private lateinit var vm: MessagePanelViewModel<*, Any, *>

    @Mock
    private lateinit var liveData: MessagePanelLiveData

    @Mock
    private lateinit var messageInteractor: MessagePanelMessageInteractor<*, Any>

    @Mock
    private lateinit var messageHelper: MessageResultHelper<*, Any>

    @Mock
    private lateinit var attachmentsHelper: MessagePanelAttachmentHelper

    @Mock
    private lateinit var resourceProvider: ResourceProvider

    @Mock
    private lateinit var draftInteractor: MessagePanelDraftInteractor<*>

    @Mock
    private lateinit var coreConversationInfo: CoreConversationInfo

    private lateinit var machine: MessagePanelStateMachine<*, Any, *>

    @Before
    fun setUp() {
        liveData.setUpDefaultSubjects()
        whenever(vm.liveData).thenReturn(liveData)
        machine = MessagePanelStateMachineImpl(vm, Schedulers.trampoline())
        whenever(vm.messageInteractor).thenReturn(messageInteractor)
        whenever(vm.messageResultHelper).thenReturn(messageHelper)
        whenever(vm.attachmentPresenter).thenReturn(attachmentsHelper)
        Mockito.lenient().`when`(vm.stateMachine).thenReturn(machine)
        Mockito.lenient().`when`(vm.resourceProvider).thenReturn(resourceProvider)
    }

    @Before
    fun setUpLiveDataDependencies() {
        val messageSubject = BehaviorSubject.createDefault(RxContainer(messageText))
        whenever(liveData.messageText).thenReturn(messageSubject)
        whenever(liveData.document).thenReturn(BehaviorSubject.createDefault(RxContainer(documentUuid)))
        whenever(liveData.conversationUuid).thenReturn(BehaviorSubject.createDefault(RxContainer(conversationUuid)))
        whenever(liveData.folderUuid).thenReturn(BehaviorSubject.createDefault(RxContainer(folderUuid)))
        whenever(liveData.quotedMessageUuid).thenReturn(BehaviorSubject.createDefault(RxContainer(quotedMessageUuid)))
        whenever(liveData.answeredMessageUuid).thenReturn(BehaviorSubject.createDefault(RxContainer(answeredMessageUuid)))
        whenever(liveData.quotePanelVisible).thenReturn(PublishSubject.create())
        Mockito.lenient().`when`(liveData.attachments).thenReturn(PublishSubject.create())
        Mockito.lenient().`when`(liveData.recipientsSelected).thenReturn(PublishSubject.create())
        Mockito.lenient().`when`(liveData.quotePanelVisible).thenReturn(PublishSubject.empty())
    }

    private fun setUpDraftLoadingDependencies() {
        whenever(vm.draftInteractor).thenReturn(draftInteractor)
        whenever(vm.conversationInfo).thenReturn(coreConversationInfo)
        whenever(coreConversationInfo.conversationUuid).thenReturn(conversationUuid)
        whenever(coreConversationInfo.document).thenReturn(documentUuid)
        whenever(draftInteractor.loadDraft(conversationUuid, documentUuid)).thenReturn(Single.never())
    }

    @After
    fun tearDown() {
        machine.stop()
    }

    @Test
    fun `Clean text and attachments on sending success result`() {
        mockSendMessage(isSentResultError = false)

        verify(liveData, atLeastOnce()).setMessageText(StringUtils.EMPTY)
        verify(liveData, never()).setMessageText(messageText)
        verify(attachmentsHelper).clearAttachments()
    }

    @Test
    fun `Don't clean data on sending error result`() {
        mockSendMessage(isSentResultError = true, errorText = sentErrorTest)

        verify(liveData, never()).setMessageText(stringParamMapper(any()))
        verify(attachmentsHelper, never()).clearAttachments()
    }

    @Test
    fun `Show error text from result on sending error result`() {
        mockSendMessage(isSentResultError = true, sentErrorTest)

        verify(liveData).showAlertDialog(eq(AlertDialogData(sentErrorTest)))
    }

    private fun mockSendMessage(isSentResultError: Boolean, errorText: String? = null) {
        if (isSentResultError) {
            mockSendMessageError(errorText)
        } else {
            mockSendMessageSuccess()
        }

        machine.setState(SendingSimpleMessageState(vm))
    }

    private fun mockSendMessageError(errorText: String?) {
        whenever(messageHelper.isSentResultError(any())).thenReturn(true)
        whenever(messageHelper.getSentResultError(any())).thenReturn(errorText)
        messageInteractor.mockSendMessageAction()
    }

    private fun mockSendMessageSuccess() {
        setUpDraftLoadingDependencies()
        whenever(messageHelper.isSentResultError(any())).thenReturn(false)
        messageInteractor.mockSendMessageAction()
    }

    private fun MessagePanelMessageInteractor<*, *>.mockSendMessageAction() {
        whenever(sendMessage(
            text = messageText,
            attachments = emptyList(),
            recipientUuids = emptyList(),
            documentUuid = documentUuid,
            conversationUuid = conversationUuid,
            folderUuid = folderUuid,
            signActions = null,
            quotedMessageUuid = quotedMessageUuid,
            answeredMessageUuid = answeredMessageUuid,
            metaData = null
        )).thenReturn(
            Single.just(
                SendMessageResult(null, null, CommandStatus(ErrorCode.SUCCESS, sentErrorTest))
            )
        )
    }
}