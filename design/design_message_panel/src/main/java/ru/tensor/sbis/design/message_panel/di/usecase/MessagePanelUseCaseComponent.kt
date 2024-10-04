package ru.tensor.sbis.design.message_panel.di.usecase

import dagger.BindsInstance
import dagger.Subcomponent
import ru.tensor.sbis.design.message_panel.domain.AbstractMessagePanelUseCase
import ru.tensor.sbis.design.message_panel.domain.common.CleanupUseCase
import ru.tensor.sbis.design.message_panel.domain.common.DraftUseCase
import ru.tensor.sbis.design.message_panel.domain.common.SendUseCase
import ru.tensor.sbis.design.message_panel.vm.MessagePanelViewModel
import javax.inject.Provider

/**
 * Компонент собирает переиспользуемые сценарии работы
 *
 * @author ma.kolpakov
 */
@Subcomponent(modules = [MessagePanelUseCaseModule::class])
internal interface MessagePanelUseCaseComponent {

    val sendUseCaseProvider: Provider<SendUseCase<Any, Any>>
    val draftUseCaseProvider: Provider<DraftUseCase<Any>>
    val cleanupUseCase: Provider<CleanupUseCase>

    @Subcomponent.Factory
    interface Factory {

        fun create(
            @BindsInstance parentUseCase: AbstractMessagePanelUseCase,
            @BindsInstance vm: MessagePanelViewModel
        ): MessagePanelUseCaseComponent
    }
}
