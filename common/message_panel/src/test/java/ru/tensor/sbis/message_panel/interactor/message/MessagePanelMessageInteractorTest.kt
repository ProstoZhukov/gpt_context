package ru.tensor.sbis.message_panel.interactor.message

import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import io.reactivex.Single
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.After
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.message_panel.decl.MessageServiceWrapper
import java.util.*

/**
 * @author vv.chekurda
 * Создан 10/3/2019
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class MessagePanelMessageInteractorTest {

    @Mock
    private lateinit var serviceWrapper: MessageServiceWrapper<Any, *, *>

    @InjectMocks
    private lateinit var messageInteractor: MessagePanelMessageInteractorImpl<Any, *>

    @Before
    fun setUp() {

        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.newThread() }
    }

    @After
    fun tearDown() {
        RxAndroidPlugins.reset()
    }

    /**
     * Fix https://online.sbis.ru/opendoc.html?guid=6bd66402-7b38-4a17-a1a1-699e869a72c8
     */
    @Test
    fun `When message in cache, then interactor should return successful result`() {
        val messageUuid: UUID = mock()
        val conversationUuid: UUID = mock()
        val documentUuid: UUID = mock()
        val message: Any = mock()

        whenever(serviceWrapper.read(messageUuid, conversationUuid, documentUuid)).thenReturn(Single.just(message))

        val resultObserver = messageInteractor.getMessageByUuid(
            messageUuid,
            conversationUuid,
            documentUuid
        ).test()

        resultObserver.await().assertValue(message)
    }

    /**
     * Fix https://online.sbis.ru/opendoc.html?guid=6bd66402-7b38-4a17-a1a1-699e869a72c8
     */
    @Test
    @Ignore("TODO: 10/21/2020 Уточнить актуальность ошибки https://online.sbis.ru/opendoc.html?guid=ba5c3817-c121-4bee-8bdb-925218413930")
    fun `When message not in cache, then interactor should return fail result without message`() {
//        val messageUuid: UUID = mock()
//
//        whenever(controller.setDataRefreshCallback(any())).then {
//            (it.arguments.first() as DataRefreshCallback).execute(null)
//            return@then mock<Subscription>()
//        }
//
//        val resultObserver = messageInteractor.getMessageByUuid(messageUuid).test()
//
//        resultObserver.await().assertValue {
//            it.data == null && it.status.errorCode == ErrorCode.MESSAGE_ID_NOT_FOUND
//        }
    }
}