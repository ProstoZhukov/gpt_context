package ru.tensor.sbis.navigation_service

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.withContext
import ru.tensor.sbis.desktop.navigation.generated.ImenuItem
import ru.tensor.sbis.desktop.navigation.generated.NavigationChangedCallback
import ru.tensor.sbis.desktop.navigation.generated.PageData
import ru.tensor.sbis.desktop.navigation.generated.TContentConfig
import ru.tensor.sbis.main_screen_decl.navigation.service.NavigationPageContentConfig
import ru.tensor.sbis.main_screen_decl.navigation.service.NavigationPageData
import ru.tensor.sbis.main_screen_decl.navigation.service.NavigationService
import ru.tensor.sbis.main_screen_decl.navigation.service.NavigationServiceNode
import ru.tensor.sbis.main_screen_decl.navigation.service.asFlatList
import ru.tensor.sbis.navigation_service.model.getData
import ru.tensor.sbis.toolbox_decl.navigation.NavxIdDecl
import timber.log.Timber
import java.util.UUID
import ru.tensor.sbis.desktop.navigation.generated.NavigationService as ControllerNavigationService

/**
 * Взаимодействует с микросервисом навигации для получения доступных для пользователя разделов приложения.
 *
 * @author us.bessonov
 */
internal class NavigationServiceImpl : NavigationService {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val controller by lazy {
        ControllerNavigationService.instance()
    }

    private val hierarchyFlow by lazy {
        getNavigationChangedEventFlow()
            .shareIn(scope, SharingStarted.Eagerly)
            .onSubscription {
                emit(getNavigationHierarchyRoot())
            }
    }

    override fun getAvailableItemsFlow() = hierarchyFlow.map { it.asFlatList() }

    override suspend fun getAvailableItems() = withContext(Dispatchers.IO) {
        getNavigationHierarchyRoot().asFlatList()
    }

    override fun getNavigationHierarchyFlow() = hierarchyFlow

    override suspend fun getNavigationHierarchy() = withContext(Dispatchers.IO) {
        getNavigationHierarchyRoot()
    }

    override suspend fun getPageData(navxId: NavxIdDecl): NavigationPageData? {
        navxId.ids.forEach { id ->
            controller.getPage(id)?.let {
                return it.map()
            }
        }
        return null
    }

    private fun getNavigationNode(item: ImenuItem): NavigationServiceNode {
        val children = item.children().map {
            getNavigationNode(it)
        }
        return NavigationServiceNode(
            item.getData(controller.getPage(item.data().id)?.map()),
            children
        ).also { parent ->
            children.forEach { it.parent = parent }
        }
    }

    private fun getNavigationHierarchyRoot(): NavigationServiceNode? {
        return getRoot()?.let(::getNavigationNode)
    }

    private fun getRoot(): ImenuItem? {
        @Suppress("UNNECESSARY_NOT_NULL_ASSERTION")
        return try {
            controller.root()!!
        } catch (e: Exception) {
            Timber.w("Cannot get available navigation items")
            null
        }
    }

    private fun getNavigationChangedEventFlow() = callbackFlow {
        val subscription = controller.navigationChanged().subscribe(object : NavigationChangedCallback() {
            override fun onEvent(user: UUID) {
                trySend(getNavigationHierarchyRoot())
            }
        })
        awaitClose { subscription.disable() }
    }
}

private fun TContentConfig.map() = NavigationPageContentConfig(frameId, dashboards)

private fun PageData.map() = NavigationPageData(contentConfig.map())
