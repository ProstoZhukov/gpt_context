package ru.tensor.sbis.message_panel.viewModel.stateMachine

import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.apache.commons.lang3.StringUtils
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import ru.tensor.sbis.common.generated.CommandStatus
import ru.tensor.sbis.common.testing.modify
import ru.tensor.sbis.common.util.statemachine.StateMachineImpl
import ru.tensor.sbis.common.util.statemachine.StateMachineInner
import ru.tensor.sbis.communicator.generated.MessageTextWithMentions
import ru.tensor.sbis.message_panel.attachments.MessagePanelAttachmentHelper
import ru.tensor.sbis.message_panel.interactor.message.MessagePanelMessageInteractor
import ru.tensor.sbis.message_panel.model.CoreConversationInfo
import ru.tensor.sbis.message_panel.model.EditContent
import ru.tensor.sbis.message_panel.viewModel.MessagePanelViewModel
import ru.tensor.sbis.message_panel.viewModel.livedata.MessagePanelLiveData
import ru.tensor.sbis.message_panel.viewModel.livedata.initLiveData
import java.util.*

/**
 * Тестирование активации кнопки отправки только при изменении текста
 *
 * Fix [ошибки](https://online.sbis.ru/opendoc.html?guid=b313e645-fba6-4074-94e9-3dc1b354b4ae)
 *
 * @author vv.chekurda
 * Создан 9/9/2019
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class EditingStateSendButtonActivationTest {

    private val title = "Test text title"
    private val text = "Original test text"
    private val messageTextWithMentions = MessageTextWithMentions(text, StringUtils.EMPTY)
    private val messageUuid = UUID.randomUUID()
    private val conversationUuid = UUID.randomUUID()

    /**
     * Нужна какая-нибудь машина, чтобы выполнить вызов инициализации при установке
     */
    private val machine: StateMachineInner = StateMachineImpl(Schedulers.trampoline(), Schedulers.trampoline())

    private lateinit var liveData: MessagePanelLiveData

    private lateinit var editState: EditingState<*, *, *>

    @Before
    fun setUp() {
        val vm = mock<MessagePanelViewModel<*, *, *>>()

        liveData = initLiveData(info = CoreConversationInfo(conversationUuid = conversationUuid))
        liveData.setConversationUuid(conversationUuid)
        whenever(vm.liveData).thenReturn(liveData)

        val interactor = mock<MessagePanelMessageInteractor<*, *>>()
        val attachmentsPresenter = mock<MessagePanelAttachmentHelper>()
        whenever(vm.messageInteractor).thenReturn(interactor)
        whenever(vm.attachmentPresenter).thenReturn(attachmentsPresenter)

        whenever(interactor.getMessageText(messageUuid, conversationUuid))
            .thenReturn(Single.just(messageTextWithMentions))
        whenever(interactor.beginEditMessage(any())).thenReturn(Single.just(CommandStatus()))

        whenever(vm.saveDraft()).thenReturn(Completable.complete())

        val event = EventEdit(title, EditContent(messageUuid))
        editState = EditingState(vm, event)
        // костыль для запуска инициализации (у машины циклическая зависимость с liveData)
        machine.setState(editState)
    }

    @Test
    fun `Send button inactive on edit by default`() {
        val activationTestObserver = liveData.sendControlActivated.test()

        activationTestObserver
            .assertValueCount(1)
            .assertValueAt(0) { !it.value!! }
    }

    @Test
    fun `Send button activated on text changed`() {
        val activationTestObserver = liveData.sendControlActivated.test()

        val changedText = text.modify(canBeBlank = false)
        liveData.setMessageText(changedText)

        assertThat(
            "Changed text is '$changedText'",
            activationTestObserver.values().map { it.value!! },
            equalTo(
                listOf(
                    // по умолчанию
                    false,
                    // установлена строка, но проверка эквивалентности ещё не прошла
                    false,
                    // прошла проверка эквивалентности
                    true
                )
            )
        )
    }

    @Test
    fun `Send button become inactive when text become equal to original`() {
        val activationTestObserver = liveData.sendControlActivated.test()

        // изменение текста
        val changedText = text.modify(canBeBlank = false)
        liveData.setMessageText(changedText)
        // возвращение оригинального
        liveData.setMessageText(text)

        assertThat(
            "Changed text is '$changedText'",
            activationTestObserver.values().map { it.value!! },
            equalTo(
                listOf(
                    // по умолчанию
                    false,
                    // установлена строка, но проверка эквивалентности ещё не прошла
                    false,
                    // прошла проверка эквивалентности - строки разные
                    true,
                    // установлена прежняя строка, но проверка эквивалентности ещё не прошла
                    true,
                    // прошла проверка эквивалентности - строки снова разные -> запрет на отправку
                    false
                )
            )
        )
    }
}
