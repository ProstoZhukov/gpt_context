package ru.tensor.sbis.main_screen_decl.content

import ru.tensor.sbis.main_screen_decl.MainScreen
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.toolbox_decl.navigation.DefaultNavxIdResolver
import ru.tensor.sbis.toolbox_decl.navigation.NavxIdDecl

/**
 * Экран с содержимым. Структурная единица главного экрана, которая может быть представлена как самостоятельным пунктом
 * навигации, так и вложенным (вкладка).
 *
 * @author us.bessonov
 */
interface MainScreenEntry : Feature {
    /**
     * Идентификатор соответствующего элемента в структуре навигации приложения.
     */
    val id: NavxIdDecl?
        get() = null

    /**
     * Является ли раздел вкладкой.
     */
    val isTab: Boolean
        get() = true

    /**
     * Проверка дополнительного условия соответствия persistentUniqueIdentifier с MainScreenEntry.
     * Переопределять если одного navxId недостаточно.
     */
    infix fun associatedWith(persistentUniqueIdentifier: String): Boolean = true

    /** @SelfDocumented */
    fun createScreen(
        entryPoint: ContentController.EntryPoint,
        mainScreen: MainScreen
    ): SimplifiedContentController.ContentInfo
}