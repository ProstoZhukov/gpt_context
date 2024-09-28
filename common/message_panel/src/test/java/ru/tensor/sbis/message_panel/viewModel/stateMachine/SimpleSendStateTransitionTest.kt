package ru.tensor.sbis.message_panel.viewModel.stateMachine

import org.mockito.kotlin.*
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import junitparams.JUnitParamsRunner
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.*
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
import ru.tensor.sbis.common.util.ResourceProvider
import ru.tensor.sbis.communicator.generated.SendMessageResult
import ru.tensor.sbis.message_panel.attachments.MessagePanelAttachmentHelper
import ru.tensor.sbis.message_panel.decl.MessageResultHelper
import ru.tensor.sbis.message_panel.interactor.message.MessagePanelMessageInteractor
import ru.tensor.sbis.message_panel.setUpDefaultSubjects
import ru.tensor.sbis.message_panel.viewModel.MessagePanelViewModel
import ru.tensor.sbis.message_panel.viewModel.livedata.MessagePanelLiveData
import java.util.*

/**
 * @author vv.chekurda
 * Создан 8/16/2019
 */
@RunWith(JUnitParamsRunner::class)
class SimpleSendStateTransitionTest {

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

    private lateinit var machine: MessagePanelStateMachine<*, Any, *>

    @Before
    fun setUp() {
        liveData.setUpDefaultSubjects()
        whenever(vm.liveData).thenReturn(liveData)
        machine = MessagePanelStateMachineImpl(vm, Schedulers.trampoline())
        whenever(vm.stateMachine).thenReturn(machine)
        whenever(vm.messageInteractor).thenReturn(messageInteractor)
        whenever(vm.messageResultHelper).thenReturn(messageHelper)
        whenever(vm.attachmentPresenter).thenReturn(attachmentsHelper)
        Mockito.lenient().`when`(vm.resourceProvider).thenReturn(resourceProvider)
    }

    @Before
    fun setUpLiveDataDependencies() {
        // важно, что бы возвращались именно BehaviorSubject для получения value в ru.tensor.sbis.common.rx.livedata
        val messageSubject = BehaviorSubject.createDefault(RxContainer(messageText))
        whenever(liveData.messageText).thenReturn(messageSubject)
        whenever(liveData.document).thenReturn(BehaviorSubject.createDefault(RxContainer(documentUuid)))
        whenever(liveData.conversationUuid).thenReturn(BehaviorSubject.createDefault(RxContainer(conversationUuid)))
        whenever(liveData.folderUuid).thenReturn(BehaviorSubject.createDefault(RxContainer(folderUuid)))
        whenever(liveData.quotedMessageUuid).thenReturn(BehaviorSubject.createDefault(RxContainer(quotedMessageUuid)))
        whenever(liveData.answeredMessageUuid).thenReturn(BehaviorSubject.createDefault(RxContainer(answeredMessageUuid)))

        whenever(liveData.attachments).thenReturn(PublishSubject.create())
        whenever(liveData.recipientsSelected).thenReturn(PublishSubject.create())
        whenever(liveData.quotePanelVisible).thenReturn(PublishSubject.empty())
    }

    @After
    fun tearDown() {
        machine.stop()
    }

    /**
     * Fix https://online.sbis.ru/opendoc.html?guid=f5811ea6-4264-4097-bb60-19a8db77ca60
     *
     * При отправке сообщения в случае получения ошибки панель сообщений не должна чиститься,
     * иначе в сценариях нового диалога при отправке первого сообщения с ошибкой - последующие сообщения могут отправиться без получателей,
     * из-за чего на облаке не создастся диалог, а отправленные сообщения кэшируются впустую без привязки к конкретному диалогу
     */
    @Test
    fun `Simple state on sending failed`() {
        whenever(messageHelper.isSentResultError(any())).thenReturn(true)
        whenever(messageHelper.getSentResultError(any())).thenReturn(sentErrorTest)
        messageInteractor.mockSendMessage()

        machine.setState(SendingSimpleMessageState(vm))

        assertThat(machine.currentState(), instanceOf(SimpleSendState::class.java))
    }

    private fun MessagePanelMessageInteractor<*, *>.mockSendMessage(
        errorCode: ErrorCode = ErrorCode.SUCCESS
    ) {
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
        )).thenReturn(Single.just(
            SendMessageResult(null, null, CommandStatus(errorCode, sentErrorTest)))
        )
    }
}