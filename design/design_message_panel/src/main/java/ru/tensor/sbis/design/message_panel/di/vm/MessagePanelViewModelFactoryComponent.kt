package ru.tensor.sbis.design.message_panel.di.vm

import android.view.View
import dagger.BindsInstance
import dagger.Subcomponent
import ru.tensor.sbis.design.message_panel.vm.MessagePanelViewModel

/**
 * Компонент собирает граф для хранилища [MessagePanelViewModel] (поставляет vm из него)
 *
 * @see MessagePanelViewModelComponent
 *
 * @author ma.kolpakov
 */
@Subcomponent(modules = [MessagePanelViewStoreModule::class])
internal interface MessagePanelViewModelFactoryComponent {

    val viewModel: MessagePanelViewModel

    @Subcomponent.Factory
    interface Factory {

        fun create(@BindsInstance view: View): MessagePanelViewModelFactoryComponent
    }
}
