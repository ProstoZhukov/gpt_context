package ru.tensor.sbis.messages_main_screen_addon

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mikepenz.iconics.IconicsDrawable
import ru.tensor.sbis.android_ext_decl.viewprovider.OverlayFragmentHolder
import ru.tensor.sbis.application_tools.DebugTools
import ru.tensor.sbis.common.controller.synchronization.ControllerCancellable
import ru.tensor.sbis.common.navigation.MenuNavigationItemType
import ru.tensor.sbis.common.navigation.NavxId
import ru.tensor.sbis.common.util.DeviceConfigurationUtils
import ru.tensor.sbis.communicator.common.contract.CommunicatorCommonFeature.Companion.EXTRA_CONVERSATION_ACTIVITY_CONVERSATION_TYPE_KEY
import ru.tensor.sbis.communicator.common.util.castTo
import ru.tensor.sbis.communicator.declaration.host_factory.ThemesRegistryHostFragmentFactory
import ru.tensor.sbis.communicator.declaration.model.CommunicatorRegistryType
import ru.tensor.sbis.communicator.declaration.model.DialogType
import ru.tensor.sbis.communicator.generated.MessageController
import ru.tensor.sbis.communicator.generated.ThemeController
import ru.tensor.sbis.communicator.push.MessageContentCategory
import ru.tensor.sbis.deeplink.*
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.navigation.view.model.NavigationItem
import ru.tensor.sbis.design.navigation.view.model.NavigationItemIcon
import ru.tensor.sbis.design.navigation.view.model.NavigationItemLabel
import ru.tensor.sbis.info_decl.dialogs.DialogNotificationContentCategory
import ru.tensor.sbis.main_screen_decl.ConfigurableMainScreen
import ru.tensor.sbis.main_screen_decl.MainScreen
import ru.tensor.sbis.main_screen_decl.MainScreenAddon
import ru.tensor.sbis.main_screen_decl.content.ContentContainer
import ru.tensor.sbis.main_screen_decl.content.ContentController
import ru.tensor.sbis.main_screen_decl.content.SimplifiedContentController
import ru.tensor.sbis.main_screen_decl.content.install.NonCacheFragmentInstallationStrategy
import ru.tensor.sbis.main_screen_decl.env.BottomBarProviderExt
import ru.tensor.sbis.main_screen_decl.navigation.DefaultNavigationItem
import ru.tensor.sbis.main_screen_deeplink_handle_extension.DeepLinkHandleMainScreenExtension
import ru.tensor.sbis.main_screen_deeplink_handle_extension.deepLinkHandleExtension
import ru.tensor.sbis.main_screen_navigation_event_handle_extension.NavigationEventHandleMainScreenExtension
import ru.tensor.sbis.main_screen_navigation_event_handle_extension.navigationEventHandleExtension
import ru.tensor.sbis.main_screen_push_handle_extension.PushHandleMainScreenExtension
import ru.tensor.sbis.main_screen_push_handle_extension.pushHandleExtension
import ru.tensor.sbis.messages_main_screen_addon.MessagesMainScreenAddonPlugin.communicatorPushKeyboardHelperProvider
import ru.tensor.sbis.messages_main_screen_addon.MessagesMainScreenAddonPlugin.themeTabHistory
import ru.tensor.sbis.pushnotification.PushContentCategory
import ru.tensor.sbis.pushnotification.PushType
import ru.tensor.sbis.pushnotification.controller.notification.DigestContentCategory
import ru.tensor.sbis.toolbox_decl.navigation.NavxIdDecl
import ru.tensor.sbis.common.R as RCommon
import ru.tensor.sbis.design.R as RDesign

/**
 * Плагин раздела сообщений на главном экране
 *
 * @author kv.martyshenko
 */
