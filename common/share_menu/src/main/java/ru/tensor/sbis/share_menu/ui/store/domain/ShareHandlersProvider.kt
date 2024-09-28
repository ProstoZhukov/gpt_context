package ru.tensor.sbis.share_menu.ui.store.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ru.tensor.sbis.main_screen_decl.navigation.service.NavigationService
import ru.tensor.sbis.main_screen_decl.navigation.service.NavigationServiceItem
import ru.tensor.sbis.toolbox_decl.share.ShareData
import ru.tensor.sbis.toolbox_decl.share.ShareHandler
import ru.tensor.sbis.verification_decl.login.LoginInterface
import ru.tensor.sbis.verification_decl.permission.PermissionFeature

/**
 * Поставщик доступных фунций для шаринга, зарегистрированных в приложении.
 *
 * @author vv.chekurda
 */
internal class ShareHandlersProvider(
    val appShareHandlers: List<ShareHandler>,
    private val navigationService: NavigationService,
    private val loginInterface: LoginInterface,
    private val permissionFeature: PermissionFeature?
) {
    private val isPhysicAccount: Boolean
        get() = loginInterface.getCurrentAccount()?.isPhysic ?: false

    /**
     * Подписаться на список доступного функционала шаринга.
     * @see [getAvailableHandlers]
     */
    fun getAvailableHandlersFlow(shareData: ShareData): Flow<List<ShareHandler>> =
        navigationService.getAvailableItemsFlow()
            .map { items -> getAvailableHandlers(shareData = shareData, items = items) }
            .flowOn(Dispatchers.IO)

    /**
     * Получить список доступного функционала шаринга, с учетом возможности поддержки текущего [shareData],
     * доступности разделов в [NavigationService], и при наличии нужных разрешений.
     */
    suspend fun getAvailableHandlers(shareData: ShareData): List<ShareHandler> =
        withContext(Dispatchers.IO) {
            getAvailableHandlers(shareData = shareData, items = null)
        }

    private suspend fun getAvailableHandlers(
        shareData: ShareData,
        items: List<NavigationServiceItem>? = null
    ): List<ShareHandler> =
        appShareHandlers
            .filter { feature -> feature.isShareSupported(shareData) }
            .filterByNavigationService(items)
            .filterByPermissions()
            .sortedBy { it.menuItem.order }

    /**
     * Получить список включенного функционала шаринга, с учётом возможности отключения релевантных разделов на веб.
     */
    private suspend fun List<ShareHandler>.filterByNavigationService(
        items: List<NavigationServiceItem>?
    ): List<ShareHandler> =
        if (isPhysicAccount) {
            this
        } else {
            val availableItems = items ?: navigationService.getAvailableItems()
            if (availableItems.isNotEmpty()) {
                val visibleFeaturesIds = availableItems
                    .filter { it.isVisible }
                    .map { it.itemId }
                    .toSet()
                filter { it.navxIds?.any { id -> visibleFeaturesIds.contains(id) } ?: true }
            } else {
                this
            }
        }

    private fun List<ShareHandler>.filterByPermissions(): List<ShareHandler> {
        if (permissionFeature == null) return this
        val permissions = permissionFeature.permissionChecker.checkPermissionsNow(mapNotNull { it.permissionScope })
        val isPhysic = isPhysicAccount
        return filter { feature ->
            val permission = permissions.find { it.scope == feature.permissionScope }
            val isPermissionsGranted = permission?.let { feature.checkPermission(it, isPhysic) } ?: true
            isPermissionsGranted
        }
    }
}