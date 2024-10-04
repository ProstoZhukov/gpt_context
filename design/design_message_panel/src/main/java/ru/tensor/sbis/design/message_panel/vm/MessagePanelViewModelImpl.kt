package ru.tensor.sbis.design.message_panel.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.tensor.sbis.design.message_panel.vm.attachments.AttachmentsDelegate
import ru.tensor.sbis.design.message_panel.vm.draft.DraftDelegate
import ru.tensor.sbis.design.message_panel.vm.keyboard.KeyboardDelegate
import ru.tensor.sbis.design.message_panel.vm.notification.NotificationDelegate
import ru.tensor.sbis.design.message_panel.vm.quote.QuoteDelegate
import ru.tensor.sbis.design.message_panel.vm.recipients.RecipientsDelegate
import ru.tensor.sbis.design.message_panel.vm.state.StateDelegate
import ru.tensor.sbis.design.message_panel.vm.text.TextDelegate
import ru.tensor.sbis.design.message_panel.vm.usecase.UseCaseDelegate
import javax.inject.Inject

/**
 * Композиция функциональных областей панели ввода
 *
 * @author ma.kolpakov
 */
internal class MessagePanelViewModelImpl @Inject constructor(
    useCaseDelegate: UseCaseDelegate,
    stateDelegate: StateDelegate,
    textDelegate: TextDelegate,
    attachmentsDelegate: AttachmentsDelegate,
    recipientsDelegate: RecipientsDelegate,
    private val quoteDelegate: QuoteDelegate,
    draftDelegate: DraftDelegate,
    notificationDelegate: NotificationDelegate,
    keyboardDelegate: KeyboardDelegate
) : ViewModel(),
    MessagePanelViewModel,
    UseCaseDelegate by useCaseDelegate,
    StateDelegate by stateDelegate,
    TextDelegate by textDelegate,
    AttachmentsDelegate by attachmentsDelegate,
    RecipientsDelegate by recipientsDelegate,
    QuoteDelegate by quoteDelegate,
    DraftDelegate by draftDelegate,
    NotificationDelegate by notificationDelegate,
    KeyboardDelegate by keyboardDelegate {

    init {
        viewModelScope.launch {
            useCase.collect { useCase ->
                useCase.setup(this@MessagePanelViewModelImpl)
            }
        }
        attachmentsDelegate.attachAttachmentsScope(viewModelScope)
        recipientsDelegate.attachRecipientsScope(viewModelScope)
        quoteDelegate.attachQuoteScope(viewModelScope)
        keyboardDelegate.init(viewModelScope, stateDelegate.isEnabled)
    }

    override fun onSendClicked() {
        // отправка должна завершиться независимо от наличия панели ввода на экране
        viewModelScope.launch {
            GlobalScope.launch {
                useCase.value.send()
            }.join()
            // TODO: доставка сообщений в GlobalScope, но переключение во vm scope
            updateUseCase { env ->
                buildSendMessageUseCase(env.conversationUuid)
            }
        }
    }

    override fun onQuoteClearClicked() {
        quoteDelegate.onQuoteClearClicked()
        updateUseCase { env ->
            buildSendMessageUseCase(env.conversationUuid)
        }
    }

    override fun onCleared() {
        super.onCleared()
        // сохранение черновика должно отработать независимо от жц vm
        GlobalScope.launch {
            useCase.value.save()
        }
    }
}
