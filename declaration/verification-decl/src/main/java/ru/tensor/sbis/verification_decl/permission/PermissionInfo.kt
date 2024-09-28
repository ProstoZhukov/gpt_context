package ru.tensor.sbis.verification_decl.permission

/**
 * Объекты класса предоставляют информацию об уровне доступа к области [scope].
 *
 * @param scope [PermissionScope], по которому получена информация о разрешении
 * @param level уровень доступа к области [scope]
 *
 * @see [PermissionScope]
 * @see [PermissionLevel]
 * @see [PermissionChecker]
 *
 * @author ma.kolpakov
 * Создан 11/26/2018
 */
data class PermissionInfo(val scope: PermissionScope, val level: PermissionLevel)