package ru.tensor.sbis.message_panel.viewModel

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.Schedulers
import org.junit.Assert.*
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.*
import ru.tensor.sbis.attachments.decl.mapper.AttachmentRegisterModelMapper
import ru.tensor.sbis.common.util.FileUriUtil
import ru.tensor.sbis.common.util.ResourceProvider
import ru.tensor.sbis.communication_decl.selection.recipient.manager.RecipientSelectionResultManager
import ru.tensor.sbis.design.message_panel.decl.recipients.data.RecipientDepartmentItem
import ru.tensor.sbis.design.message_panel.decl.recipients.data.RecipientItem
import ru.tensor.sbis.design.message_panel.decl.recipients.data.RecipientPersonItem
import ru.tensor.sbis.message_panel.decl.DraftResultHelper
import ru.tensor.sbis.message_panel.decl.MessageResultHelper
import ru.tensor.sbis.message_panel.interactor.attachments.MessagePanelAttachmentsInteractor
import ru.tensor.sbis.message_panel.interactor.draft.MessagePanelDraftInteractor
import ru.tensor.sbis.message_panel.interactor.message.MessagePanelMessageInteractor
import ru.tensor.sbis.message_panel.interactor.recipients.MessagePanelRecipientsInteractor
import ru.tensor.sbis.message_panel.model.ClearOption
import ru.tensor.sbis.message_panel.model.CoreConversationInfo
import ru.tensor.sbis.message_panel.model.ShareContent
import ru.tensor.sbis.message_panel.viewModel.livedata.MessagePanelLiveDataImpl
import ru.tensor.sbis.message_panel.viewModel.stateMachine.*
import ru.tensor.sbis.mvp.interactor.crudinterface.subscribing.SubscriptionManager
import ru.tensor.sbis.persons.IPersonModel
import ru.tensor.sbis.persons.PersonName
import ru.tensor.sbis.verification_decl.login.LoginInterface
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Интеграционные тесты, в которых задействована связка из:
 * - [MessagePanelViewModelImpl]
 * - [MessagePanelStateMachineImpl]
 * - [MessagePanelLiveDataImpl]
 *
 * [MockitoJUnitRunner.StrictStubs] нельзя удалять без чистки кода тестов.
 *
 * @author vv.chekurda
 * @since 11/24/2019
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class MessagePanelViewModelImplTest {

    private val senderName = "Test sender name"

    @Mock
    private lateinit var conversationUuid: UUID
    @Mock
    private lateinit var messageUuid: UUID
    @Mock
    private lateinit var draftUuid: UUID
    @Mock
    private lateinit var documentUuid: UUID
    @Mock
    private lateinit var draftMessage: DraftMessage
    @Mock
    private lateinit var message: MessageResult
    @Mock
    private lateinit var senderUuid: UUID
    @Mock
    private lateinit var sender: IPersonModel

    @Mock
    private lateinit var recipientsInteractor: MessagePanelRecipientsInteractor
    @Mock
    private lateinit var attachmentsInteractor: MessagePanelAttachmentsInteractor
    @Mock
    private lateinit var messageInteractor: MessagePanelMessageInteractor<MessageResult, MessageSentResult>
    @Mock
    private lateinit var messageResultHelper: MessageResultHelper<MessageResult, MessageSentResult>
    @Mock
    private lateinit var draftResultHelper: DraftResultHelper<DraftMessage>
    @Mock
    private lateinit var draftInteractor: MessagePanelDraftInteractor<DraftMessage>
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

    private lateinit var vm: MessagePanelViewModel<MessageResult, MessageSentResult, DraftMessage>

    @Before
    fun setUp() {
        whenever(attachmentsInteractor.setAttachmentListRefreshCallback(any())).thenReturn(Observable.empty())
        whenever(subscriptionManager.batch()).thenReturn(mock())

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
        )
    }

    @Test
    fun `When recipients are selected by user and it is exist dialog, then core info shouldn't be overridden`() {
        val info = CoreConversationInfo(conversationUuid = UUID.randomUUID())
        val defaultInfo = vm.conversationInfo

        vm.liveData.setRecipientsSelected(true)

        assertFalse(vm.setConversationInfo(info))
        assertSame(defaultInfo, vm.conversationInfo)
    }

    /**
     * Fix https://online.sbis.ru/opendoc.html?guid=fa0ae3a4-7c63-4c04-b74d-48fa1aa3c5d8
     *
     * Для новых диалогов установка [CoreConversationInfo] должна отрабатывать независимо от
     * пользовательского выбора получателей.
     * [CoreConversationInfo.conversationUuid] может поступать отложено в новых диалогах
     */
    @Test
    fun `When recipients are selected by user but it is new dialog, then core info should be overridden`() {
        val info = CoreConversationInfo(conversationUuid = UUID.randomUUID(), isNewConversation = true)

        vm.liveData.setRecipientsSelected(true)

        assertTrue(vm.setConversationInfo(info))
        assertEquals(info, vm.conversationInfo)
    }

    @Test
    fun `When recipients aren't selected by user, then core info should be overridden`() {
        val info = CoreConversationInfo(conversationUuid = UUID.randomUUID())

        vm.liveData.setRecipientsSelected(false)

        assertTrue(vm.setConversationInfo(info))
        assertEquals(info, vm.conversationInfo)
    }

    /**
     * В тесте описан техпроцесс ответа на комментарий к новости при переходе через пуш.
     *
     * Fix https://online.sbis.ru/opendoc.html?guid=817dcc1f-70dc-4db7-86ff-913d9f7e9197
     */
    @Test
    @Ignore("TODO починить https://online.sbis.ru/opendoc.html?guid=ba5c3817-c121-4bee-8bdb-925218413930")
    fun `When user replay to message by push, then replayed message should be sent`() {
        /*
        Основная информация об ответе на комментарий, которую нужно проверить в тесте
         */
        val messageUuid = UUID.randomUUID()
        val conversationUuid = UUID.randomUUID()
        val documentUuid = UUID.randomUUID()
        val showKeyboard = false

        /*
        Информация, с которой инициализируется панель ввода
         */
        val info = CoreConversationInfo(
            conversationUuid = conversationUuid,
            messageUuid = messageUuid,
            document = documentUuid,
            showOneRecipient = true,
            clearOnSendOptions = EnumSet.of(ClearOption.CLEAR_RECIPIENTS, ClearOption.HIDE_KEYBOARD)
        )

        /*
        Информация о получателе при переходе в режим ответа
         */
        val senderFirstName = "SenderFirstName"
        val senderUuid = UUID.randomUUID()
        val messageSender: IPersonModel = mock()
        whenever(messageSender.uuid).thenReturn(senderUuid)
        val senderName: PersonName = mock {
            on { firstName } doReturn senderFirstName
            on { lastName } doReturn "SenderLastName"
            on { patronymicName } doReturn "SenderPatronymic"
        }
        whenever(messageSender.name).thenReturn(senderName)
        val messageResult: MessageResult = mock()

        /*
        Информация для отправки сообщения
         */
        val messageText = "$senderFirstName, "
        val recipientUuid = UUID.randomUUID()
        val recipient: RecipientItem = mock { on { uuid } doReturn recipientUuid }

        /*
        Информация о результатах отправки сообщения
         */
        val sendResult: MessageSentResult = mock()

        // загрузка получателей при инициализации
        whenever(recipientsInteractor.loadRecipientModels(emptyList())).thenReturn(Maybe.empty())
        // подписка на события от CRUD фасада вложений
        whenever(attachmentsInteractor.setAttachmentListRefreshCallback(any())).thenReturn(Observable.empty())
        // загрузка информации по сообщению, на которое отвечают
        whenever(messageInteractor.getMessageByUuid(
            messageUuid,
            conversationUuid,
            documentUuid
        )).thenReturn(Single.just(messageResult))
        // получение информации об отправителе сообщения
        whenever(messageResultHelper.getSender(messageResult)).thenReturn(messageSender)
        // загрузка получателя (пользователя, на чьё сообщение отвечают)
        whenever(recipientsInteractor.loadRecipientModels(listOf(senderUuid))).thenReturn(Maybe.just(listOf(recipient)))
        // отправка ответа
        whenever(messageInteractor.sendMessage(messageText, emptyList(), listOf(recipientUuid), documentUuid, conversationUuid, null, null, null, messageUuid, null)).thenReturn(Single.just(sendResult))
        // запрос нового черновика после отправки сообщения
        whenever(draftInteractor.loadDraft(conversationUuid, documentUuid)).thenReturn(mock())

        // подписка на состояния машины для предварительной проверки завершения полного цикла отправки ответа
        val stateObserver = vm.stateMachine.currentStateObservable.test()

        /*
        Последовательность вызовов при формировании для ответа на комментарий
         */
        vm.setConversationInfo(info)
        vm.enable()
        vm.replyComment(conversationUuid, messageUuid, documentUuid, showKeyboard)
        vm.sendMessage()

        // ожидаемые состояния техпроцесса
        stateObserver
            .awaitCount(5)
            .assertValueAt(0) { it is DisabledState }
            .assertValueAt(1) { it is CleanSendState<*, *, *> }
            .assertValueAt(2) { it is ReplayingState<*, *, *> }
            .assertValueAt(3) { it is SendingSimpleMessageState<*, *, *> }
            .assertValueAt(4) { it is CleanSendState<*, *, *> }
        // ключевая проверка: sendMessage должен быть вызван с идентификаторами, которые переданы в replyComment
        verify(messageInteractor).sendMessage(messageText, emptyList(), listOf(recipientUuid), documentUuid, conversationUuid, null, null, null, messageUuid, null)
    }

    /**
     * При условии, что контроллер не меняет порядок получателей, порядок от vm до liveData не меняется
     *
     * Fix https://online.sbis.ru/opendoc.html?guid=4e22d0cb-12db-4e68-bda4-b9aafe5df7fa
     */
    @Test
    fun `When view model receive list of recipient UUIDs, then live data receive models without reordering`() {
        val recipientsObserver = vm.liveData.recipients.flatMapIterable { it }.map { item -> item.uuid }.test()
        val uuid1: UUID = mock()
        val uuid2: UUID = mock()
        val uuid3: UUID = mock()
        val uuidList = listOf(uuid3, uuid1, uuid2)
        val contact1: RecipientItem = mock<RecipientPersonItem> { on { uuid } doReturn uuid1 }
        val contact2: RecipientItem = mock<RecipientDepartmentItem> { on { uuid } doReturn uuid2 }
        val contact3: RecipientItem = mock<RecipientPersonItem> { on { uuid } doReturn uuid3 }
        whenever(recipientsInteractor.loadRecipientModels(emptyList())).thenReturn(Maybe.just(emptyList()))
        whenever(recipientsInteractor.loadRecipientModels(uuidList))
            // контроллер загружает контакты без изменения порядка
            .thenReturn(Maybe.just(listOf(contact3, contact1, contact2)))

        vm.enable()
        vm.setRecipients(uuidList)

        recipientsObserver.awaitCount(3).assertValues(uuid3, uuid1, uuid2)
    }

    //region Fix https://online.sbis.ru/opendoc.html?guid=0fe2c66b-26c0-45bf-9e11-48dc6bb17329
    @Test
    @Ignore("TODO починить https://online.sbis.ru/opendoc.html?guid=ba5c3817-c121-4bee-8bdb-925218413930")
    fun `When view model load empty draft with recipients, then set recipient override draft recipients`() {
        val (draftContact: RecipientItem, contacts: List<RecipientItem>, recipientsObserver) = mockRecipientsDraft(true)

        recipientsObserver.awaitCount(3)
            // значение после сброса получателей на новой панели ввода
            .assertValueAt(0) { it.isEmpty() }
            // получатели, которые загружены из черновика
            .assertValueAt(1) { it == listOf(draftContact) }
            // переопределение получателей из черновика внешней установкой
            .assertValueAt(2) { it == contacts }
    }

    @Test
    @Ignore("TODO починить https://online.sbis.ru/opendoc.html?guid=ba5c3817-c121-4bee-8bdb-925218413930")
    fun `When view model load draft with content, then set recipient should not override recipients`() {
        val (draftContact: RecipientItem, contacts: List<RecipientItem>, recipientsObserver) = mockRecipientsDraft(false)

        recipientsObserver.awaitCount(2)
            // значение после сброса получателей на новой панели ввода
            .assertValueAt(0) { it.isEmpty() }
            // получатели, которые загружены из черновика
            .assertValueAt(1) { it == listOf(draftContact) }
            // внешние получатели не устанавливаются т.к контент не пустой
            .assertNever(contacts)
    }

    /**
     * Подготовка окружения для загрузки получателей из черновика
     */
    private fun mockRecipientsDraft(
        contentIsEmpty: Boolean
    ): Triple<RecipientItem, List<RecipientItem>, TestObserver<List<RecipientItem>>> {
        val messageUUID: UUID = mock()
        val draftRecipient: UUID = mock()
        val draft: DraftMessage = mock()
        val draftContact: RecipientItem = mock()

        // тестирование на основе загрузки черновика для беседы
        val conversationUuid: UUID = mock()
        val info = CoreConversationInfo(conversationUuid = conversationUuid)

        // пара получателей для внешней установки чтобы отличать от одного получателя из черновика
        val recipient1: UUID = mock()
        val recipient2: UUID = mock()
        val recipients = listOf(recipient1, recipient2)
        val contact1: RecipientItem = mock()
        val contact2: RecipientItem = mock()
        val contacts = listOf(contact1, contact2)

        whenever(recipientsInteractor.loadRecipientModels(emptyList())).thenReturn(Maybe.just(emptyList()))
        whenever(recipientsInteractor.loadRecipientModels(listOf(draftRecipient))).thenReturn(Maybe.just(listOf(draftContact)))
        whenever(recipientsInteractor.loadRecipientModels(recipients)).thenReturn(Maybe.just(contacts))
        // загрузка черновика с имитацией задержки
        val draftSingle = Single.just(draft).delay(100L, TimeUnit.MILLISECONDS)
        whenever(draftInteractor.loadDraft(conversationUuid, null)).thenReturn(draftSingle)
        whenever(draftResultHelper.isEmpty(draft)).thenReturn(contentIsEmpty)
        whenever(draftResultHelper.getId(draft)).thenReturn(messageUUID)
        whenever(draftResultHelper.getRecipients(draft)).thenReturn(recipients)
        whenever(attachmentsInteractor.loadAttachments(messageUUID)).thenReturn(Single.just(emptyList()))

        val recipientsObserver = vm.liveData.recipients.skip(1/*значение по умолчанию*/).test()

        vm.setConversationInfo(info)
        vm.loadDraft()
        vm.setRecipients(listOf(recipient1, recipient2))

        return Triple(draftContact, contacts, recipientsObserver)
    }
    //endregion

    /**
     * Применена простая стратегия - игнорирование черновика
     *
     * Fix https://online.sbis.ru/opendoc.html?guid=0fe2c66b-26c0-45bf-9e11-48dc6bb17329
     */
    @Test
    fun `When view model load draft and replay event received, then state machine should switch to replaying state`() {
        // тестирование на основе загрузки черновика для беседы
        val info = CoreConversationInfo(conversationUuid = conversationUuid)

        val replayConversationUuid: UUID = mock()

        whenever(recipientsInteractor.loadRecipientModels(recipients = any())).thenReturn(Maybe.just(emptyList()))
        // имитация задержки при загрузке черновика
        val draftSource = Single.just(draftMessage).delay(100L, TimeUnit.MILLISECONDS)
        whenever(draftInteractor.loadDraft(conversationUuid, null)).thenReturn(draftSource)
        whenever(draftResultHelper.getId(draftMessage)).thenReturn(draftUuid)
        whenever(messageInteractor.getMessageByUuid(any(), any(), any())).thenReturn(Single.just(message))
        val mockPersonName = mock<PersonName> {
            on { firstName } doReturn ""
        }
        val mockSender = mock<IPersonModel> {
            on { name } doReturn mockPersonName
        }
        whenever(messageResultHelper.getSender(any())).thenReturn(mockSender)
        val mockCompletable = mock<Completable> {
            on { subscribe() } doReturn mock()
        }
        whenever(messageInteractor.notifyUserTyping(any())).thenReturn(mockCompletable)

        val stateObserver = vm.stateMachine.currentStateObservable.test()

        vm.setConversationInfo(info)
        vm.loadDraft()
        vm.replyComment(replayConversationUuid, mock(), mock(), false)

        stateObserver
            .awaitCount(3)
            .assertValueAt(0) { it is DisabledState }
            .assertValueAt(1) { it is DraftLoadingState<*, *, *> }
            .assertValueAt(2) { it is ReplayingState<*, *, *> }
            .assertValueCount(3)
            // https://online.sbis.ru/opendoc.html?guid=ff3a94bc-19bd-4976-b6b4-0fc9f3ea2763&client=3
            .await(100, TimeUnit.MILLISECONDS)
    }

    //region Fix https://online.sbis.ru/opendoc.html?guid=d9082a1e-f7e9-4c6e-9a03-06a2536d6163
    @Test
    @Ignore("TODO: 5/3/2021 https://online.sbis.ru/opendoc.html?guid=ba5c3817-c121-4bee-8bdb-925218413930")
    fun `When state machine moved to replaying state and then got share event, then share content should be inserted into message panel`() {
        mockReplayState()
        val sharedText = "Test shared message"
        val expectedText = "$senderName, $sharedText"
        val attachments = listOf("file://test/file.txt")

        whenever(draftInteractor.saveDraft(
            draftUuid,
            conversationUuid,
            documentUuid,
            listOf(senderUuid),
            expectedText,
            emptyList(),
            null,
            messageUuid,
            null
        )).thenReturn(Completable.complete())
        whenever(attachmentsInteractor.addAttachments(draftUuid, attachments)).thenReturn(Completable.complete())

        val textObserver = vm.liveData.messageText.test()
        val stateObserver = vm.stateMachine.currentStateObservable.test()

        vm.setConversationInfo(CoreConversationInfo(
            conversationUuid = conversationUuid,
            messageUuid = messageUuid,
            document = documentUuid
        ))
        vm.loadDraft()
        vm.replyComment(conversationUuid, messageUuid, documentUuid, false)
        vm.shareMessage(ShareContent(sharedText, attachments))
        vm.disable()

        // убедимся, что перешли в нужное состояние
        stateObserver.awaitCount(3).assertOf { it.values().last() is ReplayingState<*, *, *> }

        textObserver.awaitCount(4).assertOf{
            @Suppress("UnusedEquals" /* в контейнере всегда строка */)
            it.values().last().value == expectedText
        }

        // дождёмся окончания работы, чтобы гарантировать вызов addAttachments()
        stateObserver.awaitCount(4).assertOf { it.values().last() is DisabledState }
        verify(attachmentsInteractor).addAttachments(draftUuid, attachments)
    }


    @Test
    @Ignore("TODO: 5/3/2021 https://online.sbis.ru/opendoc.html?guid=ba5c3817-c121-4bee-8bdb-925218413930")
    fun `When draft loading interrupted by replay event, then at least draft id should be loaded`() {
        mockReplayState()

        val draftUuidObserver = vm.liveData.draftUuidUpdater.test()
        val stateObserver = vm.stateMachine.currentStateObservable.test()

        vm.setConversationInfo(CoreConversationInfo(
            conversationUuid = conversationUuid,
            messageUuid = messageUuid,
            document = documentUuid
        ))
        vm.loadDraft()
        vm.replyComment(conversationUuid, messageUuid, documentUuid, false)

        // убедимся, что перешли в нужное состояние
        stateObserver.awaitCount(3).assertOf { it.values().last() is ReplayingState<*, *, *> }
        draftUuidObserver.awaitCount(1).assertValue(draftUuid)
    }

    private fun mockReplayState() {
        // ключевой атрибут, при больших значениях которого тест переход в состояние ответа минуя загрузку идентификатора черновика
        val draftLoadingDelay = 30L
        whenever(sender.uuid).thenReturn(senderUuid)
        whenever(sender.name).thenReturn(PersonName(senderName, "Test sender last name", "Test sender patronymic name"))
        // загруженный отправитель сообщения, кому отвечаем
        val recipient: RecipientItem = mock { on { uuid } doReturn senderUuid }

        whenever(recipientsInteractor.loadRecipientModels(emptyList())).thenReturn(Maybe.just(emptyList()))
        whenever(recipientsInteractor.loadRecipientModels(listOf(senderUuid))).thenReturn(Maybe.just(listOf(recipient)))
        whenever(draftInteractor.loadDraft(conversationUuid, documentUuid))
            .thenReturn(Single.just(draftMessage).delay(draftLoadingDelay, TimeUnit.MILLISECONDS))
        whenever(draftResultHelper.getId(draftMessage)).thenReturn(draftUuid)
        whenever(messageInteractor.getMessageByUuid(
            messageUuid,
            conversationUuid,
            documentUuid
        )).thenReturn(Single.just(message))
        whenever(messageResultHelper.getSender(message)).thenReturn(sender)
    }
    //endregion
}

/**
 * Тестовая модель результата запроса сообщения
 */
private interface MessageResult

/**
 * Тестовая модель результата отправки сообщения
 */
private interface MessageSentResult

/**
 * Тестовая модель черновика сообщения
 */
private interface DraftMessage
