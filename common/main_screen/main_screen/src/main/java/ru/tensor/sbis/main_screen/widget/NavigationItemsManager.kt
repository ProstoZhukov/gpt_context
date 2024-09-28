package ru.tensor.sbis.main_screen.widget

import androidx.lifecycle.Observer
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ru.tensor.sbis.common.navigation.NavxId
import ru.tensor.sbis.common.util.AppConfig
import ru.tensor.sbis.design.navigation.util.ControllerNavIcon
import ru.tensor.sbis.design.navigation.util.mapTextToIconRes
import ru.tensor.sbis.design.navigation.view.model.NavigationItem
import ru.tensor.sbis.design.navigation.view.model.NavigationItemLabel
import ru.tensor.sbis.design.navigation.view.view.NavigationView
import ru.tensor.sbis.design.theme.res.PlatformSbisString
import ru.tensor.sbis.main_screen.widget.dashboard.DashboardNavigationBuilder
import ru.tensor.sbis.main_screen.widget.storage.NavigationItemStorage
import ru.tensor.sbis.main_screen.widget.util.NOT_FOUND_ICON_STUB
import ru.tensor.sbis.main_screen.widget.util.TopNavigationTitleUpdateManager
import ru.tensor.sbis.main_screen_decl.ConfigurableMainScreen
import ru.tensor.sbis.main_screen_decl.env.SideMenu
import ru.tensor.sbis.main_screen_decl.navigation.service.ItemType
import ru.tensor.sbis.main_screen_decl.navigation.service.NavigationPageData
import ru.tensor.sbis.main_screen_decl.navigation.service.NavigationService
import ru.tensor.sbis.main_screen_decl.navigation.service.NavigationServiceItem
import ru.tensor.sbis.main_screen_decl.navigation.service.NavigationServiceNode
import ru.tensor.sbis.main_screen_decl.navigation.service.asFlatList
import ru.tensor.sbis.toolbox_decl.navigation.NavxIdDecl
import ru.tensor.sbis.toolbox_decl.toolbar.NavigationTab
import ru.tensor.sbis.toolbox_decl.toolbar.ToolbarTabsController
import ru.tensor.sbis.verification_decl.login.LoginInterface
import timber.log.Timber

/**
 * Управляет видимостью и иконками пунктов навигации.
 * Видимость пунктов определяется источником ([ConfigurableMainScreen.MenuItemConfiguration.visibilitySource]), который
 * задаётся в плагинах разделов, а также списком доступных разделов, полученным от микросервиса ([NavigationService]).
 *
 * @param onItemHidden Вызывается при скрытии элемента, с указанием набора идентификаторов, которые на данный момент
 * видимы.
 *
 * Иконки разделов определяются из данных с микросервиса ([NavigationService])
 * @author us.bessonov
 */
