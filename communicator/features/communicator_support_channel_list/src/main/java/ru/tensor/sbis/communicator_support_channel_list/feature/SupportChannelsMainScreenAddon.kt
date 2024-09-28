package ru.tensor.sbis.communicator_support_channel_list.feature

import android.os.Build
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import ru.tensor.sbis.common.navigation.NavxId
import ru.tensor.sbis.communicator.push.SupportClientConversationCategory
import ru.tensor.sbis.communicator_support_channel_list.R
import ru.tensor.sbis.communicator_support_channel_list.di.SupportChannelListPlugin.communicatorPushKeyboardHelperProvider
import ru.tensor.sbis.deeplink.DeeplinkActionNode
import ru.tensor.sbis.deeplink.OpenSupportConversationDeepLinkAction
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
import ru.tensor.sbis.main_screen_push_handle_extension.PushHandleMainScreenExtension
import ru.tensor.sbis.main_screen_push_handle_extension.pushHandleExtension
import ru.tensor.sbis.pushnotification.PushContentCategory
import java.util.UUID

internal class SupportChannelsMainScreenAddon(
    private val visibilitySource: LiveData<Boolean>,
    fragmentInstallationStrategy: FragmentInstallationStrategy,
    private val fragmentFactory: (UUID?) -> Fragment
) : SimplifiedContentController(fragmentInstallationStrategy),
    MainScreenAddon, PushHandleMainScreenExtension.PushIntentResolver {

    private val navigationItemLabel = NavigationItemLabel(
        default = R.string.communicator_support_channel_list_title,
        short = R.string.communicator_support_channel_list_title_reduce
    )

    private val navigationItemIcon = NavigationItemIcon(
        ru.tensor.sbis.design.R.string.design_nav_icon_support,
        ru.tensor.sbis.design.R.string.design_nav_icon_support_fill
    )

    private val navigationItem = DefaultNavigationItem(
        navigationItemLabel,
        navigationItemIcon,
        persistentUniqueIdentifier = PERSISTENT_UNIQUE_IDENTIFIER,
        navxIdentifier = NavxId.SUPPORT
    )

    override fun setup(mainScreen: ConfigurableMainScreen) {

        val configuration = ConfigurableMainScreen.MenuItemConfiguration(
            visibilitySource = visibilitySource
        )

        mainScreen.addItem(navigationItem, configuration, this)
        requireNotNull(mainScreen.pushHandleExtension()).registerPushResolver(this)
    }

    override fun reset(mainScreen: ConfigurableMainScreen) {
        requireNotNull(mainScreen.pushHandleExtension()).unregisterPushResolver(this)
    }

    override fun recognizePushCategory(pushContentCategory: PushContentCategory): Boolean {
        val isSupportCategory = pushContentCategory is SupportClientConversationCategory
        if (isSupportCategory) {
            communicatorPushKeyboardHelperProvider.get()
                .getCommunicatorPushKeyboardHelper()
                .hideKeyboard.tryEmit(true)
        }
        return isSupportCategory
    }

    override fun update(
        navigationItem: NavigationItem,
        entryPoint: ContentController.EntryPoint,
        mainScreen: MainScreen,
        contentContainer: ContentContainer
    ) {
        val pushEntryPoint = entryPoint as? PushHandleMainScreenExtension.PushNotification

        val action = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pushEntryPoint?.intent?.getSerializableExtra(
                DeeplinkActionNode.EXTRA_DEEPLINK_ACTION,
                OpenSupportConversationDeepLinkAction::class.java
            )
        } else {
            pushEntryPoint?.intent?.getSerializableExtra(DeeplinkActionNode.EXTRA_DEEPLINK_ACTION) as? OpenSupportConversationDeepLinkAction
        }
        if (action != null) {
            val contentInfo = ContentInfo(
                fragmentFactory(action.dialogUuid),
                SUPPORT_CHANNELS_MAIN_SCREEN_ADDON_TAG
            )

            contentContainer.fragmentManager
                .beginTransaction()
                .replace(contentContainer.containerId, contentInfo.fragment, contentInfo.tag)
                .commit()
        }
    }

    override fun getAssociatedMenuItemForPush(pushContentCategory: PushContentCategory): NavigationItem = navigationItem

    @SuppressWarnings("deprecation")
    override fun createScreen(selectionInfo: ContentController.SelectionInfo, mainScreen: MainScreen): ContentInfo {
        val entryPoint = selectionInfo.entryPoint as? PushHandleMainScreenExtension.PushNotification

        val action = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            entryPoint?.intent?.getSerializableExtra(
                DeeplinkActionNode.EXTRA_DEEPLINK_ACTION,
                OpenSupportConversationDeepLinkAction::class.java
            )
        } else {
            entryPoint?.intent?.getSerializableExtra(DeeplinkActionNode.EXTRA_DEEPLINK_ACTION) as? OpenSupportConversationDeepLinkAction
        }

        return ContentInfo(
            fragmentFactory(action?.dialogUuid),
            SUPPORT_CHANNELS_MAIN_SCREEN_ADDON_TAG
        )
    }

    companion object {
        private const val SUPPORT_CHANNELS_MAIN_SCREEN_ADDON_TAG = "SupportChannelsMainScreenAddonTag"
        private const val PERSISTENT_UNIQUE_IDENTIFIER = "support"
    }
}

