package ru.tensor.sbis.catalog_decl.warehouse.list

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.catalog_decl.warehouse.data.WarehouseData

/**
 * Результат выбора складов.
 *
 * @author mv.ilin
 */
sealed interface WarehouseListHostResult : Parcelable {

    @Parcelize
    data class OnReturnWarehouses(val warehouses: List<WarehouseData>) : WarehouseListHostResult

    @Parcelize
    data class OnCurrentSelectedWarehouses(val warehouses: Set<Long>) : WarehouseListHostResult

    @Parcelize
    data class OnChangeWarehouseTitle(val title: String?) : WarehouseListHostResult
}