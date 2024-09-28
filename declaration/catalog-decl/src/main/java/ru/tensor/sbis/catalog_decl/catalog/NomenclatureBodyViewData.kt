package ru.tensor.sbis.catalog_decl.catalog

/**
 * Модель данных для view информация по номенклатуре.
 *
 * @param description - Описание.
 * @param prodVolume - Кол-во состава.
 * @param prodInfoCookingTime - Время приготовления.
 * @param prodInfoDescription - Технологический процесс.
 * @param capacity - Объем.
 * @param alcoholVolume - Крепость.
 * @param retail - МРЦ.
 * @param customNomenclature - код номенклатуры.
 * @param customNomenclatureBlockIsVisible - отобразить блок "Код".
 * @param attributes - параметры номенклатуры.
 * @param addProps - дополнительные поля номенклатуры.
 * @param contentsNomenclature - состав номенклатуры.
 * @param shortUnitsName - ед. изм.
 *
 * @author mv.ilin
 */
data class NomenclatureBodyViewData(
    val description: CharSequence? = null,
    val prodVolume: String? = null,
    val prodInfoCookingTime: String? = null,
    val prodInfoDescription: CharSequence? = null,
    val capacity: Double? = null,
    val alcoholVolume: Double? = null,
    val retail: Double? = null,
    val customNomenclature: String? = null,
    val customNomenclatureBlockIsVisible: Boolean = false,
    val attributes: List<NomenclatureAttributes> = emptyList(),
    val addProps: NomenclatureAddProps? = null,
    val contentsNomenclature: List<ContentsNomenclatureViewData> = emptyList(),
    val shortUnitsName: String? = null
)