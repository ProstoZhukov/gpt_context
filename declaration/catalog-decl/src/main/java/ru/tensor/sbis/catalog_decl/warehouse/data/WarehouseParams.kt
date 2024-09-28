package ru.tensor.sbis.catalog_decl.warehouse.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

const val WAREHOUSE_PERMISSION_SCOPE = "Warehouse"

/**
 * Начальные параметры окна выбора склада.
 *
 * @param isMultiSelect множественный выбор.
 * @param organisationId идентификатор организации для выборки списка складов.
 * @param organisationScopeAreas список зон доступа, по которым отбирать компании.
 * @param toSideIsLast необходимо ли отображать раздел "На сторону" в конце списка.
 * @param withOnlyUsed - отображать склады, которые находятся не в архиве.
 * @param warehouseIds список идентификаторов уже выбранных складов.
 * @param displayFilterByOurOrganisation отображать ли фильтр по НО, если это возможно.
 * @param needHideOnScrollSearchPanel необходимо ли скрывать строку поиска при прокрутке списка.
 *
 * @author as.mozgolin
 */
@Parcelize
data class WarehouseParams(
    val isMultiSelect: Boolean = false,
    val organisationId: OurOrganisation? = null,
    val organisationScopeAreas: List<String> = emptyList(),
    val toSideIsLast: Boolean = false,
    val withOnlyUsed: Boolean = true,
    val warehouseIds: Set<Long> = emptySet(),
    val displayFilterByOurOrganisation: Boolean = false,
    val needHideOnScrollSearchPanel: Boolean = true,
    val warehouseScopesAreas: List<String>? = listOf(WAREHOUSE_PERMISSION_SCOPE),
) : Parcelable