package ru.tensor.sbis.main_screen_generic_intent_handle_extension

import android.content.Intent
import ru.tensor.sbis.design.navigation.view.model.NavigationItem
import ru.tensor.sbis.main_screen_decl.ConfigurableMainScreen
import ru.tensor.sbis.main_screen_decl.basic.BasicMainScreenViewApi
import ru.tensor.sbis.main_screen_decl.content.ContentController
import ru.tensor.sbis.main_screen_decl.intent.IntentHandleExtension

/**
 * Расширение главного экрана для обработки произвольных [Intent].
 *
 * @author kv.martyshenko
 */
class GenericIntentHandleMainScreenExtension : IntentHandleExtension<GenericIntentHandleMainScreenExtension.Key> {
    private val intentResolvers = mutableListOf<IntentResolver>()

    /**
     * Метод для регистрации обработчика интентов
     *
     * @param intentResolver
     */
    fun registerIntentResolver(intentResolver: IntentResolver) {
        intentResolvers.add(intentResolver)
    }

    /**
     * Метод для отключения обработчика интентов
     *
     * @param intentResolver
     */
    fun unregisterIntentResolver(intentResolver: IntentResolver) {
        intentResolvers.remove(intentResolver)
    }

    override val key: Key = Key

    override fun resolveIntent(intent: Intent): IntentHandleExtension.ResolutionResult? {
        return this@GenericIntentHandleMainScreenExtension.intentResolvers.firstOrNull { it.recognizeIntent(intent) }
            ?.getAssociatedMenuItemForIntent(intent)
            ?.let {
                IntentHandleExtension.ResolutionResult.SelectItem(
                    it,
                    GenericIntent(intent)
                )
            }
    }

    object Key : IntentHandleExtension.ExtensionKey

    /**
     * Попадание через произвольный интент
     *
     * @property intent
     */
    class GenericIntent(
        val intent: Intent
    ) : ContentController.EntryPoint

    /**
     * Обработчик интентов.
     *
     * @author kv.martyshenko
     */
    interface IntentResolver {

        /**
         * Распознали ли интент.
         *
         * @param intent
         */
        fun recognizeIntent(intent: Intent): Boolean

        /**
         * Метод для получения [NavigationItem], ассоциированного с данным интентом.
         *
         * @param intent
         */
        fun getAssociatedMenuItemForIntent(intent: Intent): NavigationItem

    }

}

/**
 * Метод для удобного получения [GenericIntentHandleMainScreenExtension].
 */
fun ConfigurableMainScreen.genericIntentHandleExtension(): GenericIntentHandleMainScreenExtension? {
    return getIntentHandleExtension(GenericIntentHandleMainScreenExtension.Key)
}

/**
 * Метод для удобного получения [GenericIntentHandleMainScreenExtension].
 */
fun BasicMainScreenViewApi.genericIntentHandleExtension(): GenericIntentHandleMainScreenExtension? {
    return getIntentHandleExtension(GenericIntentHandleMainScreenExtension.Key)
}