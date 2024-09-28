@file:Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")

package ru.tensor.sbis.communicator.contacts_registry.ui.contactlist

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import androidx.annotation.LayoutRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import ru.tensor.sbis.base_components.adapter.checkable.CheckHelper
import ru.tensor.sbis.base_components.adapter.selectable.SelectionHelper
import ru.tensor.sbis.base_components.autoscroll.AutoScroller
import ru.tensor.sbis.base_components.autoscroll.LinearAutoScroller
import ru.tensor.sbis.base_components.fragment.FragmentBackPress
import ru.tensor.sbis.common.generated.CommandStatus
import ru.tensor.sbis.common.generated.ErrorCode
import ru.tensor.sbis.common.navigation.NavxId
import ru.tensor.sbis.common.provider.BottomBarProvider
import ru.tensor.sbis.common.util.CommonUtils
import ru.tensor.sbis.common.util.DeviceConfigurationUtils
import ru.tensor.sbis.common.util.NetworkUtils
import ru.tensor.sbis.common.util.SharedPreferencesUtils.shouldRequestContactsPermissions
import ru.tensor.sbis.common.util.UUIDUtils
import ru.tensor.sbis.common.util.getParentFragmentAs
import ru.tensor.sbis.common.util.mapIndexed
import ru.tensor.sbis.common.util.storeIn
import ru.tensor.sbis.common.util.withArgs
import ru.tensor.sbis.communicator.base_folders.ROOT_FOLDER_UUID
import ru.tensor.sbis.communicator.base_folders.keyboard.CommunicatorKeyboardMarginsHelper
import ru.tensor.sbis.communicator.base_folders.keyboard.CommunicatorKeyboardMarginsHelperImpl
import ru.tensor.sbis.communicator.common.data.theme.ConversationModel
import ru.tensor.sbis.communicator.common.di.CommunicatorCommonComponent
import ru.tensor.sbis.communicator.common.import_contacts.ContactsImportConfirmationListener
import ru.tensor.sbis.communicator.common.import_contacts.ImportContactsHelper
import ru.tensor.sbis.communicator.common.ui.hostfragment.contracts.FabKeeper
import ru.tensor.sbis.communicator.common.util.SwipeMenuViewPoolLifecycleManager
import ru.tensor.sbis.communicator.common.util.castTo
import ru.tensor.sbis.communicator.common.util.layout_manager.CommunicatorLayoutManager
import ru.tensor.sbis.communicator.common.view.SegmentDividerItemDecoration
import ru.tensor.sbis.communicator.contacts_declaration.registry.ContactsRegistryMode
import ru.tensor.sbis.communicator.contacts_declaration.registry.ContactsRegistryFragmentFactory
import ru.tensor.sbis.communicator.contacts_declaration.registry.ContactsRouter
import ru.tensor.sbis.communicator.contacts_registry.ContactsRegistryFeatureFacade.contactsDependency
import ru.tensor.sbis.communicator.contacts_registry.ContactsRegistryPlugin.customizationOptions
import ru.tensor.sbis.communicator.contacts_registry.R
import ru.tensor.sbis.communicator.contacts_registry.databinding.CommunicatorFragmentContactListBinding
import ru.tensor.sbis.communicator.contacts_registry.di.factories.ContactFoldersViewModelFactory
import ru.tensor.sbis.communicator.contacts_registry.di.list.ContactListComponent
import ru.tensor.sbis.communicator.contacts_registry.di.list.DaggerContactListComponent
import ru.tensor.sbis.communicator.contacts_registry.ui.addcontacts.AddContactResult
import ru.tensor.sbis.communicator.contacts_registry.ui.addcontacts.internalemployees.AddInternalEmployeesActivity
import ru.tensor.sbis.communicator.contacts_registry.ui.addcontacts.newcontacts.AddNewContactsActivity
import ru.tensor.sbis.communicator.contacts_registry.ui.contactlist.ContactListAdapter.ContactListActionsListener
import ru.tensor.sbis.communicator.contacts_registry.ui.contactlist.ContactListAdapter.HOLDER_EMPTY
import ru.tensor.sbis.communicator.contacts_registry.ui.contactlist.ContactListAdapter.HOLDER_PROGRESS
import ru.tensor.sbis.communicator.contacts_registry.ui.contactlist.model.ContactRegistryModel
import ru.tensor.sbis.communicator.contacts_registry.ui.contactlist.model.ContactsModel
import ru.tensor.sbis.communicator.contacts_registry.ui.contactlist.stub_helper.ContactsStubHelperImpl
import ru.tensor.sbis.communicator.contacts_registry.ui.contactlist.stub_helper.ContactsStubs
import ru.tensor.sbis.communicator.contacts_registry.ui.contactlist.util.ContactItemMatcher
import ru.tensor.sbis.communicator.contacts_registry.ui.filters.ContactFilterConfiguration
import ru.tensor.sbis.communicator.contacts_registry.ui.spinner.ContactSortOrder
import ru.tensor.sbis.communicator.contacts_registry.utils.FoldersViewHolderHelper
import ru.tensor.sbis.communicator.declaration.MasterFragment
import ru.tensor.sbis.communicator.declaration.model.EntitledItem
import ru.tensor.sbis.deeplink.DeeplinkAction
import ru.tensor.sbis.deeplink.DeeplinkActionNode
import ru.tensor.sbis.deeplink.OpenEntityDeeplinkAction
import ru.tensor.sbis.deeplink.OpenProfileDeeplinkAction
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.breadcrumbs.CurrentFolderView
import ru.tensor.sbis.design.buttons.SbisRoundButton
import ru.tensor.sbis.design.container.DimType
import ru.tensor.sbis.design.container.locator.AnchorHorizontalLocator
import ru.tensor.sbis.design.container.locator.AnchorVerticalLocator
import ru.tensor.sbis.design.container.locator.HorizontalAlignment
import ru.tensor.sbis.design.container.locator.HorizontalLocator
import ru.tensor.sbis.design.container.locator.ScreenHorizontalLocator
import ru.tensor.sbis.design.container.locator.VerticalAlignment
import ru.tensor.sbis.design.context_menu.DefaultItem
import ru.tensor.sbis.design.context_menu.MenuItemState
import ru.tensor.sbis.design.context_menu.R.dimen
import ru.tensor.sbis.design.context_menu.SbisMenu
import ru.tensor.sbis.design.context_menu.showMenuWithLocators
import ru.tensor.sbis.design.folders.FoldersView
import ru.tensor.sbis.design.folders.data.model.ROOT_FOLDER_ID
import ru.tensor.sbis.design.folders.support.FoldersViewModel
import ru.tensor.sbis.design.folders.support.extensions.attach
import ru.tensor.sbis.design.folders.support.extensions.createStubViewMediator
import ru.tensor.sbis.design.folders.support.extensions.detach
import ru.tensor.sbis.design.folders.support.utils.stub_integration.StubViewMediator
import ru.tensor.sbis.design.list_utils.decoration.SelectionMarkItemDecoration
import ru.tensor.sbis.design.navigation.util.ActiveTabOnClickListener
import ru.tensor.sbis.design.navigation.util.NavigationDrawerStateListener
import ru.tensor.sbis.design.navigation.view.model.NavigationItem
import ru.tensor.sbis.design.profile.util.ImageBitmapPreFetcher
import ru.tensor.sbis.design.profile_decl.person.PhotoSize
import ru.tensor.sbis.design.stubview.StubViewCase
import ru.tensor.sbis.design.theme.res.PlatformSbisString
import ru.tensor.sbis.design.topNavigation.api.SbisTopNavigationContent
import ru.tensor.sbis.design.utils.KeyboardUtils
import ru.tensor.sbis.design.utils.PinnedHeaderViewHelper
import ru.tensor.sbis.design.utils.extentions.updateTopMargin
import ru.tensor.sbis.design.view.input.searchinput.util.AppBarLayoutWithDynamicElevationBehavior
import ru.tensor.sbis.design.view.input.searchinput.util.expandSearchInput
import ru.tensor.sbis.design_dialogs.fragment.AlertDialogFragment.YesNoListener
import ru.tensor.sbis.design_dialogs.fragment.BottomSelectionPane.OptionClickListener
import ru.tensor.sbis.design_dialogs.fragment.NavigationDrawerContent
import ru.tensor.sbis.design_notification.SbisPopupNotification
import ru.tensor.sbis.design_notification.popup.SbisPopupNotificationStyle
import ru.tensor.sbis.modalwindows.dialogalert.PopupConfirmation
import ru.tensor.sbis.mvp.search.BaseSearchableView
import ru.tensor.sbis.mvp_extensions.view_state.EmptyViewState
import ru.tensor.sbis.mvp_extensions.view_state.EmptyViewState.DEFAULT
import ru.tensor.sbis.mvp_extensions.view_state.EmptyViewState.EMPTY
import java.net.UnknownHostException
import java.util.UUID
import javax.inject.Inject
import ru.tensor.sbis.base_components.R as RBaseComponents
import ru.tensor.sbis.communicator.design.R as RCommunicatorDesign
import ru.tensor.sbis.design.R as RDesign
import ru.tensor.sbis.design.profile.R as RDesignProfile
import ru.tensor.sbis.design.stubview.R as RStubView

