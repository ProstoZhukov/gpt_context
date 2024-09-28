package ru.tensor.sbis.catalog_decl.warehouse.change_warehouse

import androidx.fragment.app.DialogFragment
import ru.tensor.sbis.catalog_decl.warehouse.data.WarehouseParams

/**
 * Фабрика для создания фрагментов выбора складов.
 *
 * @author mv.ilin
 */
fun interface ChangeWarehouseFactory {
    fun create(params: WarehouseParams): DialogFragment
}