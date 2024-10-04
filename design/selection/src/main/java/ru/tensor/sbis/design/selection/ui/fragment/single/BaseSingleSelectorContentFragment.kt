package ru.tensor.sbis.design.selection.ui.fragment.single

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import ru.tensor.sbis.design.selection.R
import ru.tensor.sbis.design.selection.bl.vm.selection.single.SingleSelectionViewModel
import ru.tensor.sbis.design.selection.ui.di.SbisListComponent
import ru.tensor.sbis.design.selection.ui.di.single.DaggerSingleSelectionSbisListComponent
import ru.tensor.sbis.design.selection.ui.factories.ItemMetaFactory
import ru.tensor.sbis.design.selection.ui.fragment.AbstractSelectorContentFragment
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.design.selection.ui.utils.SingleSelectionListScroller
import ru.tensor.sbis.design.selection.ui.utils.singleDependenciesFactory
import ru.tensor.sbis.design.toolbar.Toolbar
import ru.tensor.sbis.design.view.input.searchinput.SearchInput
import ru.tensor.sbis.list.view.SbisList

/**
 * Базовый класс фрагмента с содержимым одиночного выбора, зависящим от уровня вложенности
 *
 * @author us.bessonov
 */
internal abstract class BaseSingleSelectorContentFragment(
    @LayoutRes contentLayoutId: Int
) : AbstractSelectorContentFragment(contentLayoutId) {

    @Suppress("UNCHECKED_CAST")
    protected val singleSelectorFragment: SingleSelectorFragment
        get() = requireParentFragment() as SingleSelectorFragment

    /**
     * Вью модель одиночного выбора доступна после вызова [onAttach]
     */
    protected lateinit var selectionViewModel: SingleSelectionViewModel<SelectorItemModel>
        private set
    final override lateinit var metaFactory: ItemMetaFactory
        private set

    override fun onAttach(context: Context) {
        selectionViewModel = singleSelectorFragment.selectionComponent.selectionVm
        metaFactory = singleSelectorFragment.selectionComponent.metaFactory
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHeaderViewWithShadow(if (hasCreateGroupButton) view.findViewById(R.id.fixedButtonPanel) else lowerHeaderView)
        view.findViewById<SbisList>(R.id.list).apply {
            adapter!!.registerAdapterDataObserver(SingleSelectionListScroller(this))
        }
    }

    override fun onStart() {
        super.onStart()

        disposable.add(
            selectionViewModel.result.ignoreElement().subscribe(listViewModel::onSelectionCompleted)
        )
    }

    override fun createListComponent(): SbisListComponent {
        val appContext = requireContext().applicationContext
        return componentArguments.singleDependenciesFactory.run {
            DaggerSingleSelectionSbisListComponent.factory().create(
                this@BaseSingleSelectorContentFragment,
                getServiceWrapper(appContext),
                getSelectionLoader(appContext),
                getFilterFactory(appContext),
                getResultHelper(appContext),
                getMapperFunction(appContext),
                singleSelectorFragment.selectorStrings,
                singleSelectorFragment.selectionComponent
            )
        }
    }

    override fun getSearchPanel(): SearchInput = singleSelectorFragment.requireView().findViewById(R.id.searchPanel)

    override fun getToolbar(): Toolbar = singleSelectorFragment.requireView().findViewById(R.id.headerContent)
}