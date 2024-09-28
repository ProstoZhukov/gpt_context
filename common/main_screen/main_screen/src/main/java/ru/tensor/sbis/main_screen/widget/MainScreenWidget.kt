package ru.tensor.sbis.main_screen.widget

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentManager.FragmentLifecycleCallbacks
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.tracing.Trace
import androidx.tracing.trace
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import ru.tensor.sbis.common.navigation.NavxId
import ru.tensor.sbis.common.navigation.TabNavScrollHelper
import ru.tensor.sbis.common.util.DeviceConfigurationUtils
import ru.tensor.sbis.common.util.safeThrow
import ru.tensor.sbis.design.navigation.util.ScrollToTopSubscriptionHolder
import ru.tensor.sbis.design.navigation.view.DebouncedObserver
import ru.tensor.sbis.design.navigation.view.adapter.NavAdapter
import ru.tensor.sbis.design.navigation.view.model.ItemSelected
import ru.tensor.sbis.design.navigation.view.model.ItemSelectedByUser
import ru.tensor.sbis.design.navigation.view.model.NavigationCounters
import ru.tensor.sbis.design.navigation.view.model.NavigationEvent
import ru.tensor.sbis.design.navigation.view.model.NavigationItem
import ru.tensor.sbis.design.navigation.view.model.SelectedSameItem
import ru.tensor.sbis.design.navigation.view.view.HideableNavigationView
import ru.tensor.sbis.design.navigation.view.view.fab.HideableFabView
import ru.tensor.sbis.design.topNavigation.view.SbisTopNavigationView
import ru.tensor.sbis.design.utils.extentions.doOnDetachedFromWindow
import ru.tensor.sbis.design.utils.extentions.doOnNextGlobalLayout
import ru.tensor.sbis.design.utils.findViewInHierarchy
import ru.tensor.sbis.main_screen.widget.dashboard.DashboardNavigationBuilder
import ru.tensor.sbis.main_screen.widget.interactor.MainScreenWidgetInteractor
import ru.tensor.sbis.main_screen.widget.permission.MainScreenPermissionHandler
import ru.tensor.sbis.main_screen.widget.permission.SerialStartupPermissionLauncher
import ru.tensor.sbis.main_screen.widget.permission.StartupPermissionLauncher
import ru.tensor.sbis.main_screen.widget.storage.MainScreenStorage
import ru.tensor.sbis.main_screen.widget.util.NavigationPageStatistics
import ru.tensor.sbis.main_screen.widget.util.TopNavigationTitleUpdateManager
import ru.tensor.sbis.main_screen.widget.util.getSavedStateHandle
import ru.tensor.sbis.main_screen.widget.util.logFragmentManagerDestroyedIssueAnalytics
import ru.tensor.sbis.main_screen.widget.util.requireId
import ru.tensor.sbis.main_screen.widget.view.DrawerMenuListener
import ru.tensor.sbis.main_screen.widget.view.HidableNavBunchView
import ru.tensor.sbis.main_screen.widget.view.MenuItemSelectionConsumer
import ru.tensor.sbis.main_screen.widget.viewmodel.MainScreenWidgetViewModel
import ru.tensor.sbis.main_screen_decl.ConfigurableMainScreen
import ru.tensor.sbis.main_screen_decl.MainScreen
import ru.tensor.sbis.main_screen_decl.MainScreenAddon
import ru.tensor.sbis.main_screen_decl.content.ContentContainer
import ru.tensor.sbis.main_screen_decl.content.ContentController
import ru.tensor.sbis.main_screen_decl.content.DashboardContentController
import ru.tensor.sbis.main_screen_decl.content.MainScreenEntry
import ru.tensor.sbis.main_screen_decl.env.MainScreenHost
import ru.tensor.sbis.main_screen_decl.env.MainScreenMenu
import ru.tensor.sbis.main_screen_decl.env.MenuCounters
import ru.tensor.sbis.main_screen_decl.env.SideMenu
import ru.tensor.sbis.main_screen_decl.intent.IntentHandleExtension
import ru.tensor.sbis.main_screen_decl.navigation.CollapsibleNavigationItem
import ru.tensor.sbis.main_screen_decl.navigation.NavigationVisibilityProvider
import ru.tensor.sbis.main_screen_decl.navigation.service.NavigationPageData
import ru.tensor.sbis.toolbox_decl.navigation.NavxIdDecl
import ru.tensor.sbis.verification_decl.permission.PermissionChecker
import ru.tensor.sbis.verification_decl.permission.PermissionInfo
import ru.tensor.sbis.verification_decl.permission.PermissionLevel
import ru.tensor.sbis.verification_decl.permission.PermissionScope
import timber.log.Timber
import java.lang.ref.WeakReference
import kotlin.collections.set
import kotlin.properties.Delegates
import ru.tensor.sbis.design.R as RDesign

