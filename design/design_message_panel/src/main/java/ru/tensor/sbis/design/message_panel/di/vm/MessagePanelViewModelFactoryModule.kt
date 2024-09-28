package ru.tensor.sbis.design.message_panel.di.vm

import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import ru.tensor.sbis.design.message_panel.vm.MessagePanelViewModelFactory

/**
 * Модуль собирает фабрику [MessagePanelViewModelFactory]
 *
 * @author ma.kolpakov
 */
@Module(subcomponents = [MessagePanelViewModelComponent::class])
internal class MessagePanelViewModelFactoryModule {

    @Provides
    fun provideViewModelFactory(
        vmComponentFactory: MessagePanelViewModelComponent.Factory
    ): ViewModelProvider.Factory =
        MessagePanelViewModelFactory(vmComponentFactory)
}
