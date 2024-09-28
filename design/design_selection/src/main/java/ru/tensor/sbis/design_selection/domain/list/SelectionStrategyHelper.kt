package ru.tensor.sbis.design_selection.domain.list

import ru.tensor.sbis.communication_decl.selection.SelectionMode
import ru.tensor.sbis.design_selection.contract.data.SelectionFolderItem
import ru.tensor.sbis.design_selection.contract.data.SelectionItem
import ru.tensor.sbis.design_selection.ui.content.vm.search.SelectionSearchViewModel
import ru.tensor.sbis.design_selection.ui.main.utils.SelectionRulesHelper
import ru.tensor.sbis.design_selection.ui.main.vm.contract.live_data.SelectionLiveData

/**
 * Вспомогательная реализация для определения стратегии обработки команды выбора элемента в компоненте выбора.
 *
 * @property selectionModeProvider поставщик режима выбора.
 * @property searchVM вью-модель поисковой строки.
 * @property liveData состояние выбранных элементов.
 * @property rulesHelper вспомогательная реализация для определения правил выбора.
 *
 * @author vv.chekurda
 */
internal class SelectionStrategyHelper<ITEM : SelectionItem>(
    private val selectionModeProvider: SelectionModeProvider,
    private val searchVM: SelectionSearchViewModel,
    private val liveData: SelectionLiveData<ITEM>,
    private val rulesHelper: SelectionRulesHelper
) {
    private val isEmptySearch: Boolean
        get() = searchVM.searchQuery.isEmpty()

    private val selectionMode: SelectionMode
        get() = selectionModeProvider.selectionMode

    /**
     * Создать стратегию обработки выбора [SelectionStrategy].
     *
     * @param item выбираемый элемент списка.
     * @param append true, если это явная команда добавления элемента к списку выбранных.
     */
    fun produceSelectStrategy(item: SelectionItem, append: Boolean): SelectionStrategy =
        if (item is SelectionFolderItem) {
            handleFolderItem(item, append)
        } else {
            handleSelectableItem(append)
        }

    /**
     * Обработать событие отмены выбранного элемента.
     */
    fun onUnselect() {
        selectionModeProvider.selectionMode = SelectionMode.ALWAYS_ADD
    }

    private fun handleFolderItem(folderItem: SelectionFolderItem, append: Boolean): SelectionStrategy =
        if (folderItem.openable && !append) {
            SelectionStrategy.OPEN_FOLDER
        } else {
            handleSelectableItem(append)
        }

    private fun handleSelectableItem(append: Boolean): SelectionStrategy =
        when (selectionMode) {
            SelectionMode.ALWAYS_ADD -> handleAlwaysAddMode(append)
            SelectionMode.REPLACE_ALL_IF_FIRST -> handleReplaceAllMode(append)
            SelectionMode.SINGLE,
            SelectionMode.SINGLE_WITH_APPEND -> handleSingleMode()
        }

    private fun handleAlwaysAddMode(append: Boolean): SelectionStrategy =
        when {
            !append && !rulesHelper.isFinalComplete && !liveData.selectedData.hasSelectedItems -> {
                selectionModeProvider.selectionMode = SelectionMode.REPLACE_ALL_IF_FIRST
                SelectionStrategy.COMPLETE
            }
            isEmptySearch -> SelectionStrategy.SELECT
            else -> SelectionStrategy.SELECT_AND_CANCEL_SEARCH
        }

    private fun handleReplaceAllMode(append: Boolean): SelectionStrategy {
        if (rulesHelper.isFinalComplete || !liveData.selectedData.hasSelectedItems) {
            selectionModeProvider.selectionMode = SelectionMode.ALWAYS_ADD
        }
        return when {
            append && !isEmptySearch -> {
                SelectionStrategy.SELECT_AND_CANCEL_SEARCH
            }
            append -> {
                SelectionStrategy.SELECT
            }
            !liveData.selectedData.hasSelectedItems -> {
                SelectionStrategy.COMPLETE
            }
            !isEmptySearch -> {
                SelectionStrategy.REPLACE_SELECTED_AND_CANCEL_SEARCH
            }
            else -> {
                SelectionStrategy.REPLACE_SELECTED
            }
        }
    }

    private fun handleSingleMode(): SelectionStrategy =
        SelectionStrategy.COMPLETE
}

/**
 * Стратегия обработки команды выбора.
 */
internal enum class SelectionStrategy {

    /**
     * Открыть папку.
     */
    OPEN_FOLDER,

    /**
     * Выбрать.
     */
    SELECT,

    /**
     * Выбрать и сбросить поиск.
     */
    SELECT_AND_CANCEL_SEARCH,

    /**
     * Заменить всех выбранных.
     */
    REPLACE_SELECTED,

    /**
     * Заменить всех выбранных и закрыть поиск.
     */
    REPLACE_SELECTED_AND_CANCEL_SEARCH,

    /**
     * Выбрать и завершить.
     */
    COMPLETE
}
