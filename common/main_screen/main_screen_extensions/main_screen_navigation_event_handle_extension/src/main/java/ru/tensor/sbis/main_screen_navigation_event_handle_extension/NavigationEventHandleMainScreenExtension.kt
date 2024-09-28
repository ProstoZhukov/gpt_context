package ru.tensor.sbis.main_screen_navigation_event_handle_extension

import android.content.Intent
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.tensor.sbis.android_ext_decl.IntentAction
import ru.tensor.sbis.application_tools.DebugTools
import ru.tensor.sbis.common.navigation.MenuNavigationItemType
import ru.tensor.sbis.design.navigation.view.model.NavigationItem
import ru.tensor.sbis.login.common.utils.autotests.extractLogLevelOption
import ru.tensor.sbis.login.common.utils.autotests.updateLogLevel
import ru.tensor.sbis.login.common.utils.autotests.updateLogLevelCompletable
import ru.tensor.sbis.main_screen_decl.ConfigurableMainScreen
import ru.tensor.sbis.main_screen_decl.content.ContentController
import ru.tensor.sbis.main_screen_decl.intent.IntentHandleExtension

/**
 * Расширение главного экрана для обработки [MenuNavigationItemType].
 *
 * @author kv.martyshenko
 */
open class NavigationEventHandleMainScreenExtension(
    @Deprecated("Используйте scope", ReplaceWith("scope"))
    private val disposables: CompositeDisposable = CompositeDisposable(),
    private val scope: CoroutineScope? = null
) : IntentHandleExtension<NavigationEventHandleMainScreenExtension.Key> {
    private val navIntentResolvers = mutableListOf<NavTypeIntentResolver>()

    /**
     * Метод для регистрации обработчика навигационных-интентов
     *
     * @param navTypeIntentResolver
     */
    fun registerNavResolver(navTypeIntentResolver: NavTypeIntentResolver) {
        navIntentResolvers.add(navTypeIntentResolver)
    }

    /**
     * Метод для отключения обработчика навигационных-интентов
     *
     * @param navTypeIntentResolver
     */
    fun unregisterNavResolver(navTypeIntentResolver: NavTypeIntentResolver) {
        navIntentResolvers.remove(navTypeIntentResolver)
    }

    override val key: Key = Key

    override fun resolveIntent(intent: Intent): IntentHandleExtension.ResolutionResult? {
        val extras = intent.extras ?: return null

        return if (extras.containsKey(IntentAction.Extra.NAVIGATION_MENU_POSITION)) { // открытие по ссылке на карточку
            var menuNavigationItemType =
                intent.getSerializableExtra(IntentAction.Extra.NAVIGATION_MENU_POSITION) as? MenuNavigationItemType
            /**
             * Для авто тестов. Переходы по навигации с помощью команды:
             * adb shell am start -n ru.tensor.sbis.droid.debug/ru.tensor.sbis.droid.MainActivity -c SBIS_AUTOTEST_LAUNCH --es NAVIGATION_MENU_POSITION "NEWS"
             */
            if (DebugTools.updateIsAutoTestLaunch(intent)) {
                val menuNavigationItemTypeFromString = extras.getString(IntentAction.Extra.NAVIGATION_MENU_POSITION)
                    ?.let {
                        try {
                            MenuNavigationItemType.valueOf(it)
                        } catch (e: Exception) {
                            null
                        }
                    }
                if (menuNavigationItemTypeFromString != null) {
                    menuNavigationItemType = menuNavigationItemTypeFromString
                }

                if (scope != null) {
                    scope.launch {
                        updateLogLevel(extractLogLevelOption(intent))
                    }
                } else {
                    disposables.add(
                        updateLogLevelCompletable(extractLogLevelOption(intent))
                            .observeOn(Schedulers.io())
                            .subscribe()
                    )
                }
            }

            menuNavigationItemType?.let { navType ->
                this@NavigationEventHandleMainScreenExtension.navIntentResolvers.firstOrNull {
                    it.recognizeNavType(
                        navType
                    )
                }
                    ?.getAssociatedMenuItemForNav(navType)
            }?.let {
                IntentHandleExtension.ResolutionResult.SelectItem(it, NavEvent(menuNavigationItemType, intent))
            }
        } else null
    }

    object Key : IntentHandleExtension.ExtensionKey

    /**
     * Попадание через навигационный интент
     *
     * @property navType
     * @property intent
     */
    class NavEvent(
        val navType: MenuNavigationItemType,
        val intent: Intent
    ) : ContentController.EntryPoint

    /**
     * Обработчик навигационных интентов
     *
     * @author kv.martyshenko
     */
    interface NavTypeIntentResolver {

        /**
         * Распознали ли навигационный тип.
         *
         * @param menuNavigationItemType
         */
        fun recognizeNavType(menuNavigationItemType: MenuNavigationItemType): Boolean

        /**
         * Метод для получения [NavigationItem], ассоциированного с данным навигационным типом.
         *
         * @param menuNavigationItemType
         */
        fun getAssociatedMenuItemForNav(menuNavigationItemType: MenuNavigationItemType): NavigationItem

    }

}

/**
 * Метод для удобного получения [NavigationEventHandleMainScreenExtension].
 */
fun ConfigurableMainScreen.navigationEventHandleExtension(): NavigationEventHandleMainScreenExtension? {
    return getIntentHandleExtension(NavigationEventHandleMainScreenExtension.Key)
}