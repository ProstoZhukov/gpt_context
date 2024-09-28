package ru.tensor.sbis.catalog_decl.catalog

/**
 *  Модель данных для заполнения состава номенклатуры в [NomenclatureBodyViewData]
 *
 *  [name] - название
 *  [qty] - кол-во
 *  [shortUnitsName] - ед. изм.
 *  [childrenContentsNomenclature] - вложеные ингредиенты
 *
 * @author sp.lomakin
 */
class ContentsNomenclatureViewData(
    val name: String?,
    val qty: Double?,
    val shortUnitsName: String?,
    val childrenContentsNomenclature: List<ContentsNomenclatureViewData>
)