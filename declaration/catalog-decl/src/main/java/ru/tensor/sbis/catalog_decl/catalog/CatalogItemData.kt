package ru.tensor.sbis.catalog_decl.catalog

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

/**
 * Модель данных элемент каталога
 *
 * @author sp.lomakin
 */
@Parcelize
data class CatalogItemData(
    val uuid: UUID,
    val originalId: Long?,
    val parent: UUID?,
    val name: String?,
    val isFolder: Boolean?,
    val balance: Double?,
    val shortUnitsName: String?,
    val price: Double?,
    val cost: Double?,
    val fullImage: String?,
    val unitsId: String?,
    val capacity: Double?,
    val alcoVolume: Double?,
    val vatRate: Double?,
    val customNomenclature: String?,
    val article: String?,
    val nomType: NomenclatureTypeData?,
    val markedProductionGroup: MarkedProductionGroup?,
    val executionTime: String?,
    val outputWeight: String?,
    val catalogNomId: Long? = null,
    val priceNomId: Long? = null,
    val isDeleted: Boolean? = null,
    val packs: Packs? = null,
    val parentOriginalId: Long? = null,
    val isSaleCatalog: Boolean = false,
    val markedProductionType: MarkedProductionType? = null,
    val isMarkingNotRequired: Boolean? = null,
    val hasChilds: Boolean? = null,
) : Parcelable