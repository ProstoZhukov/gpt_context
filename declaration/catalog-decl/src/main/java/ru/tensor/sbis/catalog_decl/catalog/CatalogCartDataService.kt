package ru.tensor.sbis.catalog_decl.catalog

import io.reactivex.Single
import java.util.*

/**
 * Корзина, хранит количество и uuid номенклатур.
 *
 * @author sp.lomakin
 */
interface CatalogCartDataService: CatalogCartDataSource {

    /**
     *  Получить номенклатуру сохранённую в корзине.
     */
    fun loadNomenclatureOfCart(): Single<List<CatalogCartNomenclatureData>>

    /**
     *  Удалить элементы.
     */
    fun removeItemsByUUID(itemsUUID: List<UUID>)
}