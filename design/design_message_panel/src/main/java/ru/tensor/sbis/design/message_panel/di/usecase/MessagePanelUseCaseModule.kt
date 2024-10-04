package ru.tensor.sbis.design.message_panel.di.usecase

import dagger.Module
import dagger.Provides
import ru.tensor.sbis.design.message_panel.decl.draft.MessageDraftService
import ru.tensor.sbis.design.message_panel.decl.draft.MessageDraftServiceHelper
import ru.tensor.sbis.design.message_panel.decl.message.MessageService
import ru.tensor.sbis.design.message_panel.decl.message.MessageServiceHelper
import ru.tensor.sbis.design.message_panel.domain.AbstractMessagePanelUseCase
import ru.tensor.sbis.design.message_panel.domain.common.CleanupUseCase
import ru.tensor.sbis.design.message_panel.domain.common.DraftUseCase
import ru.tensor.sbis.design.message_panel.domain.common.SendUseCase
import ru.tensor.sbis.design.message_panel.vm.MessagePanelViewModel

/**
 * @author ma.kolpakov
 */
@Module
internal class MessagePanelUseCaseModule {

    @Provides
    fun provideSendUseCase(
        parentUseCase: AbstractMessagePanelUseCase,
        messageService: MessageService<Any, Any>,
        messageServiceHelper: MessageServiceHelper<Any, Any>,
        vm: MessagePanelViewModel
    ) = SendUseCase(parentUseCase, messageService, messageServiceHelper, vm)

    @Provides
    fun provideDraftUseCase(
        parentUseCase: AbstractMessagePanelUseCase,
        draftService: MessageDraftService<Any>,
        draftServiceHelper: MessageDraftServiceHelper<Any>,
        vm: MessagePanelViewModel
    ) = DraftUseCase(parentUseCase, draftService, draftServiceHelper, vm)

    @Provides
    fun provideCleanupUseCase(
        vm: MessagePanelViewModel
    ) = CleanupUseCase(vm)
}
