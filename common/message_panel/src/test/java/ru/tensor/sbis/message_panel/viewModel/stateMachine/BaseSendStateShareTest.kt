package ru.tensor.sbis.message_panel.viewModel.stateMachine

import org.junit.Ignore
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

/**
 * Тестирование установки содержимого, которым нужно поделиться
 *
 * @author vv.chekurda
 * Создан 8/10/2019
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
@Ignore("TODO починить https://online.sbis.ru/opendoc.html?guid=ba5c3817-c121-4bee-8bdb-925218413930")
class BaseSendStateShareTest {
/*
    @get:Rule
    val rxSchedulerRule = TrampolineSchedulerRule()

    private val text = "Content text stub"
    private lateinit var content: ShareContent

    @Mock
    private lateinit var vm: MessagePanelViewModel

    private lateinit var machine: MessagePanelStateMachine

    private lateinit var liveData: MessagePanelLiveData

    @Mock
    private lateinit var attachments: MessagePanelAttachmentHelper

    @Mock
    private lateinit var files: List<String>

    @InjectMocks
    private lateinit var state: BaseSendState

    @Before
    fun setUpMachine() {
        whenever(vm.conversationInfo).thenReturn(CoreConversationInfo())

        machine = MessagePanelStateMachineImpl(vm, Schedulers.trampoline())
        whenever(vm.stateMachine).thenReturn(machine)
        liveData = spy(initLiveData(vm))
        whenever(vm.liveData).thenReturn(liveData)
        whenever(vm.attachmentPresenter).thenReturn(attachments)
    }

    @Before
    fun setUp() {
        content = ShareContent(text, files)
    }

    @After
    fun tearDown() {
        machine.stop()
    }

    @Test
    fun `Live data receive text and attachment helper started to load files`() {
        machine.setState(state)

        machine.fire(EventShare(content))

        verify(liveData).setMessageText(text)
        verify(attachments, only()).onFilesAttached(files)
    }

    @Test
    fun `Clean state changed to simple send state`() {
        machine.setState(CleanSendState(vm, false))

        machine.fire(EventShare(content))

        assertThat(machine.currentState(), instanceOf(SimpleSendState::class.java))
    }

    @Test
    fun `Simple send state not changed on data sharing`() {
        machine.setState(SimpleSendState(vm))

        machine.fire(EventShare(content))

        assertThat(machine.currentState(), instanceOf(SimpleSendState::class.java))
    }

    /**
     * Fix https://online.sbis.ru/opendoc.html?guid=fa0ae3a4-7c63-4c04-b74d-48fa1aa3c5d8
     *
     * Тест проверяет, что связка [MessagePanelLiveDataImpl] и [MessagePanelStateMachineImpl] не теряет UUID после
     * отправки сообщения с [content]
     */
    @Test
    fun `When text with attachment shared, then conversation UUID shouldn't be loosed`() {
        val typedText = "Typed by user text"
        val conversationUuid: UUID = mock()
        val attachmentList: List<MessageAttachment> = listOf(mock())
        val emptyAttachmentList: List<MessageAttachment> = mock()
        val interactor: MessagePanelRecipientsInteractor = mock()
        val sendResult = SendMessageResult(conversationUuid, mock(), CommandStatus(ErrorCode.SUCCESS, null))

        whenever(interactor.sendMessage(any(), any(), any(), eq(null), eq(conversationUuid), eq(null), eq(null), eq(null), eq(null)))
            .thenReturn(Single.just(sendResult))
        whenever(vm.interactor).thenReturn(interactor)
        whenever(attachments.attachments).thenReturn(attachmentList)

        // устанавливаем UUID, как это делает vm
        liveData.setConversationUuid(conversationUuid)

        machine.setState(state)
        machine.fire(EventShare(content))
        assertThat(machine.currentState(), instanceOf(SimpleSendState::class.java))

        machine.fire(EventSend())
        verify(interactor).sendMessage(eq(text), eq(attachmentList), any(), eq(null), eq(conversationUuid), eq(null), eq(null), eq(null), eq(null))

        whenever(attachments.attachments).thenReturn(emptyAttachmentList)

        liveData.setMessageText(typedText)
        machine.fire(EventSend())

        verify(interactor).sendMessage(eq(typedText), eq(emptyAttachmentList), any(), eq(null), eq(conversationUuid), eq(null), eq(null), eq(null), eq(null))

        assertThat(machine.currentState(), instanceOf(CleanSendState::class.java))
    }
 */
}