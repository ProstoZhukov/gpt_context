package ru.tensor.sbis.crud.sbis.pricing.crud.linked_price_list

import ru.tensor.sbis.mvp.interactor.crudinterface.filter.AnchorPositionQueryBuilder
import ru.tensor.sbis.mvp.interactor.crudinterface.filter.ListFilter
import ru.tensor.sbis.pricing.generated.LinkedPricelistFilter
import ru.tensor.sbis.pricing.generated.Navigation
import java.io.Serializable

class LinkedPriceListFilter : Serializable, ListFilter() {

    var salePointID: Long = 0
    var page: Int = 0
    var pageSize: Int = 50

    override fun queryBuilder(): Builder<*, *> =
            LinkedPriceListFilterBuilder(
                salePointID = salePointID,
                page = page,
                pageSize = pageSize
            )
                    .searchQuery(mSearchQuery)

    private class LinkedPriceListFilterBuilder
    internal constructor(private val salePointID: Long,
                         private val page: Int,
                         private val pageSize: Int) :
            AnchorPositionQueryBuilder<Any, LinkedPricelistFilter>() {

        override fun build(): LinkedPricelistFilter =
                LinkedPricelistFilter(
                    salePointId = salePointID,
                    searchQuery = mSearchQuery.orEmpty(),
                    nav = Navigation(page, pageSize),
                    forSync = null
                )
    }
}