/**
 * Компонент главного экрана
 *
 * @property persistentStrategy стратегия сохранения состояния компонента (выбранного элемента и прочее)
 * @property host хост
 * @property menu меню
 * @property contentContainer информация о контейнере для контента
 * @property permissionChecker чекер проверки прав
 * @property hideMenuOnScroll требуется ли скрывать ННП при скролле
 * @param initialIntent стартовый интент
 * @property defaultSelectedItemIdentifier идентификатор выбранного элемента по-умолчанию
 * @property addons плагины главного экрана
 * @property monitorPermissionOnLifecycle требуется ли проверка разрешений на доступные разделы на onStart и при переключении разделов.
 * @param startupPermissionLauncher компонент для запроса разрешений у пользователя при старте виджета.
 * Необходимо создать инстанс не позднее чем [androidx.fragment.app.Fragment.onCreate] или [android.app.Activity.onCreate]
 *
 * @author kv.martyshenko
 */
class MainScreenWidget(
    private val persistentStrategy: PersistentStrategy = PersistentStrategy.StoreUntilAppRestart,
    override val host: MainScreenHost,
    override val menu: MainScreenMenu,
    private val contentContainer: ContentContainer,
    private val permissionChecker: PermissionChecker,
    private val hideMenuOnScroll: Boolean = !DeviceConfigurationUtils.isTablet(host.context),
    initialIntent: Intent?,
    private val defaultSelectedItemIdentifier: String,
    private val addons: List<MainScreenAddon>,
    private val intentHandleExtensions: List<IntentHandleExtension<out IntentHandleExtension.ExtensionKey>>,
    private val monitorPermissionOnLifecycle: Boolean = false,
    startupPermissionLauncher: StartupPermissionLauncher = SerialStartupPermissionLauncher(
        host.resultCaller,
        host.viewLifecycleOwner
    ) { host.fragmentActivity }
) : MainScreen,
    ConfigurableMainScreen,
    NavigationVisibilityProvider {
    private var mainScope: CoroutineScope? = null

    private var lastEntryPoint: ContentController.EntryPoint? = null

    private var fragmentLifecycleCallbacks: FragmentLifecycleCallbacks? = null

    private val backPressedCallback: OnBackPressedCallback = object : OnBackPressedCallback(true) {

        override fun handleOnBackPressed() {
            val drawerLayout: DrawerLayout? = menu.sideMenu?.drawerLayout

            if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                val consumed =
                    activeItem?.let { controllerOf(it) }?.backPressed(this@MainScreenWidget, contentContainer) ?: false
                if (!consumed) {
                    isEnabled = false
                    host.backPressedDispatcherOwner.onBackPressedDispatcher.onBackPressed()
                    isEnabled =
                        true // взводим обратно, так как нас могли оставить на экране (например, после показа диалога)
                }
            }
        }

    }
    private val viewModel: MainScreenWidgetViewModel by lazy {
        ViewModelProvider(host.viewModelStoreOwner, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return if (modelClass.isAssignableFrom(MainScreenWidgetViewModel::class.java)) {
                    MainScreenWidgetViewModel(MainScreenWidgetInteractor(permissionChecker)) as T
                } else throw IllegalArgumentException("Unknown $modelClass")
            }

        })[MainScreenWidgetViewModel::class.java]
    }
    private val stateHandle: SavedStateHandle by lazy {
        host.getSavedStateHandle(null)
    }
    private val storage: MainScreenStorage by lazy {
        MainScreenStorage(host.context.applicationContext)
    }

    private val navigationSubscriptions = CompositeDisposable()
    private val permissionData = mutableMapOf<PermissionScope, MutableLiveData<PermissionLevel>>()
    private val menuItems = MenuConfigurator()
    private val itemPageData = mutableMapOf<String, NavigationPageData>()
    private val dashboardNavigationBuilder = DashboardNavigationBuilder(menuItems, this)

    private var intent: Intent? = initialIntent
    private var selectionInProgress = false
    private var drawerListener: DrawerLayout.DrawerListener? = null
    private var pendingSwitchToFirstVisibleItem = false

    private var activeItem: NavigationItem? by Delegates.observable(null) { _, _, newItem ->
        requireNotNull(newItem)
        stateHandle[KEY_SELECTED_ITEM_ID] = newItem.persistentUniqueIdentifier
        if (persistentStrategy is PersistentStrategy.StoreUntilLogout
            && menuItems[newItem.persistentUniqueIdentifier]!!.shouldPersistSelectState) {
            storage.saveString(persistentStrategy.userId.value.toString(), newItem.persistentUniqueIdentifier)
        }
    }

    private var activeTabNavxId: NavxIdDecl? = null
        set(value) {
            field = value
            storage.saveString(KEY_SELECTED_TAB_ID, value?.ids?.first().orEmpty())
        }

    private val topNavigationTitleUpdateManager = TopNavigationTitleUpdateManager()

    private val navigationItemsManager = NavigationItemsManager(
        menu.sideMenu?.run { getNavView() },
        menu.bottomMenu?.run { getNavView() },
        menu.sideMenu?.header as? SideMenu.DefaultHeader?,
        menu.sideMenu?.footer as? SideMenu.DefaultFooter?,
        menuItems,
        { item, _ -> onItemHidden(item) },
        ::onTabSelected,
        ::onPageDataAvailable,
        ::ensureItemsOrdered,
        topNavigationTitleUpdateManager,
        dashboardNavigationBuilder
    )

    private val permissionHandler = MainScreenPermissionHandler(
        lifecycleOwner = host.viewLifecycleOwner,
        permissions = MainScreenPlugin.startupPermissions.flatMap {
            it.get().permissions
        },
        launcher = startupPermissionLauncher
    )

    private val navigationPageStatistics = NavigationPageStatistics()

    private val handler by lazy { Handler(Looper.getMainLooper()) }

    init {
        intentHandleExtensions.forEach { it.setNavigationVisibilityProvider(this) }
    }

    // region MainScreen
    override fun addItem(item: NavigationItem, contentController: ContentController) {
        addItem(item, ConfigurableMainScreen.MenuItemConfiguration(), contentController)
    }

    override fun addItem(
        item: NavigationItem,
        configuration: ConfigurableMainScreen.MenuItemConfiguration,
        contentController: ContentController
    ) {
        onItemAdded(menuItems.addItem(item, configuration, contentController))
    }

    override fun removeItem(item: NavigationItem) {
        menuItems.removeItem(item)
            ?.let(navigationItemsManager::onItemRemoved)
    }

    override fun select(item: NavigationItem) {
        selectManually(item, ContentController.MenuClick(), false)
    }

    override fun isCurrentlySelecting(): Boolean = selectionInProgress

    override fun getDashboardScreenProvider() = MainScreenPlugin.dashboardScreenProvider?.get()

    override fun monitorPermissionScope(permissionScope: PermissionScope): LiveData<PermissionLevel?> {
        return permissionData.getOrPut(permissionScope) { MutableLiveData<PermissionLevel>() }
    }

    override fun registerDashboardContentController(dashboardContentController: DashboardContentController) {
        dashboardNavigationBuilder.registerDashboardContentController(
            dashboardContentController.navxId,
            dashboardContentController
        )
    }

    override fun isItemVisible(item: NavigationItem) = navigationItemsManager.isItemVisible(item)

    override fun <K : IntentHandleExtension.ExtensionKey, E : IntentHandleExtension<K>> getIntentHandleExtension(key: K): E? {
        return intentHandleExtensions.firstOrNull { it.key == key } as? E
    }

    override val screenEntries: List<MainScreenEntry>
        get() = MainScreenPlugin.screenEntries.map { it.get() }
    // endregion

    // region Lifecycle
    /**
     * Метод для выполнения начальной конфигурации.
     * @param mainFabInContainer находится ли главная плавающая кнопка в общем контейнере кнопок
     */
    fun setup(mainFabInContainer: Boolean = false) = trace("MainScreenWidget#setup") {

        menuItems.setAdapter(NavAdapter(host.viewLifecycleOwner, navigationSubscriptions, false))

        configureMenu(host.viewLifecycleOwner, menu)

        setupExceptionHandler()

        trace("MainScreenWidget#setupAddons") {
            val block: (MainScreenAddon) -> Unit = if (Trace.isEnabled()) {
                { delegate ->
                    trace("MainScreenWidget#setupAddon ${delegate::class.java.simpleName}") {
                        delegate.setup(this)
                    }
                }
            } else {
                { delegate ->
                    delegate.setup(this@MainScreenWidget)
                }
            }
            addons.forEach(block)
        }

        checkAndObserveNavigationPermissions()

        initScrollHelper(mainFabInContainer)

        setupRegistrationOfCreatedTopNavigationViews()

        navigationItemsManager.init(host.viewLifecycleOwner.lifecycleScope)

        performInitialSelect()
    }

    private fun setupRegistrationOfCreatedTopNavigationViews() {
        contentContainer.fragmentManager.registerFragmentLifecycleCallbacks(
            object : FragmentLifecycleCallbacks() {
                override fun onFragmentViewCreated(fm: FragmentManager, f: Fragment, v: View, savedInstanceState: Bundle?) {
                    super.onFragmentViewCreated(fm, f, v, savedInstanceState)
                    findViewInHierarchy<SbisTopNavigationView>(v, TOP_NAVIGATION_SEARCH_MAX_DEPTH)
                        ?.let(topNavigationTitleUpdateManager::registerView)
                }
            }.also { fragmentLifecycleCallbacks = it }, true
        )
    }

    private fun checkAndObserveNavigationPermissions() = with(viewModel) {
        fun List<PermissionInfo>.applyPermissionData() = forEach {
            permissionData[it.scope]?.value = it.level
        }

        trace("MainScreenWidget#checkPermissions") {
            checkPermissions(permissionData.keys)
            permissionsData.value?.applyPermissionData()
        }

        permissionsData.observe(host.viewLifecycleOwner) { permissionsInfo ->
            permissionsInfo?.applyPermissionData()
        }
    }

    /**
     * Метод для выполнения активации.
     */
    fun activate() = trace("MainScreenWidget#activate") {
        host.backPressedDispatcherOwner.onBackPressedDispatcher.addCallback(backPressedCallback)
        mainScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        executeOnCurrentController { navigationItem, contentController ->
            contentController.start(navigationItem, this, contentContainer)
        }
        refreshPermissionOnLifecycle()
        permissionHandler.activate()
    }

    /**
     * Метод для возобновления после приостановки.
     */
    fun resume() = trace("MainScreenWidget#resume") {
        executeOnCurrentController { navigationItem, contentController ->
            contentController.resume(navigationItem, this, contentContainer)
        }
        if (pendingSwitchToFirstVisibleItem) {
            pendingSwitchToFirstVisibleItem = false
            switchToFirstVisibleItem()
        }
    }

    /**
     * Метод для выполнения приостановки.
     */
    fun pause() = trace("MainScreenWidget#pause") {
        executeOnCurrentController { navigationItem, contentController ->
            contentController.pause(navigationItem, this, contentContainer)
        }
    }

    /**
     * Метод для выполнения деактивации.
     */
    fun deactivate() = trace("MainScreenWidget#deactivate") {
        backPressedCallback.remove()
        mainScope?.cancel()
        executeOnCurrentController { navigationItem, contentController ->
            contentController.stop(navigationItem, this, contentContainer)
        }
        permissionHandler.deactivate()
    }

    /**
     * Метод для выполнения сброса выполненных настроек в методе [MainScreenWidget.setup].
     */
    fun reset() = trace("MainScreenWidget#reset") {
        navigationSubscriptions.dispose()

        addons.forEach { delegate -> delegate.reset(this) }

        permissionData.values.forEach { liveData ->
            liveData.removeObservers(host.viewLifecycleOwner)
        }
        permissionData.clear()
        menuItems.setAdapter(null)

        menu.sideMenu?.let { sideMenu ->
            sideMenu.drawerLayout.removeDrawerListener(requireNotNull(drawerListener))
            drawerListener = null
        }

        navigationItemsManager.onReset()

        fragmentLifecycleCallbacks?.let {
            contentContainer.fragmentManager.unregisterFragmentLifecycleCallbacks(it)
            fragmentLifecycleCallbacks = null
        }
    }
    // endregion

    /**
     * Метод для обработки нового интента
     */
    fun handleNewIntent(intent: Intent) = trace("MainScreenWidget#handleNewIntent") {
        val intentCopy = Intent(intent)
        intent.data = null
        intent.replaceExtras(null)

        var resolutionResult: IntentHandleExtension.ResolutionResult? = null
        for (extension in intentHandleExtensions) {
            resolutionResult = with(extension) { resolveIntent(intentCopy) }
            if (resolutionResult != null) {
                break
            }
        }
        if (resolutionResult != null) {
            when (resolutionResult) {
                is IntentHandleExtension.ResolutionResult.SelectItem -> {
                    if (navigationItemsManager.isItemVisible(resolutionResult.targetMenuItem)) {
                        navigationPageStatistics.startTrace(resolutionResult.targetMenuItem.requireId())
                        selectManually(
                            item = resolutionResult.targetMenuItem,
                            resolutionResult.entryPoint,
                            isRestored = false
                        )
                    }
                }

                is IntentHandleExtension.ResolutionResult.SideEffect -> {
                    resolutionResult.action(host)
                }
            }

        }
    }

    /** @SelfDocumented */
    internal fun onItemAdded(record: MenuItemRecord) = navigationItemsManager.onItemAdded(record)

    /**
     * Обновляет актуальность прав на нужные области
     */
    private fun refreshPermissionOnLifecycle() {
        if (monitorPermissionOnLifecycle &&
            host.viewLifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)
        ) {
            mainScope?.launch {
                viewModel.refreshPermissions(permissionData.keys)
            }
        }
    }

    private fun performInitialSelect() = trace("MainScreenWidget#performInitialSelect") {
        val savedItemId: String? = stateHandle[KEY_SELECTED_ITEM_ID]
        var savedTabId: NavxIdDecl? = null
        val isRestored = savedItemId != null

        val selectItemId = when {
            savedItemId != null -> savedItemId
            !shouldRestoreNavigationItem() -> defaultSelectedItemIdentifier
            else -> {
                val selectedItemIdFromPersistentStorage: String? = when (persistentStrategy) {
                    PersistentStrategy.StoreUntilAppRestart -> {
                        null
                    }

                    is PersistentStrategy.StoreUntilLogout -> {
                        val id = storage.getString(persistentStrategy.userId.value.toString())
                        savedTabId = getSavedTabNavxId()
                        if (id == null) {
                            storage.reset()
                        }
                        id
                    }
                }
                selectedItemIdFromPersistentStorage ?: defaultSelectedItemIdentifier
            }
        }

        val item = menuItems[selectItemId]?.item
            ?: menuItems[defaultSelectedItemIdentifier]?.item
            ?: throw IllegalStateException("Не зарегистрирован обработчик дефолтного элемента $defaultSelectedItemIdentifier")

        val startedIntent = intent?.let {
            val copy = Intent(it)
            it.data = null
            it.replaceExtras(null)
            intent = null
            host.fragmentActivity.intent = null // нужно сбросить, чтобы корректно обрабатывать новые.
            copy
        }

        lastEntryPoint = ContentController.MenuClick()

        if (startedIntent == null) {
            selectManually(item, ContentController.MenuClick(), isRestored)
        } else {
            var resolutionResult: IntentHandleExtension.ResolutionResult? = null
            for (extension in intentHandleExtensions) {
                resolutionResult = with(extension) { resolveIntent(startedIntent) }
                if (resolutionResult != null) {
                    break
                }
            }

            when (resolutionResult) {
                is IntentHandleExtension.ResolutionResult.SelectItem -> with(resolutionResult) {
                    if (targetMenuItem.persistentUniqueIdentifier != savedItemId) {
                        navigationPageStatistics.startTrace(targetMenuItem.requireId())
                        selectManually(targetMenuItem, entryPoint, isRestored = false)
                    } else {
                        selectManually(targetMenuItem, entryPoint, isRestored)
                    }
                    lastEntryPoint = entryPoint
                }

                is IntentHandleExtension.ResolutionResult.SideEffect -> {
                    selectManually(item, ContentController.MenuClick(), isRestored)
                    resolutionResult.action(host)
                }

                null -> {
                    selectManually(item, ContentController.MenuClick(), isRestored, savedTabId)
                }
            }
        }
    }

    private fun initScrollHelper(mainFabInContainer: Boolean) {
        if (!hideMenuOnScroll) return

        val bottomMenu = menu.bottomMenu ?: return

        val fabBottomSpacing = host.context.resources.getDimensionPixelSize(RDesign.dimen.bottom_navigation_height)
        val hideableElements = arrayListOf(
            HideableFabView({ contentContainer.bottomBarProvider.extraFabContainer }, fabBottomSpacing)
        )
        if (!mainFabInContainer) {
            hideableElements.add(HideableFabView({ contentContainer.bottomBarProvider.mainFab }, fabBottomSpacing))
        }
        val hideableNavigationView: HideableNavigationView = HidableNavBunchView(
            bottomMenu.run { getNavView() },
            *(hideableElements.toTypedArray())
        )
        navigationSubscriptions.add(TabNavScrollHelper(contentContainer.scrollHelper, hideableNavigationView))
    }

    private fun configureMenu(lifecycleOwner: LifecycleOwner, menu: MainScreenMenu) {
        val adapter = requireAdapter()
        menu.sideMenu?.run { getNavView() }?.setAdapter(adapter, host.viewLifecycleOwner)
        menu.bottomMenu?.run { getNavView() }?.setAdapter(adapter, host.viewLifecycleOwner)

        val sideMenu = menu.sideMenu
        val bottomMenu = menu.bottomMenu

        if (bottomMenu != null && sideMenu != null) {
            bottomMenu.run { getNavView() }.bindToNavigationDrawer(sideMenu.drawerLayout)
        }

        if (sideMenu != null) {
            drawerListener = DrawerMenuListener(contentContainer) {
                activeItem?.let(this::controllerOf)
            }
            sideMenu.drawerLayout.addDrawerListener(drawerListener!!)
        }

        val handleEvent: (NavigationEvent<NavigationItem>?) -> Unit = { event ->
            var isSame = false
            when (event) {
                is ItemSelected -> event.selectedItem
                is ItemSelectedByUser -> {
                    navigationPageStatistics.startTrace(event.selectedItem.requireId())
                    event.selectedItem
                }

                is SelectedSameItem -> {
                    isSame = true
                    event.selectedItem
                }

                else -> null
            }?.let {
                performSelection(
                    it,
                    ContentController.MenuClick(event is ItemSelectedByUser),
                    isRestored = false,
                    isActiveTabClicked = isSame
                )
            }
        }

        val eventConsumer: Consumer<NavigationEvent<NavigationItem>?> = if (sideMenu != null) {
            MenuItemSelectionConsumer(sideMenu.drawerLayout, handleEvent)
        } else {
            Consumer(handleEvent)
        }

        val debouncedObserver = DebouncedObserver(
            consumer = eventConsumer,
            windowDuration = FRAGMENT_SHOWING_TIME_INTERVAL_LONG
        ) { e ->
            // Предотвращаем блокировку выбора другого элемента, если в release произошла ошибка при выборе текущего.
            selectionInProgress = false
            safeThrow(e)
        }
        navigationSubscriptions.add(debouncedObserver)
        adapter.navigationEvents.observe(lifecycleOwner, debouncedObserver)
        lifecycleOwner.lifecycleScope.launchWhenResumed {
            menu.counters?.collect { counters ->
                adapter.updateCounters(counters.mapValues { it.value.map() })
                menu.sideMenu?.run { getNavView() }?.updateCounters(counters.mapValues { it.value.map() })
            }
        }
    }

    private fun selectManually(
        item: NavigationItem,
        entryPoint: ContentController.EntryPoint,
        isRestored: Boolean,
        selectedTabId: NavxIdDecl? = null
    ) {
        performSelection(item, entryPoint, isRestored, false, selectedTabId)
        requireAdapter().setSelected(item)
    }

    private fun performSelection(
        item: NavigationItem,
        entryPoint: ContentController.EntryPoint,
        isRestored: Boolean,
        isActiveTabClicked: Boolean,
        selectedTabId: NavxIdDecl? = null
    ) {
        if (item == activeItem) {
            if (selectionInProgress) return

            if (entryPoint is ContentController.MenuClick) {
                runWithLockingSelectionFromChanges {
                    controllerOf(item)!!.reselect(item, entryPoint, this, contentContainer)
                }
            } else {
                runWithLockingSelectionFromChanges {
                    controllerOf(item)!!.update(item, entryPoint, this, contentContainer)
                }
            }
            if (isActiveTabClicked) {
                activeItem?.let(this::controllerOf)?.activeTabClicked(item, contentContainer)
                //TODO Убрать когда все откажутся от ScrollToTopSubscriptionHolder
                mainScope?.launch { ScrollToTopSubscriptionHolder.emitEvent(item.persistentUniqueIdentifier) }
            }
            return
        }

        val containerController = controllerOf(item)
            ?: throw IllegalStateException("Не зарегистрирован обработчик для элемента $item")

        runWithLockingSelectionFromChanges {
            // Требуется присвоить раньше выполнения транзакции, так как любой из аддонов может сгенерить доп.событие во время транзакции и это может сломать повторно его затриггерить
            val oldItem = activeItem
            activeItem = item

            refreshPermissionOnLifecycle()

            if (isRestored) {
                containerController.restore(item, this, contentContainer)
                if (entryPoint !is ContentController.MenuClick) {
                    controllerOf(item)!!.update(item, entryPoint, this, contentContainer)
                }
            } else trace("MainScreenWidget#runTransaction") {
                if (selectedTabId == null) {
                    // Сбрасываем предыдущую выбранную вкладку до смены выбранного экрана.
                    navigationItemsManager.onItemSelectionChanged(null)
                }
                val selectionInfo = ContentController.SelectionInfo(
                    newSelectedItem = item,
                    oldSelectedItem = oldItem,
                    entryPoint = entryPoint,
                    itemPageData[item.persistentUniqueIdentifier]
                )
                val transaction = contentContainer.fragmentManager.beginTransaction()
                    .disallowAddToBackStack()
                    .runOnCommit {
                        if (navigationItemsManager.hasChildren(item.navxId)) {
                            navigationPageStatistics.endTracePostponed()
                        } else {
                            navigationPageStatistics.endTrace()
                        }
                    }

                oldItem?.let { controllerOf(it) }?.run {
                    deselect(selectionInfo, this@MainScreenWidget, contentContainer, transaction)
                }

                // Нельзя помещать внуть runOnCommit(), так как отработает после основных вызовов жизненного цикла фрагмента.
                // Если встраиваемые фрагменты при старте отправляют события в ScrollHelper, то это приведет к рассинхрону.
                contentContainer.scrollHelper.resetState()
                containerController.select(selectionInfo, this, contentContainer, transaction)

                if (entryPoint !is ContentController.NavigationChangeEvent) {
                    removeOverlayFragment(item.navxIdentifier)
                }

                trace("MainScreenWidget#commitTransaction") {
                    try {
                        transaction.commitNowAllowingStateLoss()
                    } catch (e: IllegalStateException) {
                        logFragmentManagerDestroyedIssueAnalytics(host.fragmentActivity.supportFragmentManager)
                        throw e
                    }
                }
            }
            // Восстанавливаем выбранную вкладку с последней сессии, когда хост уже проинициализирован.
            selectedTabId?.let {
                navigationItemsManager.onItemSelectionChanged(selectedTabId)
            }

        }

        containerController.onSelectionFinished()
    }

    private fun removeOverlayFragment(selectedItemId: String) {
        fun remove() = host.overlayFragmentHolder?.removeFragmentImmediate()
        try {
            remove()
        } catch (e: IllegalStateException) {
            val fragments = host.fragmentActivity.supportFragmentManager.fragments
            Timber.w("Unable to remove overlayFragment (${e.message}). Selected item: $selectedItemId. Fragments: $fragments")
            Handler(Looper.getMainLooper()).post(::remove)
        }
    }

    private fun runWithLockingSelectionFromChanges(block: () -> Unit) {
        ensureNonLockingSelection()
        selectionInProgress = true
        block()
        selectionInProgress = false
    }

    private fun ensureNonLockingSelection() {
        require(!selectionInProgress) { "Нельзя выбирать элемент в процессе совершения транзакции" }
    }

    private fun requireAdapter(): NavAdapter<NavigationItem> = menuItems.requireAdapter()

    private fun controllerOf(item: NavigationItem): ContentController? =
        menuItems[item.persistentUniqueIdentifier]?.controller

    private fun executeOnCurrentController(block: (NavigationItem, ContentController) -> Unit) {
        val selectedItem = activeItem
            ?: throw IllegalStateException("Нет активной вкладки на главном экране.")
        val selectedController = controllerOf(selectedItem)
            ?: throw IllegalStateException("Не найден ContentController для ${selectedItem.persistentUniqueIdentifier}")
        block(selectedItem, selectedController)
    }

    private fun onItemHidden(record: MenuItemRecord) {
        /*
        Перемещаем выбор на первый видимый элемент, если ранее выбранный был скрыт. Если значение видимости не было
        получено от источника, не перемещаем выделение, пока не убедимся, что начальное значение видимости - `false`.
         */
        if (record.persistentUniqueIdentifier == activeItem?.persistentUniqueIdentifier
            && record.hasReceivedVisibilityValue && isItemNotCollapsed(record.item)) {
            if (isResumed()) {
                switchToFirstVisibleItem()
            } else {
                // Можем столкнуться с IllegalStateException, откладываем перевыбор элемента.
                pendingSwitchToFirstVisibleItem = true
            }
        }
    }

    private fun switchToFirstVisibleItem() {
        navigationItemsManager.findFirstVisibleItem()?.let {
            selectManually(it, ContentController.NavigationChangeEvent, false)
        }
    }

    private fun onTabSelected(navxId: NavxIdDecl) {
        if (contentContainer.fragmentManager.isStateSaved) return
        if (activeTabNavxId != null || navxId != getSavedTabNavxId()) {
            sendTabOpeningStatistics(navxId.ids.first())
        }
        activeItem?.let(::controllerOf)?.selectSubScreen(
            navxId,
            lastEntryPoint?.also { lastEntryPoint = null }
                ?: ContentController.MenuClick(),
            this,
            contentContainer
        )
        activeTabNavxId = navxId
    }

    private fun sendTabOpeningStatistics(navxId: String) {
        val fragmentManager = contentContainer.fragmentManager
        val endTabTraceAction = Runnable {
            navigationPageStatistics.endTrace(navxId)
        }
        /*
        Опираемся на событие OnGlobalLayout для определения приблизительного момента отображения нового содержимого
        после смены вкладки.
        */
        fragmentManager.fragments.firstOrNull()?.view?.run {
            val listener = doOnNextGlobalLayout {
                endTabTraceAction.run()
                handler.removeCallbacks(endTabTraceAction)
            }
            // Чтобы избежать случаев, когда observer не удаляется, и становится вечным.
            doOnDetachedFromWindow {
                viewTreeObserver.removeOnGlobalLayoutListener(listener)
            }
        }
        navigationPageStatistics.startOrOverrideTrace(navxId)
        /*
        Для подстраховки, если OnGlobalLayout долго не наступал, по истечении таймаута принудительно отправляем событие
        смены вкладки для статистики.
        */
        handler.postDelayed(endTabTraceAction, TAB_STATISTICS_TIMEOUT_MS)
    }

    private fun onPageDataAvailable(pageData: NavigationPageData, id: String) {
        itemPageData[id] = pageData
        menuItems[id]?.controller?.onPageDataAvailable(pageData, this, contentContainer)
    }

    private fun ensureItemsOrdered(items: List<NavigationItem>) {
        requireAdapter().reorder(items)
    }

    private fun isResumed() = host.viewLifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)

    private fun isItemNotCollapsed(item: NavigationItem) = (item as? CollapsibleNavigationItem)?.isCollapsed != true

    /**
     * Установка дефолтного обработчика крашей приложения.
     * В случае если у нас приложение крашнулось, приложение должно открываться
     * на дефолтном пункте навигации, чтобы не блокировать UI пользователя.
     */
    private fun setupExceptionHandler() {
        val defaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()

        // Используем слабые ссылки чтобы не хранить MainScreenWidget с контекстом (см. MainScreenHost) в статике.
        val weakPersistentStrategy = WeakReference(persistentStrategy)
        val weakStorage = WeakReference(storage)

        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            if (weakPersistentStrategy.get() is PersistentStrategy.StoreUntilLogout)
                weakStorage.get()?.reset()
            defaultUncaughtExceptionHandler?.uncaughtException(thread, throwable)
        }
    }

    private fun MenuCounters.map(): NavigationCounters {
        return NavigationCounters(
            name = name,
            unreadCounter = unreadCounter,
            unviewedCounter = unviewedCounter,
            totalCounter = totalCounter
        )
    }

    private fun shouldRestoreNavigationItem() =
        MainScreenPlugin.autotestsParametersProvider?.get()?.restoreActiveNavigationItem ?: true

    private fun getSavedTabNavxId() = storage.getString(KEY_SELECTED_TAB_ID)?.let { NavxId.of(it) }

    companion object {
        private const val KEY_SELECTED_ITEM_ID = "menu_selected_item_id"
        private const val KEY_SELECTED_TAB_ID = "menu_selected_tab_id"

        private const val FRAGMENT_SHOWING_TIME_INTERVAL_LONG = 300L
        private const val TOP_NAVIGATION_SEARCH_MAX_DEPTH = 5
        private const val TAB_STATISTICS_TIMEOUT_MS = 800L
    }

    /**
     * Стратегия сохранения состояния компонента. (выбранный элемент и прочее)
     *
     * @author kv.martyshenko
     */
    sealed interface PersistentStrategy {

        /**
         * Храним состояние до перезапуска приложения
         */
        object StoreUntilAppRestart : PersistentStrategy

        /**
         * Храним состояние до перелогина
         */
        class StoreUntilLogout(val userId: Lazy<Int>) : PersistentStrategy
    }

}