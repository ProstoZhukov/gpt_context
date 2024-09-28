package ru.tensor.sbis.crud.sbis.pricing.crud.available_price_list

import io.reactivex.Completable
import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.crud.sbis.pricing.model.AvailablePriceList
import ru.tensor.sbis.mvp.interactor.crudinterface.command.*
import ru.tensor.sbis.pricing.generated.AvailablePricelistFilter
import ru.tensor.sbis.pricing.generated.AvailablePricelistModel
import ru.tensor.sbis.pricing.generated.DataRefreshedAvailablePricelistFacadeCallback
import java.util.*

/**
 * Wrapper команд для контроллера
 */
interface AvailablePriceListCommandWrapper {
    val listCommand: BaseListObservableCommand<PagedListResult<AvailablePriceList>, AvailablePricelistFilter, DataRefreshedAvailablePricelistFacadeCallback>

    /**
     * Обновляет связи прайслистов с точкой продаж
     *
     * @param warehouseId - идентификатор склада
     * @param pricesList - список прайслистов
     */
    fun updateLinks(salePointID: Long, pricesList: ArrayList<AvailablePricelistModel>): Completable
}