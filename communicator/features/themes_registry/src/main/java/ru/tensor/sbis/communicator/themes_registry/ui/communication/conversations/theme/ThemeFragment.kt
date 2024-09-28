package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.theme

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.android_ext_decl.getSerializableUniversally
import ru.tensor.sbis.base_components.autoscroll.AutoScroller
import ru.tensor.sbis.base_components.autoscroll.LinearAutoScroller
import ru.tensor.sbis.common.generated.CommandStatus
import ru.tensor.sbis.common.generated.ErrorCode
import ru.tensor.sbis.common.navigation.NavxId
import ru.tensor.sbis.common.provider.BottomBarProvider
import ru.tensor.sbis.common.util.*
import ru.tensor.sbis.communication_decl.complain.data.ComplainUseCase
import ru.tensor.sbis.communicator.base_folders.ROOT_FOLDER_UUID
import ru.tensor.sbis.communicator.base_folders.keyboard.CommunicatorKeyboardMarginsHelper
import ru.tensor.sbis.communicator.base_folders.keyboard.CommunicatorKeyboardMarginsHelperImpl
import ru.tensor.sbis.communicator.common.conversation_preview.ConversationPreviewMenuAction
import ru.tensor.sbis.communicator.common.data.theme.ConversationModel
import ru.tensor.sbis.communicator.common.data.theme.getNewStubItem
import ru.tensor.sbis.communicator.common.di.CommunicatorCommonComponent
import ru.tensor.sbis.communicator.common.import_contacts.ContactsImportConfirmationListener
import ru.tensor.sbis.communicator.common.import_contacts.ImportContactsHelper
import ru.tensor.sbis.communicator.common.navigation.contract.CommunicatorThemesRouter
import ru.tensor.sbis.communicator.common.themes_registry.CHAT_TYPE_ID
import ru.tensor.sbis.communicator.common.themes_registry.DIALOG_TYPE_ID
import ru.tensor.sbis.communicator.common.themes_registry.ThemesRegistry
import ru.tensor.sbis.communicator.common.ui.hostfragment.contracts.FabKeeper
import ru.tensor.sbis.communicator.common.util.SwipeMenuViewPoolLifecycleManager
import ru.tensor.sbis.communicator.common.util.castTo
import ru.tensor.sbis.communicator.common.util.doIf
import ru.tensor.sbis.communicator.common.util.layout_manager.CommunicatorLayoutManager
import ru.tensor.sbis.communicator.common.util.share.ConversationUtils
import ru.tensor.sbis.communicator.common.util.share.ThemeShareSelectionResultListener
import ru.tensor.sbis.communicator.common.view.SegmentDividerItemDecoration
import ru.tensor.sbis.communicator.core.firebase_metrics.MetricsDispatcher
import ru.tensor.sbis.communicator.core.firebase_metrics.MetricsType.FIREBASE_SHOW_FOLDER
import ru.tensor.sbis.communicator.core.views.conversation_views.utils.CommunicatorTheme
import ru.tensor.sbis.communicator.core.views.conversation_views.utils.CommunicatorUtils
import ru.tensor.sbis.communicator.core.views.safeUpdate
import ru.tensor.sbis.communicator.declaration.MasterFragment
import ru.tensor.sbis.communicator.declaration.model.ChatType
import ru.tensor.sbis.communicator.declaration.model.CommunicatorRegistryType
import ru.tensor.sbis.communicator.declaration.model.CommunicatorRegistryType.ChatsRegistry
import ru.tensor.sbis.communicator.declaration.model.CommunicatorRegistryType.DialogsRegistry
import ru.tensor.sbis.communicator.declaration.model.CommunicatorRegistryType.ThemeRegistryType
import ru.tensor.sbis.communicator.declaration.model.DialogType
import ru.tensor.sbis.communicator.declaration.model.EntitledItem
import ru.tensor.sbis.communicator.declaration.theme.ThemesRegistryFragmentFactory
import ru.tensor.sbis.communicator.sbis_conversation.CommunicatorSbisConversationPlugin
import ru.tensor.sbis.communicator.themes_registry.R
import ru.tensor.sbis.communicator.themes_registry.ThemesRegistryFacade.themesRegistryDependency
import ru.tensor.sbis.communicator.themes_registry.ThemesRegistryPlugin.customizationOptions
import ru.tensor.sbis.communicator.themes_registry.ThemesRegistryPlugin.dependency
import ru.tensor.sbis.communicator.themes_registry.di.factories.DialogFoldersViewModelFactory
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.ConversationListAdapter
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.ConversationListView
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.FoldersViewHolderHelper
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.di.ConversationListComponent
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.di.DaggerConversationListComponent
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.diffutil.ThemeItemMatcher
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.theme.contract.ThemePresenter
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.theme.contract.ThemeView
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.theme.delegates.stubs.Stubs
import ru.tensor.sbis.communicator.themes_registry.ui.communication.filters.ConversationFilterConfiguration
import ru.tensor.sbis.deeplink.DeeplinkAction
import ru.tensor.sbis.deeplink.HandlePushNotificationDeeplinkAction
import ru.tensor.sbis.deeplink.OpenConversationDeeplinkAction
import ru.tensor.sbis.deeplink.OpenNewsDeepLinkAction
import ru.tensor.sbis.deeplink.OpenProfileDeeplinkAction
import ru.tensor.sbis.deeplink.OpenWebViewDeeplinkAction
import ru.tensor.sbis.deeplink.ShareToMessagesDeeplinkAction
import ru.tensor.sbis.deeplink.SwitchThemeTabDeeplinkAction
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.breadcrumbs.CurrentFolderView
import ru.tensor.sbis.design.confirmation_dialog.ConfirmationDialog
import ru.tensor.sbis.design.container.DimType
import ru.tensor.sbis.design.container.locator.AnchorHorizontalLocator
import ru.tensor.sbis.design.container.locator.AnchorVerticalLocator
import ru.tensor.sbis.design.container.locator.HorizontalAlignment
import ru.tensor.sbis.design.container.locator.HorizontalLocator
import ru.tensor.sbis.design.container.locator.ScreenHorizontalLocator
import ru.tensor.sbis.design.container.locator.VerticalAlignment
import ru.tensor.sbis.design.context_menu.MenuItem
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
import ru.tensor.sbis.design.list_header.HeaderDateView
import ru.tensor.sbis.design.list_utils.decoration.SelectionMarkItemDecoration
import ru.tensor.sbis.design.navigation.util.ActiveTabOnClickListener
import ru.tensor.sbis.design.navigation.util.NavigationDrawerStateListener
import ru.tensor.sbis.design.navigation.view.model.NavigationItem
import ru.tensor.sbis.design.person_suggest.input.PersonInputLayout
import ru.tensor.sbis.design.person_suggest.input.contract.PersonInputLayoutListener
import ru.tensor.sbis.design.person_suggest.service.PersonSuggestData
import ru.tensor.sbis.design.person_suggest.suggest.PersonSuggestView
import ru.tensor.sbis.design.profile.util.ImageBitmapPreFetcher
import ru.tensor.sbis.design.profile_decl.person.PhotoSize
import ru.tensor.sbis.design.stubview.StubViewCase
import ru.tensor.sbis.design.tabs.api.SbisTabViewItemContent
import ru.tensor.sbis.design.tabs.api.SbisTabsViewItem
import ru.tensor.sbis.design.tabs.view.SbisTabsView
import ru.tensor.sbis.design.theme.HorizontalPosition
import ru.tensor.sbis.design.theme.res.PlatformSbisString
import ru.tensor.sbis.design.toolbar.SbisButtonToolbarView
import ru.tensor.sbis.design.topNavigation.api.SbisTopNavigationContent
import ru.tensor.sbis.design.topNavigation.api.SbisTopNavigationSyncState
import ru.tensor.sbis.design.topNavigation.view.SbisTopNavigationView
import ru.tensor.sbis.design.utils.DebounceActionHandler
import ru.tensor.sbis.design.utils.KeyboardUtils
import ru.tensor.sbis.design.utils.PinnedHeaderViewHelper
import ru.tensor.sbis.design.utils.extentions.updateTopMargin
import ru.tensor.sbis.design.utils.getThemeColorInt
import ru.tensor.sbis.design.view.input.searchinput.util.AppBarLayoutWithDynamicElevationBehavior
import ru.tensor.sbis.design.view.input.searchinput.util.expandSearchInput
import ru.tensor.sbis.design_notification.SbisPopupNotification
import ru.tensor.sbis.design_notification.popup.SbisPopupNotificationStyle
import ru.tensor.sbis.design_notification.snackbar.hideImmediately
import ru.tensor.sbis.modalwindows.dialogalert.PopupConfirmation
import ru.tensor.sbis.mvp.layoutmanager.PaginationLayoutManager
import ru.tensor.sbis.mvp.search.BaseSearchableView
import ru.tensor.sbis.persons.ConversationRegistryItem
import ru.tensor.sbis.toolbox_decl.navigation.NavxIdDecl
import java.util.LinkedList
import java.util.UUID
import javax.inject.Inject
import ru.tensor.sbis.common.R as RCommon
import ru.tensor.sbis.communicator.design.R as RCommunicatorDesign
import ru.tensor.sbis.design.R as RDesign
import ru.tensor.sbis.design.profile.R as RDesignProfile
import ru.tensor.sbis.design.stubview.R as RStubView
import ru.tensor.sbis.message_panel.R as RMessagePanel
import ru.tensor.sbis.modalwindows.R as RModalWindows

