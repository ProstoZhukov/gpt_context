package ru.tensor.sbis.design.message_panel.di.vm

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.tensor.sbis.design.message_panel.di.usecase.MessagePanelUseCaseComponent
import ru.tensor.sbis.design.message_panel.domain.MessagePanelUseCaseBuilder
import ru.tensor.sbis.design.message_panel.vm.usecase.UseCaseDelegate
import ru.tensor.sbis.design.message_panel.vm.usecase.UseCaseDelegateImpl

/**
 * Модуль собирает [UseCaseDelegate]
 *
 * @see MessagePanelViewModelFactoryComponent
 *
 * @author ma.kolpakov
 */
@Module
internal class MessagePanelUseCaseDelegateModule {

    @Provides
    @MessagePanelViewModelScope
    fun provideUseCaseBuilder(
        appContext: Context,
        useCaseComponent: MessagePanelUseCaseComponent.Factory
    ) = MessagePanelUseCaseBuilder(appContext, useCaseComponent)

    @Provides
    @MessagePanelViewModelScope
    fun providesUseCaseDelegate(useCaseBuilder: MessagePanelUseCaseBuilder): UseCaseDelegate =
        UseCaseDelegateImpl(useCaseBuilder)
}
