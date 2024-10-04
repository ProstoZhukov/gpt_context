package ru.tensor.sbis.design.message_panel.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import ru.tensor.sbis.attachments.decl.mapper.AttachmentRegisterModelMapper
import ru.tensor.sbis.design.message_panel.decl.attachments.AttachmentsService
import ru.tensor.sbis.design.message_panel.decl.draft.MessageDraftService
import ru.tensor.sbis.design.message_panel.decl.draft.MessageDraftServiceHelper
import ru.tensor.sbis.design.message_panel.decl.message.MessageService
import ru.tensor.sbis.design.message_panel.decl.message.MessageServiceHelper
import ru.tensor.sbis.design.message_panel.decl.recipients.RecipientService
import ru.tensor.sbis.design.message_panel.decl.recipients.RecipientServiceHelper
import ru.tensor.sbis.design.message_panel.di.usecase.MessagePanelUseCaseModule
import ru.tensor.sbis.design.message_panel.di.vm.MessagePanelViewModelFactoryComponent
import ru.tensor.sbis.design.message_panel.di.vm.MessagePanelViewModelModule
import ru.tensor.sbis.persons.IContactVM
import ru.tensor.sbis.verification_decl.login.CurrentAccount

/**
 * Компонент панели ввода с зависимостями уровня приложения
 *
 * @author ma.kolpakov
 */
@Component(modules = [MessagePanelViewModelModule::class, MessagePanelUseCaseModule::class])
internal interface MessagePanelComponent {

    val viewModelComponent: MessagePanelViewModelFactoryComponent.Factory

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun bindAppContext(context: Context): Builder

        //region Сообщения
        @BindsInstance
        fun messageService(service: MessageService<Any, Any>): Builder

        @BindsInstance
        fun messageServiceHelper(helper: MessageServiceHelper<Any, Any>): Builder
        //endregion

        //region Получатели
        @BindsInstance
        fun recipientService(service: RecipientService<IContactVM>): Builder

        @BindsInstance
        fun recipientServiceHelper(helper: RecipientServiceHelper<IContactVM>): Builder
        //endregion

        //region Черновики
        @BindsInstance
        fun draftService(service: MessageDraftService<Any>): Builder

        @BindsInstance
        fun draftServiceHelper(helper: MessageDraftServiceHelper<Any>): Builder
        //endregion

        //region Вложения
        @BindsInstance
        fun attachmentsService(service: AttachmentsService): Builder

        @BindsInstance
        fun attachmentsMappingService(service: AttachmentRegisterModelMapper): Builder
        //endregion

        @BindsInstance
        fun accountService(service: CurrentAccount): Builder

        fun build(): MessagePanelComponent
    }
}
