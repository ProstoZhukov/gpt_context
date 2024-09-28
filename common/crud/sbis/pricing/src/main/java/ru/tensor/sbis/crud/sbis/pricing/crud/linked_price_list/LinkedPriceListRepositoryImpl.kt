package ru.tensor.sbis.crud.sbis.pricing.crud.linked_price_list

import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.platform.generated.Subscription
import ru.tensor.sbis.pricing.generated.*
import kotlin.collections.ArrayList

internal class LinkedPriceListRepositoryImpl(private val controller: DependencyProvider<LinkedPricelistFacade>) :
        LinkedPriceListRepository {

    override fun updateLinks(salePointID: Long, pricesList: ArrayList<LinkedPricelistModel>) =
        controller.get().setPricelistLinks(pricesList, salePointID)

    override fun list(filter: LinkedPricelistFilter): ListResultOfLinkedPricelistModelMapOfStringString =
            controller.get().list(filter)

    override fun refresh(filter: LinkedPricelistFilter): ListResultOfLinkedPricelistModelMapOfStringString =
            controller.get().refresh(filter)

    override fun subscribeDataRefreshedEvent(callback: DataRefreshedLinkedPricelistFacadeCallback): Subscription =
            controller.get().dataRefreshed().subscribe(callback)
}
