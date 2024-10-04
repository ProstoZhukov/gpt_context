package ru.tensor.sbis.design.message_panel.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.tensor.sbis.design.message_panel.di.vm.MessagePanelViewModelComponent
import javax.inject.Inject

/**
 * Фабрика для создания [MessagePanelViewModel] на основе графа зависимостей [MessagePanelViewModelComponent]
 *
 * @author ma.kolpakov
 */
internal class MessagePanelViewModelFactory @Inject constructor(
    private val viewModelComponentFactory: MessagePanelViewModelComponent.Factory
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass === MessagePanelViewModelImpl::class.java)

        // составные части для работы vm создаются только по необходимости (первый вызов фабрики)
        @Suppress("UNCHECKED_CAST")
        return viewModelComponentFactory.create().viewModel as T
    }
}
