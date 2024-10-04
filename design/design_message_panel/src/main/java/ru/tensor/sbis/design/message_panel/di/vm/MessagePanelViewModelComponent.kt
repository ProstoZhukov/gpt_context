package ru.tensor.sbis.design.message_panel.di.vm

import dagger.Subcomponent
import ru.tensor.sbis.design.message_panel.di.vm.MessagePanelViewModelDelegatesModule.Companion.UNSCOPED_VM
import ru.tensor.sbis.design.message_panel.vm.MessagePanelViewModel
import javax.inject.Named

/**
 * Компонент собирает граф для [MessagePanelViewModel]
 *
 * @author ma.kolpakov
 */
@MessagePanelViewModelScope
@Subcomponent(modules = [MessagePanelViewModelDelegatesModule::class])
internal interface MessagePanelViewModelComponent {

    @get:Named(UNSCOPED_VM)
    val viewModel: MessagePanelViewModel

    @Subcomponent.Factory
    interface Factory {

        fun create(): MessagePanelViewModelComponent
    }
}
