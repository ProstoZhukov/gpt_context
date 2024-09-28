package ru.tensor.sbis.crud.sbis.pricing.crud.available_price_list

import io.reactivex.Completable
import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.mvp.interactor.BaseInteractor
import ru.tensor.sbis.crud.sbis.pricing.model.AvailablePriceList
import ru.tensor.sbis.mvp.interactor.crudinterface.command.*
import ru.tensor.sbis.pricing.generated.AvailablePricelistFilter
import ru.tensor.sbis.pricing.generated.DataRefreshedAvailablePricelistFacadeCallback
import java.util.*
import ru.tensor.sbis.pricing.generated.AvailablePricelistModel as ControllerAvailablePriceListModel

internal class AvailablePriceListCommandWrapperImpl(val repository: AvailablePriceListRepository,
                                                    override val listCommand: BaseListObservableCommand<PagedListResult<AvailablePriceList>, AvailablePricelistFilter, DataRefreshedAvailablePricelistFacadeCallback>) :
        AvailablePriceListCommandWrapper,
        BaseInteractor() {

    override fun updateLinks(salePointID: Long, pricesList: ArrayList<ControllerAvailablePriceListModel>): Completable =
            Completable.fromCallable { repository.updateLinks(salePointID, pricesList) }
                    .compose(completableBackgroundSchedulers)
}