/**
 * Фрагмент для отображения реестра диалогов/чатов
 *
 * @author rv.krohalev
 */
internal class ThemeFragment :
    BaseSearchableView<ConversationRegistryItem, ConversationListAdapter, ThemeView, ThemePresenter>(),
    ThemeView,
    NavigationDrawerStateListener,
    PopupConfirmation.DialogYesNoWithTextListener,
    PopupConfirmation.DialogCancelListener,
    PopupConfirmation.DialogItemClickListener,
    ContactsImportConfirmationListener,
    FoldersViewHolderHelper,
    MasterFragment,
    CommunicatorThemesRouter by themesRegistryDependency.getCommunicatorThemesRouter(),
    ThemesRegistry,
    ActiveTabOnClickListener {

    companion object : ThemesRegistryFragmentFactory {

        override fun createThemeFragment(type: CommunicatorRegistryType?): Fragment =
            if (type is ChatsRegistry) newChatInstance(type.chatType) else newDialogInstance((type as? DialogsRegistry)?.dialogType)

        override fun createShareThemeFragment(type: ThemeRegistryType): Fragment =
            if (type is ChatsRegistry) {
                newChatInstance(ChatType.ALL, true)
            } else {
                newDialogInstance(DialogType.ALL, true)
            }

        /**
         * Instance реестра диалогов.
         * @param dialogType       фильтр реестра.
         * @param isSharingMode    режим отображения реестра.
         * @return [ThemeFragment] фрагмент реестра диалогов.
         */
        @JvmStatic
        fun newDialogInstance(dialogType: DialogType? = null, isSharingMode: Boolean = false) = ThemeFragment().withArgs {
            putSerializable(INIT_AS_CHAT_KEY, false)
            dialogType?.let { putSerializable(DIALOG_TYPE_ID, it) }
            putSerializable(INIT_AS_SHARING_MODE, isSharingMode)
        }

        /**
         * Instance реестра чатов.
         * @param chatType         фильтр реестра.
         * @param isSharingMode    режим отображения реестра.
         * @return [ThemeFragment] фрагмент реестра чатов.
         */
        @JvmStatic
        fun newChatInstance(
            chatType: ChatType? = null,
            isSharingMode: Boolean = false
        ) = ThemeFragment().withArgs {
            putSerializable(INIT_AS_CHAT_KEY, true)
            chatType?.let { putSerializable(CHAT_TYPE_ID, it) }
            putBoolean(INIT_AS_SHARING_MODE, isSharingMode)
        }
    }

    private var binding: IWouldLikeToBeADataBinding? = null
    private var personSuggestView: PersonSuggestView? = null
    private val conversationListComponent: ConversationListComponent by lazy(::createComponent)
    private var keyboardHelper: CommunicatorKeyboardMarginsHelper = CommunicatorKeyboardMarginsHelperImpl()

    private var needToStoreSwipe: Boolean = false

    private val importContactsHelper: ImportContactsHelper?
        get() = themesRegistryDependency.importContactsHelperProvider?.importContactsHelper

    private lateinit var autoScroller: AutoScroller
    private var disposables: CompositeDisposable = CompositeDisposable()

    override val adapter: ConversationListAdapter
        get() = mAdapter!!

    // Максимальное количество диалогов на экране. Значение будет вычислено в [onCreate]
    private var maxDialogsOnScreen = 15
    private var shouldPrefetchPhotos = false

    private var snackbar: Snackbar? = null

    private var newSelectedModel: ConversationModel? = null
    private var resetTypeIfUnanswered: Boolean = false

    private var isEmptyFolderList = true
    private var isFirstSetDataList = true
    private var isConversationPoolInitialized = false

    private val navigationHeight by lazy {
        resources.getDimensionPixelSize(RDesign.dimen.tab_navigation_menu_horizontal_height)
    }

    private var isRouterInitialized: Boolean = false
    private var postponedDeeplinkAction: DeeplinkAction? = null

    private var isUnreadCountersInitialized: Boolean = false

    private val additionalKeyboardHeightForContactsSearchPanel by lazy {
        if (isTablet && !resources.getBoolean(RCommon.bool.is_landscape)) {
            navigationHeight
        } else {
            0
        }
    }
    private val isSharingMode
        get() = requireArguments().getBoolean(INIT_AS_SHARING_MODE)

    @Inject
    internal lateinit var swipeMenuViewPoolLifecycleManager: SwipeMenuViewPoolLifecycleManager

    @Inject
    internal lateinit var foldersViewModelFactory: DialogFoldersViewModelFactory

    private val foldersViewModel by lazy {
        ViewModelProvider(this, foldersViewModelFactory)[FoldersViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter // запуск инициализации
        foldersViewModel // запуск инициализации
        swipeMenuViewPoolLifecycleManager.onCreate()
        CommunicatorUtils.setupDensities(requireContext())
        CommunicatorTheme.updateThemeResources(requireContext())

        if (newSelectedModel != null) {
            presenter.selectItem(newSelectedModel!!)
            newSelectedModel = null
        }

        if (resetTypeIfUnanswered) {
            resetTypeIfUnanswered = false
            presenter.resetTypeIfUnanswered()
        }

        if (!isSharingMode) calculateMaxDialogsOnScreen()
        if (presenter.isImportContactsAllowed()) presenter.subscribeOnProfileSettingsDataRefreshed()
        initCommunicatorRouter(this)

        isRouterInitialized = true
        postponedDeeplinkAction?.also(::onNewDeeplinkAction)
        postponedDeeplinkAction = null

        initAdapter()
    }

    private fun initAdapter() {
        mAdapter = conversationListComponent.conversationListAdapter
        adapter.run {
            setItemClickHandler(presenter)
            setDialogListActionsListener(presenter)
            setChatListActionsListener(presenter)
            setAttachmentClickListener(presenter)
            setOnContactClickListener(presenter)
            setOnContactPhotoClickListener(presenter)
            setFoldersViewHolderHelper(this@ThemeFragment)
        }
        val context = requireContext()
        lifecycleScope.launch(Dispatchers.Default) {
            try {
                adapter.warmUpDex(context)
            } catch(ex: Exception) {
                // 100% выбросит ошибку, но асинхронщина нужна
                // для иницализации классов всех биндеров приложения на фоновом потоке
            }
        }
    }

    override fun onPause() {
        super.onPause()
        mSbisListView?.castTo<ConversationListView>()?.shouldResetTouchEvents = true
    }

    override fun onResume() {
        super.onResume()
        mSbisListView?.castTo<ConversationListView>()?.shouldResetTouchEvents = false
        initConversationViewPool()
    }

    /**
     * Устанавливает в презентер минимально необходимое число диалогов для первой порции результатов поискового запроса.
     *
     * Необходимое число диалогов вычисляется как (высота экрана / минимальная высота item'а диалога).
     * Минимальная высота item'а равна (высота аватарки + отступы сверху и снизу).
     * Так как в вычислениях не учитывается высота тулбара и статус-бара, то точным округлением можно пренебречь.
     */
    private fun calculateMaxDialogsOnScreen() {
        val screenHeight = resources.displayMetrics.heightPixels
        val minDialogItemHeight = (
            resources.getDimensionPixelSize(RDesign.dimen.date_header_separator_margin_top)
                + resources.getDimensionPixelSize(RDesignProfile.dimen.design_profile_sbis_person_view_photo_large_size)
                + resources.getDimensionPixelSize(RCommunicatorDesign.dimen.communicator_dialog_item_separator_margin_bottom)
            )
        maxDialogsOnScreen = screenHeight / minDialogItemHeight
        presenter.setDialogItemsMinCount(maxDialogsOnScreen)
    }

    /** @SelfDocumented */
    @SuppressLint("ClickableViewAccessibility")
    override fun initViews(mainView: View, savedInstanceState: Bundle?) {
        super.initViews(mainView, savedInstanceState)

        binding = IWouldLikeToBeADataBinding(
            mainView,
            arguments?.getBoolean(INIT_AS_CHAT_KEY),
            isSharingMode
        ) { presenter }

        binding!!.bottomCheckActionsPanel?.setListener { presenter.onCheckModeCancelClicked() }

        val listView: ConversationListView = mainView.findViewById(R.id.communicator_dialog_list_list_view)
        val layoutManager = CommunicatorLayoutManager(
            requireContext(),
            conversationListComponent.scrollHelper,
        )
        autoScroller = LinearAutoScroller(layoutManager, AUTO_SCROLL_THRESHOLD, ThemeItemMatcher())
        mSbisListView = listView.tune(layoutManager, !isSharingMode)

        if (!isSharingMode) addItemDecorationForSbisListView(layoutManager)

        with(binding!!) {
            appBarLayout.addOnOffsetChangedListener(
                PinnedHeaderViewHelper(
                    pinnedView = binding!!.folderTitleLayout,
                    updateListViewTopMargin = { mSbisListView?.updateTopMargin(it) }
                ) { pinnedViewOffset: Int ->
                    if (isSharingMode && headerDate.isVisible) {
                        headerDate.isVisible = false
                    }
                    headerDate.translationY = pinnedViewOffset.toFloat()
                }
            )

            updateToolbarByAvailabilityChanges(presenter.isChannelTab())
            presenter.collectAvailableTabs()

            folderTitleLayout.setOnClickListener { presenter.onRootFolderSelected() }
        }

        if (isSharingMode) {
            mSearchPanel.setHasFilter(false)
        }
        initFilter()
        initPersonSuggestView()

        savedInstanceState?.let {
            needToStoreSwipe = true
            restoreFromBundle(it)
        }
    }

    private fun ConversationListView.tune(layoutManager: CommunicatorLayoutManager, isDefaultMode: Boolean = true): ConversationListView {
        setRecyclerViewBackgroundColor(context.getThemeColorInt(com.google.android.material.R.attr.backgroundColor))
        setHasFixedSize(true)
        setLayoutManager(layoutManager)
        setAdapter(adapter)
        maxDialogsOnScreen

        disableOnChangeAnimation()
        recyclerView.itemAnimator?.apply {
            addDuration = 0
            changeDuration = 0
            moveDuration = 0
            removeDuration = 0
        }
        reloadCurrentPage = {
            presenter.onRefresh()
        }

        val stubViewMediator: StubViewMediator?
        if (isDefaultMode) {
            stubViewMediator = foldersViewModel.createStubViewMediator(this@ThemeFragment, recyclerView)
            adapter.setStubViewMediator(stubViewMediator)
        } else {
            stubViewMediator = null
        }
        conversationListComponent.listDateViewUpdater.bind(recyclerView, binding!!.headerDate)
        keyboardHelper.initKeyboardHelper(this, stubViewMediator)

        onStubClick = { presenter.onRefresh() }

        setInformationViewPaddingBottom(navigationHeight)
        return this
    }

    /**
     * Инициализация лисенеров на View компонентах
     */
    override fun initViewListeners() {
        super.initViewListeners()
        binding!!.personInputLayout?.listener = object : PersonInputLayoutListener {
            override fun onPersonClick(personUuid: UUID) {
                presenter.onPersonFilterViewClicked(personUuid)
            }

            override fun onCancelPersonFilterClick() {
                presenter.onSearchClearButtonClicked()
            }
        }
    }

    private fun initFilter() {
        mSearchPanel.filterClickObservable().subscribe {
            DebounceActionHandler.INSTANCE.handle { presenter.onFilterClick() }
        }.storeIn(disposables)
    }

    private fun initPersonSuggestView() {
        personSuggestView =
            if (!isTablet) {
                binding!!.personSuggestView
            } else {
                // для планшета ContactsSearchPanel вручную добавляется
                // в layout MainActivity для отображения на весь экран и удаляется после ухода из реестра
                PersonSuggestView(requireContext()).also { view ->
                    view.id = R.id.communicator_person_suggest_view
                    view.isVisible = false
                    val lp = FrameLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
                        .apply { gravity = Gravity.BOTTOM }
                    requireActivity().addContentView(view, lp)
                }
            }?.apply {
                init() {
                    onKeyboardCloseMeasure(0)
                    changeContactsSearchPanelVisibility(false)
                    presenter.onPersonSuggestClick(it)
                }
            }
    }

    override fun attachFoldersView(view: FoldersView) {
        presenter.trySetFoldersSync()
        view.let {
            foldersViewModel.attach(
                host = this,
                foldersView = it,
                actionsListener = presenter,
                dataUpdateListener = { isEmpty ->
                    val isFoldersEnabled = !isEmpty && !isSharingMode
                    isEmptyFolderList = isEmpty
                    presenter.setFoldersEnabled(isFoldersEnabled)
                }
            )
            foldersViewModel.isCompact.observe(this.viewLifecycleOwner) { isCompact ->
                if (!isCompact) { presenter.sendAnalyticOpenedDialogsFolders() }
            }
        }
    }

    override fun detachFoldersView(view: FoldersView) {
        foldersViewModel.detach(this, view)
    }

    private fun addItemDecorationForSbisListView(layoutManager: PaginationLayoutManager) {
        mSbisListView!!.run {
            if (isTablet) {
                addItemDecoration(SelectionMarkItemDecoration(context))
            }
            addItemDecoration(DialogSectionsDivider())
        }

        SegmentDividerItemDecoration(
            R.layout.communicator_dialog_list_segment_divider_item,
            layoutManager,
            object : SegmentDividerItemDecoration.Callback {

                override val isAllItemsVisible: Boolean
                    get() = layoutManager.findLastCompletelyVisibleItemPosition() >= adapter.content.size

                override fun getSegmentDividerPosition() =
                    if (presenter.getContactListSize() == 0) Integer.MAX_VALUE else adapter.lastContactPositionBeforeDialogs()

                override fun getTopSeparatorMargin(): Int {
                    val segmentDividerView = layoutManager.findViewByPosition(getSegmentDividerPosition())
                    return if (segmentDividerView != null) {
                        segmentDividerView.height + resources.getDimensionPixelSize(RCommunicatorDesign.dimen.communicator_dialog_item_content_container_separator_height)
                    } else {
                        0
                    }
                }

                override fun onVisibilityChanged(isVisible: Boolean) {
                    val appBarParams = binding!!.appBarLayout.layoutParams as CoordinatorLayout.LayoutParams
                    val behavior = appBarParams.behavior as AppBarLayoutWithDynamicElevationBehavior
                    behavior.setShouldHideElevation(isVisible)
                }
            }
        ).let { mSbisListView!!.addItemDecoration(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (presenter.isImportContactsAllowed()) presenter.tryAutoImportContacts(savedInstanceState)
        requireActivity().supportFragmentManager.setFragmentResultListener(CONVERSATION_PREVIEW_RESULT, this) { _, result ->
            presenter.handleConversationPreviewAction(
                result.getSerializableUniversally<ConversationPreviewMenuAction.ThemeConversationPreviewMenuAction>(CONVERSATION_PREVIEW_RESULT)!!
            )
        }
    }

    private fun requestImportContactsConfirmation() {
        themesRegistryDependency.importContactsConfirmationFragmentFactory?.createImportContactsConfirmationFragment()?.let {
            if (it is BottomSheetDialogFragment) it.show(childFragmentManager, it::class.java.simpleName)
        }
    }

    override fun contactsImportConfirmed() {
        importContactsHelper?.requestPermissions(this)
    }

    override fun contactsImportDeclined() {
        importContactsHelper?.disableRequestContactPermissions(requireContext())
    }

    override fun onSaveInstanceState(outState: Bundle) {
        with(outState) {
            super.onSaveInstanceState(this)
            adapter.onSavedInstanceState(this)
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun restoreFromBundle(savedInstanceState: Bundle) {
        with(savedInstanceState) {
            adapter.onRestoreInstanceState(this)
        }
    }

    override fun setContentToAdapter(list: MutableList<ConversationRegistryItem>) {
        adapter.setContent(list)
    }

    override fun hideSwipePanel(uuid: UUID?) {
        adapter.closeSwipePanel(uuid)
    }

    override fun setFolderTitle(title: String?) {
        binding?.run {
            folderTitleLayout.setTitle(title ?: "")
            folderTitleLayout.visibility = if (title != null) View.VISIBLE else View.GONE
            MetricsDispatcher.stopTrace(FIREBASE_SHOW_FOLDER)
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

        if (isEmptyFolderList) presenter.moveDialogToNewFolder()
        foldersViewModel.onFolderSelectionClicked(folderId)
    }

    /**
     * Установить доступность фильтров
     */
    override fun enableFilters() {
        mSearchPanel?.enableFilters(true)
    }

    /**
     * Установить недоступность папок
     */
    override fun disableFilters() {
        mSearchPanel?.enableFilters(false)
    }

    override fun onCheckStateChanged(hasCheckedDialogs: Boolean, canReadDialogs: Boolean, canUnreadDialogs: Boolean) {
        binding?.configureMassButtonsPanel(
            hasCheckedDialogs = hasCheckedDialogs,
            canReadDialogs = canReadDialogs,
            canUnreadDialogs = canUnreadDialogs
        )
    }

    override fun setUnreadAndUnviewedCounters(
        unreadChats: Int?,
        unreadDialogs: Int?,
        unviewedChats: Int?,
        unviewedDialogs: Int?
    ) {
        if (customizationOptions.splittingChannelsAndDialogsEnabled) return
        val binding = binding ?: return

        unreadChats?.let(binding.unreadChatsCounter::tryEmit)
        unreadDialogs?.let(binding.unreadDialogsCounter::tryEmit)
        unviewedChats?.let(binding.unviewedChatsCounter::tryEmit)
        unviewedDialogs?.let(binding.unviewedDialogsCounter::tryEmit)
        afterCountersChanged()
    }

    /**
     * Костыль до выполнения фикса со стороны платформы
     * https://online.sbis.ru/opendoc.html?guid=fc2e5cd6-df0d-4d4c-9d25-94688d04f51c&client=3
     * Единственный быстрый способ избавиться от запоздалого смаргивания счетчиков при открытии раздела.
     */
    private fun afterCountersChanged() {
        if (!isUnreadCountersInitialized) {
            resetTabs()
            isUnreadCountersInitialized = true
        }
    }

    private fun resetTabs() {
        val tabsView = binding?.sbisToolbar?.tabsView ?: return
        val currentTabs = tabsView.tabs
        tabsView.tabs = LinkedList()
        tabsView.tabs = currentTabs
        tabsView.selectedTabIndex = binding!!.getSelectedTabIndex(presenter.isChannelTab())
    }

    override fun changeThemesRegistry(registryType: ThemeRegistryType) {
        presenter.onBranchTypeTabClick(registryType.registryTypeToTabId())
        if (registryType is ChatsRegistry) adapter.closeFoldersView()
        if (customizationOptions.appHasChatNavigationMenuItem) {
            changeNavigationSelectedItem(if (presenter.isChannelTab()) ChatsRegistry() else DialogsRegistry())
        }
    }

    private fun ThemeRegistryType.registryTypeToTabId(): NavxId =
        if (this is DialogsRegistry) NavxId.DIALOGS
        else NavxId.CHATS

    override fun changeFilterByType(itemToDisplay: EntitledItem, displayFilterName: Boolean) {
        val filterName: String?
        val selectedFilters = ArrayList<String>()
        if (displayFilterName) {
            filterName = requireContext().getString(itemToDisplay.getTitleRes())
            selectedFilters.add(filterName)
        }
        mSearchPanel.setSelectedFilters(selectedFilters)
    }

    override fun setCheckMode(show: Boolean) {
        setBottomActionsPanelVisibility(show)
        if (show) adapter.showCheckMode()
        else adapter.hideCheckMode()
    }

    override fun showFilterSelection(
        initialConfiguration: ConversationFilterConfiguration,
        currentConfiguration: ConversationFilterConfiguration
    ) {
        val rootView = view ?: return
        if (KeyboardUtils.isKeyboardVisible(rootView)) {
            hideKeyboard()
            rootView.postDelayed({ showFilterSelection(initialConfiguration, currentConfiguration) }, 100)
            return
        }
        val anchor = mSearchPanel.filter
        val themeItems =
            if (presenter.isChannelTab()) {
                listOf(*ChatType.values()).map { convertThemeTypesToMenuItem(it, currentConfiguration.chatType) }
            } else {
                listOf(*DialogType.values()).map { convertThemeTypesToMenuItem(it, currentConfiguration.dialogType) }
            }
        val horizontalLocator: HorizontalLocator

        if (isTablet) {
            horizontalLocator = AnchorHorizontalLocator(
                alignment = HorizontalAlignment.RIGHT,
                force = true,
                innerPosition = true,
                offsetRes = dimen.context_menu_horizontal_margin
            ).apply { anchorView = anchor }
        } else {
            horizontalLocator = ScreenHorizontalLocator(
                alignment = HorizontalAlignment.RIGHT
            )
        }

        val sbisMenu = SbisMenu(children = themeItems)
        sbisMenu.showMenuWithLocators(
            fragmentManager = childFragmentManager,
            verticalLocator = AnchorVerticalLocator(
                alignment = VerticalAlignment.BOTTOM,
                force = true,
                offsetRes = dimen.context_menu_anchor_margin
            ).apply { anchorView = anchor },
            horizontalLocator = horizontalLocator,
            dimType = DimType.SOLID
        )
    }

    /**
     * Преобразование DialogType и ChatType в модель необходмиую для отображения в SbisMenu
     */
    private fun convertThemeTypesToMenuItem(item: EntitledItem, currentItem: EntitledItem) =
        if (item == currentItem) MenuItem(
            title = getString(item.getTitleRes()),
            state = MenuItemState.ON
        ) { presenter.onThemeTypeSelected(item) }
        else MenuItem(
            title = getString(item.getTitleRes()),
            state = MenuItemState.MIXED
        ) { presenter.onThemeTypeSelected(item) }

    override fun showHideChatConfirmation() {
        PopupConfirmation.newSimpleInstance(DIALOG_CODE_CONFIRM_HIDE_CHAT).also {
            it.requestTitle(getString(RCommunicatorDesign.string.communicator_theme_remove_channel_alert_title))
            it.requestPositiveButton(getString(RCommon.string.dialog_button_ok))
            it.requestNegativeButton(getString(RDesign.string.design_undo))
            it.setEventProcessingRequired(true)
        }.show(childFragmentManager, PopupConfirmation::class.simpleName)
    }

    override fun showGroupInvitedDialog(comment: String) {
        ConfirmationDialog.OkDialog(
            message = null,
            comment = comment
        ) { container, _ ->
            container.closeContainer()
        }.show(childFragmentManager)
    }

    override fun setFabVisible(visible: Boolean) {
        if (isHidden) return
        getParentFragmentAs<FabKeeper>()?.setFabClickListener(if (visible) presenter::onNewDialogClick else null)
            ?: setFabFromBottomBar(visible)
    }

    private fun setFabFromBottomBar(isVisible: Boolean) {
        if (isVisible) {
            (activity as? BottomBarProvider)?.setNavigationFabClickListener { presenter.onNewDialogClick() }
        } else {
            (activity as? BottomBarProvider)?.setNavigationFabClickListener(null)
        }
    }

    override fun setSelectedPersonToFilter(person: PersonSuggestData) {
        binding!!.personInputLayout?.personFilter = person
    }

    override fun updateListItemsLayoutRules(showHeaderDate: Boolean, showItemsCollages: Boolean) {
        // скрывать sticky-header, если в реестре диалогов при поиске отображаются и диалоги, и контакты
        binding!!.headerDate.alpha = if (showHeaderDate) 1f else 0f
        adapter.changeItemsCollagesVisibility(showItemsCollages)
    }

    override fun showSyncIndicator() {
        binding?.sbisToolbar?.syncState = SbisTopNavigationSyncState.Running
    }

    override fun hideSyncIndicator() {
        binding?.sbisToolbar?.syncState = SbisTopNavigationSyncState.NotRunning
    }

    override fun showNetworkWaitingIndicator(show: Boolean) = when {
        show -> {
            binding?.sbisToolbar?.syncState = SbisTopNavigationSyncState.NoInternet
        }
        else -> {
            binding?.sbisToolbar?.syncState = SbisTopNavigationSyncState.NotRunning
        }
    }

    override fun showSyncErrorNotification(isNetworkError: Boolean) {
        val message = resources.getString(
            if (isNetworkError) RCommunicatorDesign.string.communicator_sync_error_message
            else RCommon.string.common_service_error
        )
        val icon = SbisMobileIcon.Icon.smi_WiFiNone.character.toString()
        SbisPopupNotification.push(requireContext(), SbisPopupNotificationStyle.ERROR, message, icon)
    }

    override fun showShareToNotAvailableChannelPopup() {
        val message = resources.getString(RCommunicatorDesign.string.communicator_share_to_not_available_channel)
        val icon = SbisMobileIcon.Icon.smi_information.character.toString()
        SbisPopupNotification.push(SbisPopupNotificationStyle.ERROR, message, icon)
    }

    /** @SelfDocumented */
    override fun showOlderLoadingProgress(show: Boolean) {
        // будем показывать индикатор загрузки по сигналу из контроллера через метод showPagingLoadingProgress.
    }

    override fun showPagingLoadingProgress(show: Boolean) {
        adapter.showPagingLoadingProgress(show)
    }

    override fun showPagingLoadingError() {
        adapter.showPagingLoadingError()
    }

    override fun resetPagingLoadingIndicator() {
        adapter.resetPagingLoadingIndicator()
    }

    /** @SelfDocumented */
    override fun showLoadingError(errorTextResId: Int) {
        if (errorTextResId != -1) {
            super.showLoadingError(errorTextResId)
        }
    }

    override fun showStub(stub: Stubs?) {
        val listView = mSbisListView as ConversationListView
        if (stub == null) {
            hideInformationView()
        } else {
            showPagingLoadingProgress(false)
            listView.setInProgress(false)
            val stubActions = mapOf(RStubView.string.design_stub_view_no_connection_details_clickable to { presenter.onRefresh() })
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

    /** @SelfDocumented */
    override fun showSearchPanel() {
        expandSearchInput(binding!!.appBarLayout)
    }

    override fun clearFocusFromSearchPanel() {
        mSearchPanel.clearFocus()
    }

    /**
     * Очистка поискового запроса
     */
    override fun clearSearchQuery() {
        clearSearchQuery(true)
    }

    override fun clearSearchQuery(makeSearchRequest: Boolean) {
        binding!!.personInputLayout?.clearPersonFilter()
        presenter.onCancelSearchClick(makeSearchRequest)
        super.clearSearchQuery()
    }

    private fun hideDismissalUndoSnackbar() {
        snackbar?.hideImmediately()
    }

    override fun inject() {
        conversationListComponent.inject(this)
        presenter.initRouter(this)
    }

    /**
     * @return layout ресурс реестра
     */
    override fun getLayoutRes(): Int =
        R.layout.communicator_fragment_conversation_list

    override fun createPresenter(): ThemePresenter =
        conversationListComponent.themePresenter

    override fun getPresenterView(): ThemeFragment = this

    private fun createComponent(): ConversationListComponent = with(DaggerConversationListComponent.builder()) {
        context(requireContext())
        communicatorCommonComponent(CommunicatorCommonComponent.getInstance(requireContext()))
        communicatorDialogChatDependency(themesRegistryDependency)
        tablet(DeviceConfigurationUtils.isTablet(requireContext()))
        arguments?.also {
            if (it.containsKey(DIALOG_TYPE_ID)) {
                dialogType(requireArguments().getSerializable(DIALOG_TYPE_ID) as DialogType?)
            }
            if (it.containsKey(CHAT_TYPE_ID)) {
                chatType(requireArguments().getSerializable(CHAT_TYPE_ID) as ChatType?)
            }
            initAsChat(it.getBoolean(INIT_AS_CHAT_KEY))
            initAsSharing(it.getBoolean(INIT_AS_SHARING_MODE))
        }

        return build()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        importContactsHelper?.onRequestPermissionsResult(requestCode, grantResults)
            ?.subscribeToImportResult()
            ?.storeIn(disposables)
        @Suppress("DEPRECATION")
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun Single<CommandStatus>.subscribeToImportResult(): Disposable =
        subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
            { commandStatus -> showImportResult(commandStatus) },
            { showContactsImportFailed() }
        )

    override fun importContactsSafe() {
        importContactsHelper?.importContactsSafe(fallback = { requestImportContactsConfirmation() })
            ?.subscribeToImportResult()
            ?.storeIn(disposables)
    }

    override fun changeTabSelection(navxId: NavxId) {
        binding?.let {
            it.sbisToolbar?.tabsView?.selectedTabIndex = it.getSelectedTabIndex(navxId == NavxId.CHATS)
        }
    }

    override fun changeHeaderByTabsAvailabilityChanges(
        dialogsAvailable: Boolean,
        channelsAvailable: Boolean
    ) {
        binding?.updateToolbarByAvailabilityChanges(
            presenter.isChannelTab(),
            dialogsAvailable,
            channelsAvailable
        )
    }

    override fun showComplainDialogFragment(complainUseCase: ComplainUseCase) {
        themesRegistryDependency.complainFragmentFeature?.showComplainDialogFragment(
            childFragmentManager,
            complainUseCase
        )
    }

    private fun Maybe<CommandStatus>.subscribeToImportResult(): Disposable =
        subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
            { commandStatus -> showImportResult(commandStatus) },
            { showContactsImportFailed() }
        )

    private fun showImportResult(commandStatus: CommandStatus) {
        when (commandStatus.errorCode) {
            ErrorCode.SUCCESS           -> {
                val message = getString(RCommunicatorDesign.string.communicator_contacts_import_success_text)
                SbisPopupNotification.push(requireContext(), SbisPopupNotificationStyle.SUCCESS, message)
            }
            ErrorCode.NO_ATTACHED_PHONE -> presenter.onPhoneVerificationRequired()
            else                        -> {
                val message = commandStatus.errorMessage
                val icon = SbisMobileIcon.Icon.smi_alert.character.toString()
                SbisPopupNotification.push(requireContext(), SbisPopupNotificationStyle.ERROR, message, icon)
            }
        }
    }

    private fun showContactsImportFailed() {
        val message = getString(RCommunicatorDesign.string.communicator_contacts_import_failed_error)
        val icon = SbisMobileIcon.Icon.smi_alert.character.toString()
        SbisPopupNotification.push(requireContext(), SbisPopupNotificationStyle.ERROR, message, icon)
    }

    override fun onBackPressed(): Boolean =
        presenter.onBackPressed()

    override fun onDestroyView() {
        swipeMenuViewPoolLifecycleManager.onDestroy()
        binding = null
        snackbar = null
        adapter.clear()
        enableFolders()
        enableFilters()
        hideDismissalUndoSnackbar()
        disposables.clear()
        removeContactsSearchPanelViewFromMain()
        keyboardHelper.clearKeyboardHelper()
        mSbisListView?.clearOnScrollListener()
        super.onDestroyView()
    }

    override fun onDestroy() {
        importContactsHelper?.onDestroy()
        detachCommunicatorRouter()
        super.onDestroy()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        if (customizationOptions.splittingChannelsAndDialogsEnabled) {
            if (!hidden) {
                initCommunicatorRouter(this)
                setFabVisible(true)
            } else {
                adapter.closeFoldersView()
                resetUiState()
                clearSearchQuery()
            }
            presenter.onViewVisibilityChanged(hidden)
        }
    }

    private fun removeContactsSearchPanelViewFromMain() {
        if (isTablet) personSuggestView?.let { (it.parent as ViewGroup).removeView(it) }
    }

    /** @SelfDocumented */
    override fun resetUiState() {
        // Скроллимся только если не происходит переключение вкладки диалоги / чаты.
        // В ином случае, чтобы не происходило прыжка к началу списка перед переключением,
        // скролл будет сброшен при получении данных для новой вкладки (см. updateDataList),
        if (!presenter.isBranchTypeChanging()) {
            mSbisListView?.scrollToPosition(0)
            view?.post {
                mSbisListView?.scrollRecyclerBy(0, -1)
            }
        }
        hideAllSwipedPanels()
    }

    /** @SelfDocumented */
    override fun showControls() = Unit

    /** @SelfDocumented */
    override fun hideControls() {
        presenter.onKeyboardClosed(true)
    }

    override fun setPersonSuggestData(data: List<PersonSuggestData>) {
        personSuggestView?.data = data
    }

    override fun changeContactsSearchPanelVisibility(needToShow: Boolean) {
        personSuggestView?.showOnKeyboard = needToShow
    }

    //endregion
    override fun resetStateForNewData(
        selectedConversationUuid: UUID?,
        selectedMessageUuid: UUID?,
        resetTypeIfUnanswered: Boolean
    ) {
        if (resetTypeIfUnanswered) {
            if (isAdded) {
                presenter.resetTypeIfUnanswered()
            } else {
                this.resetTypeIfUnanswered = true
            }
        }

        val model = getNewStubItem()
        model.uuid = selectedConversationUuid ?: UUID.randomUUID()
        model.messageUuid = selectedMessageUuid
        if (isAdded) {
            presenter.changeSelection(model)
        } else {
            newSelectedModel = model
        }
    }

    private fun setBottomActionsPanelVisibility(visible: Boolean): Unit = with(binding!!) {
        if (!visible && bottomCheckActionsPanel?.visibility == View.GONE ||
            visible && bottomCheckActionsPanel?.visibility == View.VISIBLE
        ) return
        binding?.initMassPanel()

        val height = bottomCheckActionsPanel?.layoutParams?.height
        val (fromY, toY) = mutableListOf(height?.toFloat() ?: 0f, 0f).apply { if (!visible) reverse() }

        TranslateAnimation(0f, 0f, fromY, toY).apply {
            duration = BOTTOM_ACTION_PANEL_ANIMATION_DURATION
            setAnimationListener(object : CommonUtils.SimpleAnimationListener() {
                override fun onAnimationEnd(animation: Animation) {
                    bottomCheckActionsPanel?.visibility = if (visible) View.VISIBLE else View.GONE
                }
            })
            bottomCheckActionsPanel?.startAnimation(this)
        }
    }

    override fun setBranchTypeTitle(navxId: NavxIdDecl) {
        with(binding!!) {
            sbisToolbar?.content = if (navxId == NavxId.CHATS) {
                getChannelsTitle()
            } else {
                getDialogsTitle()
            }
        }
    }

    override fun cleanCurrentIntentSharingContent() {
        ConversationUtils.cleanSharingExtras(activity?.intent)
    }

    /** @SelfDocumented */
    override fun onYes(requestCode: Int, text: String?) {
        when (requestCode) {
            DIALOG_CODE_CONFIRM_DELETE_DIALOG   -> presenter.deleteDialogs()
            DIALOG_CODE_CONFIRM_MASS_DELETE_DIALOG_FOR_ALL -> presenter.deleteDialogs(true)
            DIALOG_CODE_CONFIRM_HIDE_CHAT       -> presenter.onHideChatConfirmationAlertClicked(true)
            DIALOG_CODE_IMPORT_CONTACTS_REQUEST -> importContactsHelper?.requestPermissions(this)
        }
    }

    /** @SelfDocumented */
    override fun onNo(requestCode: Int, text: String?) {
        when (requestCode) {
            DIALOG_CODE_CONFIRM_DELETE_DIALOG -> hideAllSwipedPanels()
            DIALOG_CODE_CONFIRM_HIDE_CHAT     -> presenter.onHideChatConfirmationAlertClicked(false)
        }
    }

    override fun onCancel(requestCode: Int) = onNo(requestCode, null)

    /** @SelfDocumented */
    override fun onNavigationDrawerStateChanged() {
        if (isAdded) hideAllSwipedPanels()
    }

    override fun hideSwipePanelOnScroll() {
        if (needToStoreSwipe) {
            needToStoreSwipe = false
        } else {
            hideAllSwipedPanels()
        }
    }

    override fun hideAllSwipedPanels() {
        adapter.closeAllSwipePanels()
    }

    override fun clearSwipeMenuState() {
        adapter.clearSwipeMenuState()
    }

    /** @SelfDocumented */
    override fun onKeyboardOpenMeasure(keyboardHeight: Int): Boolean {
        super.onKeyboardOpenMeasure(keyboardHeight)
        personSuggestView?.onKeyboardOpenMeasure(keyboardHeight + additionalKeyboardHeightForContactsSearchPanel)
        val padding = keyboardHeight + if (personSuggestView?.isVisible == true) personSuggestView?.measuredHeight ?: 0 else 0
        keyboardHelper.onKeyboardOpenMeasure(padding)
        return true
    }

    /** @SelfDocumented */
    override fun onKeyboardCloseMeasure(keyboardHeight: Int): Boolean {
        super.onKeyboardCloseMeasure(keyboardHeight)
        personSuggestView?.onKeyboardCloseMeasure(0)
        keyboardHelper.onKeyboardCloseMeasure(0)
        return true
    }

    override fun restartProgress() {
        mSbisListView?.setInProgress(true)
    }

    override fun isRegistryDeeplinkAction(deeplink: DeeplinkAction): Boolean =
        when (deeplink) {
            is SwitchThemeTabDeeplinkAction,
            is OpenProfileDeeplinkAction,
            is OpenWebViewDeeplinkAction,
            is OpenNewsDeepLinkAction,
            is OpenConversationDeeplinkAction,
            is ShareToMessagesDeeplinkAction,
            is HandlePushNotificationDeeplinkAction -> true
            else -> false
        }

    override fun onNewDeeplinkAction(args: DeeplinkAction) {
        if (args is SwitchThemeTabDeeplinkAction) {
            presenter.onNewDeeplinkAction(args)
        } else {
            if (args is ShareToMessagesDeeplinkAction) activity?.intent = args.dataIntent
            if (isRouterInitialized) {
                presenter.onNewDeeplinkAction(args)
            } else {
                postponedDeeplinkAction = args
            }
        }
    }

    override fun showDeletingConfirmationDialog() {
        showDeletingConfirmationDialog(titleRes = RCommunicatorDesign.string.communicator_delete_dialog_forever)
    }

    override fun showNoticeDeletingConfirmationDialog() {
        showDeletingConfirmationDialog(titleRes = RCommunicatorDesign.string.communicator_delete_notifications_forever)
    }

    override fun showMassDeletingConfirmationDialogForAll() {
        showMassDeletingConfirmationDialog(
            titleRes = RCommunicatorDesign.string.communicator_delete_dialog_forever,
            messageRes = RCommunicatorDesign.string.communicator_delete_dialog_forever_message
        )
    }

    override fun showMassDeletingDialogsAndNoticeConfirmationDialog() {
        showMassDeletingConfirmationDialog(
            titleRes = RCommunicatorDesign.string.communicator_delete_selected_notifications_forever,
            messageRes = RCommunicatorDesign.string.communicator_delete_notice_forever_message
        )
    }

    private fun showDeletingConfirmationDialog(titleRes: Int) {
        PopupConfirmation.newMessageInstance(
            DIALOG_CODE_CONFIRM_DELETE_DIALOG,
            getString(RCommunicatorDesign.string.communicator_delete_dialog_forever_message)
        )
            .requestTitle(getString(titleRes))
            .requestNegativeButton(getString(RCommunicatorDesign.string.communicator_delete_dialog_negative))
            .requestPositiveButton(getString(RCommunicatorDesign.string.communicator_delete_dialog_positive), true)
            .setEventProcessingRequired(true)
            .show(childFragmentManager, PopupConfirmation::class.java.canonicalName)
    }

    private fun showMassDeletingConfirmationDialog(titleRes: Int, messageRes: Int) {
        PopupConfirmation.newMessageInstance(
            DIALOG_CODE_CONFIRM_MASS_DELETE_DIALOG_FOR_ALL,
            getString(messageRes)
        )
            .requestTitle(getString(titleRes))
            .requestNegativeButton(getString(RCommunicatorDesign.string.communicator_delete_dialog_negative))
            .requestPositiveButton(getString(RCommunicatorDesign.string.communicator_delete_dialog_positive), true)
            .setEventProcessingRequired(true)
            .show(childFragmentManager, PopupConfirmation::class.java.canonicalName)
    }

    override fun showDeletingConfirmationDialogForAll() {
        PopupConfirmation.newButtonsListInstance(
            DIALOG_CODE_CONFIRM_DELETE_DIALOG_FOR_ALL,
            arrayListOf(
                getString(RCommunicatorDesign.string.communicator_delete_message_or_dialog_for_all),
                getString(RCommunicatorDesign.string.communicator_delete_message_or_dialog_for_me),
                getString(RCommunicatorDesign.string.communicator_delete_message_or_dialog_negative)
            ),
            getString(RCommunicatorDesign.string.communicator_delete_dialog_forever_message),
            hashMapOf(Pair(getString(RCommunicatorDesign.string.communicator_delete_message_or_dialog_for_all), RModalWindows.style.ModalWindowsAlertDialogButtonRemoval))
        )
            .requestTitle(getString(RMessagePanel.string.message_panel_delete_dialog_forever))
            .setEventProcessingRequired(true)
            .show(childFragmentManager, PopupConfirmation::class.java.simpleName)
    }

    override fun onItemClicked(requestCode: Int, itemValue: String?) {
        when (requestCode) {
            DIALOG_CODE_CONFIRM_DELETE_DIALOG_FOR_ALL -> deleteMyDialogOrOutgoingMessage(requestCode, itemValue)
        }
    }

    private fun deleteMyDialogOrOutgoingMessage(requestCode: Int, itemValue: String?) {
        when (itemValue) {
            getString(RCommunicatorDesign.string.communicator_delete_message_or_dialog_negative) -> super.onItemClicked(requestCode, itemValue)
            getString(RCommunicatorDesign.string.communicator_delete_message_or_dialog_for_me) -> presenter.deleteDialogs(false)
            else -> presenter.deleteDialogs(true)
        }
    }

    override fun showDialogInFolderAlready(errorMessage: String) {
        val message = if (errorMessage != StringUtils.EMPTY) {
            errorMessage
        } else {
            getString(RCommunicatorDesign.string.communicator_dialog_already_in_folder)
        }
        val icon = SbisMobileIcon.Icon.smi_information.character.toString()
        SbisPopupNotification.push(SbisPopupNotificationStyle.INFORMATION, message, icon)
    }

    override fun showSuccessMoveToFolder(count: Int) {
        val message = String.format(resources.getQuantityString(RCommunicatorDesign.plurals.communicator_move_dialogs_success_message, count))
        SbisPopupNotification.push(
            SbisPopupNotificationStyle.SUCCESS,
            message,
            SbisMobileIcon.Icon.smi_Successful.character.toString()
        )
    }

    override fun updateDataList(
        oldList: List<ConversationRegistryItem>?,
        newList: List<ConversationRegistryItem>?,
        offset: Int,
        forceNotifyDataSetChanged: Boolean
    ) {
        val possibleAutoScroll = offset == 0
        if (possibleAutoScroll) {
            autoScroller.onBeforeContentChanged(oldList)
        }

        checkPhotosToPrefetch(newList)

        if (forceNotifyDataSetChanged) {
            // пришли данные после переключения вкладки диалоги / чаты - нужно сбросить позицию скролла
            // или поменялась выбранная папка
            mSbisListView?.scrollToPosition(0)
        }

        // Обновляем данные в списке
        adapter.setData(oldList, newList, offset, forceNotifyDataSetChanged)

        if (isFirstSetDataList && oldList.isNullOrEmpty() && !newList.isNullOrEmpty()) {
            isFirstSetDataList = false
            initConversationViewPool()
        }

        if (possibleAutoScroll) {
            autoScroller.onAfterContentChanged(adapter.content)
        }
    }

    private fun initConversationViewPool() {
        if (isConversationPoolInitialized || !isFirstSetDataList || !isResumed) return
        isConversationPoolInitialized = true
        themesRegistryDependency.conversationViewPoolInitializer?.initViewPool(this)
    }

    override fun updateDataList(dataList: MutableList<ConversationRegistryItem>?, offset: Int) {
        // Происходит сброс пейджинга при переключении вкладок. Игнорим этот апдейт
        if (presenter.isBranchTypeChanging()) {
            return
        }

        updateDataList(null, dataList, offset, false)
    }

    override val isScrolling: Boolean
        get() = mSbisListView!!.recyclerView.scrollState != SCROLL_STATE_IDLE

    override val isTablet: Boolean
        get() = DeviceConfigurationUtils.isTablet(requireContext())

    override fun notifyShareSelectionListener(conversationModel: ConversationModel) {
        (parentFragment as ThemeShareSelectionResultListener).onConversationSelected(conversationModel)
    }

    override fun shouldRequestContactsPermissions(): Boolean =
        SharedPreferencesUtils.shouldRequestContactsPermissions(requireContext())

    override fun notifyItemsInserted(position: Int, count: Int) {
        super.notifyItemsInserted(position, count)
        autoScroller.onContentRangeInserted(position, count)
    }

    override fun notifyItemsRemoved(position: Int, count: Int) {
        mSbisListView?.recyclerView.safeUpdate {
            super.notifyItemsRemoved(position, count)
        }
    }

    /**
     * Обеспечить загрузку в кэш аватарок, отображение которых ожидается на экране.
     */
    private fun checkPhotosToPrefetch(list: List<ConversationRegistryItem>?) {
        if (!shouldPrefetchPhotos) return
        shouldPrefetchPhotos = false
        list?.let { it.ifEmpty { null } } ?: return

        val visibleList = if (maxDialogsOnScreen + 1 < list.size) {
            list.subList(0, maxDialogsOnScreen + 1)
        } else {
            list
        }

        val photosData = visibleList.mapNotNull { it.castTo<ConversationModel>()?.participantsCollage }
            .flatten()

        val photoSizePx = resources.getDimensionPixelSize(PhotoSize.M.photoSize)
        ImageBitmapPreFetcher.prefetchPhotos(photosData, photoSizePx)
    }

    override fun onActiveTabClicked(item: NavigationItem) {
        presenter.onScrollToTopPressed()
    }
}

private class IWouldLikeToBeADataBinding(
    private val mainView: View,
    val isChannelsTabOnly: Boolean? = false,
    isSharingMode: Boolean,
    private val presenterProvider: () -> ThemePresenter
) {

    val headerDate: HeaderDateView = mainView.findViewById(R.id.communicator_dialog_list_header_date)
    val sbisToolbar: SbisTopNavigationView? =
        if (!isSharingMode) mainView.findViewById(R.id.communicator_sbis_toolbar) else null

    val unreadDialogsCounter = MutableStateFlow(0)
    val unreadChatsCounter = MutableStateFlow(0)
    val unviewedDialogsCounter = MutableStateFlow(0)
    val unviewedChatsCounter = MutableStateFlow(0)

    val appBarLayout: AppBarLayout = mainView.findViewById(R.id.communicator_app_bar)

    val folderTitleLayout: CurrentFolderView = mainView.findViewById(R.id.communicator_dialog_list_folder_title_layout)

    @Suppress("DEPRECATION")
    val bottomCheckActionsPanel: SbisButtonToolbarView? =
        if (!isSharingMode) mainView.findViewById(R.id.communicator_dialog_check_panel) else null

    val personSuggestView: PersonSuggestView? =
        if (!isSharingMode) mainView.findViewById(R.id.communicator_person_suggest_view) else null

    val personInputLayout: PersonInputLayout? =
        if (!isSharingMode) mainView.findViewById(R.id.communicator_person_input_layout) else null

    var markGroupAsReadButton: View? = null
    var markGroupAsNotReadButton: View? = null
    var deleteGroupButton: View? = null
    var moveGroupButton: View? = null

    private var hasCheckedDialogs: Boolean = false
    private var canReadDialogs: Boolean = false
    private var canUnreadDialogs: Boolean = false

    init {
        when {
            isSharingMode -> {
                folderTitleLayout.isVisible = false
                expandSearchInput(appBarLayout)
                mainView.findViewById<View>(R.id.themes_registry_conversation_list_recycler_id).fitsSystemWindows = false
            }
            else -> {
                sbisToolbar!!.visibility = View.VISIBLE
                }
        }
    }

    fun configureMassButtonsPanel(
        hasCheckedDialogs: Boolean = this.hasCheckedDialogs,
        canReadDialogs: Boolean = this.canReadDialogs,
        canUnreadDialogs: Boolean = this.canUnreadDialogs
    ) {
        this.hasCheckedDialogs = hasCheckedDialogs
        this.canReadDialogs = canReadDialogs
        this.canUnreadDialogs = canUnreadDialogs

        deleteGroupButton?.let { bottomCheckActionsPanel?.setButtonEnabled(it, hasCheckedDialogs) }
        if (!presenterProvider().isDeleted()) {
            moveGroupButton?.let { bottomCheckActionsPanel?.setButtonEnabled(it, hasCheckedDialogs) }
            markGroupAsReadButton?.let { bottomCheckActionsPanel?.setButtonEnabled(it, canReadDialogs) }
            markGroupAsNotReadButton?.let { bottomCheckActionsPanel?.setButtonEnabled(it, canUnreadDialogs) }
        }
    }

    fun initMassPanel() {
        markGroupAsReadButton != null && return
        val context = mainView.context

        fun addButtonInCenter(buttonAction: ThemeBottomCheckAction): View? {
            return bottomCheckActionsPanel?.addButtonInCenter(
                context.getString(buttonAction.iconResId),
                context.getString(buttonAction.textResId)
            ) { buttonAction.action() }
        }

        presenterProvider().prepareThemeBottomCheckAction().forEach { action ->
            when (action) {
                ThemeBottomCheckAction.MARK_GROUP_AS_READ -> {
                    markGroupAsReadButton = addButtonInCenter(action)
                }

                ThemeBottomCheckAction.MARK_GROUP_AS_UNREAD -> {
                    markGroupAsNotReadButton = addButtonInCenter(action)
                }

                ThemeBottomCheckAction.MOVE_GROUP -> {
                    moveGroupButton = addButtonInCenter(action)
                }

                ThemeBottomCheckAction.DELETE_GROUP -> {
                    deleteGroupButton = addButtonInCenter(action)
                }
            }
        }
        configureMassButtonsPanel()
    }


    fun getSelectedTabIndex(isChat: Boolean): Int = if (isChat) CHATS_TAB_INDEX else DIALOGS_TAB_INDEX

    /**
     * Обновить шапку исходя из изменений в доступности табов.
     */
    fun updateToolbarByAvailabilityChanges(
        isChannelTab: Boolean,
        dialogsAvailable: Boolean = true,
        channelsAvailable: Boolean = true
    ) {
        sbisToolbar?.apply {
            val bothTabsAvailable = dialogsAvailable && channelsAvailable &&
                !customizationOptions.splittingChannelsAndDialogsEnabled
            content = when {
                bothTabsAvailable -> getTabs(isOldToolbarDesign)
                (isChannelsTabOnly == true || channelsAvailable) && isChannelTab ->
                    getChannelsTitle()
                else -> getDialogsTitle()
            }
            doIf(bothTabsAvailable) { tabsView?.init() }
        }
    }

    private fun getTabs(isOldToolbar: Boolean): SbisTopNavigationContent = SbisTopNavigationContent.Tabs(
        LinkedList<SbisTabsViewItem>().apply {
            add(
                SbisTabsViewItem(
                    getTabContent(
                        titleRes = customizationOptions.dialogsRegistryTabTitle,
                        accentedCounter = unviewedDialogsCounter,
                        unaccentedCounter = unreadDialogsCounter
                    ),
                    isSelected = false,
                    isMain = !isOldToolbar,
                    HorizontalPosition.LEFT,
                    id = NavxId.DIALOGS.id,
                    navxId = NavxId.DIALOGS
                )
            )
            add(
                SbisTabsViewItem(
                    getTabContent(
                        titleRes = customizationOptions.channelsRegistryTabTitle,
                        accentedCounter = unviewedChatsCounter,
                        unaccentedCounter = unreadChatsCounter
                    ),
                    isSelected = false,
                    isMain = false,
                    if (isOldToolbar) HorizontalPosition.LEFT else HorizontalPosition.RIGHT,
                    id = NavxId.CHATS.id,
                    navxId = NavxId.CHATS
                )
            )
        }
    )

    private fun getTabContent(
        titleRes: Int,
        accentedCounter: StateFlow<Int>,
        unaccentedCounter: StateFlow<Int>
    ): LinkedList<SbisTabViewItemContent> = LinkedList<SbisTabViewItemContent>().apply {
        add(SbisTabViewItemContent.Text(PlatformSbisString.Res(titleRes)))
        add(SbisTabViewItemContent.Counter(accentedCounter, unaccentedCounter))
    }

    fun getChannelsTitle(): SbisTopNavigationContent {
        return with(customizationOptions) {
            getTitle(channelsRegistryTabTitle, channelsRegistryTopNavigationNavxId)
        }
    }

    fun getDialogsTitle(): SbisTopNavigationContent {
        return with(customizationOptions) {
            getTitle(dialogsRegistryTabTitle, dialogsRegistryTopNavigationNavxId)
        }
    }

    private fun getTitle(
        titleRes: Int,
        navxId: NavxIdDecl?
    ): SbisTopNavigationContent = SbisTopNavigationContent.LargeTitle(PlatformSbisString.Res(titleRes), navxId = navxId)

    private fun SbisTabsView.init() {
        setOnTabClickListener { tab ->
            tab.id?.let { themesRegistryDependency.themeTabHistory.saveLastSelectedTab(it) }
        }
        selectedTabIndex = getSelectedTabIndex(themesRegistryDependency.themeTabHistory.chatsIsLastSelectedTab())
    }
}

internal const val EXPECTED_VISIBLE_SWIPE_MENU_ITEM_COUNT = 40

private const val DIALOG_CODE_CONFIRM_DELETE_DIALOG = 2

private const val DIALOG_CODE_CONFIRM_DELETE_DIALOG_FOR_ALL = 5

private const val DIALOG_CODE_CONFIRM_MASS_DELETE_DIALOG_FOR_ALL = 6

private const val DIALOG_CODE_CONFIRM_HIDE_CHAT = 3

private const val DIALOG_CODE_IMPORT_CONTACTS_REQUEST = 4

private const val BOTTOM_ACTION_PANEL_ANIMATION_DURATION = 200L

private const val AUTO_SCROLL_THRESHOLD = 3

private const val INIT_AS_CHAT_KEY = "INIT_AS_CHAT"

private const val INIT_AS_SHARING_MODE = "INIT_AS_SHARING_MODE"

private const val DIALOGS_TAB_INDEX = 0

private const val CHATS_TAB_INDEX = 1

private const val CONVERSATION_PREVIEW_RESULT = "CONVERSATION_PREVIEW_RESULT"