package ru.tensor.sbis.design_selection.ui.main.vm

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.tensor.sbis.design_selection.contract.customization.selected.panel.listener.SelectedItemClickListener
import ru.tensor.sbis.design_selection.contract.data.SelectionItem
import ru.tensor.sbis.design_selection.contract.header_button.HeaderButtonContract
import ru.tensor.sbis.design_selection.ui.main.utils.SelectionRulesHelper
import ru.tensor.sbis.design_selection.ui.main.vm.contract.live_data.DoneButtonDelegate

/**
 * Фабрика для создания вью-модели компонента выбора [SelectionViewModelImpl].
 *
 * @property selectedItemsClickListener слушатель кликов по выбранным элементам.
 * @property doneButtonViewModel вью-модель кнопки подтверждения выбора.
 * @property headerButtonContract контракт для работы с головной кнопкой.
 * @property rulesHelper вспомогательная реализация для определения правил выбора.
 *
 * @author vv.chekurda
 */
internal class SelectionViewModelFactory(
    private val selectedItemsClickListener: SelectedItemClickListener<SelectionItem>,
    private val doneButtonViewModel: DoneButtonDelegate<SelectionItem>,
    private val headerButtonContract: HeaderButtonContract<SelectionItem, FragmentActivity>?,
    private val rulesHelper: SelectionRulesHelper
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass == SelectionViewModelImpl::class.java)
        @Suppress("UNCHECKED_CAST")
        return SelectionViewModelImpl(
            selectedItemsClickListener,
            rulesHelper,
            doneButtonViewModel,
            headerButtonContract
        ) as T
    }
}