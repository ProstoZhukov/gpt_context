package ru.tensor.sbis.contacts_main_screen_addon

import androidx.lifecycle.LiveData
import ru.tensor.sbis.common.navigation.MenuNavigationItemType
import ru.tensor.sbis.common.navigation.NavxId
import ru.tensor.sbis.communicator.contacts_declaration.registry.ContactsRegistryFragmentFactory
import ru.tensor.sbis.design.navigation.view.model.NavigationItem
import ru.tensor.sbis.design.navigation.view.model.NavigationItemIcon
import ru.tensor.sbis.design.navigation.view.model.NavigationItemLabel
import ru.tensor.sbis.main_screen_decl.ConfigurableMainScreen
import ru.tensor.sbis.main_screen_decl.MainScreen
import ru.tensor.sbis.main_screen_decl.MainScreenAddon
import ru.tensor.sbis.main_screen_decl.content.ContentController
import ru.tensor.sbis.main_screen_decl.content.SimplifiedContentController
import ru.tensor.sbis.main_screen_decl.content.install.FragmentInstallationStrategy
import ru.tensor.sbis.main_screen_decl.navigation.DefaultNavigationItem
import ru.tensor.sbis.main_screen_decl.navigation.NavigationPermissionsUtil
import ru.tensor.sbis.main_screen_navigation_event_handle_extension.NavigationEventHandleMainScreenExtension
import ru.tensor.sbis.main_screen_navigation_event_handle_extension.navigationEventHandleExtension
import ru.tensor.sbis.person_card.EmployeesPermissionScope
import ru.tensor.sbis.common.R as RCommon
import ru.tensor.sbis.design.R as RDesign

/**
 * Плагин раздела контактов на главном экране.
 *
 * @author da.zhukov
 */
internal class ContactsMainScreenAddon(
    private val navItem: NavigationItem,
    private val visibilitySourceProvider: (ConfigurableMainScreen) -> LiveData<Boolean>,
    private val contactsRegistryFragmentFactory: ContactsRegistryFragmentFactory,
    fragmentInstallationStrategy: FragmentInstallationStrategy
) : SimplifiedContentController(fragmentInstallationStrategy),
    MainScreenAddon,
    NavigationEventHandleMainScreenExtension.NavTypeIntentResolver {

    override fun setup(mainScreen: ConfigurableMainScreen) {
        mainScreen.addItem(
            navItem,
            configuration = ConfigurableMainScreen.MenuItemConfiguration(
                visibilitySource = visibilitySourceProvider(mainScreen)
            ),
            this
        )
        requireNotNull(mainScreen.navigationEventHandleExtension()).registerNavResolver(this)
    }

    override fun reset(mainScreen: ConfigurableMainScreen) {
        mainScreen.removeItem(navItem)
        requireNotNull(mainScreen.navigationEventHandleExtension()).unregisterNavResolver(this)
    }

    override fun createScreen(
        selectionInfo: ContentController.SelectionInfo,
        mainScreen: MainScreen
    ): ContentInfo {
        return ContentInfo(contactsRegistryFragmentFactory.createContactsRegistryFragment())
    }

    override fun recognizeNavType(menuNavigationItemType: MenuNavigationItemType): Boolean {
        return menuNavigationItemType == MenuNavigationItemType.CONTACTS
    }

    override fun getAssociatedMenuItemForNav(menuNavigationItemType: MenuNavigationItemType): NavigationItem {
        return if (menuNavigationItemType == MenuNavigationItemType.CONTACTS) navItem
        else throw IllegalStateException()
    }

    companion object {

        @Suppress("MemberVisibilityCanBePrivate")
        const val CONTACTS_ITEM_IDENTIFIER = "CONTACTS"

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