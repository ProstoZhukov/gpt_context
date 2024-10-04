package ru.tensor.sbis.design.selection.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import android.view.inputmethod.EditorInfo
import androidx.annotation.LayoutRes
import androidx.core.view.updatePadding
import io.reactivex.disposables.CompositeDisposable
import ru.tensor.sbis.design.list_utils.util.ListHeaderElevationHelper
import ru.tensor.sbis.design.selection.R
import ru.tensor.sbis.design.selection.databinding.SelectionListContentBinding
import ru.tensor.sbis.design.selection.ui.di.SbisListComponent
import ru.tensor.sbis.design.selection.ui.factories.ItemMetaFactory
import ru.tensor.sbis.design.selection.ui.list.ListItemMapper
import ru.tensor.sbis.design.selection.ui.list.SelectionListScreenVM
import ru.tensor.sbis.design.selection.ui.utils.*
import ru.tensor.sbis.design.selection.ui.utils.fixed_button.FixedButtonType
import ru.tensor.sbis.design.selection.ui.utils.fixed_button.inflateFixedButton
import ru.tensor.sbis.design.selection.ui.utils.vm.SearchViewModel
import ru.tensor.sbis.design.swipeback.SwipeBackFragment
import ru.tensor.sbis.design.toolbar.Toolbar
import ru.tensor.sbis.design.utils.getDimenPx
import ru.tensor.sbis.design.view.input.searchinput.SearchInput
import ru.tensor.sbis.list.view.utils.ListData

/**
 * Инфраструктурный фрагмент, где должен обеспечиваться доступ к хост фрагменту
 *
 * @author ma.kolpakov
 */
internal abstract class AbstractSelectorContentFragment(
    @LayoutRes private val contentLayoutId: Int
) : SwipeBackFragment() {

    protected val disposable = CompositeDisposable()

    private lateinit var listComponent: SbisListComponent
    protected val listViewModel: SelectionListScreenVM get() = listComponent.screenVm
    protected lateinit var searchViewModel: SearchViewModel
        private set
    protected val listItemMapper: ListItemMapper get() = listComponent.listItemMapper
    protected abstract val metaFactory: ItemMetaFactory

    /**
     * Аргументы компонента выбора, глобальные настройки
     */
    internal val componentArguments: Bundle get() = requireParentFragment().requireArguments()

    protected val hasCreateGroupButton: Boolean
        get() = componentArguments.fixedButtonType == FixedButtonType.CREATE_GROUP

    /**
     * Нижний [View] шапки, у которого по умолчанию должна отображаться тень при возможности прокрутки вверх
     */
    protected abstract val lowerHeaderView: View

    private var headerElevationHelper: ListHeaderElevationHelper? = null

    private var viewBindingNullable: SelectionListContentBinding? = null
    private val viewBinding get() = viewBindingNullable!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.cloneWithTheme(requireContext(), componentArguments.themeRes)
            .inflate(contentLayoutId, container, false).apply {
                with(componentArguments) {
                    fixedButtonType?.let { type ->
                        val fixedButton: ViewStub = findViewById(R.id.fixedButton)
                        fixedButton.inflateFixedButton(type, listViewModel, viewLifecycleOwner)
                    }
                }
            }
            .run { addToSwipeBackLayout(this) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBindingNullable = SelectionListContentBinding.bind(view.findViewById(R.id.listContent))
        /*
        Размер пула подобран на основании количества элементов, которые могут поместиться на экран. Отрабатывает, когда
        нужно разово получить много view для биндинга при развороте списка выбранных
         */
        viewBinding.list.recycledViewPool.setMaxRecycledViews(0, 20)
        if (componentArguments.enableHeaderShadow) {
            headerElevationHelper = ListHeaderElevationHelper()
                .also { viewBinding.list.addOnScrollListener(it) }
        }
        getToolbar().leftPanel.updatePadding(
            right = viewBinding.root.context.getDimenPx(R.attr.Selector_headerIconPadding)
        )
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        listComponent = createListComponent()
        searchViewModel = listComponent.searchVm

        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            // TODO: 5/21/2020 использовать parentFragmentManager после обновления библиотек https://online.sbis.ru/opendoc.html?guid=56a4f25e-1959-4af1-b238-bebb1f96b4ae
            SelectorHostBackPressedCallback(requireParentFragment().childFragmentManager, searchViewModel)
        )
    }

    override fun onStart() {
        super.onStart()

        listViewModel.apply {
            listData.observe(viewLifecycleOwner, ::setListData)
            listVisibility.observe(viewLifecycleOwner, viewBinding.list::setVisibility)
            loadNextAvailability.observe(viewLifecycleOwner, viewBinding.list::loadNextAvailability)
            loadNextVisibility.observe(viewLifecycleOwner, viewBinding.list::loadNextProgressIsVisible)
            loadPreviousVisibility.observe(
                viewLifecycleOwner,
                viewBinding.list::loadPreviousProgressIsVisible
            )
            progressVisibility.observe(viewLifecycleOwner, viewBinding.progress::setVisibility)

            stubContent.observe(viewLifecycleOwner, viewBinding.errorView::setContentFactory)
            stubViewVisibility.observe(viewLifecycleOwner, viewBinding.errorView::setVisibility)

            viewBinding.list.setLoadMoreCallback(this)
        }

        disposable.add(searchViewModel.hideKeyboardEvent.subscribe { getSearchPanel().hideKeyboard() })

        with(getSearchPanel()) {
            disposable.addAll(
                searchViewModel.searchText.subscribe(::setSearchText),
                searchViewModel.searchQuery.subscribe(listViewModel::refresh),
                searchQueryChangedObservable().subscribe(searchViewModel::setSearchText),
                cancelSearchObservable().subscribe { searchViewModel.cancelSearch() },
                searchFieldEditorActionsObservable().subscribe { actionId ->
                    if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_SEARCH) {
                        searchViewModel.finishEditingSearchQuery()
                    }
                }
            )
        }

        listComponent.fixedButtonListener?.let { listener ->
            disposable.add(
                listViewModel.fixedButtonClicked.subscribe { buttonData ->
                    listener.onButtonClicked(requireActivity(), buttonData)
                }
            )
        }

        viewBinding.list.setShouldThrottleItemClicksSeparately(!componentArguments.isHierarchicalData)
    }

    override fun onStop() {
        super.onStop()
        disposable.clear()
    }

    override fun onResume() {
        if (searchViewModel.isFocused) {
            getSearchPanel().showKeyboard()
        }
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        searchViewModel.isFocused = getSearchPanel().hasFocus()
        if (searchViewModel.isFocused) {
            getSearchPanel().hideKeyboard()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        headerElevationHelper?.let { viewBinding.list.removeOnScrollListener(it) }
        headerElevationHelper = null
        viewBindingNullable = null
    }

    /**
     * Обновляет [View], у которого должна отображаться тень при возможности прокрутки вверх
     */
    protected fun setHeaderViewWithShadow(view: View?) {
        headerElevationHelper?.setHeaderView(view)
    }

    /** @SelfDocumented */
    protected open fun setListData(data: ListData) = viewBinding.list.setListData(data)

    /** @SelfDocumented */
    protected abstract fun createListComponent(): SbisListComponent

    /** @SelfDocumented */
    protected abstract fun getSearchPanel(): SearchInput

    /** @SelfDocumented */
    protected abstract fun getToolbar(): Toolbar

}