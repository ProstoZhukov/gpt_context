package ru.tensor.sbis.toolbox_decl.share.delegates

import ru.tensor.sbis.verification_decl.permission.PermissionInfo
import ru.tensor.sbis.verification_decl.permission.PermissionLevel
import ru.tensor.sbis.verification_decl.permission.PermissionScope

/**
 * Делегат для проверки разрешений раздела для функциональности поделиться.
 *
 * В будущем запрос прав переедет в NavigationService.
 *
 * @author vv.chekurda
 */
interface SharePermissionsChecker {

    /**
     * Область полномочий, по которой требуется определить разрешение доступа для возможности использования опции.
     */
    val permissionScope: PermissionScope?
        get() = null

    /**
     * Проверить разрешение на использование данной функциональности.
     * Напрямую зависит от указанных требуемых разрешений в [permissionScope].
     */
    fun checkPermission(permission: PermissionInfo, isPhysic: Boolean): Boolean =
        permission.level != PermissionLevel.NONE
}