package ru.tensor.sbis.catalog_decl.warehouse

import androidx.activity.result.contract.ActivityResultContract
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import ru.tensor.sbis.catalog_decl.warehouse.change_warehouse.ChangeWarehouseFactory
import ru.tensor.sbis.catalog_decl.warehouse.change_warehouse.ChangeWarehouseResult
import ru.tensor.sbis.catalog_decl.warehouse.data.WarehouseData
import ru.tensor.sbis.catalog_decl.warehouse.data.WarehouseParams
import ru.tensor.sbis.catalog_decl.warehouse.list.WarehouseListHostFactory
import ru.tensor.sbis.catalog_decl.warehouse.list.WarehouseListHostResult
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Описывает функционал, который данный модуль предоставляет
 *
 * Обработка переходов "назад"
 * Во время события:
 *  1.  Найдите фрагмент в прикладном [FragmentManager]-е
 *  2.  Приведите его к ru.tensor.sbis.base_components.fragment.FragmentBackPress
 *  3.  Вызовите onBackPressed
 *
 * @author sp.lomakin
 */
interface WarehouseFeature : Feature {

    /**
     * Возвращает контракт Activity для выбора складов
     */
    fun getWarehouseSelectActivityContract(): ActivityResultContract<WarehouseParams, List<WarehouseData>?>

    /**
     * Получить фабрику создания списка складов.
     *
     * @param fragment фрагмент.
     * @param onResult сюда будет возвращен результат выбора.
     */

    fun warehouseListContract(
        fragment: Fragment,
        onResult: (WarehouseListHostResult) -> Unit
    ): WarehouseListHostFactory

    /**
     * Получить фабрику создания фрагментов выбора склада в окне выбора.
     *
     * @param fragment фрагмент.
     * @param onResult сюда будет возвращен результат выбора.
     */
    fun createSelectionWindowContent(
        fragment: Fragment,
        onResult: (ChangeWarehouseResult) -> Unit
    ): ChangeWarehouseFactory
}
