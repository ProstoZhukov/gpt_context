package ru.tensor.sbis.design_selection.ui.content.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.tensor.sbis.communication_decl.selection.SelectionConfig
import ru.tensor.sbis.design_selection.contract.data.SelectionFolderItem
import ru.tensor.sbis.design_selection.contract.data.SelectionItem
import ru.tensor.sbis.design_selection.domain.SelectionInteractor
import ru.tensor.sbis.design_selection.domain.list.SelectionListComponent
import ru.tensor.sbis.design_selection.domain.list.SelectionStrategyHelper
import ru.tensor.sbis.design_selection.ui.content.listener.SelectionItemClickListener
import ru.tensor.sbis.design_selection.ui.content.vm.search.SelectionSearchViewModel
import ru.tensor.sbis.design_selection.ui.main.utils.SelectionRulesHelper
import ru.tensor.sbis.design_selection.ui.main.vm.contract.SelectionContentDelegate

/**
 * Фабрика для создания вью-модели области контента со списком невыбранных элементов компонента выбора.
 *
 * @param contentDelegate делегат области контента.
 * @param searchVM вью-модель поисковой строки.
 * @param selectionInteractor интерактор компонента выбора.
 * @param listComponent компонент списка.
 * @param selectionHelper вспомогательная реализация для обработки команд выбора.
 * @param rulesHelper вспомогательная реализация для определения правил выбора.
 * @param clickListener слушатель кликов по невыбранным элементам.
 * @param folderItem папка, в которой находится пользователь.
 *
 * @author vv.chekurda
 */
internal class SelectionContentViewModelFactory(
    private val contentDelegate: SelectionContentDelegate<SelectionItem>,
    private val searchVM: SelectionSearchViewModel,
    private val selectionInteractor: SelectionInteractor<SelectionItem>,
    private val listComponent: SelectionListComponent,
    private val selectionHelper: SelectionStrategyHelper<SelectionItem>,
    private val rulesHelper: SelectionRulesHelper,
    private val clickListener: SelectionItemClickListener<SelectionItem>,
    private val folderItem: SelectionFolderItem?,
    private val config: SelectionConfig
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass == SelectionContentViewModelImpl::class.java)
        @Suppress("UNCHECKED_CAST")
        return SelectionContentViewModelImpl(
            contentDelegate = contentDelegate,
            searchVM = searchVM,
            selectionInteractor = selectionInteractor,
            listComponent = listComponent,
            selectionHelper = selectionHelper,
            rulesHelper = rulesHelper,
            clickListener = clickListener,
            folderItem = folderItem,
            config = config
        ) as T
    }
}