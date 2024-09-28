package ru.tensor.sbis.crud.sbis.pricing.crud.available_price_list

import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.platform.generated.Subscription
import ru.tensor.sbis.pricing.generated.AvailablePricelistFacade
import ru.tensor.sbis.pricing.generated.AvailablePricelistFilter
import ru.tensor.sbis.pricing.generated.AvailablePricelistModel
import ru.tensor.sbis.pricing.generated.ListResultOfAvailablePricelistModelMapOfStringString
import ru.tensor.sbis.pricing.generated.DataRefreshedAvailablePricelistFacadeCallback

import java.util.*

internal class AvailablePriceListRepositoryImpl(private val controller: DependencyProvider<AvailablePricelistFacade>) :
        AvailablePriceListRepository {

    override fun list(filter: AvailablePricelistFilter): ListResultOfAvailablePricelistModelMapOfStringString =
            controller.get().list(filter)

    override fun refresh(filter: AvailablePricelistFilter): ListResultOfAvailablePricelistModelMapOfStringString =
            controller.get().refresh(filter)

    override fun subscribeDataRefreshedEvent(callback: DataRefreshedAvailablePricelistFacadeCallback): Subscription =
            controller.get().dataRefreshed().subscribe(callback)

    override fun updateLinks(salePointID: Long, pricesList: ArrayList<AvailablePricelistModel>) =
            controller.get().UpdateLinks(salePointID, pricesList)
}
