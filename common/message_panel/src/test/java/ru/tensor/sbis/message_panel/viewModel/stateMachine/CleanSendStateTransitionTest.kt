package ru.tensor.sbis.message_panel.viewModel.stateMachine

import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.common.generated.CommandStatus
import ru.tensor.sbis.common.generated.ErrorCode
import ru.tensor.sbis.common.rx.RxContainer
import ru.tensor.sbis.common.testing.TrampolineSchedulerRule
import ru.tensor.sbis.communicator.generated.SendMessageResult
import ru.tensor.sbis.message_panel.interactor.message.MessagePanelMessageInteractor
import ru.tensor.sbis.message_panel.setUpDefaultSubjects
import ru.tensor.sbis.message_panel.viewModel.MessagePanelViewModel
import ru.tensor.sbis.message_panel.viewModel.livedata.MessagePanelLiveData

/**
 * Тестирование переходов в состояние [CleanSendState]
 *
 * @author vv.chekurda
 * @since 7/19/2019
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class CleanSendStateTransitionTest {

    @get:Rule
    val rxSchedulerRule = TrampolineSchedulerRule()

    private val messageText = "Test message text"

    @Mock
    private lateinit var liveData: MessagePanelLiveData

    @Mock
    private lateinit var messageInteractor: MessagePanelMessageInteractor<Any, *>

    @Mock
    private lateinit var vm: MessagePanelViewModel<Any, *, *>

    private lateinit var machine: MessagePanelStateMachine<Any, *, *>

    @Before
    fun setUp() {
        whenever(vm.liveData).thenReturn(liveData)
        liveData.setUpDefaultSubjects()
        machine = MessagePanelStateMachineImpl(vm, Schedulers.trampoline())
        whenever(vm.stateMachine).thenReturn(machine)
        // TODO починить https://online.sbis.ru/opendoc.html?guid=ba5c3817-c121-4bee-8bdb-925218413930
        //whenever(vm.attachmentPresenter).thenReturn(attachments)

        machine.start()
    }

    @Before
    fun setUpLiveDataDependencies() {
        // важно, что бы возвращались именно BehaviorSubject для получения value в ru.tensor.sbis.common.rx.livedata
        val messageSubject = BehaviorSubject.createDefault(RxContainer(messageText))
        whenever(liveData.messageText).thenReturn(messageSubject)
        val text: String? = any()
        whenever(liveData.setMessageText(text)).then { messageSubject.onNext(RxContainer(text)) }
        // TODO починить https://online.sbis.ru/opendoc.html?guid=ba5c3817-c121-4bee-8bdb-925218413930
//        whenever(liveData.document).thenReturn(BehaviorSubject.createDefault(RxContainer(documentUuid)))
//        whenever(liveData.folderUuid).thenReturn(BehaviorSubject.createDefault(RxContainer(folderUuid)))
//        whenever(liveData.answeredMessageUuid).thenReturn(BehaviorSubject.createDefault(RxContainer(answeredMessageUuid)))

        whenever(liveData.attachments).thenReturn(PublishSubject.create())
        whenever(liveData.recipientsSelected).thenReturn(PublishSubject.create())
        whenever(liveData.quotePanelVisible).thenReturn(PublishSubject.empty())
    }

    @After
    fun tearDown() {
        machine.stop()
    }

    @Test
    fun `Clean state on enable event`() {
        machine.setState(DisabledState(vm))
        machine.fire(EventEnable())
        assertState()
    }

    @Test
    @Ignore("TODO починить https://online.sbis.ru/opendoc.html?guid=ba5c3817-c121-4bee-8bdb-925218413930")
    fun `Clean state on sending success`() {
        messageInteractor.mockSendMessage(ErrorCode.SUCCESS)
        machine.setState(SendingSimpleMessageState(vm))
        assertState()
    }

    @Test
    fun `Clear state enable recipients panel visibility`() {
        /*
        Fix https://online.sbis.ru/opendoc.html?guid=48d0aa1e-d0e6-4785-a1a2-b3e23a60717d
        Актуально, пока в методе вызывается MessagePanelLiveData.forceHideRecipientsPanel()
         */
        machine.fire(EventEnable())
        verify(vm).resetConversationInfo()
    }

    private fun assertState() {
        assertThat(machine.currentState(), instanceOf(CleanSendState::class.java))
    }

    private fun MessagePanelMessageInteractor<*, *>.mockSendMessage(errorCode: ErrorCode) {
        whenever(sendMessage(
            text = any(),
            attachments = any(),
            recipientUuids = any(),
            documentUuid = any(),
            conversationUuid = any(),
            folderUuid = any(),
            signActions = any(),
            quotedMessageUuid = any(),
            answeredMessageUuid = any(),
            metaData = null
        )).thenReturn(Single.just(
            SendMessageResult(null, null, CommandStatus(errorCode, "")))
        )
    }
}