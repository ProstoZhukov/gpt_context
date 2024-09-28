package ru.tensor.sbis.contacts_main_screen_addon

import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.mikepenz.iconics.IconicsDrawable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import ru.tensor.sbis.common.CommonUtilsPlugin
import ru.tensor.sbis.common.navigation.MenuNavigationItemType
import ru.tensor.sbis.common.navigation.NavxId
import ru.tensor.sbis.common.util.SharedPreferencesUtils
import ru.tensor.sbis.communicator.declaration.host_factory.ContactsRegistryHostFragmentFactory
import ru.tensor.sbis.communicator.declaration.host_factory.EmployeesRegistryHostFragmentFactory
import ru.tensor.sbis.communicator.declaration.model.CommunicatorRegistryType
import ru.tensor.sbis.contacts_main_screen_addon.ContactsMainScreenAddonPlugin.employeesContactsTabHistory
import ru.tensor.sbis.contacts_main_screen_addon.ContactsMainScreenAddonPlugin.navigationServiceProvider
import ru.tensor.sbis.deeplink.DeeplinkAction
import ru.tensor.sbis.deeplink.DeeplinkActionNode
import ru.tensor.sbis.deeplink.OpenEntityDeeplinkAction
import ru.tensor.sbis.deeplink.SwitchContactTabDeeplinkAction
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.navigation.util.NavTabSelectionListener
import ru.tensor.sbis.design.navigation.view.model.NavigationItem
import ru.tensor.sbis.design.navigation.view.model.NavigationItemIcon
import ru.tensor.sbis.design.navigation.view.model.NavigationItemLabel
import ru.tensor.sbis.main_screen_decl.ConfigurableMainScreen
import ru.tensor.sbis.main_screen_decl.MainScreen
import ru.tensor.sbis.main_screen_decl.MainScreenAddon
import ru.tensor.sbis.main_screen_decl.content.ContentContainer
import ru.tensor.sbis.main_screen_decl.content.ContentController
import ru.tensor.sbis.main_screen_decl.content.SimplifiedContentController
import ru.tensor.sbis.main_screen_decl.content.install.CachedFragmentInstallationStrategy
import ru.tensor.sbis.main_screen_decl.navigation.DefaultNavigationItem
import ru.tensor.sbis.main_screen_decl.navigation.NavigationPermissionsUtil
import ru.tensor.sbis.main_screen_decl.navigation.service.NavigationServiceItem
import ru.tensor.sbis.main_screen_deeplink_handle_extension.DeepLinkHandleMainScreenExtension
import ru.tensor.sbis.main_screen_navigation_event_handle_extension.NavigationEventHandleMainScreenExtension
import ru.tensor.sbis.main_screen_navigation_event_handle_extension.navigationEventHandleExtension
import ru.tensor.sbis.main_screen_push_handle_extension.PushHandleMainScreenExtension
import ru.tensor.sbis.person_card.EmployeesPermissionScope
import ru.tensor.sbis.toolbox_decl.navigation.NavxIdDecl
import ru.tensor.sbis.verification_decl.permission.PermissionLevel
import ru.tensor.sbis.common.R as RCommon
import ru.tensor.sbis.design.R as RDesign

/**
 * Плагин раздела сотрудников/контактов на главном экране
 *
 * @author kv.martyshenko
 */
