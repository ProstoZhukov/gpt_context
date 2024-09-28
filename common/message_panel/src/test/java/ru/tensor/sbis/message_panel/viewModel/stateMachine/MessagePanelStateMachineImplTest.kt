package ru.tensor.sbis.message_panel.viewModel.stateMachine

import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.common.generated.CommandStatus
import ru.tensor.sbis.communicator.generated.Message
import ru.tensor.sbis.communicator.generated.MessageResult
import ru.tensor.sbis.message_panel.decl.MessageResultHelper
import ru.tensor.sbis.message_panel.interactor.message.MessagePanelMessageInteractor
import ru.tensor.sbis.message_panel.model.CoreConversationInfo
import ru.tensor.sbis.message_panel.viewModel.MessagePanelViewModel
import ru.tensor.sbis.message_panel.viewModel.livedata.MessagePanelLiveData
import ru.tensor.sbis.message_panel.viewModel.livedata.initLiveData
import ru.tensor.sbis.persons.IPersonModel
import ru.tensor.sbis.persons.PersonName
import java.util.*
import java.util.concurrent.CountDownLatch

/**
 * Тестирование реализации [MessagePanelStateMachineImpl] по специфичным сценариям
 *
 * @author vv.chekurda
 * Создан 10/2/2019
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class MessagePanelStateMachineImplTest {

    @Mock
    private lateinit var vm: MessagePanelViewModel<MessageResult, *, *>

    @Mock
    private lateinit var messageInteractor: MessagePanelMessageInteractor<MessageResult, *>

    @Mock
    private lateinit var messageHelper: MessageResultHelper<MessageResult, *>

    private lateinit var liveData: MessagePanelLiveData

    private lateinit var machine: MessagePanelStateMachine<MessageResult, *, *>


    @Before
    fun setUp() {
        machine = MessagePanelStateMachineImpl(vm, Schedulers.io())
        whenever(vm.stateMachine).thenReturn(machine)

        whenever(vm.conversationInfo).thenReturn(CoreConversationInfo())

        whenever(vm.messageInteractor).thenReturn(messageInteractor)
        whenever(vm.messageResultHelper).thenReturn(messageHelper)

        liveData = initLiveData(vm)
        whenever(vm.liveData).thenReturn(liveData)

        machine.start()
    }

    /**
     * Fix https://online.sbis.ru/opendoc.html?guid=6bd66402-7b38-4a17-a1a1-699e869a72c8
     */
    @Test(timeout = 2500L)
    fun `Given enabled machine when replay event received then machine should move to replaying state`() {
        val conversationUuid: UUID = mock()
        val messageUuid: UUID = mock()
        val documentUuid: UUID = mock()
        val showKeyboard = true

        val message: Message = mock()
        val senderUUID: UUID = mock()
        val sender: IPersonModel = mock {
            on { uuid } doReturn senderUUID
            on { name } doReturn PersonName("Test first name", "Test last name", "Test patronymic name")
        }
        val messageResult = MessageResult(message, CommandStatus())
        whenever(messageInteractor.getMessageByUuid(
            messageUuid,
            conversationUuid,
            documentUuid
        )).thenReturn(Single.just(messageResult))
        whenever(messageHelper.getSender(messageResult)).thenReturn(sender)

        val lastInvocation = CountDownLatch(1)
        whenever(vm.loadRecipients(listOf(senderUUID), true)).thenAnswer { lastInvocation.countDown() }

        val stateSubscriber = machine.currentStateObservable.test()

        machine.fire(EventEnable())
        machine.fire(EventReplay(conversationUuid, messageUuid, documentUuid, showKeyboard))

        stateSubscriber.awaitCount(3)
            .assertValueAt(0) { it is DisabledState }
            .assertValueAt(1) { it is CleanSendState<*, *, *> }
            .assertValueAt(2) { it is ReplayingState<*, *, *> }
            .assertValueCount(3)

        // важно дождаться последнего вызова в инициализации ReplayingState, чтобы быть уверенным в корректности
        lastInvocation.await()
    }
}