package ru.tensor.sbis.crud.sbis.pricing.crud.linked_price_list

import ru.tensor.sbis.mvp.interactor.crudinterface.BaseListRepository
import ru.tensor.sbis.pricing.generated.DataRefreshedLinkedPricelistFacadeCallback
import ru.tensor.sbis.pricing.generated.LinkedPricelistFilter
import ru.tensor.sbis.pricing.generated.LinkedPricelistModel
import ru.tensor.sbis.pricing.generated.ListResultOfLinkedPricelistModelMapOfStringString

/**
 * Интерфейс для связи с контроллером.
 */
interface LinkedPriceListRepository :
    BaseListRepository<ListResultOfLinkedPricelistModelMapOfStringString, LinkedPricelistFilter, DataRefreshedLinkedPricelistFacadeCallback> {

    /**
     * Обновляет связи прайслистов с точкой продаж
     *
     * @param warehouseId - идентификатор склада
     * @param pricesList - список прайслистов
     */
    fun updateLinks(salePointID: Long, pricesList: ArrayList<LinkedPricelistModel>)
}
