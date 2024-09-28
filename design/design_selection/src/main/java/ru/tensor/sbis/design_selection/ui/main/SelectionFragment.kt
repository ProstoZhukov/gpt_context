package ru.tensor.sbis.design_selection.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.*
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
import com.google.android.material.appbar.AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
import com.google.android.material.appbar.AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP
import com.google.android.material.appbar.CollapsingToolbarLayout
import io.reactivex.disposables.CompositeDisposable
import ru.tensor.sbis.base_components.BaseFragment
import ru.tensor.sbis.base_components.fragment.FragmentBackPress
import ru.tensor.sbis.common.util.AdjustResizeHelper
import ru.tensor.sbis.common.util.storeIn
import ru.tensor.sbis.common.util.withArgs
import ru.tensor.sbis.communication_decl.selection.SelectionConfig
import ru.tensor.sbis.communication_decl.selection.SelectionHeaderMode
import ru.tensor.sbis.communication_decl.selection.SelectionMode
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.buttons.SbisRoundButton
import ru.tensor.sbis.design.buttons.base.models.icon.SbisButtonTextIcon
import ru.tensor.sbis.design.buttons.base.models.style.SuccessButtonStyle
import ru.tensor.sbis.design.buttons.round.model.SbisRoundButtonSize
import ru.tensor.sbis.design.topNavigation.api.SbisTopNavigationContent
import ru.tensor.sbis.design.utils.HalfHeightViewOutlineProvider
import ru.tensor.sbis.design.utils.extentions.setHorizontalMargin
import ru.tensor.sbis.design.utils.getThemeBoolean
import ru.tensor.sbis.design.utils.preventViewFromDoubleClickWithDelay
import ru.tensor.sbis.design.view.input.searchinput.SearchInput
import ru.tensor.sbis.design.view.input.searchinput.util.AppBarLayoutWithDynamicElevationBehavior
import ru.tensor.sbis.design.view.input.searchinput.util.expandSearchInput
import ru.tensor.sbis.design_dialogs.dialogs.content.Content
import ru.tensor.sbis.design_notification.SbisPopupNotification
import ru.tensor.sbis.design_notification.popup.SbisPopupNotificationStyle
import ru.tensor.sbis.design_selection.R
import ru.tensor.sbis.design_selection.contract.SelectionDependenciesFactory
import ru.tensor.sbis.design_selection.contract.customization.selected.panel.SelectedData
import ru.tensor.sbis.design_selection.contract.data.SelectionItem
import ru.tensor.sbis.design_selection.contract.listeners.SelectionDelegate
import ru.tensor.sbis.design_selection.databinding.DesignSelectionHostFragmentBinding
import ru.tensor.sbis.design_selection.domain.completion.ApplySelection
import ru.tensor.sbis.design_selection.domain.completion.CancelSelection
import ru.tensor.sbis.design_selection.domain.completion.CompleteEvent
import ru.tensor.sbis.design_selection.ui.content.SelectionContentFragment
import ru.tensor.sbis.design_selection.ui.content.utils.SelectionStatisticUtil
import ru.tensor.sbis.design_selection.ui.main.di.DaggerSelectionComponent
import ru.tensor.sbis.design_selection.ui.main.di.SelectionComponent
import ru.tensor.sbis.design_selection.ui.main.utils.*
import ru.tensor.sbis.design_selection.ui.main.vm.contract.SelectionHostViewModel
import ru.tensor.sbis.toolbox_decl.selection_statistic.SelectionStatisticAction.OPEN_SELECTION
import ru.tensor.sbis.toolbox_decl.selection_statistic.SelectionStatisticEvent
import timber.log.Timber

/**
 * Компонент выбора.
 *
 * @author vv.chekurda
 */