internal class ContactsMainScreenAddon(
    private val employeesNavItem: NavigationItem,
    private val employeeVisibilitySourceProvider: (ConfigurableMainScreen) -> LiveData<Boolean>,
    private val contactsNavItem: NavigationItem,
    private val contactsVisibilitySourceProvider: (ConfigurableMainScreen) -> LiveData<Boolean>,
    private val employeesRegistryHostFragmentFactory: EmployeesRegistryHostFragmentFactory,
    private val contactsRegistryHostFragmentFactory: ContactsRegistryHostFragmentFactory,
) : SimplifiedContentController(CachedFragmentInstallationStrategy()),
    MainScreenAddon,
    NavigationEventHandleMainScreenExtension.NavTypeIntentResolver {

    private val navigationService = navigationServiceProvider?.get()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var hasEmployeeTab: Boolean = true
    private var hasContactsTab: Boolean = true

    private val employeeFragmentFactory: (DeeplinkAction?) -> Fragment = {
        employeesRegistryHostFragmentFactory.createEmployeesHostFragment(
            CommunicatorRegistryType.EmployeesRegistry,
            it
        )
    }
    private val contactsFragmentFactory: (DeeplinkAction?) -> Fragment = {
        contactsRegistryHostFragmentFactory.createContactsHostFragment(
            CommunicatorRegistryType.ContactsRegistry,
            it
        )
    }

    init {
        navigationService?.let { service ->
            scope.launch {
                val availableItems = service.getAvailableItems()
                hasEmployeeTab = availableItems.hasEmployeeTab()
                hasContactsTab = availableItems.hasContactsTab()
                service.getAvailableItemsFlow().collect {
                    hasEmployeeTab = it.hasEmployeeTab()
                    hasContactsTab = it.hasContactsTab()
                }
            }
        }
    }

    private fun List<NavigationServiceItem>.hasEmployeeTab() =
        find { navItem -> navItem.navxId == NavxId.STAFF } != null

    private fun List<NavigationServiceItem>.hasContactsTab() =
        find { navItem -> navItem.navxId == NavxId.CONTACTS } != null

    // region MainScreenAddon
    override fun setup(mainScreen: ConfigurableMainScreen) {
        mainScreen.addItem(
            employeesNavItem,
            configuration = ConfigurableMainScreen.MenuItemConfiguration(
                visibilitySource = employeeVisibilitySourceProvider(mainScreen)
            ),
            this
        )
        mainScreen.addItem(
            contactsNavItem,
            configuration = ConfigurableMainScreen.MenuItemConfiguration(
                visibilitySource = contactsVisibilitySourceProvider(mainScreen)
            ),
            this
        )

        requireNotNull(mainScreen.navigationEventHandleExtension()).registerNavResolver(this)

        val employeePermission = mainScreen.monitorPermissionScope(EmployeesPermissionScope)
        employeePermission.observe(mainScreen.host.viewLifecycleOwner) { permission ->
            val hasPermission = permission != null && permission >= PermissionLevel.READ
            // TODO костыль для работы модуля сотрудников. Будет поравлено в рамках рефакторинга в модуле сотрудников.
            if (permission != null) {
                saveEmployeeState(hasPermission)
            }
        }
    }

    override fun reset(mainScreen: ConfigurableMainScreen) {
        mainScreen.removeItem(employeesNavItem)
        mainScreen.removeItem(contactsNavItem)

        requireNotNull(mainScreen.navigationEventHandleExtension()).unregisterNavResolver(this)
    }
    // endregion

    // region ContainerController
    override fun createScreen(selectionInfo: ContentController.SelectionInfo, mainScreen: MainScreen): ContentInfo {
        return ContentInfo(createFragment(selectionInfo.newSelectedItem, selectionInfo.entryPoint))
    }

    override fun selectSubScreen(
        navxId: NavxIdDecl,
        entryPoint: ContentController.EntryPoint,
        mainScreen: MainScreen,
        contentContainer: ContentContainer
    ) {
        if (navxId != NavxId.CONTACTS && navxId != NavxId.STAFF) return
        val isContactsTab = (navxId == NavxId.CONTACTS || !hasEmployeeTab) && hasContactsTab
        (fragmentInstallationStrategy.findContent(contentContainer) as? DeeplinkActionNode)?.onNewDeeplinkAction(
            args = extractDeepLinkAction(entryPoint) ?: SwitchContactTabDeeplinkAction(isContactsTab)
        )
    }

    override fun update(
        navigationItem: NavigationItem,
        entryPoint: ContentController.EntryPoint,
        mainScreen: MainScreen,
        contentContainer: ContentContainer
    ) {
        val deepLinkAction = extractDeepLinkAction<OpenEntityDeeplinkAction>(entryPoint)
        if (deepLinkAction != null && !handleEntryPoint(deepLinkAction, contentContainer)) {
            val contentInfo = ContentInfo(createFragment(navigationItem, entryPoint))

            contentContainer.fragmentManager
                .beginTransaction()
                .replace(contentContainer.containerId, contentInfo.fragment, contentInfo.tag)
                .commit()
        }
    }

    override fun onSelectionChanged(
        navigationItem: NavigationItem,
        isSelected: Boolean,
        mainScreen: MainScreen,
        contentContainer: ContentContainer
    ) {
        val fabIcon = if (isSelected) {
            IconicsDrawable(mainScreen.host.context, SbisMobileIcon.Icon.smi_navBarPlus)
        } else null
        contentContainer.bottomBarProvider.setNavigationFabIcon(fabIcon)
        (fragmentInstallationStrategy.findContent(contentContainer) as? NavTabSelectionListener)?.changeSelection(isSelected)
    }
    // endregion

    // region NavTypeIntentResolver
    override fun recognizeNavType(menuNavigationItemType: MenuNavigationItemType): Boolean {
        return menuNavigationItemType == MenuNavigationItemType.CONTACTS ||
                menuNavigationItemType == MenuNavigationItemType.EMPLOYEES
    }

    override fun getAssociatedMenuItemForNav(menuNavigationItemType: MenuNavigationItemType): NavigationItem {
        return when (menuNavigationItemType) {
            MenuNavigationItemType.CONTACTS -> contactsNavItem
            MenuNavigationItemType.EMPLOYEES -> employeesNavItem
            else -> throw IllegalStateException()
        }
    }
    // endregion

    private fun saveEmployeeState(isEmployeeEnabled: Boolean) {
        CommonUtilsPlugin.singletonComponent.sharedPreferences.edit {
            putInt(
                SharedPreferencesUtils.SHARED_PREFS_EMPLOYEES_AVAILABILITY_MODULE_SETTING,
                if (isEmployeeEnabled) 1 else 0
            )
        }
    }

    private fun handleEntryPoint(deeplinkAction: DeeplinkAction, contentContainer: ContentContainer): Boolean {
        val fragment = contentContainer.fragmentManager.findFragmentById(contentContainer.containerId)
        (fragment as? DeeplinkActionNode)?.onNewDeeplinkAction(deeplinkAction)
            ?.also { return true }
        return false
    }

    private fun createFragment(navigationItem: NavigationItem, entryPoint: ContentController.EntryPoint): Fragment {
        val action = extractDeepLinkAction<OpenEntityDeeplinkAction>(entryPoint)
        val identifier = navigationItem.persistentUniqueIdentifier
        val needContacts = NavxId.CONTACTS.ids.contains(employeesContactsTabHistory?.get()?.getLastSelectedTab()) &&
            hasContactsTab
        return when {
            identifier == employeesNavItem.persistentUniqueIdentifier && hasEmployeeTab && !needContacts ->
                employeeFragmentFactory(action)
            identifier == contactsNavItem.persistentUniqueIdentifier || !hasEmployeeTab || needContacts ->
                contactsFragmentFactory(action)
            else -> throw IllegalArgumentException()
        }
    }

    companion object {

        @Suppress("MemberVisibilityCanBePrivate")
        const val EMPLOYEES_ITEM_IDENTIFIER = "EMPLOYEES"

        @Suppress("MemberVisibilityCanBePrivate")
        const val CONTACTS_ITEM_IDENTIFIER = "CONTACTS"

        @JvmStatic
        fun createDefaultEmployeesItem(): DefaultNavigationItem {
            return DefaultNavigationItem(
                navigationItemLabel = NavigationItemLabel(
                    default = RCommon.string.common_navigation_menu_item_employees,
                    short = RCommon.string.common_navigation_menu_item_employees_reduced
                ),
                navigationItemIcon = NavigationItemIcon(
                    default = RDesign.string.design_nav_icon_staff,
                    selected = RDesign.string.design_nav_icon_staff_fill
                ),
                persistentUniqueIdentifier = EMPLOYEES_ITEM_IDENTIFIER,
                navxIdentifier = NavxId.EMPLOYEES
            )
        }

        @JvmStatic
        fun defaultEmployeesVisibilitySourceProvider(): (ConfigurableMainScreen) -> LiveData<Boolean> {
            return NavigationPermissionsUtil
                .createPermissionBasedVisibilitySourceProvider(EmployeesPermissionScope)
        }

        @JvmStatic
        fun createDefaultContactsItem(): DefaultNavigationItem {
            return DefaultNavigationItem(
                navigationItemLabel = NavigationItemLabel(
                    default = RCommon.string.common_navigation_menu_item_contacts
                ),
                navigationItemIcon = NavigationItemIcon(
                    default = RDesign.string.design_nav_icon_profile,
                    selected = RDesign.string.design_nav_icon_profile_fill
                ),
                persistentUniqueIdentifier = CONTACTS_ITEM_IDENTIFIER,
                navxIdentifier = NavxId.CONTACTS
            )
        }

        @JvmStatic
        fun defaultContactsVisibilitySourceProvider(): (ConfigurableMainScreen) -> LiveData<Boolean> {
            return NavigationPermissionsUtil
                .createPermissionBasedVisibilitySourceProvider(EmployeesPermissionScope)
        }
    }
}

/**
 * Извлекает [DeeplinkAction] заданного типа, в зависимости от конкретного типа [ContentController.EntryPoint]
 */
private inline fun <reified ACTION: DeeplinkAction> extractDeepLinkAction(entryPoint: ContentController.EntryPoint): ACTION? {
    return when (entryPoint) {
        is PushHandleMainScreenExtension.PushNotification    -> {
            DeeplinkActionNode.getDeeplinkAction<ACTION>(entryPoint.intent)
        }
        is DeepLinkHandleMainScreenExtension.DeepLink        -> {
            entryPoint.action as? ACTION
        }
        is NavigationEventHandleMainScreenExtension.NavEvent -> {
            DeeplinkActionNode.getDeeplinkAction<ACTION>(entryPoint.intent)
        }
        else -> null
    }
}