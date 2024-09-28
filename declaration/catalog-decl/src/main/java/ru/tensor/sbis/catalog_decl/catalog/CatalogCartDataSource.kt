package ru.tensor.sbis.catalog_decl.catalog

import io.reactivex.Single
import java.util.*

/**
 * Поставщик данных корзина.
 *
 * @author sp.lomakin
 */
interface CatalogCartDataSource {

    /**
     *  Сохранить [nomenclatureItem] в корзину с кол-во [count].
     *
     *  [count] может быть равен 0.
     *
     *  true успешно, false нельзя сохранить номенклатуры с указанным количеством.
     */
    fun setItem(nomenclatureItem: CatalogItemData, count: Int): Boolean

    /**
     *  Получить корзину.
     */
    fun getCart(): Single<Map<UUID, Int>>

    /**
     *  Очистить корзину, метод будет вызван при первом открытии корневой категории.
     */
    fun clear() {}
}