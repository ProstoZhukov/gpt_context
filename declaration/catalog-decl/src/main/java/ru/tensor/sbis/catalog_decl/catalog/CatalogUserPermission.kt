package ru.tensor.sbis.catalog_decl.catalog

import ru.tensor.sbis.verification_decl.permission.PermissionLevel

/**
 *  Права пользователя.
 *
 *  [base] базовые права на Каталог.
 *  [costPrice] может просматривать себестоимость.
 *  [stockBalances] может просматривать складские остатки.
 *  [warehouseReports] может просматривать складские отчеты.
 *
 *  @author sp.lomakin
 */
data class CatalogUserPermission(
    val base: PermissionLevel,
    val costPrice: Boolean,
    val stockBalances: Boolean,
    val warehouseReports: Boolean = false
) {

    val baseRead: Boolean
        get() = base >= PermissionLevel.READ

    val baseWrite: Boolean
        get() = base >= PermissionLevel.WRITE
}