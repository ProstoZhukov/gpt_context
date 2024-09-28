package ru.tensor.sbis.messages_main_screen_addon

import androidx.fragment.app.Fragment
import ru.tensor.sbis.common.navigation.NavxId
import ru.tensor.sbis.communicator.common.contract.CommunicatorCommonFeature
import ru.tensor.sbis.communicator.declaration.model.CommunicatorRegistryType
import ru.tensor.sbis.communicator.declaration.model.DialogType
import ru.tensor.sbis.deeplink.DeeplinkAction
import ru.tensor.sbis.main_screen_decl.MainScreen
import ru.tensor.sbis.main_screen_decl.content.ContentController
import ru.tensor.sbis.main_screen_decl.content.MainScreenEntry
import ru.tensor.sbis.main_screen_decl.content.SimplifiedContentController
import ru.tensor.sbis.main_screen_navigation_event_handle_extension.NavigationEventHandleMainScreenExtension
import ru.tensor.sbis.main_screen_push_handle_extension.PushHandleMainScreenExtension
import ru.tensor.sbis.toolbox_decl.navigation.NavxIdDecl

/**
 * Реализация [MainScreenEntry] для реестров диалоги/каналы.
 * @property id идентификатор раздела, см. [NavxId].
 *
 * @author dv.baranov
 */
internal class MessagesMainScreenEntry(
    override val id: NavxIdDecl
) : MainScreenEntry {

    override fun createScreen(
        entryPoint: ContentController.EntryPoint,
        mainScreen: MainScreen
    ): SimplifiedContentController.ContentInfo =
        SimplifiedContentController.ContentInfo(createFragment(entryPoint))

    private fun createFragment(entryPoint: ContentController.EntryPoint): Fragment {
        val registryType = CommunicatorRegistryType.ChatsRegistry().takeIf {
            id == NavxId.CHATS
        } ?: CommunicatorRegistryType.DialogsRegistry(extractDialogType(entryPoint))
        val deepLinkAction = extractDeepLinkAction<DeeplinkAction>(entryPoint)
        return MessagesMainScreenAddonPlugin.themesRegistryHostFragmentFactoryProvider.get().createThemeHostFragment(
            registryType,
            deepLinkAction,
        )
    }

    private fun extractDialogType(entryPoint: ContentController.EntryPoint): DialogType? {
        val extras = when (entryPoint) {
            is NavigationEventHandleMainScreenExtension.NavEvent -> entryPoint.intent.extras
            is PushHandleMainScreenExtension.PushNotification -> entryPoint.intent.extras
            else -> null
        }
        return if (extras != null && extras.containsKey(CommunicatorCommonFeature.EXTRA_CONVERSATION_ACTIVITY_CONVERSATION_TYPE_KEY)) {
            val dialogType = extras.getSerializable(CommunicatorCommonFeature.EXTRA_CONVERSATION_ACTIVITY_CONVERSATION_TYPE_KEY) as DialogType?
            extras.remove(CommunicatorCommonFeature.EXTRA_CONVERSATION_ACTIVITY_CONVERSATION_TYPE_KEY)
            dialogType
        } else null
    }

    companion object {
        fun createDefault() = arrayOf(
            MessagesMainScreenEntry(NavxId.DIALOGS),
            MessagesMainScreenEntry(NavxId.CHATS)
        )
    }
}
