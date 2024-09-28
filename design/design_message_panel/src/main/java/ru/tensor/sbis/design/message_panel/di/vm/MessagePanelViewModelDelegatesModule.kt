package ru.tensor.sbis.design.message_panel.di.vm

import dagger.Binds
import dagger.Module
import ru.tensor.sbis.design.message_panel.di.usecase.MessagePanelUseCaseComponent
import ru.tensor.sbis.design.message_panel.vm.MessagePanelViewModel
import ru.tensor.sbis.design.message_panel.vm.MessagePanelViewModelImpl
import ru.tensor.sbis.design.message_panel.vm.attachments.AttachmentsDelegate
import ru.tensor.sbis.design.message_panel.vm.attachments.AttachmentsDelegateImpl
import ru.tensor.sbis.design.message_panel.vm.draft.DraftDelegate
import ru.tensor.sbis.design.message_panel.vm.draft.DraftDelegateImpl
import ru.tensor.sbis.design.message_panel.vm.keyboard.KeyboardDelegate
import ru.tensor.sbis.design.message_panel.vm.keyboard.KeyboardDelegateImpl
import ru.tensor.sbis.design.message_panel.vm.notification.NotificationDelegate
import ru.tensor.sbis.design.message_panel.vm.notification.NotificationDelegateImpl
import ru.tensor.sbis.design.message_panel.vm.quote.QuoteDelegate
import ru.tensor.sbis.design.message_panel.vm.quote.QuoteDelegateImpl
import ru.tensor.sbis.design.message_panel.vm.recipients.RecipientsDelegate
import ru.tensor.sbis.design.message_panel.vm.recipients.RecipientsDelegateImpl
import ru.tensor.sbis.design.message_panel.vm.state.StateDelegate
import ru.tensor.sbis.design.message_panel.vm.state.StateDelegateImpl
import ru.tensor.sbis.design.message_panel.vm.text.TextDelegate
import ru.tensor.sbis.design.message_panel.vm.text.TextDelegateImpl
import javax.inject.Named

/**
 * Модуль собирает делегаты функциональных частей для [MessagePanelViewModelImpl]
 *
 * @author ma.kolpakov
 */
@Module(
    includes = [MessagePanelUseCaseDelegateModule::class],
    subcomponents = [MessagePanelUseCaseComponent::class]
)
internal interface MessagePanelViewModelDelegatesModule {

    @Binds
    @Named(UNSCOPED_VM)
    @MessagePanelViewModelScope
    fun bindViewModel(impl: MessagePanelViewModelImpl): MessagePanelViewModel

    @Binds
    @MessagePanelViewModelScope
    fun bindStateDelegate(impl: StateDelegateImpl): StateDelegate

    @Binds
    @MessagePanelViewModelScope
    fun bindTextDelegate(impl: TextDelegateImpl): TextDelegate

    @Binds
    @MessagePanelViewModelScope
    fun bindAttachmentsDelegate(impl: AttachmentsDelegateImpl): AttachmentsDelegate

    @Binds
    @MessagePanelViewModelScope
    fun bindRecipientsDelegate(impl: RecipientsDelegateImpl): RecipientsDelegate

    @Binds
    @MessagePanelViewModelScope
    fun bindQuoteDelegate(impl: QuoteDelegateImpl): QuoteDelegate

    @Binds
    @MessagePanelViewModelScope
    fun bindDraftDelegate(impl: DraftDelegateImpl): DraftDelegate

    @Binds
    @MessagePanelViewModelScope
    fun bindNotificationDelegate(impl: NotificationDelegateImpl): NotificationDelegate

    @Binds
    @MessagePanelViewModelScope
    fun bindKeyboardDelegate(impl: KeyboardDelegateImpl): KeyboardDelegate

    companion object {
        const val UNSCOPED_VM = "UNSCOPED_VM"
    }
}