/**
 * Фрагмент реестра контактов
 *
 * @author vv.chekurda
 */
internal class ContactListFragment
    : BaseSearchableView<ContactRegistryModel, ContactListAdapter, ContactListContract.View, ContactListContract.Presenter>(),
    CommunicatorKeyboardMarginsHelper by CommunicatorKeyboardMarginsHelperImpl(),
    ContactsImportConfirmationListener,
    ContactsRouter by contactsDependency.getContactsRouter(),
    PopupConfirmation.DialogYesNoWithTextListener,
    NavigationDrawerStateListener,
    ContactListActionsListener,
    ContactListContract.View,
    NavigationDrawerContent,
    OptionClickListener,
    FragmentBackPress,
    MasterFragment,
    YesNoListener,
    FoldersViewHolderHelper,
    ActiveTabOnClickListener,
    DeeplinkActionNode {

    companion object : ContactsRegistryFragmentFactory {

        private const val REGISTRY_MODE = "REGISTRY_MODE"

        override fun createContactsRegistryFragment(mode: ContactsRegistryMode) =
            ContactListFragment().withArgs {
                putInt(REGISTRY_MODE, mode.ordinal)
            }
    }

    private var binding: CommunicatorFragmentContactListBinding? = null

    @Suppress("DEPRECATION")
    private val handler = Handler()
    private var bottomButtonsEnabled: Boolean = false

    private val importContactsHelper: ImportContactsHelper?
        get() = contactsDependency.importContactsHelperProvider?.importContactsHelper

    private var networkUtils: NetworkUtils? = null

    private lateinit var communicatorLayoutManager: CommunicatorLayoutManager

    private var moveInFolderButton: View? = null
    private var deleteButton: View? = null
    private var blockButton: View? = null
    private var folderTitleLayout: CurrentFolderView? = null
    private var navigationFab: SbisRoundButton? = null
    private var stubViewMediator: StubViewMediator? = null
    private lateinit var autoScroller: AutoScroller

    private var emptyViewState: EmptyViewState? = null

    private var isTablet = false

    private var isEmptyFolderList = true

    private val disposables = CompositeDisposable()

    private val navigationHeight by lazy {
        resources.getDimensionPixelSize(RDesign.dimen.tab_navigation_menu_horizontal_height)
    }

    @Inject
    internal lateinit var viewModelFactory: ContactFoldersViewModelFactory

    @Inject
    internal lateinit var swipeMenuViewPoolLifecycleManager: SwipeMenuViewPoolLifecycleManager

    // Максимальное количество контактов на экране. Значение будет вычислено в [onCreate]
    private var maxContactsOnScreen = DEFAULT_MAX_CONTACT_ITEMS_ON_SCREEN
    private var shouldPrefetchPhotos = true

    private val foldersViewModel by lazy { ViewModelProvider(this, viewModelFactory).get(FoldersViewModel::class.java) }

    private val contactsListView: ContactsListView?
        get() = mSbisListView?.castTo<ContactsListView>()

    @LayoutRes
    override fun getLayoutRes() = R.layout.communicator_fragment_contact_list

    override fun onAttach(context: Context) {
        super.onAttach(context)
        isTablet = DeviceConfigurationUtils.isTablet(context)
    }

    override fun onDetach() {
        super.onDetach()
        hideFab()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        calculateMaxContactsOnScreen()
    }

    override fun onPause() {
        contactsListView?.shouldResetTouchEvents = true
        super.onPause()
    }

    override fun onResume() {
        contactsListView?.shouldResetTouchEvents = false
        super.onResume()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return getFragmentView(inflater).apply {
            initViews(this, savedInstanceState)
            initViewListeners()
            initRouter(this@ContactListFragment)
        }.also { viewLifecycleOwner.lifecycle.addObserver(swipeMenuViewPoolLifecycleManager) }
    }

    private fun getFragmentView(inflater: LayoutInflater): View {
        return CommunicatorFragmentContactListBinding.inflate(inflater).also {
            binding = it
            it.lifecycleOwner = this
        }.root
    }

    override fun initViews(mainView: View, savedInstanceState: Bundle?) {
        super.initViews(mainView, savedInstanceState)
        folderTitleLayout = mainView.findViewById<CurrentFolderView?>(R.id.communicator_contact_list_folder_title_layout).apply {
            setOnClickListener { presenter.onRootFolderSelected() }
        }
        if (isTablet) {
            navigationFab = mainView.findViewById<SbisRoundButton?>(R.id.communicator_contact_navigation_fab).apply {
                setOnClickListener { presenter.onAddContactBtnClick() }
            }
        }
        initTopNavigationView()
        initContactList()
        initBottomPanels()
        initStubViewMediator()
        savedInstanceState?.let(::restoreFromBundle)
    }

    override fun attachFoldersView(view: FoldersView) {
        view.let {
            foldersViewModel.attach(
                host = this,
                foldersView = it,
                actionsListener = presenter,
                dataUpdateListener = { isEmpty ->
                    isEmptyFolderList = isEmpty
                    presenter.setFoldersEnabled(!isEmpty)
                }
            )
            foldersViewModel.isCompact.observe(this.viewLifecycleOwner) { isCompact ->
                if (!isCompact) {
                    presenter.syncFolders()
                    presenter.sendAnalyticOpenedContactsFolders()
                }
            }
        }
    }

    override fun detachFoldersView(view: FoldersView) {
        foldersViewModel.detach(this, view)
    }

    private fun initStubViewMediator() {
        stubViewMediator = foldersViewModel.createStubViewMediator(this, mSbisListView!!.recyclerView)
    }

    private fun initTopNavigationView() {
        binding!!.communicatorSbisToolbar.apply {
            content = SbisTopNavigationContent.LargeTitle(
                PlatformSbisString.Res(RCommunicatorDesign.string.communicator_contacts_title_contacts),
            navxId = NavxId.CONTACTS)
            isVisible = requireArguments().getInt(REGISTRY_MODE).let {
                ContactsRegistryMode.values()[it] == ContactsRegistryMode.REGISTRY
            }
        }
    }

    private fun initContactList() = with(binding!!) {
        mSbisListView = communicatorContactsList

        communicatorAppBar.addOnOffsetChangedListener(
            PinnedHeaderViewHelper(
                pinnedView = folderTitleLayout!!,
                updateListViewTopMargin = { mSbisListView?.updateTopMargin(it) }
            ) { pinnedViewOffset: Int ->
                communicatorHeaderDate.translationY = pinnedViewOffset.toFloat()
            }
        )
        if (isTablet) {
            mSbisListView!!.addItemDecoration(SelectionMarkItemDecoration(requireContext()))
        }

        communicatorLayoutManager = CommunicatorLayoutManager(
            requireContext(),
            component.scrollHelper,
            HOLDER_PROGRESS,
            HOLDER_EMPTY
        )
        autoScroller = LinearAutoScroller(communicatorLayoutManager, AUTO_SCROLL_THRESHOLD, ContactItemMatcher())

        SegmentDividerItemDecoration(
            R.layout.communicator_contact_list_segment_divider_item,
            communicatorLayoutManager,
            object : SegmentDividerItemDecoration.Callback {

                override val isAllItemsVisible: Boolean
                    get() = communicatorLayoutManager.findLastCompletelyVisibleItemPosition() >= mAdapter.content.size

                override fun getSegmentDividerPosition() = mAdapter.segmentDividerPosition

                override fun getTopSeparatorMargin() = resources.getDimension(RCommunicatorDesign.dimen.communicator_contact_list_segment_top_separator_margin_top).toInt()

                override fun onVisibilityChanged(isVisible: Boolean) {
                    val appBarParams = communicatorAppBar.layoutParams as CoordinatorLayout.LayoutParams
                    val behavior = appBarParams.behavior as AppBarLayoutWithDynamicElevationBehavior
                    behavior.setShouldHideElevation(isVisible)
                }
            } ).let { mSbisListView!!.addItemDecoration(it) }

        mSbisListView!!.run {
            setRecyclerViewBackgroundColor(ContextCompat.getColor(context, RDesign.color.palette_color_white1))
            setRecyclerViewVerticalScrollbarEnabled(true)
            setHasFixedSize(true)
            setLayoutManager(communicatorLayoutManager)
            setAdapter(mAdapter)
            recyclerView.itemAnimator = null
            setSwipeColorSchemeResources(RDesign.color.palette_color_orange8)
            setInformationViewPaddingBottom(navigationHeight)
            component.listDateViewUpdater.bind(recyclerView, communicatorHeaderDate)
            val stubViewMediator = foldersViewModel.createStubViewMediator(this@ContactListFragment, recyclerView)
            mAdapter.setStubViewMediator(stubViewMediator)
            initKeyboardHelper(mSbisListView!!, stubViewMediator)
        }

        initFilter()
    }

    private fun initFilter() =
        mSearchPanel.filterClickObservable()
            .subscribe { presenter.onFilterClicked() }
            .storeIn(disposables)

    private fun initBottomPanels() = with(binding!!.communicatorContactCheckPanel) {
        moveInFolderButton = addButtonInCenter(
                getString(RDesign.string.design_mobile_icon_move),
                getString(RCommunicatorDesign.string.communicator_check_panel_move)
        ) { presenter.onMoveCheckedClicked() }

        if (customizationOptions.blackListEnabled) {
            blockButton = addButtonInCenter(
                SbisMobileIcon.Icon.smi_lock.character.toString(),
                getString(RCommunicatorDesign.string.communicator_check_panel_black_list)
            ) { presenter.onBlockCheckedClicked() }
        }

        deleteButton = addButtonInCenter(
                getString(RDesign.string.design_mobile_icon_delete),
                getString(RCommunicatorDesign.string.communicator_check_panel_delete)
        ) { presenter.onDeleteCheckedClicked() }

        setListener { presenter.onCheckModeCancelClicked() }
        setButtonEnabled(moveInFolderButton!!, bottomButtonsEnabled)
        setButtonEnabled(deleteButton!!, bottomButtonsEnabled)
        blockButton?.also { setButtonEnabled(it, bottomButtonsEnabled) }
    }

    override fun initViewListeners() {
        super.initViewListeners()
        mSbisListView!!.setOnRefreshListener {
            if (isNetworkError()) {
                showErrorPopup(RCommunicatorDesign.string.communicator_sync_error_message,
                    SbisMobileIcon.Icon.smi_WiFiNone.character.toString())
            }
            refreshListView()
        }
    }

    private fun refreshListView() {
        closeAllSwipeItems()
        presenter.onRefresh()
    }

    override fun onDestroyView() {
        moveInFolderButton = null
        deleteButton = null
        blockButton = null
        enableFolders()
        enableFilters()
        binding = null
        clearKeyboardHelper()
        super.onDestroyView()
    }

    override fun onDestroy() {
        mAdapter.clear()
        handler.removeCallbacksAndMessages(null)
        disposables.clear()
        importContactsHelper?.onDestroy()
        networkUtils = null
        detachRouter()
        super.onDestroy()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        if (!hidden) {
            setFabVisible(true)
        } else {
            resetUiState()
            clearSearchQuery()
            hideFab()
        }
        mAdapter?.closeFoldersView()
        presenter.onViewVisibilityChanged(hidden)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        with(outState) {
            super.onSaveInstanceState(this)
            putBoolean(BOTTOM_BUTTONS_ENABLED, bottomButtonsEnabled)
            mAdapter?.onSavedInstanceState(this)
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun restoreFromBundle(savedInstanceState: Bundle) {
        with(savedInstanceState) {
            bottomButtonsEnabled = getBoolean(BOTTOM_BUTTONS_ENABLED)
            mAdapter?.onRestoreInstanceState(this)
        }
    }

    override fun showAddContactPane(options: ArrayList<AddContactOption>) {
        val sbisMenu = SbisMenu(
            children = options.mapIndexed { index, option -> getContactOptionItem(option, index) }
        )
        val verticalLocator: AnchorVerticalLocator
        val horizontalLocator: HorizontalLocator
        if (isTablet) {
            val anchor = navigationFab ?: return
            verticalLocator = AnchorVerticalLocator(
                alignment = VerticalAlignment.TOP,
                force = false,
                offsetRes = dimen.context_menu_anchor_margin
            ).apply { anchorView = anchor }
            horizontalLocator = AnchorHorizontalLocator(
                alignment = HorizontalAlignment.CENTER,
                force = false,
                innerPosition = true
            ).apply { anchorView = anchor }
        } else {
            verticalLocator = AnchorVerticalLocator(
                alignment = VerticalAlignment.TOP,
                force = false,
                offsetRes = dimen.context_menu_anchor_margin
            ).apply { anchorView = requireActivity().findViewById(RCommunicatorDesign.id.fab) }
            horizontalLocator = ScreenHorizontalLocator(
                alignment = HorizontalAlignment.RIGHT
            )
        }
        sbisMenu.showMenuWithLocators(
            fragmentManager = childFragmentManager,
            verticalLocator = verticalLocator,
            horizontalLocator = horizontalLocator,
            dimType = DimType.SOLID
        )
    }

    private fun getContactOptionItem(option: AddContactOption, optionIndex: Int) =
        DefaultItem(title = getString(option.itemStringRes)) {
            presenter.onRequestAddContactOptionResult(optionIndex)
        }

    override fun setSearchText(searchText: String?) {
        mSearchPanel.setSearchText(searchText ?: "")
    }

    override fun showSearchPanel() {
        expandSearchInput(binding!!.communicatorAppBar)
        mSearchPanel.visibility = View.VISIBLE
    }

    override fun enableFilters() {
        mSearchPanel?.enableFilters(true)
    }

    override fun disableFilters() {
        mSearchPanel?.enableFilters(false)
    }

    override fun setFilterVisible(isVisible: Boolean) {
        mSearchPanel?.setHasFilter(isVisible)
    }

    override fun showControls() {
        //ignore
    }

    override fun hideControls() = presenter.onKeyboardClosed(true)

    override fun resetUiState() {
        super.resetUiState()
        closeAllSwipeItems()
    }

    override fun setFolderTitle(title: String?) {
        folderTitleLayout?.run {
            visibility = if (title != null) {
                setTitle(title)
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

    override fun setFoldersCompact() {
        foldersViewModel.setFoldersCompact(true)
    }

    override fun showFolderSelection(currentFolder: UUID?) {
        val folderId = when (currentFolder) {
            ROOT_FOLDER_UUID -> ROOT_FOLDER_ID
            else -> UUIDUtils.toString(currentFolder)
        }

        if (isEmptyFolderList) presenter.moveContactToNewFolder()
        foldersViewModel.onFolderSelectionClicked(folderId)
    }

    override fun showFilterSelection(currentConfiguration: ContactFilterConfiguration) {
        val rootView = view ?: return
        if (KeyboardUtils.isKeyboardVisible(rootView)) {
            hideKeyboard()
            rootView.postDelayed({ showFilterSelection(currentConfiguration) }, 100)
            return
        }
        val anchor: View
        val horizontalLocator: HorizontalLocator
        val verticalLocator: AnchorVerticalLocator
        val contactFilterItems = listOf(*ContactSortOrder.values()).map {
            convertContactSortOrderToMenuItem(
                it,
                currentConfiguration.contactSortOrder
            )
        }
        if (isTablet) {
            anchor = mSearchPanel.filter
            horizontalLocator = AnchorHorizontalLocator(
                alignment = HorizontalAlignment.RIGHT,
                force = true,
                innerPosition = true,
                offsetRes = dimen.context_menu_horizontal_margin
            ).apply { anchorView = anchor }
            verticalLocator = AnchorVerticalLocator(
                alignment = VerticalAlignment.BOTTOM,
                force = true,
                offsetRes = dimen.context_menu_anchor_margin
            )
        } else {
            anchor = mSbisListView!!.findViewById<View>(R.id.communicator_contacts_list)
            horizontalLocator = ScreenHorizontalLocator(
                alignment = HorizontalAlignment.RIGHT
            )
            verticalLocator = AnchorVerticalLocator(
                alignment = VerticalAlignment.TOP,
                force = true,
                offsetRes = dimen.context_menu_anchor_margin,
                innerPosition = true
            )
        }

        val sbisMenu = SbisMenu(children = contactFilterItems)
        sbisMenu.showMenuWithLocators(
            fragmentManager = childFragmentManager,
            verticalLocator = verticalLocator.apply { anchorView = anchor },
            horizontalLocator = horizontalLocator,
            dimType = DimType.SOLID
        )
    }

    private fun convertContactSortOrderToMenuItem(item: ContactSortOrder, currentItem: ContactSortOrder) =
        if (item == currentItem) DefaultItem(
            title = getString(item.getTitleRes()),
            state = MenuItemState.ON
        ) { presenter.onContactTypeSelected(item) }
        else DefaultItem(
            title = getString(item.getTitleRes()),
            state = MenuItemState.MIXED
        ) { presenter.onContactTypeSelected(item) }

    override fun changeFilterByType(newSortOrderType: EntitledItem) {
        val filterName = newSortOrderType.getFilterTitleRes()?.let {
            resources.getString(it)
        }
        val default = newSortOrderType == ContactSortOrder.BY_LAST_MESSAGE_DATE
        mSearchPanel.setSelectedFilters(if (filterName != null) listOf(filterName) else emptyList(), default)
    }

    override fun setFabVisible(isVisible: Boolean) {
        if (isHidden) return
        if (isTablet) {
            navigationFab?.isVisible = isVisible
        } else {
            getParentFragmentAs<FabKeeper>()?.setFabClickListener(if (isVisible) presenter::onAddContactBtnClick else null)
                ?: setFabFromBottomBar(isVisible)
        }

    }

    private fun setFabFromBottomBar(isVisible: Boolean) {
        if (isVisible) {
            (activity as? BottomBarProvider)?.setNavigationFabClickListener { presenter.onAddContactBtnClick() }
        } else {
            (activity as? BottomBarProvider)?.setNavigationFabClickListener(null)
        }
    }

    override fun showAddNewContactsDisabledMessage(messageStringId: Int) {
        PopupConfirmation.newMessageInstance(-1, getString(messageStringId))
            .requestPositiveButton(getString(RBaseComponents.string.base_components_dialog_button_ok))
            .show(childFragmentManager, ADD_NEW_CONTACTS_DISABLED_ALERT)
    }

    override fun showBlockContactsDialog(messageStringId: Int, count: Int) {
        val message = resources.getQuantityString(messageStringId, count)
        PopupConfirmation.newSimpleInstance(BLOCK_CONTACTS_REQUEST_CODE)
            .requestTitle(message)
            .requestNegativeButton(getString(RBaseComponents.string.base_components_dialog_button_cancel))
            .requestPositiveButton(getString(RBaseComponents.string.base_components_dialog_button_ok))
            .setEventProcessingRequired(true)
            .show(childFragmentManager, BLOCK_CONTACTS_ALERT)
    }

    override fun showContactDetailsScreen(contactUuid: UUID) = showProfile(contactUuid)

    override fun hideContactDetails() = removeSubContent()

    override fun cancelDismiss(uuid: UUID) = closeAllOpenSwipeMenus()

    override fun closeAllOpenSwipeMenus() = mAdapter.closeAllOpenSwipeMenus()

    override fun closeAllSwipeItems() = mAdapter.closeAllSwipeItems()

    override fun clearSwipeMenuState() {
        mAdapter.clearSwipeMenuState()
    }

    override fun updateEmptyViewState(emptyViewState: EmptyViewState) {
        if (this.emptyViewState != emptyViewState) {
            this.emptyViewState = emptyViewState
            when (emptyViewState) {
                EMPTY   -> binding!!.communicatorContactsList
                    .showInformationViewData(ContactsStubHelperImpl.noContactsStubViewContent())
                DEFAULT -> showProgress()
                else    -> Unit
            }
        }
    }

    override fun showStub(stub: ContactsStubs?) {
        val listView = mSbisListView as ContactsListView
        if (stub == null) {
            hideInformationView()
        } else {
            listView.setInProgress(false)
            val stubActions =
                mapOf(RStubView.string.design_stub_view_no_connection_details_clickable to { presenter.onRefresh() })
            listView.setStub(stub, stubActions)
            listView.updateViewState()
        }
    }

    override fun createEmptyViewContent(messageTextId: Int): Any {
        // заглушки контролируются контроллером
        // необходимо вернуть хоть что-то, чтобы исключить exception в логе
        return StubViewCase.SBIS_ERROR.getContent()
    }

    override fun createEmptyViewContent(messageTextId: Int, detailTextId: Int): Any {
        // заглушки контролируются контроллером
        // необходимо вернуть хоть что-то, чтобы исключить exception в логе
        return StubViewCase.SBIS_ERROR.getContent()
    }

    override fun showCheckMode() = with(binding!!.communicatorContactCheckPanel) {
        mAdapter.showCheckMode()

        if (visibility == View.VISIBLE) return

        val animation = TranslateAnimation(0f, 0f, height.toFloat(), 0f).apply {
            duration = CHECK_PANEL_BUTTONS_ANIMATION_DURATION
            setAnimationListener(object : CommonUtils.SimpleAnimationListener() {
                override fun onAnimationStart(animation: Animation) {
                    visibility = View.VISIBLE
                }
            } )
        }
        startAnimation(animation)
    }

    override fun hideCheckMode() = with(binding!!.communicatorContactCheckPanel) {
        mAdapter.hideCheckMode()

        if (visibility == View.GONE) return

        onCheckStateChanged(false)
        val animation = TranslateAnimation(0f, 0f, 0f, height.toFloat()).apply {
            duration = CHECK_PANEL_BUTTONS_ANIMATION_DURATION
            setAnimationListener(object : CommonUtils.SimpleAnimationListener() {
                override fun onAnimationEnd(animation: Animation) {
                    visibility = View.GONE
                }
            })
        }
        startAnimation(animation)
    }

    override fun enableHeaders(enable: Boolean) = mAdapter.enableHeaders(enable)

    override fun updateDataList(dataList: MutableList<ContactRegistryModel>?, offset: Int) {
        if (offset == 0) autoScroller.onBeforeContentChanged(mAdapter.content)
        checkPhotosToPrefetch(dataList)
        super.updateDataList(dataList, offset)
        if (offset == 0) autoScroller.onAfterContentChanged(mAdapter.content)
    }

    /**
     * Обеспечить загрузку в кэш аватарок, отображение которых ожидается на экране.
     */
    private fun checkPhotosToPrefetch(list: List<ContactRegistryModel>?) {
        if (!shouldPrefetchPhotos || list.isNullOrEmpty()) return
        shouldPrefetchPhotos = false

        val visibleList = if (maxContactsOnScreen < list.size) {
            list.subList(0, maxContactsOnScreen)
        } else {
            list
        }

        val photosData = visibleList.mapNotNull { it.castTo<ConversationModel>()?.participantsCollage }
            .flatten()

        val photoSizePx = resources.getDimensionPixelSize(PhotoSize.M.photoSize)
        ImageBitmapPreFetcher.prefetchPhotos(photosData, photoSizePx)
    }

    override fun notifyItemsInserted(position: Int, count: Int) {
        super.notifyItemsInserted(position, count)
        autoScroller.onContentRangeInserted(position, count)
    }

    override fun notifyContactRemoved(uuid: UUID, position: Int) {
        mAdapter.removeSavedSwipeState(uuid)
        notifyItemsRemoved(position, 1)
    }

    /**
     * Устанавливает в презентер минимально необходимое число контактов для первой порции результатов поискового запроса.
     *
     * Необходимое число контактов вычисляется как (высота экрана / минимальная высота item'а контакта).
     * Минимальная высота item'а равна (высота аватарки + отступы сверху и снизу).
     * Так как в вычислениях не учитывается высота тулбара и статус-бара, то точным округлением можно пренебречь.
     */
    private fun calculateMaxContactsOnScreen() {
        val screenHeight = resources.displayMetrics.heightPixels
        val minContactItemHeight = (
            resources.getDimensionPixelSize(RDesign.dimen.date_header_separator_margin_below) * 2
                + resources.getDimensionPixelSize(RDesignProfile.dimen.design_profile_sbis_person_view_photo_large_size)
        )
        maxContactsOnScreen = screenHeight / minContactItemHeight
        presenter.setContactItemsMaxCountOnScreen(maxContactsOnScreen)
    }

    override fun showProgress() = hideInformationView()

    override fun onSwipeRemoveClicked(contactsModel: ContactsModel) {
        presenter.onDismissOrDeleteContactClick(contactsModel.contact.uuid)
        closeAllOpenSwipeMenus()
    }

    override fun onSwipeMoveToFolderClicked(contactsModel: ContactsModel) {
        presenter.onMoveBySwipeClicked(contactsModel)
        closeAllOpenSwipeMenus()
    }

    override fun onSwipeSendMessageClicked(contactsModel: ContactsModel) {
        hideKeyboard()
        presenter.onSendMessageClicked(contactsModel)
        closeAllOpenSwipeMenus()
    }

    override fun onDismissed(uuid: UUID) = presenter.onDismissOrDeleteContactClick(uuid)

    override fun onDismissedWithoutMessage(uuid: String?) = presenter.onDismissedWithoutMessage(uuid)

    override fun onBackPressed() = presenter.onBackButtonClicked()

    override fun showDefaultLoadingError() =
        showLoadingError(RCommunicatorDesign.string.communicator_contact_list_update_error)

    override fun getToolbarState() = NavigationDrawerContent.ToolbarState.HIDE

    override fun onYes(dialogCode: Int) {
        when (dialogCode) {
            BLOCK_CONTACTS_REQUEST_CODE -> presenter.onBlockContactsGranted()
            DIALOG_CODE_IMPORT_CONTACTS_REQUEST -> importContactsHelper?.requestPermissions(this)
        }
    }

    override fun onYes(requestCode: Int, text: String?) = onYes(requestCode)

    override fun onNo(dialogCode: Int) = Unit

    override fun onItem(dialogCode: Int, which: Int) = onOptionClick(dialogCode, which, null)

    override fun onOptionClick(dialogCode: Int, position: Int, item: Any?) {
        if (dialogCode == DIALOG_CODE_ADD_CONTACT_OPTIONS) {
            presenter.onRequestAddContactOptionResult(position)
        }
    }

    @SuppressLint("CheckResult")
    override fun importContacts() {
        presenter.isCanImportContacts.subscribe(::handleRBResult, ::handleRBError)
    }

    private fun handleRBResult(canImport: Pair<Boolean, Boolean>) {
        val (redButtonProvided, canImportContacts) = canImport
        if (canImportContacts) {
            importContactsHelper?.let { it ->
                it.importContactsSafe(fallback = { requestImportContactsConfirmation() })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        { handleContactsImportStatus(it) },
                        { showErrorPopup(RCommunicatorDesign.string.communicator_contacts_import_failed_error, SbisMobileIcon.Icon.smi_AlertNull.character.toString()) })
                    .storeIn(disposables)
            }
        } else if (redButtonProvided) {
            showErrorPopup(RCommunicatorDesign.string.communicator_import_contacts_with_clicked_red_button, SbisMobileIcon.Icon.smi_AlertNull.character.toString())
        }
    }

    private fun requestImportContactsConfirmation() {
        contactsDependency.importContactsConfirmationFragmentFactory?.createImportContactsConfirmationFragment()?.let {
            if (it is BottomSheetDialogFragment) it.show(childFragmentManager, it::class.java.simpleName)
        }
    }

    override fun contactsImportConfirmed() {
        importContactsHelper?.requestPermissions(this)
    }

    override fun contactsImportDeclined() {
        importContactsHelper?.disableRequestContactPermissions(requireContext())
    }

    private fun handleRBError(error: Throwable) {
        if (error is UnknownHostException || isNetworkError())
            showErrorPopup(RCommunicatorDesign.string.communicator_sync_error_message, SbisMobileIcon.Icon.smi_WiFiNone.character.toString())
        else
            showErrorPopup(RCommunicatorDesign.string.communicator_contacts_import_failed_error, SbisMobileIcon.Icon.smi_AlertNull.character.toString())
    }

    private fun isNetworkError(): Boolean {
        // Если networkUtils == null, то чтобы следующий if не срабатывал присвоим
        // isConnected = true
        val isConnected = networkUtils?.isConnected ?: true
        if (!isConnected) {
            return true
        }
        return false
    }

    override fun showSuccessPopup(stringId: Int, icon: String?) {
        SbisPopupNotification.push(requireContext(), SbisPopupNotificationStyle.SUCCESS,
            resources.getString(stringId), icon)
    }

    override fun showErrorPopup(stringId: Int, icon: String?) {
        SbisPopupNotification.push(requireContext(), SbisPopupNotificationStyle.ERROR,
            resources.getString(stringId), icon)
    }

    override fun showInformationPopup(stringId: Int, icon: String?) {
        SbisPopupNotification.push(requireContext(), SbisPopupNotificationStyle.INFORMATION,
            resources.getString(stringId), icon)
    }

    override fun showPopupWithPlurals(
        stringId: Int,
        icon: String?,
        count: Int,
        typeOfPopup: SbisPopupNotificationStyle?
    ) {
        val message = resources.getQuantityString(stringId, count)
        when (typeOfPopup) {
            SbisPopupNotificationStyle.SUCCESS -> showSuccessPopup(message, icon)
            SbisPopupNotificationStyle.INFORMATION -> showInformationPopup(message, icon)
            SbisPopupNotificationStyle.ERROR -> showErrorPopup(message, icon)
            else -> {}
        }
    }

    private fun showErrorPopup(message: String, icon: String? = null) {
        SbisPopupNotification.push(requireContext(), SbisPopupNotificationStyle.ERROR,
            message, icon)
    }

    private fun showInformationPopup(message: String, icon: String? = null) {
        SbisPopupNotification.push(requireContext(), SbisPopupNotificationStyle.INFORMATION,
            message, icon)
    }

    private fun showSuccessPopup(message: String, icon: String? = null) {
        SbisPopupNotification.push(requireContext(), SbisPopupNotificationStyle.SUCCESS,
            message, icon)
    }

    private fun handleContactsImportStatus(commandStatus: CommandStatus) = with(commandStatus) {
        when (commandStatus.errorCode) {
            ErrorCode.NO_ATTACHED_PHONE -> presenter.onPhoneVerificationRequired()
            else                        -> {
                if (isSuccess) {
                    showSuccessPopup(RCommunicatorDesign.string.communicator_contacts_import_success_text, null)
                }
                else {
                    showErrorPopup(errorMessage, SbisMobileIcon.Icon.smi_AlertNull.character.toString())
                }
                refreshListView()
            }
        }
    }

    override fun showPhoneVerification() {
        showVerificationFragment(R.id.communicator_folders_parent_fragment_id)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        importContactsHelper?.let { it ->
            it.onRequestPermissionsResult(requestCode, grantResults)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { handleContactsImportStatus(it) },
                    { showErrorPopup(RCommunicatorDesign.string.communicator_contacts_import_failed_error, SbisMobileIcon.Icon.smi_AlertNull.character.toString()) }
                ).storeIn(disposables)
        }
        @Suppress("DEPRECATION")
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    @Suppress("DEPRECATION")
    override fun openAddInternalEmployeesScreen(folderUuid: UUID?) =
        startActivityForResult(
                AddInternalEmployeesActivity.provideStartingIntent(
                        requireActivity(),
                        if (!UUIDUtils.equals(ROOT_FOLDER_UUID, folderUuid)) folderUuid else null
                ),
            AddInternalEmployeesActivity.REQUEST_CODE
        )

    @Suppress("DEPRECATION")
    override fun openFindContactsScreen(folderUuid: UUID?) =
        startActivityForResult(
                AddNewContactsActivity.provideStartingIntent(
                        requireActivity(),
                        if (!UUIDUtils.equals(ROOT_FOLDER_UUID, folderUuid)) folderUuid else null
                ),
            AddNewContactsActivity.REQUEST_CODE
        )

    override fun openEmployees() {
        contactsDependency.saveLastSelectedTab(NavxId.STAFF.id)
        if (isTablet) presenter.resetSelection()
        presenter.onCheckModeCancelClicked()
    }

    // Новый механизм вкладок открывает реестр сотрудников раньше, чем срабатывает setFabVisible
    // из-за этого fab остается видимым, чтобы этого избежать используем эту функцию.
    private fun hideFab() {
        if (isTablet) {
            navigationFab?.isVisible = false
        } else {
            getParentFragmentAs<FabKeeper>()?.setFabClickListener(null) ?: setFabFromBottomBar(false)
        }
    }

    override fun showContactInAnotherFolderAlready(errorMessage: String) =
        showErrorPopup(errorMessage, SbisMobileIcon.Icon.smi_AlertNull.character.toString())

    override fun firstCompletelyVisibleItemPosition(): Int =
        communicatorLayoutManager.findFirstCompletelyVisibleItemPosition()

    override fun showChat(personUuid: UUID) =
        startNewConversation(personUuid)

    override fun onCheckStateChanged(hasCheckedContacts: Boolean) {
        with(binding!!.communicatorContactCheckPanel) {
            bottomButtonsEnabled = hasCheckedContacts
            setButtonEnabled(moveInFolderButton!!, bottomButtonsEnabled)
            setButtonEnabled(deleteButton!!, bottomButtonsEnabled)

            val blockButton = blockButton ?: return
            setButtonEnabled(blockButton, bottomButtonsEnabled)
        }
    }

    /**@SelfDocumented */
    @Suppress("unused")
    @Inject
    internal fun setNetworkUtils(util: NetworkUtils) {
        networkUtils = util
    }

    /**@SelfDocumented */
    @Suppress("unused")
    @Inject
    internal fun setAdapter(adapter: ContactListAdapter) {
        mAdapter = adapter.also {
            it.setContactListActionsListener(this)
            it.setContactItemsClickHandler(object : ContactHolder.ContactItemsClickHandler {
                override fun onContactItemClicked(model: ContactsModel) =
                    presenter.onContactItemClicked(model)

                override fun onContactPhotoClicked(model: ContactsModel) =
                    presenter.onContactPhotoClicked(model)

                override fun onContactItemLongClicked(model: ContactsModel) =
                    presenter.onContactItemLongClicked(model)
            })
            it.setFoldersViewHolderHelper(this)
        }
    }

    override fun attachCheckHelper(checkHelper: CheckHelper<ContactRegistryModel>) = checkHelper.attachToAdapter(mAdapter)

    override fun attachSelectionHelper(selectionHelper: SelectionHelper<ContactRegistryModel>) =
        selectionHelper.attachAdapter(mAdapter, DeviceConfigurationUtils.isTablet(requireContext()))

    override fun onNavigationDrawerStateChanged() {
        if (isAdded) {
            presenter.onNavigationDrawerStateChanged()
        }
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                AddInternalEmployeesActivity.REQUEST_CODE -> processSelectionResult(data, AddInternalEmployeesActivity.SELECTION_RESULT_EXTRA_KEY)
                AddNewContactsActivity.REQUEST_CODE -> processSelectionResult(data, AddNewContactsActivity.SELECTION_RESULT_EXTRA_KEY)
            }
        }
    }

    private fun processSelectionResult(data: Intent, extraKey: String) {
        mSbisListView?.let {
            val addContactResult = data.getParcelableExtra<AddContactResult>(extraKey) ?: return
            val resultMessage = addContactResult.message
            if (addContactResult.success) {
                showSuccessPopup(resultMessage)
                presenter.onRefresh()
            } else {
                showErrorPopup(resultMessage, SbisMobileIcon.Icon.smi_AlertNull.character.toString())
            }
        }
    }

    override fun checkShouldRequestContactsPermissions() = shouldRequestContactsPermissions(requireContext())

    override fun getPresenterView(): ContactListContract.View = this

    override fun inject() = component.inject(this)

    override fun createPresenter(): ContactListContract.Presenter = component.presenter

    private val CommandStatus.isSuccess get() = errorCode == ErrorCode.SUCCESS

    private val component: ContactListComponent by lazy {
        DaggerContactListComponent.builder()
            .communicatorCommonComponent(CommunicatorCommonComponent.getInstance(requireContext()))
            .contactsDependency(contactsDependency)
            .isViewHidden(isHidden)
            .build()
    }

    private val isCorporateAccount: Boolean
        get() = contactsDependency
            .loginInterface
            .getCurrentAccount()
            .let { it == null || !it.isPhysic }

    override fun onActiveTabClicked(item: NavigationItem) {
        presenter.onScrollToTopPressed()
    }

    override fun onNewDeeplinkAction(args: DeeplinkAction) {
        when (args) {
            is OpenEntityDeeplinkAction -> showProfile(UUIDUtils.fromString(args.uuid) ?: return)
            is OpenProfileDeeplinkAction -> showProfile(args.profileUuid)
            else -> return
        }
    }
}

private const val TAG = "ContactListFragment"
private const val BOTTOM_BUTTONS_ENABLED = "$TAG.BOTTOM_BUTTONS_ENABLED"
private const val ADD_NEW_CONTACTS_DISABLED_ALERT = "$TAG.ADD_NEW_CONTACTS_DISABLED_ALERT"
private const val BLOCK_CONTACTS_ALERT = "$TAG.BLOCK_CONTACTS_ALERT"

private const val DIALOG_CODE_ADD_CONTACT_OPTIONS = 2
private const val DIALOG_CODE_IMPORT_CONTACTS_REQUEST = 4
private const val BLOCK_CONTACTS_REQUEST_CODE = 5
private const val CONTACTS_TAB_INDEX = 1

private const val CHECK_PANEL_BUTTONS_ANIMATION_DURATION = 200L
private const val AUTO_SCROLL_THRESHOLD = 3
internal const val EXPECTED_VISIBLE_SWIPE_MENU_ITEM_COUNT = 60
internal const val DEFAULT_MAX_CONTACT_ITEMS_ON_SCREEN = 15