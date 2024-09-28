package ru.tensor.sbis.design_selection.ui.content

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_DRAGGING
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.android.material.appbar.AppBarLayout
import io.reactivex.disposables.CompositeDisposable
import ru.tensor.sbis.common.util.AdjustResizeHelper
import ru.tensor.sbis.common.util.storeIn
import ru.tensor.sbis.common.util.withArgs
import ru.tensor.sbis.communication_decl.selection.SelectionConfig
import ru.tensor.sbis.communication_decl.selection.SelectionHeaderMode
import ru.tensor.sbis.design.breadcrumbs.CurrentFolderView
import ru.tensor.sbis.design.custom_view_tools.utils.dp
import ru.tensor.sbis.design.swipeback.SwipeBackFragment
import ru.tensor.sbis.design.toolbar.behavior.BaseAppBarLayoutBehavior
import ru.tensor.sbis.design.view.input.searchinput.SearchInput
import ru.tensor.sbis.design_selection.R
import ru.tensor.sbis.design_selection.ui.content.vm.SelectionContentViewModel
import ru.tensor.sbis.design_selection.databinding.DesignSelectionContentFragmentBinding
import ru.tensor.sbis.design_selection.ui.content.di.DaggerSelectionContentComponent
import ru.tensor.sbis.design_selection.ui.content.di.SelectionContentComponent
import ru.tensor.sbis.design_selection.contract.data.SelectionFolderItem
import ru.tensor.sbis.design_selection.contract.data.SelectionItem
import ru.tensor.sbis.design_selection.domain.list.SelectionComponentSettings
import ru.tensor.sbis.design_selection.ui.content.utils.SelectionContentStackHelper
import ru.tensor.sbis.design_selection.ui.content.utils.SelectionElevationHelper
import ru.tensor.sbis.design_selection.ui.content.vm.search.SelectionSearchViewModel
import ru.tensor.sbis.design_selection.ui.main.SelectionFragment
import ru.tensor.sbis.design_selection.ui.main.utils.*
import ru.tensor.sbis.design_selection.ui.main.utils.cloneWithSelectionTheme
import ru.tensor.sbis.design_selection.ui.main.utils.folderItem

/**
 * Фрагмент области контента компонента выбора.
 * Включает в себя список невыбранных элементов и заголовок текущей папки [SelectionFragment].
 *
 * @author vv.chekurda
 */
