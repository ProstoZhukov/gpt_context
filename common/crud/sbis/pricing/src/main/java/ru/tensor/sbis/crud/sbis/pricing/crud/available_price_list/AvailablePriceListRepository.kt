package ru.tensor.sbis.crud.sbis.pricing.crud.available_price_list

import ru.tensor.sbis.mvp.interactor.crudinterface.BaseListRepository
import ru.tensor.sbis.pricing.generated.AvailablePricelistFilter
import ru.tensor.sbis.pricing.generated.AvailablePricelistModel
import ru.tensor.sbis.pricing.generated.ListResultOfAvailablePricelistModelMapOfStringString
import ru.tensor.sbis.pricing.generated.DataRefreshedAvailablePricelistFacadeCallback
import java.util.*

/**
 * Интерфейс для связи с контроллером.
 */
interface AvailablePriceListRepository :
        BaseListRepository<ListResultOfAvailablePricelistModelMapOfStringString, AvailablePricelistFilter, DataRefreshedAvailablePricelistFacadeCallback> {

    /**
     * Обновляет связи прайслистов с точкой продаж
     *
     * @param warehouseId - идентификатор склада
     * @param pricesList - список прайслистов
     */
    fun updateLinks(salePointID: Long, pricesList: ArrayList<AvailablePricelistModel>)
}
