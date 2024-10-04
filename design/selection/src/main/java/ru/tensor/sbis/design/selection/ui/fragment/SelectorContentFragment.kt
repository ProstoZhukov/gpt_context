package ru.tensor.sbis.design.selection.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import io.reactivex.subjects.BehaviorSubject
import ru.tensor.sbis.design.selection.R
import ru.tensor.sbis.design.selection.bl.vm.selection.multi.MultiSelectionViewModel
import ru.tensor.sbis.design.selection.ui.di.SbisListComponent
import ru.tensor.sbis.design.selection.ui.factories.ItemMetaFactory
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.design.selection.ui.utils.FilteredSelection
import ru.tensor.sbis.design.selection.ui.utils.SelectionListScroller
import ru.tensor.sbis.design.selection.ui.utils.createMultiListComponent
import ru.tensor.sbis.design.selection.ui.view.selecteditems.panel.AbstractSelectionPanel
import ru.tensor.sbis.design.selection.ui.view.selecteditems.viewholder.view.SelectedItemsContainerView
import ru.tensor.sbis.design.toolbar.Toolbar
import ru.tensor.sbis.design.utils.HalfHeightViewOutlineProvider
import ru.tensor.sbis.design.view.input.searchinput.SearchInput
import ru.tensor.sbis.list.view.SbisList
import ru.tensor.sbis.list.view.utils.ListData

/**
 * Инфраструктурный фрагмент, где обеспечивается доступ к [MultiSelectorFragment]
 *
 * @author ma.kolpakov
 */
internal abstract class SelectorContentFragment(
    @LayoutRes contentLayoutId: Int
) : AbstractSelectorContentFragment(contentLayoutId) {

    @Suppress("UNCHECKED_CAST")
    internal val multiSelectorFragment: MultiSelectorFragment
        get() = requireParentFragment() as MultiSelectorFragment

    /**
     * Вью модель выбранных элементов доступна после вызова [onAttach]
     */
    protected lateinit var selectionViewModel: MultiSelectionViewModel<SelectorItemModel>
        private set
    final override lateinit var metaFactory: ItemMetaFactory
        private set

    private var list: SbisList? = null
    private lateinit var selectedItems: SelectedItemsContainerView

    override fun onAttach(context: Context) {
        selectionViewModel = multiSelectorFragment.selectionComponent.selectionVm
        metaFactory = multiSelectorFragment.selectionComponent.metaFactory
        super.onAttach(context)
    }

    override fun createListComponent(): SbisListComponent = createMultiListComponent()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        selectedItems = view.findViewById(R.id.selectedItems)
        list = view.findViewById<SbisList>(R.id.list).apply {
            adapter!!.registerAdapterDataObserver(SelectionListScroller(this))
        }

        configureSelectedItemsOutline()
    }

    override fun onDestroyView() {
        list!!.adapter = null
        list = null
        super.onDestroyView()
    }

    override fun onStart() {
        super.onStart()

        val component = multiSelectorFragment.selectionComponent
        // область выбранных элементов
        val selectionView = component
            .multiSelectorCustomisation
            .createSelectionPanel(selectedItems, selectionViewModel)
        with(selectionViewModel) {
            val filteredSelection = FilteredSelection(selection, component.filterFunction)
            listViewModel.stubViewVisibility.observe(viewLifecycleOwner) {
                filteredSelection.onStubVisibilityUpdated()
            }
            disposable.addAll(
                result.ignoreElement().subscribe(listViewModel::onSelectionCompleted),
                searchViewModel.searchQuery.subscribe(filteredSelection::setQuery),
                selection.subscribe { onItemsSelected(it, selectionView) },
                selection.subscribe(listViewModel::onSelectionChanged),
                getListDataObservable().subscribe {
                    list!!.setListData(it)
                    filteredSelection.onListUpdated()
                }
            )
        }
    }

    override fun setListData(data: ListData) = Unit

    override fun getSearchPanel(): SearchInput = multiSelectorFragment.requireView().findViewById(R.id.searchPanel)

    override fun getToolbar(): Toolbar = multiSelectorFragment.requireView().findViewById(R.id.headerContent)

    private fun configureSelectedItemsOutline() {
        selectedItems.outlineProvider = HalfHeightViewOutlineProvider()
    }

    private fun onItemsSelected(
        items: List<SelectorItemModel>,
        selectionView: AbstractSelectionPanel<SelectorItemModel>
    ) {
        selectionView.setSelectedItems(items)
        setHeaderViewWithShadow(getHeaderViewWithShadow(items.isNotEmpty()))
    }

    private fun getHeaderViewWithShadow(hasSelectedItems: Boolean) = when {
        hasSelectedItems -> selectedItems
        hasCreateGroupButton -> requireView().findViewById(R.id.fixedButtonPanel)
        else -> lowerHeaderView
    }

    private fun getListDataObservable() = BehaviorSubject.create<ListData>()
        .apply { listViewModel.listData.observeForever(::onNext) }

}