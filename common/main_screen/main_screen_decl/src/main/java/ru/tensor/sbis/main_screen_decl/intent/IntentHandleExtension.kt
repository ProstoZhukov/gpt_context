package ru.tensor.sbis.main_screen_decl.intent

import android.content.Intent
import ru.tensor.sbis.design.navigation.view.model.NavigationItem
import ru.tensor.sbis.main_screen_decl.content.ContentController
import ru.tensor.sbis.main_screen_decl.env.MainScreenHost
import ru.tensor.sbis.main_screen_decl.navigation.NavigationVisibilityProvider

/**
 * Расширение главного экрана для обработки новых [Intent].
 *
 * @author kv.martyshenko
 */
interface IntentHandleExtension<K : IntentHandleExtension.ExtensionKey> {

    /**
     * Уникальный ключ расширения.
     */
    val key: K

    /**
     * Метод для обработки [Intent].
     *
     * @param intent
     */
    fun resolveIntent(intent: Intent): ResolutionResult?

    /**
     * Задать инструмент для проверки доступности пункта навигации, если необходимо.
     */
    fun setNavigationVisibilityProvider(navigationVisibilityProvider: NavigationVisibilityProvider) = Unit

    /**
     * Ключ расширения, по которому к нему потом можно будет обратиться.
     */
    interface ExtensionKey

    /**
     * Результат обработки [Intent]
     */
    sealed interface ResolutionResult {

        /**
         * Смена активного элемента
         *
         * @param targetMenuItem элемент меню
         * @param entryPoint точка входа
         */
        class SelectItem(
            val targetMenuItem: NavigationItem,
            val entryPoint: ContentController.EntryPoint
        ) : ResolutionResult

        /**
         * Выполнение побочных действий. Например, открытие сторонней активити.
         *
         * @param action действие
         */
        class SideEffect(
            val action: (MainScreenHost) -> Unit
        ) : ResolutionResult

    }
}