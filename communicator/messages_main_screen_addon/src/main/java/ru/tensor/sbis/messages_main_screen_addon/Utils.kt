package ru.tensor.sbis.messages_main_screen_addon

import ru.tensor.sbis.deeplink.DeeplinkAction
import ru.tensor.sbis.deeplink.DeeplinkActionNode
import ru.tensor.sbis.main_screen_decl.content.ContentContainer
import ru.tensor.sbis.main_screen_decl.content.ContentController
import ru.tensor.sbis.main_screen_deeplink_handle_extension.DeepLinkHandleMainScreenExtension
import ru.tensor.sbis.main_screen_navigation_event_handle_extension.NavigationEventHandleMainScreenExtension
import ru.tensor.sbis.main_screen_push_handle_extension.PushHandleMainScreenExtension

/**
 * Извлекает [DeeplinkAction] заданного типа, в зависимости от конкретного типа [ContentController.EntryPoint]
 */
internal inline fun <reified ACTION : DeeplinkAction> extractDeepLinkAction(
    entryPoint: ContentController.EntryPoint
): ACTION? {
    return when (entryPoint) {
        is PushHandleMainScreenExtension.PushNotification -> {
            DeeplinkActionNode.getDeeplinkAction<ACTION>(entryPoint.intent)
        }
        is DeepLinkHandleMainScreenExtension.DeepLink -> {
            entryPoint.action as? ACTION
        }
        is NavigationEventHandleMainScreenExtension.NavEvent -> {
            DeeplinkActionNode.getDeeplinkAction<ACTION>(entryPoint.intent)
        }
        else -> null
    }
}

/**
 * Обрабатывает действие внешней ссылки в текущем фрагменте, если это возможно
 *
 * @return true если текущий фрагмент поддерживает обработку ссылок
 */
internal fun handleEntryPoint(deeplinkAction: DeeplinkAction, contentContainer: ContentContainer): Boolean {
    val fragment = contentContainer.fragmentManager.findFragmentById(contentContainer.containerId)
    return if (fragment is DeeplinkActionNode) {
        fragment.onNewDeeplinkAction(deeplinkAction)
        return true
    } else {
        false
    }
}