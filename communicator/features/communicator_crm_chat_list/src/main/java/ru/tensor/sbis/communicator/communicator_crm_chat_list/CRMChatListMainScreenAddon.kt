package ru.tensor.sbis.communicator.communicator_crm_chat_list

import android.os.Build
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.tensor.sbis.common.navigation.NavxId
import ru.tensor.sbis.communicator.common.util.castTo
import ru.tensor.sbis.communicator.push.CRMContentCategory
import ru.tensor.sbis.deeplink.DeeplinkAction
import ru.tensor.sbis.deeplink.DeeplinkActionNode
import ru.tensor.sbis.deeplink.OpenCRMConversationDeepLinkAction
import ru.tensor.sbis.design.navigation.view.model.NavigationItem
import ru.tensor.sbis.design.navigation.view.model.NavigationItemIcon
import ru.tensor.sbis.design.navigation.view.model.NavigationItemLabel
import ru.tensor.sbis.main_screen_decl.ConfigurableMainScreen
import ru.tensor.sbis.main_screen_decl.MainScreen
import ru.tensor.sbis.main_screen_decl.MainScreenAddon
import ru.tensor.sbis.main_screen_decl.content.ContentContainer
import ru.tensor.sbis.main_screen_decl.content.ContentController
import ru.tensor.sbis.main_screen_decl.content.SimplifiedContentController
import ru.tensor.sbis.main_screen_decl.content.install.FragmentInstallationStrategy
import ru.tensor.sbis.main_screen_decl.navigation.DefaultNavigationItem
import ru.tensor.sbis.main_screen_deeplink_handle_extension.DeepLinkHandleMainScreenExtension
import ru.tensor.sbis.main_screen_deeplink_handle_extension.deepLinkHandleExtension
import ru.tensor.sbis.main_screen_push_handle_extension.PushHandleMainScreenExtension
import ru.tensor.sbis.main_screen_push_handle_extension.pushHandleExtension
import ru.tensor.sbis.pushnotification.PushContentCategory
import java.util.UUID

/**
 * Аддон реестра CRM чатов.
 *
 * @author da.zhukov
 */
internal class CRMChatListMainScreenAddon(
    private val visibilitySource: (ConfigurableMainScreen) -> LiveData<Boolean>,
    fragmentInstallationStrategy: FragmentInstallationStrategy,
    private val fragmentFactory: (UUID?) -> Fragment
) : SimplifiedContentController(fragmentInstallationStrategy),
    MainScreenAddon,
    PushHandleMainScreenExtension.PushIntentResolver,
    DeepLinkHandleMainScreenExtension.DeepLinkIntentResolver {

    private val navigationItemLabel = NavigationItemLabel(
        R.string.communicator_crm_chat_list_screen_addon_menu_item
    )

    private val navigationItemIcon = NavigationItemIcon(
        ru.tensor.sbis.design.R.string.design_nav_icon_client_chat,
        ru.tensor.sbis.design.R.string.design_nav_icon_client_chat_fill
    )

    private val navigationItem = DefaultNavigationItem(
        navigationItemLabel = navigationItemLabel,
        navigationItemIcon = navigationItemIcon,
        persistentUniqueIdentifier = CRM_CHAT_PERSISTENT_UNIQUE_IDENTIFIER,
        navxIdentifier = NavxId.CLAIM_CHATS
    )

    override fun setup(mainScreen: ConfigurableMainScreen) {
        val configuration = ConfigurableMainScreen.MenuItemConfiguration(
            visibilitySource = visibilitySource(mainScreen)
        )
        mainScreen.addItem(navigationItem, configuration, this)

        requireNotNull(mainScreen.pushHandleExtension()).registerPushResolver(this)
        requireNotNull(mainScreen.deepLinkHandleExtension()).registerDeepLinkResolver(this)
    }

    override fun reset(mainScreen: ConfigurableMainScreen) {
        mainScreen.removeItem(navigationItem)

        requireNotNull(mainScreen.pushHandleExtension()).unregisterPushResolver(this)
        requireNotNull(mainScreen.deepLinkHandleExtension()).unregisterDeepLinkResolver(this)
    }

    override fun recognizePushCategory(pushContentCategory: PushContentCategory): Boolean {
        return pushContentCategory is CRMContentCategory
    }

    override fun getAssociatedMenuItemForPush(pushContentCategory: PushContentCategory): NavigationItem = navigationItem

    override fun recognizeDeepLink(deeplinkAction: DeeplinkAction): Boolean {
        return deeplinkAction is OpenCRMConversationDeepLinkAction
    }

    override fun getAssociatedMenuItemForDeepLink(deeplinkAction: DeeplinkAction): NavigationItem {
        return navigationItem
    }

    override fun update(
        navigationItem: NavigationItem,
        entryPoint: ContentController.EntryPoint,
        mainScreen: MainScreen,
        contentContainer: ContentContainer
    ) {
        val action = extractDeepLinkAction<OpenCRMConversationDeepLinkAction>(entryPoint) ?: return
        contentContainer.fragmentManager.fragments.last().castTo<DeeplinkActionNode>()?.onNewDeeplinkAction(action)
    }

    override fun createScreen(selectionInfo: ContentController.SelectionInfo, mainScreen: MainScreen): ContentInfo {
        val entryPoint = selectionInfo.entryPoint as? PushHandleMainScreenExtension.PushNotification
        val action = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            entryPoint?.intent?.getSerializableExtra(
                DeeplinkActionNode.EXTRA_DEEPLINK_ACTION,
                OpenCRMConversationDeepLinkAction::class.java
            )
        } else {
            entryPoint?.intent?.getSerializableExtra(DeeplinkActionNode.EXTRA_DEEPLINK_ACTION) as? OpenCRMConversationDeepLinkAction
        }
        return ContentInfo(fragmentFactory(action?.dialogUuid), CRM_CHAT_LIST_MAIN_SCREEN_ADDON_TAG)
    }

    companion object {
        internal const val CRM_CHAT_LIST_MAIN_SCREEN_ADDON_TAG = "CRMChatListMainScreenAddonTag"
        private const val CRM_CHAT_PERSISTENT_UNIQUE_IDENTIFIER = "crmChats"

        @JvmStatic
        fun defaultVisibilitySourceProvider(): (ConfigurableMainScreen) -> LiveData<Boolean> {
            return { MutableLiveData(true) }
        }
    }
}

/**
 * Извлекает [DeeplinkAction] заданного типа, в зависимости от конкретного типа [ContentController.EntryPoint]
 */
private inline fun <reified ACTION : DeeplinkAction> extractDeepLinkAction(entryPoint: ContentController.EntryPoint): ACTION? {
    return when (entryPoint) {
        is PushHandleMainScreenExtension.PushNotification -> {
            DeeplinkActionNode.getDeeplinkAction<ACTION>(entryPoint.intent)
        }
        is DeepLinkHandleMainScreenExtension.DeepLink -> {
            entryPoint.action as? ACTION
        }
        else -> null
    }
}