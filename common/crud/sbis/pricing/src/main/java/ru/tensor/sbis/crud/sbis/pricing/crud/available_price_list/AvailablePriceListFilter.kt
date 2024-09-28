package ru.tensor.sbis.crud.sbis.pricing.crud.available_price_list

import ru.tensor.sbis.mvp.interactor.crudinterface.filter.AnchorPositionQueryBuilder
import ru.tensor.sbis.mvp.interactor.crudinterface.filter.ListFilter
import ru.tensor.sbis.pricing.generated.AvailablePricelistFilter
import ru.tensor.sbis.pricing.generated.Navigation
import java.io.Serializable

class AvailablePriceListFilter : Serializable, ListFilter() {

    override fun queryBuilder(): Builder<*, *> =
        AvailablePriceListFilterBuilder().searchQuery(mSearchQuery)

    class AvailablePriceListFilterBuilder internal constructor() :
        AnchorPositionQueryBuilder<Any, AvailablePricelistFilter>() {

        var salePointID: Long = 0
        var pageSize = 1000

        override fun build(): AvailablePricelistFilter =
            AvailablePricelistFilter(
                searchQuery = mSearchQuery.orEmpty(),
                nav = Navigation(0, pageSize),
                salePointId = salePointID
            )
    }
}