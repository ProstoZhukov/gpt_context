package ru.tensor.sbis.message_panel.helper

import org.mockito.kotlin.isA
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import ru.tensor.sbis.attachments.models.AttachmentRegisterModel
import ru.tensor.sbis.common.rx.RxContainer
import ru.tensor.sbis.message_panel.setUpSubjects
import ru.tensor.sbis.message_panel.viewModel.livedata.MessagePanelLiveData
import ru.tensor.sbis.message_panel.viewModel.stateMachine.EventUserInput
import ru.tensor.sbis.message_panel.viewModel.stateMachine.MessagePanelStateMachine
import ru.tensor.sbis.message_panel.viewModel.stateMachine.SimpleSendState

/**
 * Тестирование перехода в [SimpleSendState] при пользовательском вводе
 *
 * @author vv.chekurda
 * @since 7/22/2019
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class ObserveUserChangesTest {

    private val messageTextSubject = PublishSubject.create<RxContainer<String>>()
    private val attachmentsSubject = PublishSubject.create<List<AttachmentRegisterModel>>()
    private val recipientsSelectedSubject = PublishSubject.create<Boolean>()
    private val originalMessageVisibilitySubject = PublishSubject.create<Boolean>()

    @Mock
    private lateinit var liveData: MessagePanelLiveData

    @Mock
    private lateinit var machine: MessagePanelStateMachine<*, *, *>

    private lateinit var disposable: Disposable

    @Before
    fun setUp() {
        liveData.setUpSubjects(
            messageTextSubject,
            attachmentsSubject,
            recipientsSelectedSubject,
            originalMessageVisibilitySubject
        )
    }

    @After
    fun tearDown() {
        disposable.dispose()
    }

    @Test
    fun observeUserTextChanges() {
        disposable = observeUserChanges(liveData, machine)
        messageTextSubject.onNext(RxContainer("Stub string"))
        verify(machine).fire(isAEventUserInput())
    }

    @Test
    fun observeAttachmentsChanges() {
        disposable = observeUserChanges(liveData, machine)
        attachmentsSubject.onNext(listOf(mock()))
        verify(machine).fire(isAEventUserInput())
    }

    @Test
    fun observeIsRecipientsSelected() {
        disposable = observeUserChanges(liveData, machine)
        recipientsSelectedSubject.onNext(true)
        verify(machine).fire(isAEventUserInput())
    }

    @Test
    fun observeQuotePanelVisibility() {
        disposable = observeUserChanges(liveData, machine)
        originalMessageVisibilitySubject.onNext(true)
        verify(machine).fire(isAEventUserInput())
    }

    private fun isAEventUserInput(): EventUserInput = isA()
}