internal class NavigationItemsManager(
    private val sideNavView: NavigationView?,
    private val bottomNavView: NavigationView?,
    private val header: SideMenu.DefaultHeader?,
    private val footer: SideMenu.DefaultFooter?,
    private val menuItems: MenuConfigurator,
    private val onItemHidden: (MenuItemRecord, Set<String>) -> Unit,
    private val onTabSelected: (navxId: NavxIdDecl) -> Unit,
    private val onPageDataAvailable: (pageData: NavigationPageData, persistentUniqueId: String) -> Unit,
    private val ensureItemsOrdered: (items: List<NavigationItem>) -> Unit,
    private val topNavigationTitleUpdateManager: TopNavigationTitleUpdateManager,
    private val dashboardNavigationBuilder: DashboardNavigationBuilder = DashboardNavigationBuilder(menuItems),
    private val navigationService: NavigationService = MainScreenPlugin.navigationServiceProvider?.get()
        ?: AlwaysAvailableNavigationService(),
    private val toolbarTabsController: ToolbarTabsController? = MainScreenPlugin.tabsVisibilityController?.get(),
    private val loginInterface: LoginInterface? = MainScreenPlugin.loginInterfaceProvider?.get(),
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    private var showAll = true
    private var hasNavigationServiceResult = false
    private var availableItems = setOf<String>()
    private val visibleItems = mutableSetOf<String>()
    private val hiddenItems = mutableSetOf<String>()
    private val visibilityObservers = mutableMapOf<MenuItemRecord, Observer<Boolean>>()
    private var identifierIcons = mutableMapOf<NavxId, String?>()
    private var identifierLabels = mutableMapOf<NavxId, NavigationItemLabel>()
    private val children = mutableMapOf<NavxIdDecl, MutableList<NavxIdDecl>>()

    /** @SelfDocumented */
    fun init(scope: CoroutineScope) {
        //TODO: удалить, либо доработать после https://online.sbis.ru/opendoc.html?guid=8c4f9b41-babd-4614-ad34-dc3e4b271768&client=3
        NavigationItemStorage.root?.let(::updateItems)
        observeAvailableItems(scope)
        observeSelectedTab(scope)
    }

    /** @SelfDocumented */
    fun onItemAdded(itemRecord: MenuItemRecord) {
        updateItemVisibility(itemRecord)
        updateItemIcon(itemRecord.navxIdentifier)
        visibilityObservers[itemRecord] = itemRecord.observeVisibility {
            updateItemVisibility(itemRecord)
        }
    }

    /** @SelfDocumented */
    fun onItemRemoved(itemRecord: MenuItemRecord) = with(itemRecord) {
        visibilityObservers.remove(itemRecord)
            ?.let(itemRecord::removeVisibilityObserver)
        visibleItems.remove(persistentUniqueIdentifier)
    }

    /** @SelfDocumented */
    fun isItemVisible(item: NavigationItem): Boolean {
        if (!hasNavigationServiceResult) {
            //TODO: https://online.sbis.ru/opendoc.html?guid=8c4f9b41-babd-4614-ad34-dc3e4b271768&client=3
            try {
                forceFetchAvailableItems()
            } catch (e: Exception) {
                Timber.w(e,"Не удалось принудительно обновить доступные элементы")
            }
        }
        return visibleItems.contains(item.persistentUniqueIdentifier)
    }

    /** @SelfDocumented */
    fun findFirstVisibleItem() = menuItems.items.find { isItemVisible(it.navxIdentifier, it.isVisible) }?.item

    /** @SelfDocumented */
    fun onItemSelectionChanged(selectedTabNavxId: NavxIdDecl?) {
        toolbarTabsController?.setSelectedTabValue(selectedTabNavxId)
    }

    /** @SelfDocumented */
    fun onReset() {
        visibilityObservers.forEach { (record, observer) ->
            record.removeVisibilityObserver(observer)
        }
    }

    /** @SelfDocumented */
    fun hasChildren(itemId: NavxIdDecl?): Boolean {
        val children = itemId?.let { children[it] }
            ?: return false
        return children.isNotEmpty()
    }

    private fun determineChildrenByParent(root: NavigationServiceNode?) {
        children.clear()
        root.asFlatList { it.parent != null }.forEach { item ->
            item.parentId
                ?.let(NavxId.Companion::of)
                ?.let { parentNavxId ->
                    val id = item.navxId
                    if (id != null) {
                        children.getOrPut(parentNavxId) { mutableListOf() }.add(id)
                    }
                }
        }
    }

    private fun updateItemIcon(itemId: NavxIdDecl?) {
        itemId ?: return
        if (sideNavView?.isUsedNavigationIcons() == true && bottomNavView?.isUsedNavigationIcons() == true) {
            val icon = identifierIcons[itemId]
                ?.let { mapTextToIconRes(it) ?: getStubIcon() }
                ?: return
            menuItems.items
                .filter { it.navxIdentifier == itemId }
                .map { it.item }
                .forEach { item ->
                    sideNavView.changeItemIcon(item, icon)
                    bottomNavView.changeItemIcon(item, icon)
                }
        }
    }

    private fun updateItemLabel(itemId: NavxId) {
        val label = identifierLabels[itemId]
            ?: getStubLabel()
            ?: return
        menuItems.items.filter { it.navxIdentifier == itemId }
            .map { it.item }
            .forEach { item ->
                sideNavView?.changeItemLabel(item, label)
                bottomNavView?.changeItemLabel(item, label)
            }
    }

    private fun getStubIcon() = if (shouldUseStubItemData()) {
        ControllerNavIcon(NOT_FOUND_ICON_STUB, NOT_FOUND_ICON_STUB)
    } else {
        null
    }

    private fun getStubLabel() = if (shouldUseStubItemData()) {
        NavigationItemLabel(PlatformSbisString.Value(null.toString()))
    } else {
        null
    }

    private fun shouldUseStubItemData() = AppConfig.isDebug() && availableItems.isNotEmpty()

    private fun observeAvailableItems(scope: CoroutineScope) {
        scope.launch {
            navigationService.getNavigationHierarchyFlow()
                .map {
                    NavigationItemsWithUserInfo(
                        it,
                        isPhysic = isPhysicAccount()
                    )
                }
                .flowOn(ioDispatcher)
                .collect {
                    updateItems(it.root, it.isPhysic)
                    hasNavigationServiceResult = true
                    it.root?.let(NavigationItemStorage::update)
                }
        }
    }

    private fun forceFetchAvailableItems() {
        val root = runBlocking {
            navigationService.getNavigationHierarchy()
        }
        updateItems(root)
    }

    private fun updateItems(root: NavigationServiceNode?) {
        updateItems(root, isPhysicAccount())
        hasNavigationServiceResult = true
    }

    private fun updateItems(root: NavigationServiceNode?, isPhysic: Boolean) {
        val items = root.asFlatList(::isTopLevelItem)
        determineChildrenByParent(root)
        items.forEach {
            val id = it.navxId
                ?: return@forEach
            if (it.icon != null) identifierIcons[id] = it.icon
            identifierLabels[id] = it.getLabel()
            updateItemIcon(id)
            updateItemLabel(id)
        }
        availableItems = items.getAvailableItems()
        val serviceItems =  items.filter { it.itemId.isNotEmpty() }
        showAll = isPhysic || serviceItems.isEmpty()
        val availableTabs = root.asFlatList(::isAvailableTab).toNavigationTabSet()
        Timber.d("Available navigation items: $availableItems, available tabs: $availableTabs")
        dashboardNavigationBuilder.addDashboardPages(serviceItems)
        if (serviceItems.isNotEmpty()) {
            updateItemsOrder(serviceItems)
        }
        updateItemsVisibility()
        updateAvailableTabs(availableTabs)
        updateTopNavigationTitle(items)
        updateNavigationPageData(items)
    }

    private fun updateAvailableTabs(availableTabs: Set<NavigationTab>) {
        toolbarTabsController?.updateTabs(if (!showAll) availableTabs else emptySet())
    }

    private fun observeSelectedTab(scope: CoroutineScope) {
        scope.launch {
            toolbarTabsController?.tabSelectionFlow
                ?.filterNotNull()
                ?.collect(onTabSelected)
        }
    }

    private fun updateItemVisibility(itemRecord: MenuItemRecord) = with(itemRecord) {
        val isVisible = isItemVisible(navxIdentifier, isVisible)
        applyVisibility(itemRecord, isVisible)
        val isChanged = isVisible && !visibleItems.contains(persistentUniqueIdentifier)
            || !isVisible && !hiddenItems.contains(persistentUniqueIdentifier)
        if (isChanged)
            itemRecord.notifyVisibilityChanged(isVisible)
        if (isVisible) {
            visibleItems.add(persistentUniqueIdentifier)
            hiddenItems.remove(persistentUniqueIdentifier)
        } else {
            visibleItems.remove(persistentUniqueIdentifier)
            hiddenItems.add(persistentUniqueIdentifier)
            onItemHidden(itemRecord, visibleItems)
        }
        navxIdentifier?.let {
            updateHeader(it, isVisible)
            updateFooter(it, isVisible)
        }
    }

    private fun updateItemsOrder(serviceItems: List<NavigationServiceItem>) {
        val menuItemsByNavxId = menuItems.items
            .filter { it.navxIdentifier != null }
            .associate { it.navxIdentifier to it.item }
        val orderedItems = buildList {
            serviceItems.forEach {
                menuItemsByNavxId[it.navxId]?.let(::add)
            }
        }
        ensureItemsOrdered(orderedItems)
    }

    private fun updateHeader(navxId: NavxIdDecl, isEnabled: Boolean) {
        header?.takeIf { it.navxIdentifier == navxId }
            ?.let {
                it.setEnabled(isEnabled)
                it.setHeaderCounterVisibility(isEnabled)
            }
    }

    private fun updateFooter(navxId: NavxIdDecl, isVisible: Boolean) {
        footer?.takeIf { it.navxIdentifier == navxId }
            ?.setVisible(isVisible)
    }

    private fun updateNavigationPageData(items: List<NavigationServiceItem>) {
        items.filter { it.pageData != null }.forEach {
            onPageDataAvailable(it.pageData!!, getPersistentUniqueIdentifier(it.itemId))
        }
    }

    private fun getPersistentUniqueIdentifier(navxId: String): String {
        return menuItems.entries.find { (_, value) ->
            value.navxIdentifier?.ids?.any { it == navxId } ?: false
        }?.key ?: navxId
    }

    private fun applyVisibility(itemRecord: MenuItemRecord, isVisible: Boolean) = with(itemRecord) {
        if (isInstalledInSideMenu && isVisible) {
            sideNavView?.showItem(item)
        } else {
            sideNavView?.hideItem(item)
        }
        if (isInstalledInBottomMenu && isVisible) {
            bottomNavView?.showItem(item)
        } else {
            bottomNavView?.hideItem(item)
        }
    }

    private fun updateItemsVisibility() = menuItems.items.forEach(::updateItemVisibility)

    private fun updateTopNavigationTitle(items: List<NavigationServiceItem>) {
        topNavigationTitleUpdateManager.onAvailableNavigationUpdated(items)
    }

    private fun isItemVisible(navxIdentifier: NavxIdDecl?, isProbablyVisible: Boolean) = isProbablyVisible
        && (showAll || navxIdentifier?.forceEnabled == true
        || navxIdentifier?.ids?.any { availableItems.contains(it) } ?: true)

    private fun List<NavigationServiceItem>.getAvailableItems() =
        filter { it.itemId.isNotEmpty() && it.itemType == ItemType.MAIN && it.isVisible }
            .map { it.itemId }
            .toSet()

    private fun List<NavigationServiceItem>.toNavigationTabSet() =
        map { NavigationTab(it.navxId, it.parentId.orEmpty(), it.title.orEmpty()) }
            .toSet()

    private fun isPhysicAccount() = loginInterface?.getCurrentAccount()?.isPhysic ?: false

    private fun NavigationServiceItem.getLabel() = NavigationItemLabel(
        PlatformSbisString.Value(title.orEmpty()),
        PlatformSbisString.Value(shortTitle.orEmpty())
    )

    private fun isTopLevelItem(node: NavigationServiceNode) =
        // Учитываем только элементы верхнего уровня (отбрасываем те, у кого родитель - не root).
        node.parent?.isEmpty != false && node.data.itemType == ItemType.MAIN

    private fun isAvailableTab(node: NavigationServiceNode): Boolean = with(node) {
        // TODO: https://online.sbis.ru/opendoc.html?guid=c0171868-f089-41c2-95b8-7199c77ef960&client=3
        return data.itemId.isNotEmpty() && data.isVisible
    }

    private fun MenuItemRecord.notifyVisibilityChanged(isVisible: Boolean) {
        controller?.onItemVisibilityChanged(item, isVisible)
    }
}

private class NavigationItemsWithUserInfo(
    val root: NavigationServiceNode?,
    val isPhysic: Boolean
)

/**
 * Заглушка при отсутствии сервиса навигации в приложении. Обеспечивает беспрепятственное отображение пунктов навигации.
 */
private class AlwaysAvailableNavigationService : NavigationService {
    override fun getAvailableItemsFlow(): Flow<List<NavigationServiceItem>> = emptyFlow()
    override suspend fun getAvailableItems(): List<NavigationServiceItem> = emptyList()
    override fun getNavigationHierarchyFlow(): Flow<NavigationServiceNode> = emptyFlow()
    override suspend fun getNavigationHierarchy(): NavigationServiceNode? = null
}
