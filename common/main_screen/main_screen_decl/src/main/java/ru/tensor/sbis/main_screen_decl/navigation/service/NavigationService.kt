package ru.tensor.sbis.main_screen_decl.navigation.service

import kotlinx.coroutines.flow.Flow
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.toolbox_decl.navigation.NavxIdDecl

/**
 * Поставляет доступные для пользователя разделы приложения.
 *
 * @author us.bessonov
 */
interface NavigationService : Feature {

    /**
     * Подписаться на актуальный список доступных для компонентов навигации разделов приложения.
     */
    fun getAvailableItemsFlow(): Flow<List<NavigationServiceItem>>

    /**
     * Получить список доступных для компонентов навигации разделов приложения из кэша.
     */
    suspend fun getAvailableItems(): List<NavigationServiceItem>

    /**
     * Подписаться на актуальное дерево разделов приложения.
     */
    fun getNavigationHierarchyFlow(): Flow<NavigationServiceNode?>

    /**
     * Получить актуальное дерево разделов приложения из кэша.
     */
    suspend fun getNavigationHierarchy(): NavigationServiceNode?

    /**
     * Получить модель с описанием страницы мобильного приложения.
     */
    suspend fun getPageData(navxId: NavxIdDecl): NavigationPageData? = null
}