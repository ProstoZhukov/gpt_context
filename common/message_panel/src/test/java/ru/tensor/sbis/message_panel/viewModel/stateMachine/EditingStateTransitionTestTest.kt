package ru.tensor.sbis.message_panel.viewModel.stateMachine

import org.mockito.kotlin.*
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import org.apache.commons.lang3.StringUtils
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.common.generated.CommandStatus
import ru.tensor.sbis.common.rx.RxContainer
import ru.tensor.sbis.communicator.generated.MessageTextWithMentions
import ru.tensor.sbis.message_panel.interactor.draft.MessagePanelDraftInteractor
import ru.tensor.sbis.message_panel.interactor.message.MessagePanelMessageInteractor
import ru.tensor.sbis.message_panel.model.CoreConversationInfo
import ru.tensor.sbis.message_panel.model.EditContent
import ru.tensor.sbis.message_panel.viewModel.MessagePanelViewModel
import ru.tensor.sbis.message_panel.viewModel.livedata.MessagePanelLiveData
import java.util.*

/**
 * @author vv.chekurda
 * Создан 8/14/2019
 */
@RunWith(MockitoJUnitRunner::class)
class EditingStateTransitionTestTest {

    private val messageTitle = "Edit message test title"
    private val messageText = "Edit message test text"
    private val messageTextWithMentions = MessageTextWithMentions(messageText, StringUtils.EMPTY)
    private val messageUuid = UUID.randomUUID()
    private val conversationUuid = UUID.randomUUID()
    private val event = EventEdit(messageTitle, EditContent(messageUuid))

    @Mock
    private lateinit var liveData: MessagePanelLiveData

    @Mock
    private lateinit var messageInteractor: MessagePanelMessageInteractor<*, *>

    @Mock
    private lateinit var vm: MessagePanelViewModel<*, *, *>

    private lateinit var machine: MessagePanelStateMachine<*, *, *>

    @Before
    fun setUpMachine() {
        whenever(vm.liveData).thenReturn(liveData)
        whenever(vm.conversationInfo).thenReturn(mock())
        whenever(liveData.messageText).thenReturn(PublishSubject.create())
        whenever(vm.saveDraft()).thenReturn(Completable.complete())
        machine = MessagePanelStateMachineImpl(vm, Schedulers.trampoline())
    }

    @Before
    fun setUp() {
        whenever(liveData.originalMessageText).thenReturn(Observable.empty())
        whenever(liveData.messageText).thenReturn(Observable.empty())
        whenever(liveData.isAttachmentsEdited).thenReturn(Observable.empty())
        whenever(liveData.hasAttachments).thenReturn(Observable.empty())
        whenever(messageInteractor.beginEditMessage(any())).thenReturn(Single.just(CommandStatus()))
        whenever(messageInteractor.cancelEditMessage(any())).thenReturn(Single.just(CommandStatus()))
        whenever(vm.messageInteractor).thenReturn(messageInteractor)
        whenever(vm.attachmentPresenter).thenReturn(mock())
    }

    @After
    fun tearDown() {
        machine.stop()
    }

    /**
     * Fix https://online.sbis.ru/opendoc.html?guid=48d0aa1e-d0e6-4785-a1a2-b3e23a60717d
     */
    @Test
    fun `Recipients panel should be hidden in edit state`() {
        whenever(liveData.conversationUuid).thenReturn(BehaviorSubject.createDefault(RxContainer(conversationUuid)))
        whenever(messageInteractor.getMessageText(messageUuid, conversationUuid))
            .thenReturn(Single.just(messageTextWithMentions))

        machine.fire(EditingStateEvent(event))

        verify(liveData, never()).setRecipients(eq(emptyList()), any())
        verify(liveData).forceHideRecipientsPanel(true)
    }

    @Test
    fun `Verify original text set up`() {
        whenever(liveData.conversationUuid).thenReturn(BehaviorSubject.createDefault(RxContainer(conversationUuid)))
        whenever(messageInteractor.getMessageText(messageUuid, conversationUuid))
            .thenReturn(Single.just(messageTextWithMentions))

        machine.fire(EditingStateEvent(event))

        verify(liveData).setQuoteText(messageTitle, messageText)
        verify(liveData).setMessageText(messageText)
    }

    /**
     * Fix https://online.sbis.ru/opendoc.html?guid=89d3c2bf-b552-4d09-9794-0b914f3dca0e
     */
    @Test
    fun `When conversation uuid undefined, then document uuid should be used to load message text`() {
        val documentUuid = UUID.randomUUID()
        // идентификатора переписки нет
        whenever(liveData.conversationUuid).thenReturn(BehaviorSubject.createDefault(RxContainer(null)))
        // идентификатор документа есть
        whenever(vm.conversationInfo).thenReturn(CoreConversationInfo(document = documentUuid))
        whenever(messageInteractor.getMessageText(messageUuid, documentUuid))
            .thenReturn(Single.just(messageTextWithMentions))

        machine.fire(EditingStateEvent(event))

        verify(messageInteractor).getMessageText(messageUuid, documentUuid)
    }

    @Test
    fun `When conversation uuid available, then document uuid shouldn't be used to load message text`() {
        val info: CoreConversationInfo = mock()
        whenever(liveData.conversationUuid).thenReturn(BehaviorSubject.createDefault(RxContainer(conversationUuid)))
        whenever(vm.conversationInfo).thenReturn(info)
        whenever(messageInteractor.getMessageText(messageUuid, conversationUuid))
            .thenReturn(Single.just(messageTextWithMentions))

        machine.fire(EditingStateEvent(event))

        verify(messageInteractor).getMessageText(messageUuid, conversationUuid)
        verifyNoMoreInteractions(info)
    }
}