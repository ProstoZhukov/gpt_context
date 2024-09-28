package ru.tensor.sbis.communicator.themes_registry.ui.chatrecipientselection.chat_creation.di

import dagger.Component
import ru.tensor.sbis.communicator.common.di.CommunicatorCommonComponent
import ru.tensor.sbis.communicator.themes_registry.ui.chatrecipientselection.chat_creation.contract.ChatCreationPresenter

/**
 * DI компонент экрана создания нового чата
 *
 * @author vv.chekurda
 */
@ChatCreationScope
@Component(
    dependencies = [CommunicatorCommonComponent::class],
    modules = [ChatCreationModule::class]
)
internal interface ChatCreationComponent {

    val chatCreationPresenter: ChatCreationPresenter

    @Component.Builder
    interface Builder {

        fun communicatorCommonComponent(component: CommunicatorCommonComponent): Builder

        fun build(): ChatCreationComponent
    }
}