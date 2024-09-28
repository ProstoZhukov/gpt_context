package ru.tensor.sbis.toolbox_decl.language

import android.content.Context
import androidx.fragment.app.Fragment
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Публичное api фичи для работы с локализацией приложения.
 *
 * @author av.krymov
 */
interface LanguageFeature : Feature {

    /**
     * Получить текущую выбранную локаль пользователя
     *
     * @return выбранная локаль пользователя
     */
    fun currentLanguage(): Language

    /**
     * Получить обновленный контекст в соответствии с установленным языком пользователя.
     */
    fun updateContext(context: Context): Context

    /**
     * Синхронизировать локали и проверить на совпадение предыдущей локали с новой.
     *
     * @return true, если старая локаль не совпадает с новой.
     */
    fun actualizeLocale(): Boolean

    /**
     * Создать фрагмент со списком языков с возможностью выбора.
     *
     * @param withNavigation должна ли в стандартной шапке отображаться кнопка возврата.
     * @param shouldHideToolbar следует ли скрыть toolbar полностью.
     * @param demoApplication текстовое представление запрашиваемого демо приложения.
     * Если не передано, будут запрошены все языки, иначе вернутся только те, для которых локализовано переданное [demoApplication].
     * Если передано, выбранный язык вернется родительскому фрагменту по ключу.
     * @param withEnabled если false, то фрагмент будет возвращать список только доступных языков.
     */
    fun createChangeLanguageFragment(
        withNavigation: Boolean = false,
        shouldHideToolbar: Boolean = false,
        demoApplication: String = "",
        withEnabled: Boolean = true
    ): Fragment

    /**
     * Создать фрагмент со списком языков в шторке.
     * @param demoApplication текстовое представление запрашиваемого демо приложения.
     * Если не передано, будут запрошены все языки, иначе вернутся только те, для которых локализовано переданное [demoApplication].
     * Если передано, выбранный язык вернется родительскому фрагменту по ключу.
     */
    fun createPanelLanguageFragment(demoApplication: String = ""): Fragment

    /**
     * Список поддерживаемых языков для [demoApplication]
     */
    suspend fun supportedLanguagesFor(demoApplication: String = ""): List<Language>

    /** Текущая локаль приложения */
    val currentLocaleTag: String

    /** Получить приоритизированный список локалей из настроек системы */
    fun getSystemLocaleTags(): List<String>

    /**
     * Получить модель сообщения недоступности языка.
     */
    fun getUnsupportedLanguage(isoCode: String): LanguageMessage


    companion object {
        /**
         * Ключ результата фичи, прослушиваемый потребителем
         */
        const val LANGUAGE_FEATURE_RESULT = "LANGUAGE_FEATURE_RESULT"

        /**
         * Ключ для хранения выбранного языка
         */
        const val LANGUAGE_FEATURE_RESULT_LANG = "LANGUAGE_FEATURE_RESULT_LANG"
    }
}
