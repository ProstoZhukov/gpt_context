package ru.tensor.sbis.catalog_decl.warehouse.list

import androidx.fragment.app.Fragment
import ru.tensor.sbis.catalog_decl.warehouse.data.WarehouseParams

/**
 * Фабрика для создания фрагментов выбора складов.
 *
 * @author mv.ilin
 */
fun interface WarehouseListHostFactory {
    fun create(params: WarehouseParams): Fragment
}