package ru.tensor.sbis.main_screen_deeplink_handle_extension

import android.content.Intent
import ru.tensor.sbis.deeplink.DeeplinkAction
import ru.tensor.sbis.deeplink.DeeplinkActionNode
import ru.tensor.sbis.design.navigation.view.model.NavigationItem
import ru.tensor.sbis.main_screen_decl.ConfigurableMainScreen
import ru.tensor.sbis.main_screen_decl.content.ContentController
import ru.tensor.sbis.main_screen_decl.intent.IntentHandleExtension

/**
 * Расширение главного экрана для обработки [DeeplinkAction].
 *
 * @author kv.martyshenko
 */
class DeepLinkHandleMainScreenExtension : IntentHandleExtension<DeepLinkHandleMainScreenExtension.Key> {
    private val deepLinkIntentResolvers = mutableListOf<DeepLinkIntentResolver>()

    /**
     * Метод для регистрации обработчика deeplink'ов
     *
     * @param deepLinkIntentResolver
     */
    fun registerDeepLinkResolver(deepLinkIntentResolver: DeepLinkIntentResolver) {
        deepLinkIntentResolvers.add(deepLinkIntentResolver)
    }

    /**
     * Метод для отключения обработчика deeplink'ов
     *
     * @param deepLinkIntentResolver
     */
    fun unregisterDeepLinkResolver(deepLinkIntentResolver: DeepLinkIntentResolver) {
        deepLinkIntentResolvers.remove(deepLinkIntentResolver)
    }

    override val key: Key = Key

    override fun resolveIntent(intent: Intent): IntentHandleExtension.ResolutionResult? {
        val extras = intent.extras ?: return null

        val deepLinkAction = DeeplinkActionNode.getDeeplinkAction<DeeplinkAction>(intent)
        return if (deepLinkAction != null) { // открытие через deeplink
            val targetMenuItem = this@DeepLinkHandleMainScreenExtension.deepLinkIntentResolvers.firstOrNull {
                it.recognizeDeepLink(deepLinkAction)
            }
                ?.getAssociatedMenuItemForDeepLink(deepLinkAction)

            if (targetMenuItem != null) {
                IntentHandleExtension.ResolutionResult.SelectItem(
                    targetMenuItem,
                    DeepLink(deepLinkAction, intent)
                )
            } else null
        } else null
    }

    object Key : IntentHandleExtension.ExtensionKey

    /**
     * Попадание через ссылку
     *
     * @property action
     * @property intent
     */
    class DeepLink(
        val action: DeeplinkAction,
        val intent: Intent
    ) : ContentController.EntryPoint

    /**
     * Обработчик deeplink'ов
     *
     * @author kv.martyshenko
     */
    interface DeepLinkIntentResolver {

        /**
         * Распознали ли deeplink.
         *
         * @param deeplinkAction
         */
        fun recognizeDeepLink(deeplinkAction: DeeplinkAction): Boolean

        /**
         * Метод для получения [NavigationItem], ассоциированного с данным deeplink.
         *
         * @param deeplinkAction
         */
        fun getAssociatedMenuItemForDeepLink(deeplinkAction: DeeplinkAction): NavigationItem

    }

}

/**
 * Метод для удобного получения [DeepLinkHandleMainScreenExtension].
 */
fun ConfigurableMainScreen.deepLinkHandleExtension(): DeepLinkHandleMainScreenExtension? {
    return getIntentHandleExtension(DeepLinkHandleMainScreenExtension.Key)
}