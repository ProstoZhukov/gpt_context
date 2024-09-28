package ru.tensor.sbis.catalog_decl.catalog

/**
 *  Модель данных Номенклатура и кол-во
 */
data class CatalogCartNomenclatureData(
        val nomenclature: CatalogItemData,
        val amount: Int
)