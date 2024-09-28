package ru.tensor.sbis.catalog_decl.warehouse

import io.reactivex.Maybe
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 *  Интерфейс для получения сохранённого идентификатора склада
 *
 *  @author sp.lomakin
 */
interface CurrentWarehouseProvider : Feature {
    fun getCurrentWarehouseIdRx(): Maybe<Long>

    fun getDefaultWarehouseIdRx(): Maybe<Long>
}