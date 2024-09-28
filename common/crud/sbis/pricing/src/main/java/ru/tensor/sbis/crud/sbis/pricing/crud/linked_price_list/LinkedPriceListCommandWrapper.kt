package ru.tensor.sbis.crud.sbis.pricing.crud.linked_price_list

import io.reactivex.Completable
import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.crud.sbis.pricing.model.LinkedPriceList
import ru.tensor.sbis.mvp.interactor.crudinterface.command.*
import ru.tensor.sbis.pricing.generated.DataRefreshedLinkedPricelistFacadeCallback
import ru.tensor.sbis.pricing.generated.LinkedPricelistFilter
import ru.tensor.sbis.pricing.generated.LinkedPricelistModel

/**
 * Wrapper команд для контроллера
 */
interface LinkedPriceListCommandWrapper {

    val listCommand: BaseListObservableCommand<PagedListResult<LinkedPriceList>, LinkedPricelistFilter, DataRefreshedLinkedPricelistFacadeCallback>

    /**
     * Обновляет связи прайслистов с точкой продаж
     *
     * @param salePointID - идентификатор точки продаж
     * @param pricesList - список прайслистов
     */
    fun updateLinks(salePointID: Long, pricesList: ArrayList<LinkedPricelistModel>): Completable
}