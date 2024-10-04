package ru.tensor.sbis.design.selection.ui.utils.vm

import android.view.View
import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import ru.tensor.sbis.design.selection.ui.list.SelectionListInteractor
import ru.tensor.sbis.design.selection.ui.list.SelectionListScreenEntity
import ru.tensor.sbis.design.selection.ui.list.SelectionListScreenVM
import ru.tensor.sbis.design.selection.ui.list.filter.SelectorFilterCreator
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.design.selection.ui.utils.vm.choose_all.FixedButtonViewModel
import ru.tensor.sbis.list.base.presentation.ListScreenVM
import ru.tensor.sbis.list.base.presentation.ListScreenVMImpl

/**
 * @author ma.kolpakov
 */
internal class SelectorListScreenViewModel(
    private val entity: SelectionListScreenEntity<Any, Any, Any>,
    private val filterCreator: SelectorFilterCreator<*, *>,
    private val interactor: SelectionListInteractor<Any, Any, Any, SelectionListScreenEntity<Any, Any, Any>>,
    private val viewModelDelegate: ListScreenVMImpl<SelectionListScreenEntity<Any, Any, Any>>,
    fixedButtonVmDelegate: FixedButtonViewModel<Any>
) : ViewModel(),
    SelectionListScreenVM,
    ListScreenVM by viewModelDelegate,
    FixedButtonViewModel<Any> by fixedButtonVmDelegate {

    /**
     * Используется PublishSubject вместо LiveData так как события нажатий не нужно дублировать при поворотах
     */
    private val itemClickedSubject = PublishSubject.create<SelectorItemModel>()

    private var enabled = true

    init {
        stubViewVisibility.observeForever {
            val visibility = if (it == View.INVISIBLE) View.GONE else it
            fixedButtonVmDelegate.setStubVisible(visibility)
        }
    }

    override val itemClicked: Observable<SelectorItemModel> = itemClickedSubject

    override fun onItemClicked(item: SelectorItemModel) {
        itemClickedSubject.onNext(item)
    }

    override fun refresh(searchQuery: String) {
        if (filterCreator.searchQuery != searchQuery) {
            filterCreator.searchQuery = searchQuery
            reload()
        }
    }

    override fun reload() {
        if (enabled) {
            interactor.refresh(entity, viewModelDelegate)
        }
    }

    override fun onSelectionChanged(selection: List<SelectorItemModel>) {
        interactor.applySelection(selection, entity, viewModelDelegate)
    }

    override fun onSelectionCompleted() {
        enabled = false
    }
}