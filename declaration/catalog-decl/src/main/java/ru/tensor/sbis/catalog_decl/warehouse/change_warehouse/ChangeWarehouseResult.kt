package ru.tensor.sbis.catalog_decl.warehouse.change_warehouse

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.catalog_decl.warehouse.data.WarehouseData

/**
 * Результат выбора складов.
 *
 * @author mv.ilin
 */
sealed interface ChangeWarehouseResult : Parcelable {

    /**
     * Обратный вызов при подтверждении выбора складов из списка.
     *
     * @param warehouses список выбранных складов.
     */
    @Parcelize
    data class OnReturnWarehouses(val warehouses: List<WarehouseData>) : ChangeWarehouseResult
}