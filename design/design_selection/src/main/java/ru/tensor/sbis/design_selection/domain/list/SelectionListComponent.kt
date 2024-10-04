package ru.tensor.sbis.design_selection.domain.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.tensor.sbis.communication_decl.selection.SelectionItemId
import ru.tensor.sbis.crud3.ItemWithSection
import ru.tensor.sbis.crud3.ListComponentViewViewModel
import ru.tensor.sbis.design_selection.contract.data.SelectionItem
import ru.tensor.sbis.design_selection.contract.filter.SelectionFilterFactory
import ru.tensor.sbis.design_selection.contract.filter.SelectionFilterMeta
import ru.tensor.sbis.list.view.item.AnyItem
import javax.inject.Inject

/**
 * Компонент списка компонента выбора.
 *
 * @property listVM вью-модель компонента списка.
 * @property filterFactory фабрика для создания фильтра.
 *
 * @author vv.chekurda
 */
internal class SelectionListComponent @Inject constructor(
    private val listVM: ListComponentViewViewModel<ItemWithSection<AnyItem>, Any, SelectionItem>,
    private val filterFactory: SelectionFilterFactory<Any, SelectionItemId>,
    private val sourceSettings: SelectionComponentSettings = SelectionComponentSettings()
) : ListComponentViewViewModel<ItemWithSection<AnyItem>, Any, SelectionItem> by listVM {

    /**
     * Признак видимости заглушки списка.
     */
    val isStubVisible: LiveData<Boolean>
        get() = listVM.stubVisibility

    override var loadNextThrobberIsVisible: LiveData<Boolean>
        get() = if (sourceSettings.showPagingLoaders) {
            listVM.loadNextThrobberIsVisible
        } else {
            MutableLiveData(false)
        }
        set(value) {
            listVM.loadNextThrobberIsVisible = value
        }

    override var loadPreviousThrobberIsVisible: LiveData<Boolean>
        get() = if (sourceSettings.showPagingLoaders) {
            listVM.loadPreviousThrobberIsVisible
        } else {
            MutableLiveData(false)
        }
        set(value) {
            listVM.loadPreviousThrobberIsVisible = value
        }

    override val stubVisibility: LiveData<Boolean>
        get() = if (sourceSettings.showStubs) {
            listVM.stubVisibility
        } else {
            MutableLiveData(false)
        }

    /**
     * Установить новое значение фильтра и отобразить список с начальной позиции.
     *
     * @param meta данные для построения фильтра.
     */
    fun reset(meta: SelectionFilterMeta<SelectionItemId>) {
        val filter = filterFactory.createFilter(meta)
        listVM.reset(filter)
    }
}