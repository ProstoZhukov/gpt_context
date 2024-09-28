package ru.tensor.sbis.message_panel.integration

import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.communicator.generated.DataRefreshedMessageControllerCallback
import ru.tensor.sbis.communicator.generated.DataRefreshedMessageControllerEvent
import ru.tensor.sbis.communicator.generated.Message
import ru.tensor.sbis.communicator.generated.MessageController
import ru.tensor.sbis.communicator.generated.MessageResult
import ru.tensor.sbis.platform.generated.Subscription
import java.util.*

/**
 * @author vv.chekurda
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class CommunicatorMessageServiceWrapperTest {

    @Mock
    private lateinit var messageUUID: UUID

    @Mock
    private lateinit var conversationUUID: UUID

    @Mock
    private lateinit var documentUUID: UUID

    @Mock
    private lateinit var messageController: MessageController

    @Mock
    private lateinit var refreshEvent: DataRefreshedMessageControllerEvent

    @Mock
    private lateinit var dependencyProvider: DependencyProvider<MessageController>

    @InjectMocks
    private lateinit var serviceWrapper: CommunicatorMessageServiceWrapper

    @Before
    fun setUp() {
        whenever(messageController.dataRefreshed()).thenReturn(refreshEvent)
        whenever(dependencyProvider.get()).thenReturn(messageController)
    }

    /**
     * Fix https://online.sbis.ru/opendoc.html?guid=e20946b1-ddd7-499d-b2e1-ff805eedeedc
     */
    @Test
    fun `Given empty message cache, when method read called, then it should wait until message sync completed`() {
        val message: Message = mock()
        val byteArray = ByteArray(0)
        val messageResult: MessageResult = mock()

        // первый запрос из кэша, второй после обновления
        whenever(messageController.read(messageUUID)).thenReturn(null, byteArray)
        whenever(messageController.deserializeFromBinaryToMessage(byteArray)).thenReturn(messageResult)
        whenever(messageResult.data).thenReturn(message)

        // в какой-то момент данные синхронизируются и вызывается подписка на обновление
        whenever(refreshEvent.subscribe(any())).then {
            val param = hashMapOf(REQUEST_ID to "read($messageUUID)")
            it.getArgument<DataRefreshedMessageControllerCallback>(0).onEvent(param)
            mock<Subscription>()
        }

        val messageObserver = serviceWrapper.read(messageUUID, conversationUUID, documentUUID).test()

        messageObserver.assertValue { it.data == message }
    }
}