internal class SelectionFragment :
    BaseFragment(),
    AdjustResizeHelper.KeyboardEventListener,
    FragmentBackPress,
    SelectionDelegate.Provider,
    Content {

    companion object {

        /**
         * Создать инстанс фрагмента компонента выбора.
         *
         * @param config конфигурация компонента.
         * @param dependenciesProvider поставщик зависимостей компонента выбора.
         */
        fun newInstance(
            config: SelectionConfig,
            dependenciesProvider: SelectionDependenciesFactory.Provider<*, *>,
            @AttrRes themeAttr: Int = R.attr.selectionTheme,
            @StyleRes defTheme: Int = R.style.SelectionTheme
        ) = SelectionFragment().withArgs {
            this.config = config
            this.dependenciesProvider = dependenciesProvider
            this.themeAttr = themeAttr
            this.defTheme = defTheme
        }
    }

    /**
     * Компонент доступен для использования в качестве зависимости в DI графах области контента.
     */
    internal lateinit var selectionHostComponent: SelectionComponent
        private set

    internal var searchInput: SearchInput? = null

    private val selectionHostVM: SelectionHostViewModel<SelectionItem>
        get() = selectionHostComponent.selectionHostVM

    private var _binding: DesignSelectionHostFragmentBinding? = null
    private val binding: DesignSelectionHostFragmentBinding
        get() = checkNotNull(_binding)

    private val config: SelectionConfig
        get() = selectionHostComponent.config

    private var shadowHelper: SelectionHeaderShadowDelegate? = null
    private var isKeyboardOpened: Boolean = false

    private val disposer = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        selectionHostComponent = createSelectionComponent()
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            SelectionStatisticUtil.sendStatistic(
                SelectionStatisticEvent(config.useCase.name, OPEN_SELECTION.value)
            )
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        DesignSelectionHostFragmentBinding.inflate(
            inflater.cloneWithSelectionTheme(
                context = requireContext(),
                themeAttr = requireArguments().themeAttr,
                defTheme = requireArguments().defTheme,
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
        shadowHelper = SelectionHeaderShadowDelegate(childFragmentManager, binding)

        if (savedInstanceState == null) {
            addContentFragment()
        } else {
            @Suppress("DEPRECATION")
            selectionHostComponent.selectionModeProvider.selectionMode =
                savedInstanceState.getSerializable(ACTUAL_SELECTION_MODE_KEY) as SelectionMode
        }

        internalInit()
    }

    private fun internalInit() {
        selectionHostVM.setRouter(selectionHostComponent.router)
        initToolbar()
        initSelectionPanel()
        initHeaderButton()
        initSubscriptions()
    }

    private fun addContentFragment() {
        childFragmentManager.beginTransaction()
            .add(R.id.selection_content_container, SelectionContentFragment.newInstance())
            .commitNow()
    }

    private fun initToolbar() {
        when (config.headerMode) {
            SelectionHeaderMode.VISIBLE -> initAsVisibleToolbar()
            SelectionHeaderMode.GONE -> initAsGoneToolbar()
            SelectionHeaderMode.ADDITIONAL_SEARCH -> initAsOnlySearchToolbar()
        }
    }

    private fun initAsVisibleToolbar() {
        val selectionDoneButton = SbisRoundButton(requireContext()).apply {
            val horizontalMargin = requireContext().resources.getDimensionPixelSize(
                R.dimen.selection_done_button_horizontal_margin
            )
            id = R.id.selection_done_button
            style = SuccessButtonStyle
            size = SbisRoundButtonSize.S
            icon = SbisButtonTextIcon(SbisMobileIcon.Icon.smi_checked.character.toString())
            layoutParams = ViewGroup.MarginLayoutParams(WRAP_CONTENT, WRAP_CONTENT)
            setHorizontalMargin(horizontalMargin, horizontalMargin)
            setOnClickListener(
                preventViewFromDoubleClickWithDelay { selectionHostVM.onDoneButtonClicked() }
            )
            isVisible = true
        }
        binding.selectionHeaderContent.selectionToolbar.apply {
            content = SbisTopNavigationContent.SearchInput
            showBackButton = true
            searchInput?.apply {
                this@SelectionFragment.searchInput = this
                setSearchHint(getString(selectionHostComponent.selectionStrings.searchHint))
                setHasFilter(false)
            }
            backBtn?.setOnClickListener(
                preventViewFromDoubleClickWithDelay { selectionHostVM.cancel() }
            )
            isDividerVisible = binding.root.context.getThemeBoolean(R.attr.Selection_showTopNavDivider)
            rightButtons = listOf(selectionDoneButton)
            rightBtnContainer?.isVisible = false
        }
    }

    private fun initAsGoneToolbar() {
        binding.selectionHeaderContent.root.isVisible = false

        binding.selectionHeaderContent.selectionToolbar.content = SbisTopNavigationContent.SearchInput
        searchInput = binding.selectionHeaderContent.selectionToolbar.searchInput
    }

    private fun initAsOnlySearchToolbar() {
        binding.selectionHeaderContent.root.isVisible = false

        val additionalSearchId = R.id.selection_additional_search_input
        val searchInput = binding.root.findViewById<SearchInput>(additionalSearchId)
        if (searchInput != null) {
            this.searchInput = searchInput
            searchInput.hideKeyboard()
            return
        }

        val additionalSearchInput = SearchInput(binding.root.context).apply {
            id = additionalSearchId
            layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
            setHasFilter(false)
        }
        this.searchInput = additionalSearchInput

        val collapsingToolbarLayout = CollapsingToolbarLayout(binding.root.context).apply {
            id = R.id.selection_collapsing_toolbar_layout
            layoutParams = AppBarLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
                scrollFlags = SCROLL_FLAG_SCROLL or SCROLL_FLAG_ENTER_ALWAYS or SCROLL_FLAG_SNAP
            }
            addView(additionalSearchInput)
        }

        val appBarLayout = AppBarLayout(binding.root.context).apply {
            id = R.id.selection_app_bar_layout
            layoutParams = CoordinatorLayout.LayoutParams(
                ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
            ).apply {
                behavior = AppBarLayoutWithDynamicElevationBehavior().apply {
                    setShouldHideElevation(true)
                }
            }
            expandSearchInput(this)
            addView(collapsingToolbarLayout)
        }

        binding.selectionRootContainer.layoutParams = CoordinatorLayout.LayoutParams(
            binding.selectionContentContainer.layoutParams
        ).apply {
            behavior = AppBarLayout.ScrollingViewBehavior()
        }
        binding.selectionCoordinatorLayout.addView(appBarLayout, 0)
    }

    private fun initSelectionPanel() {
        binding.selectionPanel.apply {
            outlineProvider = HalfHeightViewOutlineProvider()
            init(selectionHostComponent.selectedItemsAdapter)
        }
        selectionHostVM.selectedDataObservable
            .subscribe(::setSelectedData)
            .storeIn(disposer)
    }

    private fun setSelectedData(data: SelectedData<SelectionItem>) {
        binding.selectionPanel.setData(data)
        shadowHelper?.onSelectedDataChanged(hasSelectedData = data.items.isNotEmpty())
    }

    private fun initSubscriptions() {
        selectionHostVM.doneButtonVisible
            .subscribe {
                binding.selectionHeaderContent.selectionToolbar.rightBtnContainer?.isVisible = it
            }
            .storeIn(disposer)
        selectionHostVM.result
            .subscribe(::onComplete, Timber::e)
            .storeIn(disposer)
        selectionHostVM.errorMessage
            .subscribe(::showErrorMessage, Timber::w)
            .storeIn(disposer)
        selectionHostVM.updateConfig
            .subscribe(::updateConfig)
            .storeIn(disposer)
    }

    private fun initHeaderButton() {
        val contract = selectionHostComponent.headerButtonContract ?: return
        val headerButton = with(binding.selectionHeaderButton) {
            layoutResource = contract.layout
            inflate()
        }
        headerButton.setOnClickListener {
            val strategy = contract.onButtonClicked(
                requireActivity(),
                selectionHostVM.selectedData.items,
                config
            )
            selectionHostVM.onHeaderButtonClicked(strategy)
        }
        selectionHostVM.isHeaderButtonVisible
            .subscribe { headerButton.isVisible = it }
            .storeIn(disposer)
    }

    private fun updateConfig(nextConfig: SelectionConfig) {
        disposer.clear()
        viewModelStore.clear()
        requireArguments().config = nextConfig
        selectionHostComponent = createSelectionComponent()
        internalInit()
        (childFragmentManager.fragments.first() as SelectionContentFragment).onConfigChanged()
    }

    private fun onComplete(event: CompleteEvent<SelectionItem>) {
        val delayMs = if (isKeyboardOpened) {
            binding.selectionHeaderContent.selectionToolbar.searchInput?.hideKeyboard()
            HIDE_KEYBOARD_DELAY_MS
        } else {
            0L
        }
        binding.selectionHeaderContent.selectionToolbar.postDelayed({
            when (event) {
                is CancelSelection -> {
                    selectionHostComponent.selectionResultListener.onCancel(
                        requireActivity(),
                        config.requestKey
                    )
                }
                is ApplySelection<SelectionItem> -> {
                    selectionHostComponent.selectionResultListener.onComplete(
                        requireActivity(),
                        event.result,
                        config.requestKey,
                        disposer
                    )
                }
            }
        }, delayMs)
    }

    private fun showErrorMessage(errorMessage: String) {
        SbisPopupNotification.push(
            requireContext(),
            SbisPopupNotificationStyle.ERROR,
            errorMessage
        )
    }

    override fun onStart() {
        super.onStart()
        shadowHelper?.updateShadowView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        disposer.clear()
        shadowHelper?.clear()
        shadowHelper = null
        _binding = null
        searchInput = null
        selectionHostVM.setRouter(null)
    }

    override fun getSelectionDelegate(): SelectionDelegate =
        selectionHostVM

    override fun onKeyboardOpenMeasure(keyboardHeight: Int): Boolean {
        childFragmentManager.fragments.forEach {
            (it as? AdjustResizeHelper.KeyboardEventListener)?.onKeyboardOpenMeasure(keyboardHeight)
        }
        isKeyboardOpened = true
        return true
    }

    override fun onKeyboardCloseMeasure(keyboardHeight: Int): Boolean {
        childFragmentManager.fragments.forEach {
            (it as? AdjustResizeHelper.KeyboardEventListener)?.onKeyboardCloseMeasure(keyboardHeight)
        }
        isKeyboardOpened = false
        return true
    }

    override fun onBackPressed(): Boolean =
        selectionHostVM.onBackPressed()

    override fun onViewGoneBySwipe() {
        selectionHostVM.cancel()
    }

    override fun swipeBackEnabled() =
        config.enableSwipeBack

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(ACTUAL_SELECTION_MODE_KEY, selectionHostComponent.selectionModeProvider.selectionMode)
    }

    /**
     * Подготавливает [SelectionComponent] в зависимости от окружения в аргументах
     */
    private fun createSelectionComponent(): SelectionComponent =
        DaggerSelectionComponent.factory().create(
            fragment = this@SelectionFragment,
            routerContainerId = R.id.selection_content_container,
            useRouterReplaceStrategy = requireArguments().useRouterReplaceStrategy,
            autoHideKeyboard = requireArguments().autoHideKeyboard,
            config = requireArguments().config,
            dependenciesProvider = requireArguments().dependenciesProvider
        )
}

private const val ACTUAL_SELECTION_MODE_KEY = "ACTUAL_SELECTION_MODE_KEY"

/**
 * Delay для предотвращения прыжков экрана при подтверждении выбора,
 * когда в компоненте выбора поднята клавиатура, и она должна подняться на следующем или предыдущем экране.
 * Самый простой, но действенный фикс, в теории для производительности можно доработать под флаг в конфиге.
 */
private const val HIDE_KEYBOARD_DELAY_MS = 60L