package ru.tensor.sbis.design_selection.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.tensor.sbis.design_selection.ui.main.di.SelectionController
import ru.tensor.sbis.design_selection.ui.main.di.SelectionControllerProvider
import ru.tensor.sbis.design_selection.contract.data.SelectionFolderItem

/**
 * Вью-модель для хранения инстанса поставщика контроллера на уровне жизненного цикла родительского фрагмента выбора.
 *
 * @param lazyControllerProvider ленивый поставщик контроллера компонента выбора.
 *
 * @author vv.chekurda
 */
internal class SelectionControllerHolder(
    lazyControllerProvider: Lazy<SelectionControllerProvider>
) : ViewModel() {
    private val controllerProvider by lazyControllerProvider

    /**
     * Создать инстанс контроллера компонента выбора для папки [folderItem].
     * Для корневой папки [folderItem] - null.
     */
    fun createSelectionController(folderItem: SelectionFolderItem? = null): SelectionController =
        controllerProvider.createSelectionControllerWrapper(folderItem)

    /**
     * Фабрика для создания вью-модели [SelectionControllerHolder].
     *
     * @property lazyControllerProvider ленивый поставщик контроллера компонента выбора.
     */
    class Factory(
        private val lazyControllerProvider: Lazy<SelectionControllerProvider>
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass == SelectionControllerHolder::class.java)
            @Suppress("UNCHECKED_CAST")
            return SelectionControllerHolder(lazyControllerProvider) as T
        }
    }
}