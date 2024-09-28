package ru.tensor.sbis.main_screen_push_handle_extension

import android.content.Intent
import ru.tensor.sbis.android_ext_decl.IntentAction
import ru.tensor.sbis.design.navigation.view.model.NavigationItem
import ru.tensor.sbis.main_screen_decl.ConfigurableMainScreen
import ru.tensor.sbis.main_screen_decl.basic.BasicMainScreenViewApi
import ru.tensor.sbis.main_screen_decl.content.ContentController
import ru.tensor.sbis.main_screen_decl.intent.IntentHandleExtension
import ru.tensor.sbis.main_screen_decl.navigation.NavigationVisibilityProvider
import ru.tensor.sbis.pushnotification.PushContentCategory
import ru.tensor.sbis.pushnotification.controller.notification.DigestContentCategory

/**
 * Расширение главного экрана для обработки [PushContentCategory].
 *
 * @author kv.martyshenko
 */
class PushHandleMainScreenExtension : IntentHandleExtension<PushHandleMainScreenExtension.Key> {
    private val pushIntentResolvers = mutableListOf<PushIntentResolver>()
    private var defaultPushMenuItem: NavigationItem? = null
    private var navigationVisibilityProvider: NavigationVisibilityProvider? = null

    /**
     * Метод для регистрации обработчика пуш-интентов
     *
     * @param pushIntentResolver
     */
    fun registerPushResolver(pushIntentResolver: PushIntentResolver) {
        pushIntentResolvers.add(pushIntentResolver)
    }

    /**
     * Метод для отключения обработчика пуш-интентов
     *
     * @param pushIntentResolver
     */
    fun unregisterPushResolver(pushIntentResolver: PushIntentResolver) {
        pushIntentResolvers.remove(pushIntentResolver)
    }

    /**
     * Метод для установки пункта меню по-умолчанию, если ни один из обработчиков не смог обработать
     *
     * @param navigationItem пункт меню
     */
    fun setDefaultPushMenuItem(navigationItem: NavigationItem?) {
        defaultPushMenuItem = navigationItem
    }

    override val key: Key = Key

    override fun resolveIntent(intent: Intent): IntentHandleExtension.ResolutionResult? {
        val extras = intent.extras ?: return null

        return if (extras.containsKey(IntentAction.Extra.PUSH_CONTENT_CATEGORY)) {
            val pushContentCategory =
                intent.getSerializableExtra(IntentAction.Extra.PUSH_CONTENT_CATEGORY) as PushContentCategory

            val entryPoint = PushNotification(pushContentCategory, intent)

            val targetMenuItem = pushIntentResolvers.firstOrNull {
                val matches = it.recognizePushCategory(pushContentCategory)
                matches && it.isAssociatedItemVisible(pushContentCategory)
            }
                ?.getAssociatedMenuItemForPush(pushContentCategory)
                ?: defaultPushMenuItem

            if (targetMenuItem != null) {
                IntentHandleExtension.ResolutionResult.SelectItem(
                    targetMenuItem,
                    entryPoint
                )
            } else null
        } else {
            null
        }
    }

    override fun setNavigationVisibilityProvider(navigationVisibilityProvider: NavigationVisibilityProvider) {
        this.navigationVisibilityProvider = navigationVisibilityProvider
    }

    private fun PushIntentResolver.isAssociatedItemVisible(category: PushContentCategory) =
        navigationVisibilityProvider?.isItemVisible(getAssociatedMenuItemForPush(category))
            ?: true

    object Key : IntentHandleExtension.ExtensionKey

    /**
     * Попадание через пуш-уведомление
     *
     * @property category
     * @property intent
     */
    class PushNotification(
        val category: PushContentCategory,
        val intent: Intent
    ) : ContentController.EntryPoint

    /**
     * Обработчик пуш-интентов
     *
     * @author kv.martyshenko
     */
    interface PushIntentResolver {

        /**
         * Распознали ли пуш.
         *
         * @param pushContentCategory
         */
        fun recognizePushCategory(pushContentCategory: PushContentCategory): Boolean

        /**
         * Метод для получения [NavigationItem], ассоциированного с данным пушем.
         *
         * @param pushContentCategory
         */
        fun getAssociatedMenuItemForPush(pushContentCategory: PushContentCategory): NavigationItem

    }

}

/**
 * Метод для удобного получения [PushHandleMainScreenExtension].
 */
fun ConfigurableMainScreen.pushHandleExtension(): PushHandleMainScreenExtension? {
    return getIntentHandleExtension(PushHandleMainScreenExtension.Key)
}

/**
 * Метод для удобного получения [PushHandleMainScreenExtension].
 */
fun BasicMainScreenViewApi.pushHandleExtension(): PushHandleMainScreenExtension? {
    return getIntentHandleExtension(PushHandleMainScreenExtension.Key)
}