internal class SelectionContentFragment :
    SwipeBackFragment(),
    SelectionHeaderShadowDelegate.ScrollableContent,
    AdjustResizeHelper.KeyboardEventListener {

    companion object {

        /**
         * Создать инстанс фрагмента области контента компонента выбора.
         *
         * @param folderItem папка, для которой будет отображен список.
         * null - корневая папка.
         */
        fun newInstance(folderItem: SelectionFolderItem? = null): SelectionContentFragment =
            SelectionContentFragment().withArgs {
                this.folderItem = folderItem
            }
    }

    private lateinit var contentComponent: SelectionContentComponent
    private lateinit var stackHelper: SelectionContentStackHelper

    private val contentVM: SelectionContentViewModel<SelectionItem>
        get() = contentComponent.contentVM
    private val searchVM: SelectionSearchViewModel
        get() = contentComponent.searchVM

    private val selectionFragment: SelectionFragment
        get() = requireParentFragment() as SelectionFragment

    private val selectionArgs: Bundle
        get() = selectionFragment.requireArguments()

    private var elevationHelper: SelectionElevationHelper? = null

    private var skipNextScrollToTop = false

    private var _binding: DesignSelectionContentFragmentBinding? = null
    private val binding: DesignSelectionContentFragmentBinding
        get() = checkNotNull(_binding)

    private val config: SelectionConfig
        get() = contentComponent.config

    private var searchInput: SearchInput? = null
    private var currentFolderView: CurrentFolderView? = null

    private val disposer = CompositeDisposable()
    private val searchDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        contentComponent = createSelectionContentComponent()
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        DesignSelectionContentFragmentBinding.inflate(
            inflater.cloneWithSelectionTheme(
                context = requireContext(),
                themeAttr = selectionArgs.themeAttr,
                defTheme = selectionArgs.defTheme,
                primaryThemeResolver = config::themeRes
            ),
            container,
            false
        ).run {
            _binding = this
            addToSwipeBackLayout(root)
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        internalInit(savedInstanceState)
    }

    private fun internalInit(savedInstanceState: Bundle? = null) {
        contentVM.setRouter(contentComponent.router)

        initStackHelper(savedInstanceState)
        initSearchInput()
        initFolderPanel()
        initListView()
        subscribeOnUnselect()
        if (savedInstanceState == null) {
            updateAppBarLayout()
        }
    }

    private fun initSearchInput() {
        searchInput = selectionFragment.searchInput
        if (config.headerMode == SelectionHeaderMode.ADDITIONAL_SEARCH) {
            searchVM.isFocused = false
        }
        bindSearchInput()
    }

    private fun bindSearchInput() {
        if (!stackHelper.isContentVisible) return
        searchInput?.also {
            searchDisposable.addAll(
                searchVM.searchTextObservable
                    .subscribe { searchInput?.setSearchText(it) },
                searchVM.hideKeyboardEventObservable
                    .subscribe { hideKeyboard() },
                it.searchQueryChangedObservable().subscribe(searchVM::setSearchText),
                it.cancelSearchObservable().subscribe { searchVM.cancelSearch() }
            )
        }
    }

    private fun hideKeyboard() {
        if (contentComponent.rulesHelper.autoHideKeyboard) {
            searchInput?.hideKeyboard()
        }
    }

    private fun initFolderPanel() {
        contentComponent.folderItem?.also { folder ->
            currentFolderView = (binding.selectionFolderPanel.inflate() as CurrentFolderView).apply {
                setTitle(folder.title)
                setOnClickListener { contentVM.onFolderTitleClicked() }
            }
        }
    }

    private fun initListView() {
        binding.selectionListView.bindViewModel(contentComponent.listVM)
        binding.selectionListView.findViewById<View>(ru.tensor.sbis.crud3.R.id.crud3_progress_bar_id)
            .updatePadding(
                top = resources.dp(PROGRESS_PADDING_FOR_WRAP_DP),
                bottom = resources.dp(PROGRESS_PADDING_FOR_WRAP_DP)
            )
        binding.selectionListView.list.apply {
            overScrollMode = View.OVER_SCROLL_NEVER

            repeat(RECYCLED_TYPES_COUNT) { recycledViewPool.setMaxRecycledViews(it, MAX_RECYCLED_ITEMS_COUNT_FOR_TYPE) }

            elevationHelper = SelectionElevationHelper().also(::addOnScrollListener)
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    if (newState == SCROLL_STATE_DRAGGING) {
                        if (searchInput?.isFocused == true) {
                            hideKeyboard()
                        }
                    }
                }
            })
            setShouldThrottleItemClicksSeparately(true)
            setScrollOnInsertListener()

            val duration = selectionArgs.itemsAnimationDurationMs
                .takeIf { it >= 0 }
                ?: ITEM_ANIMATION_DURATION_MS
            (itemAnimator as SimpleItemAnimator).apply {
                addDuration = duration
                removeDuration = duration
                moveDuration = duration
            }
            clipToPadding = false

            val listTopPadding = selectionArgs.listTopPadding
            if (listTopPadding > 0) {
                updatePadding(top = listTopPadding)
            }
        }
    }

    private fun initStackHelper(savedInstanceState: Bundle?) {
        stackHelper = contentComponent.stackHelper.apply {
            init(savedInstanceState) { isTopContent ->
                if (isTopContent) {
                    bindSearchInput()
                    updateAppBarLayout()
                    contentVM.reset()
                } else {
                    searchDisposable.clear()
                }
                contentVM.onContentVisibilityChanged(isVisible = isTopContent)
            }
        }
    }

    private fun subscribeOnUnselect() {
        contentVM.onUnselectClicked
            .filter { stackHelper.isContentVisible }
            .subscribe { params ->
                contentVM.unselect(params.first, params.second)
                skipNextScrollToTop = true
            }.storeIn(disposer)
    }

    private fun updateAppBarLayout() {
        selectionFragment.requireView().findViewById<AppBarLayout>(R.id.selection_app_bar_layout)?.apply {
            updateLayoutParams<CoordinatorLayout.LayoutParams> {
                val appBarBehavior = behavior as BaseAppBarLayoutBehavior
                appBarBehavior.mScrollViewId = binding.selectionListView.list.id
            }
            setExpanded(true)
        }
    }

    /**
     * Вспомогательная реализация скролла к 0 позиции при добавлении элементов в начало списка.
     * Вызов скролла должен игнорироваться в случае добавления элементов в начало при ручном удалении из выбранных.
     * В случае игнорирования скролла список сам подскролится, если пользователь находится в начале списка.
     */
    private fun setScrollOnInsertListener() {
        val list = binding.selectionListView.list
        list.adapter?.registerAdapterDataObserver(
            object : RecyclerView.AdapterDataObserver() {
                override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                    if (positionStart == 0) {
                        if (skipNextScrollToTop) {
                            skipNextScrollToTop = false
                        } else {
                            list.scrollToPosition(0)
                        }
                    }
                }
            }
        )
    }

    override fun onResume() {
        super.onResume()
        searchVM.isEnabled = true
        if (searchVM.isFocused) {
            searchInput?.showKeyboard()
        }
    }

    override fun onPause() {
        super.onPause()
        searchVM.isEnabled = false
        searchInput?.also {
            searchVM.isFocused = it.hasFocus()
            if (searchVM.isFocused) hideKeyboard()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.selectionListView.list.apply {
            elevationHelper?.let { removeOnScrollListener(it) }
            adapter = null
        }
        elevationHelper = null
        searchInput = null
        currentFolderView = null
        _binding = null
        contentVM.setRouter(null)
        stackHelper.clear()
        disposer.clear()
        searchDisposable.clear()
    }

    override fun setShadowView(view: View?) {
        elevationHelper?.setShadowView(currentFolderView ?: view)
    }

    override fun onKeyboardOpenMeasure(keyboardHeight: Int): Boolean {
        binding.selectionListView.list.updatePadding(bottom = keyboardHeight)
        return true
    }

    override fun onKeyboardCloseMeasure(keyboardHeight: Int): Boolean {
        binding.selectionListView.list.updatePadding(bottom = 0)
        return true
    }

    fun onConfigChanged() {
        searchDisposable.clear()
        disposer.clear()
        viewModelStore.clear()
        contentComponent = createSelectionContentComponent()
        internalInit()
        hideKeyboard()
    }

    /**
     * Подготавливает [SelectionContentComponent] в зависимости от окружения в аргументах.
     */
    private fun createSelectionContentComponent(): SelectionContentComponent =
        with(requireArguments()) {
            val listSettings = SelectionComponentSettings(
                showStubs = selectionArgs.showStubs,
                showPagingLoaders = selectionArgs.showLoaders
            )
            DaggerSelectionContentComponent.factory().create(
                fragment = this@SelectionContentFragment,
                folderItem = folderItem,
                listSettings = listSettings,
                selectionComponent = selectionFragment.selectionHostComponent
            ).also {
                // Инициализация для начала загрузки данных до начала onCreateView.
                it.contentVM
            }
        }
}

private const val RECYCLED_TYPES_COUNT = 5
private const val MAX_RECYCLED_ITEMS_COUNT_FOR_TYPE = 50
private const val ITEM_ANIMATION_DURATION_MS = 70L
private const val PROGRESS_PADDING_FOR_WRAP_DP = 25