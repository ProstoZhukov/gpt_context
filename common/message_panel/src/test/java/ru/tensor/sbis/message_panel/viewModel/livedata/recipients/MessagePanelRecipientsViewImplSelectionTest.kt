package ru.tensor.sbis.message_panel.viewModel.livedata.recipients

import org.mockito.kotlin.*
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.common.rx.RxContainer
import ru.tensor.sbis.communication_decl.selection.recipient.RecipientSelectionConfig
import ru.tensor.sbis.message_panel.model.CoreConversationInfo
import ru.tensor.sbis.message_panel.viewModel.MessagePanelViewModel
import ru.tensor.sbis.message_panel.viewModel.livedata.MessagePanelLiveData
import java.util.*
import kotlin.random.Random

/**
 * Тест механики работы с запросами выбора получателей
 *
 * @author vv.chekurda
 * Создан 10/8/2019
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class MessagePanelRecipientsViewImplSelectionTest {

    private lateinit var documentSubject: Subject<RxContainer<UUID?>>
    private lateinit var conversationSubject: Subject<RxContainer<UUID?>>

    @Mock
    private lateinit var documentUUID: RxContainer<UUID?>
    @Mock
    private lateinit var conversationUUID: RxContainer<UUID?>
    @Mock
    private lateinit var conversationInfo: CoreConversationInfo
    @Mock
    private lateinit var config: RecipientSelectionConfig

    @Mock
    private lateinit var configFactory: RecipientSelectionConfigFactory

    @Mock
    private lateinit var liveData: MessagePanelLiveData
    @Mock
    private lateinit var vm: MessagePanelViewModel<*, *, *>

    private lateinit var recipientsView: MessagePanelRecipientsView

    @Before
    fun setUp() {
        documentSubject = BehaviorSubject.createDefault(documentUUID)
        conversationSubject = BehaviorSubject.createDefault(conversationUUID)

        whenever(liveData.document).thenReturn(documentSubject)
        whenever(liveData.conversationUuid).thenReturn(conversationSubject)
        whenever(vm.liveData).thenReturn(liveData)
        whenever(vm.conversationInfo).thenReturn(conversationInfo)
        whenever(configFactory.apply(any(), eq(documentUUID), eq(conversationUUID))).thenReturn(config)

        recipientsView = MessagePanelRecipientsViewImpl(vm, configFactory, observeOn = Schedulers.single())
    }

    @Test
    fun `Recipient selection filter shouldn't delivered by default`() {
        val filterObserver = recipientsView.recipientSelectionScreen.test()

        verifyNoMoreInteractions(configFactory)
        filterObserver.assertNoValues()
    }

    @Test
    fun `When recipient selection requested then new selection filter should be returned`() {
        val filterObserver = recipientsView.recipientSelectionScreen.test()

        // Запросим создание фильтра на получателей
        recipientsView.requestRecipientsSelection()

        verify(configFactory, only()).apply(any(), eq(documentUUID), eq(conversationUUID))
        filterObserver.assertValue(config)
    }

    /**
     * Fix https://online.sbis.ru/opendoc.html?guid=3b339007-c1ed-4314-b254-624b5816d337
     */
    @Test
    fun `Recipient selection shouldn't be requested on conversation uuid change`() {
        val filterObserver = recipientsView.recipientSelectionScreen.test()

        recipientsView.requestRecipientsSelection()
        // Изменим идентификатор диалога, запрос пользователя только по вызову requestRecipientsSelection()
        conversationSubject.onNext(mock())

        verify(configFactory, only()).apply(any(), eq(documentUUID), eq(conversationUUID))
        filterObserver.assertValue(config)
    }

    /**
     * Fix https://online.sbis.ru/opendoc.html?guid=3b339007-c1ed-4314-b254-624b5816d337
     */
    @Test
    fun `Recipient selection shouldn't be requested on document uuid change`() {
        val filterObserver = recipientsView.recipientSelectionScreen.test()

        recipientsView.requestRecipientsSelection()
        // Изменим идентификатор документа, запрос пользователя только по вызову requestRecipientsSelection()
        documentSubject.onNext(mock())

        verify(configFactory, only()).apply(any(), eq(documentUUID), eq(conversationUUID))
        filterObserver.assertValue(config)
    }

    @Test
    fun `When recipient selection requested, then filter factory should receive recipient limit`() {
        val limit = Random.nextInt(MAX_RECIPIENTS_COUNT) + 1
        whenever(conversationInfo.recipientsLimit).thenReturn(limit)
        val filterObserver = recipientsView.recipientSelectionScreen.test()

        recipientsView.requestRecipientsSelection()

        filterObserver.awaitCount(1)
        verify(configFactory, only()).apply(eq(limit), eq(documentUUID), eq(conversationUUID))
    }
}