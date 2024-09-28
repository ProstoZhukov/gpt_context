package ru.tensor.sbis.crud.sbis.pricing.crud.linked_price_list

import io.reactivex.Completable
import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.mvp.interactor.BaseInteractor
import ru.tensor.sbis.crud.sbis.pricing.model.LinkedPriceList
import ru.tensor.sbis.mvp.interactor.crudinterface.command.*
import ru.tensor.sbis.pricing.generated.DataRefreshedLinkedPricelistFacadeCallback
import ru.tensor.sbis.pricing.generated.LinkedPricelistFilter
import ru.tensor.sbis.pricing.generated.LinkedPricelistModel

internal class LinkedPriceListCommandWrapperImpl(
    val repository: LinkedPriceListRepository,
    override val listCommand: BaseListObservableCommand<PagedListResult<LinkedPriceList>, LinkedPricelistFilter, DataRefreshedLinkedPricelistFacadeCallback>
) :
    LinkedPriceListCommandWrapper,
    BaseInteractor() {

    override fun updateLinks(
        salePointID: Long,
        pricesList: ArrayList<LinkedPricelistModel>
    ) = Completable.fromCallable { repository.updateLinks(salePointID, pricesList) }
        .compose(completableBackgroundSchedulers)

}
