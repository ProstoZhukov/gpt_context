package ru.tensor.sbis.message_panel.viewModel.stateMachine

import org.mockito.kotlin.*
import io.reactivex.Single
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.instanceOf
import org.junit.*
import org.junit.Assert.assertThat
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.common.generated.CommandStatus
import ru.tensor.sbis.common.generated.ErrorCode
import ru.tensor.sbis.common.rx.RxContainer
import ru.tensor.sbis.common.testing.TrampolineSchedulerRule
import ru.tensor.sbis.common.util.ResourceProvider
import ru.tensor.sbis.communicator.generated.SendMessageResult
import ru.tensor.sbis.message_panel.attachments.MessagePanelAttachmentHelper
import ru.tensor.sbis.message_panel.decl.MessageResultHelper
import ru.tensor.sbis.message_panel.interactor.message.MessagePanelMessageInteractor
import ru.tensor.sbis.message_panel.model.QuoteContent
import ru.tensor.sbis.message_panel.setUpDefaultSubjects
import ru.tensor.sbis.message_panel.viewModel.MessagePanelViewModel
import ru.tensor.sbis.message_panel.viewModel.livedata.MessagePanelLiveData
import java.util.*

/**
 * Тестирование активации и переходов из [QuotingState]
 *
 * @author vv.chekurda
 * Создан 7/29/2019
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class QuotingStateTransitionTest {

    @get:Rule
    val rxSchedulerRule = TrampolineSchedulerRule()

    /**
     * Пустая строка подходит одновременно для отправки и очистки
     */
    private val emptyString = ""
    private val sender = "Sender stub"
    private val text = "Text stub"
    private val documentUuid = UUID.randomUUID()
    private val conversationUuid = UUID.randomUUID()
    private val folderUuid = UUID.randomUUID()
    private val messageUuid = UUID.randomUUID()
    private val quotedMessageUuid = UUID.randomUUID()
    private val answeredMessageUuid = UUID.randomUUID()

    @Mock
    private lateinit var messageInteractor: MessagePanelMessageInteractor<*, Any>

    @Mock
    private lateinit var messageHelper: MessageResultHelper<*, Any>

    @Mock
    private lateinit var attachments: MessagePanelAttachmentHelper

    @Mock
    private lateinit var liveData: MessagePanelLiveData

    @Mock
    private lateinit var vm: MessagePanelViewModel<*, Any, *>

    @Mock
    private lateinit var content: QuoteContent

    @Mock
    private lateinit var resourceProvider: ResourceProvider

    private lateinit var machine: MessagePanelStateMachine<*, Any, *>

    @Before
    fun setUp() {
        whenever(content.uuid).thenReturn(messageUuid)
        whenever(content.sender).thenReturn(sender)
        whenever(content.text).thenReturn(text)
        whenever(liveData.attachments).thenReturn(PublishSubject.create())
        whenever(vm.liveData).thenReturn(liveData)
        whenever(vm.attachmentPresenter).thenReturn(attachments)
        whenever(vm.messageInteractor).thenReturn(messageInteractor)
        whenever(vm.messageResultHelper).thenReturn(messageHelper)
        Mockito.lenient().`when`(vm.resourceProvider).thenReturn(resourceProvider)
        liveData.setUpDefaultSubjects()

        machine = MessagePanelStateMachineImpl(vm, Schedulers.trampoline())
        whenever(vm.stateMachine).thenReturn(machine)

        machine.start()
    }

    @Before
    fun setUpLiveDataDependencies() {
        // важно, что бы возвращались именно BehaviorSubject для получения value в ru.tensor.sbis.common.rx.livedata
        val messageSubject = BehaviorSubject.createDefault(RxContainer(emptyString))
        whenever(liveData.messageText).thenReturn(messageSubject)
        whenever(liveData.document).thenReturn(BehaviorSubject.createDefault(RxContainer(documentUuid)))
        whenever(liveData.conversationUuid).thenReturn(BehaviorSubject.createDefault(RxContainer(conversationUuid)))
        whenever(liveData.folderUuid).thenReturn(BehaviorSubject.createDefault(RxContainer(folderUuid)))
        whenever(liveData.quotedMessageUuid).thenReturn(BehaviorSubject.createDefault(RxContainer(quotedMessageUuid)))
        whenever(liveData.answeredMessageUuid).thenReturn(BehaviorSubject.createDefault(RxContainer(answeredMessageUuid)))
        whenever(liveData.recipientsSelected).thenReturn(BehaviorSubject.createDefault(true))
        whenever(liveData.quotePanelVisible).thenReturn(BehaviorSubject.createDefault(true))
    }

    @After
    fun tearDown() {
        machine.stop()
    }

    @Test
    fun `Data inserted to quote panel on quote state activated`() {
        val consumer = mock<Consumer<Boolean>>()
        val captor = argumentCaptor<Boolean>()
        machine.isQuoting.subscribe(consumer)

        machine.setState(QuotingState(vm, EventQuote(content)))

        verify(consumer, times(2)).accept(captor.capture())

        assertThat(captor.allValues, equalTo(listOf(false, true)))

        verify(content).uuid
        verify(content).sender
        verify(content).text
        verifyNoMoreInteractions(content)

        verify(liveData).setQuoteText(sender, text)
        verify(liveData).setQuotedMessageUuid(content.uuid)
    }

    @Test
    @Ignore("TODO починить https://online.sbis.ru/opendoc.html?guid=ba5c3817-c121-4bee-8bdb-925218413930")
    fun `Clean state on quote sent`() {
        val consumer = mock<Consumer<Boolean>>()
        val captor = argumentCaptor<Boolean>()
        messageInteractor.mockSendMessage(ErrorCode.SUCCESS)
        machine.isQuoting.subscribe(consumer)

        machine.setState(QuotingState(vm, EventQuote(content)))

        machine.fire(EventSend())

        verify(consumer, times(3)).accept(captor.capture())
        assertThat(captor.allValues, equalTo(listOf(false, true, false)))
        assertThat(machine.currentState(), instanceOf(SimpleSendState::class.java))
    }

    @Test
    fun `Clean state on quote sent failed`() {
        val consumer = mock<Consumer<Boolean>>()
        val captor = argumentCaptor<Boolean>()
        messageInteractor.mockSendMessage(ErrorCode.OTHER_ERROR)
        whenever(messageHelper.isSentResultError(any())).thenReturn(true)
        whenever(messageHelper.getSentResultError(any())).thenReturn("Sent result error")
        machine.isQuoting.subscribe(consumer)

        machine.setState(QuotingState(vm, EventQuote(content)))

        machine.fire(EventSend())

        verify(consumer, times(3)).accept(captor.capture())
        assertThat(captor.allValues, equalTo(listOf(false, true, false)))
        assertThat(machine.currentState(), instanceOf(SimpleSendState::class.java))
    }

    @Test
    fun `Clean state on quote cancelled`() {
        val consumer = mock<Consumer<Boolean>>()
        val captor = argumentCaptor<Boolean>()
        messageInteractor.mockSendMessage(ErrorCode.OTHER_ERROR)
        machine.isQuoting.subscribe(consumer)

        machine.setState(QuotingState(vm, EventQuote(content)))

        machine.fire(EventCancel())

        verify(consumer, times(3)).accept(captor.capture())
        assertThat(captor.allValues, equalTo(listOf(false, true, false)))
        assertThat(machine.currentState(), instanceOf(SimpleSendState::class.java))
    }

    @Test
    fun `Disabled state on disable event`() {
        machine.setState(QuotingState(vm, EventQuote(content)))

        machine.fire(DisabledStateEvent())

        assertThat(machine.currentState(), instanceOf(DisabledState::class.java))
    }

    private fun MessagePanelMessageInteractor<*, *>.mockSendMessage(errorCode: ErrorCode) {
        whenever(
            sendMessage(
                text = Mockito.any(),
                attachments = any(),
                recipientUuids = Mockito.any(),
                documentUuid = Mockito.any(),
                conversationUuid = Mockito.any(),
                folderUuid = Mockito.any(),
                signActions = Mockito.any(),
                quotedMessageUuid = Mockito.any(),
                answeredMessageUuid = Mockito.any(),
                metaData = Mockito.any()
            )
        ).thenReturn(
            Single.just(
                SendMessageResult(null, null, CommandStatus(errorCode, ""))
            )
        )
    }
}