internal class MessagesMainScreenAddon(
    private val messagesNavItem: NavigationItem,
    private val visibilitySourceProvider: (ConfigurableMainScreen) -> LiveData<Boolean>,
    private val themesRegistryHostFragmentFactory: ThemesRegistryHostFragmentFactory
) : SimplifiedContentController(),
    MainScreenAddon,
    PushHandleMainScreenExtension.PushIntentResolver,
    NavigationEventHandleMainScreenExtension.NavTypeIntentResolver,
    DeepLinkHandleMainScreenExtension.DeepLinkIntentResolver,
    ControllerCancellable {

    // region MainScreenAddon
    override fun setup(mainScreen: ConfigurableMainScreen) {
        val configuration = ConfigurableMainScreen.MenuItemConfiguration(
            counter = MessagesNavCounter(),
            visibilitySource = visibilitySourceProvider(mainScreen)
        )
        mainScreen.addItem(messagesNavItem, configuration, this)

        requireNotNull(mainScreen.pushHandleExtension()).registerPushResolver(this)
        requireNotNull(mainScreen.navigationEventHandleExtension()).registerNavResolver(this)
        requireNotNull(mainScreen.deepLinkHandleExtension()).registerDeepLinkResolver(this)
    }

    override fun reset(mainScreen: ConfigurableMainScreen) {
        mainScreen.removeItem(messagesNavItem)

        requireNotNull(mainScreen.pushHandleExtension()).unregisterPushResolver(this)
        requireNotNull(mainScreen.navigationEventHandleExtension()).unregisterNavResolver(this)
        requireNotNull(mainScreen.deepLinkHandleExtension()).unregisterDeepLinkResolver(this)
    }
    // endregion

    // region ContainerController
    override fun createScreen(selectionInfo: ContentController.SelectionInfo, mainScreen: MainScreen): ContentInfo {
        val deepLinkAction = extractDeepLinkAction<DeeplinkAction>(selectionInfo.entryPoint)
        return ContentInfo(createFragment(selectionInfo.entryPoint, deepLinkAction))
    }

    override fun selectSubScreen(
        navxId: NavxIdDecl,
        entryPoint: ContentController.EntryPoint,
        mainScreen: MainScreen,
        contentContainer: ContentContainer
    ) {
        when (navxId) {
            NavxId.DIALOGS,
            NavxId.CHATS,
            NavxId.COMMUNICATOR -> Unit
            else -> return
        }
        val isChatTab = navxId == NavxId.CHATS || themeTabHistory.get().chatsIsLastSelectedTab()
        (fragmentInstallationStrategy.findContent(contentContainer) as? DeeplinkActionNode)
            ?.onNewDeeplinkAction(args = extractDeepLinkAction(entryPoint) ?: SwitchThemeTabDeeplinkAction(isChatTab))
    }

    override fun onSelectionChanged(
        navigationItem: NavigationItem,
        isSelected: Boolean,
        mainScreen: MainScreen,
        contentContainer: ContentContainer
    ) {
        val fabIcon = if (isSelected) {
            IconicsDrawable(mainScreen.host.context, SbisMobileIcon.Icon.smi_navBarPlus)
        } else { null }
        contentContainer.bottomBarProvider.setNavigationFabIcon(fabIcon)
    }

    override fun update(
        navigationItem: NavigationItem,
        entryPoint: ContentController.EntryPoint,
        mainScreen: MainScreen,
        contentContainer: ContentContainer
    ) {
        val deepLinkAction = extractDeepLinkAction<DeeplinkAction>(entryPoint)
        if (deepLinkAction != null && !handleEntryPoint(deepLinkAction, contentContainer)) {
            val contentInfo = ContentInfo(createFragment(entryPoint, deepLinkAction))

            contentContainer.fragmentManager
                .beginTransaction()
                .replace(contentContainer.containerId, contentInfo.fragment, contentInfo.tag)
                .commit()
        }
    }

    override fun BottomBarProviderExt.configureActionButtons(mainScreen: MainScreen) {
        val maxInitialVisibleButtons = if (DeviceConfigurationUtils.isTablet(mainScreen.host.context)) 0 else 1
        resetActionButtonsVisibility(maxInitialVisibleButtons)
    }

    // endregion

    // region PushIntentResolver
    override fun recognizePushCategory(pushContentCategory: PushContentCategory): Boolean {
        val isMessageContentCategory = pushContentCategory is MessageContentCategory ||
            pushContentCategory is DialogNotificationContentCategory
        if (isMessageContentCategory) {
            communicatorPushKeyboardHelperProvider.get()
                .getCommunicatorPushKeyboardHelper()
                .hideKeyboard.tryEmit(true)
        }
        return isMessageContentCategory ||
            (pushContentCategory is DigestContentCategory && pushContentCategory.pushType == PushType.NEW_MESSAGE)
    }

    override fun getAssociatedMenuItemForPush(pushContentCategory: PushContentCategory): NavigationItem {
        return messagesNavItem
    }
    // endregion

    // region NavTypeIntentResolver
    override fun recognizeNavType(menuNavigationItemType: MenuNavigationItemType): Boolean =
        when (menuNavigationItemType) {
            MenuNavigationItemType.MESSAGES,
            MenuNavigationItemType.NOTIFICATIONS -> true
            else -> false
        }

    override fun getAssociatedMenuItemForNav(menuNavigationItemType: MenuNavigationItemType): NavigationItem {
        return messagesNavItem
    }
    // endregion

    // region DeepLinkHandleMainScreenExtension.DeepLinkIntentResolver
    override fun recognizeDeepLink(deeplinkAction: DeeplinkAction): Boolean =
        when (deeplinkAction) {
            is CommunicatorDeeplinkAction,
            is OpenArticleDiscussionDeeplinkAction,
            is OpenWebViewDeeplinkAction,
            is OpenProfileDeeplinkAction,
            is OpenViolationDeeplinkAction,
            is OpenInstructionDeeplinkAction,
            is HandlePushNotificationDeeplinkAction -> true
            else -> false
        }

    override fun getAssociatedMenuItemForDeepLink(deeplinkAction: DeeplinkAction): NavigationItem {
        return messagesNavItem
    }
    // endregion

    private fun createFragment(entryPoint: ContentController.EntryPoint, deepLinkAction: DeeplinkAction?): Fragment {
        val registryType = CommunicatorRegistryType.ChatsRegistry().takeIf {
            deepLinkAction?.castTo<OpenConversationDeeplinkAction>()?.isChat == true ||
                (deepLinkAction == null && themeTabHistory.get().chatsIsLastSelectedTab())
        } ?: CommunicatorRegistryType.DialogsRegistry(extractDialogType(entryPoint))
        return themesRegistryHostFragmentFactory.createThemeHostFragment(registryType, deepLinkAction)
    }

    private fun extractDialogType(entryPoint: ContentController.EntryPoint): DialogType? {
        val extras = when (entryPoint) {
            is NavigationEventHandleMainScreenExtension.NavEvent -> entryPoint.intent.extras
            is PushHandleMainScreenExtension.PushNotification -> entryPoint.intent.extras
            else -> null
        }
        return if (extras != null && extras.containsKey(EXTRA_CONVERSATION_ACTIVITY_CONVERSATION_TYPE_KEY)) {
            val dialogType = extras.getSerializable(EXTRA_CONVERSATION_ACTIVITY_CONVERSATION_TYPE_KEY) as DialogType?
            extras.remove(EXTRA_CONVERSATION_ACTIVITY_CONVERSATION_TYPE_KEY)
            dialogType
        } else { null }
    }

    override fun deselect(
        selectionInfo: ContentController.SelectionInfo,
        mainScreen: MainScreen,
        contentContainer: ContentContainer,
        transaction: FragmentTransaction
    ) {
        clearOverlayBackStackForAutoTest(mainScreen)
        if (fragmentInstallationStrategy is NonCacheFragmentInstallationStrategy) {
            // Необходимо для отмены асинхронных операций вызванных методом list (см. CRUD).
            // Должен вызываться синхронно при уходе из реестра сообщений.
            cancelControllerSynchronizations()
        }
        super.deselect(selectionInfo, mainScreen, contentContainer, transaction)
    }

    override fun cancelControllerSynchronizations() {
        ThemeController.instance().cancelAll()
        MessageController.instance().cancelAll()
    }

    /**
     * Чистим стек фрагментов в оверлее при уходе из экрана в автотестах.
     * Переход в другой реестр и возврат используется автотестами для сброса состояния текущего реестра.
     */
    private fun clearOverlayBackStackForAutoTest(mainScreen: MainScreen) {
        val activity = mainScreen.host.context as? AppCompatActivity ?: return
        val intent = activity.intent ?: return
        if (DebugTools.updateIsAutoTestLaunch(intent) && activity is OverlayFragmentHolder && activity.hasFragment()) {
            activity.supportFragmentManager.apply {
                for (i in 0 until backStackEntryCount) {
                    popBackStackImmediate()
                }
            }
        }
    }

    companion object {
        @Suppress("MemberVisibilityCanBePrivate")
        const val MESSAGES_ITEM_IDENTIFIER = "communicator"

        @JvmStatic
        fun createDefaultMessagesItem(): DefaultNavigationItem {
            val currentNavx = MessagesMainScreenAddonPlugin.customizationOptions.navxIdentifier
            val navxId: NavxId = when (currentNavx) {
                "communicator" -> NavxId.COMMUNICATOR
                "dialogs" -> NavxId.DIALOGS
                else -> NavxId.CHATS            }
            return DefaultNavigationItem(
                navigationItemLabel = NavigationItemLabel(
                    default = RCommon.string.common_navigation_menu_item_messages,
                    short = RCommon.string.common_navigation_menu_item_messages_reduced
                ),
                navigationItemIcon = NavigationItemIcon(
                    default = RDesign.string.design_nav_icon_contacts,
                    selected = RDesign.string.design_nav_icon_contacts_fill,
                ),
                persistentUniqueIdentifier = currentNavx,
                navxIdentifier = navxId
            )
        }

        @JvmStatic
        fun defaultVisibilitySourceProvider(): (ConfigurableMainScreen) -> LiveData<Boolean> {
            return { MutableLiveData(true) }
        }
    }
}