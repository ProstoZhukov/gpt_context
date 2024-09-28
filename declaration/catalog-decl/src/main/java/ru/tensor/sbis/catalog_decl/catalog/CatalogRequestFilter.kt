package ru.tensor.sbis.catalog_decl.catalog

/**
 *  Описание запроса для [CatalogItemProvider]
 */
class CatalogRequestFilter(
        val byText: String? = null,
        val byBarCode: String? = null,
        val foldersOrLeafs: Boolean? = null,
        val disableOnline: Boolean = false,
        val calcTotal: Boolean = false,
        val count: Long = 0
)