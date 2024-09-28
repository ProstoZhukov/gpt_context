package ru.tensor.sbis.crud.sbis.pricing.di.repository

import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.common.modelmapper.BaseModelMapper
import ru.tensor.sbis.crud.sbis.pricing.crud.available_price_list.AvailablePriceListCommandWrapper
import ru.tensor.sbis.crud.sbis.pricing.crud.available_price_list.AvailablePriceListFilter
import ru.tensor.sbis.crud.sbis.pricing.crud.available_price_list.AvailablePriceListRepository
import ru.tensor.sbis.crud.sbis.pricing.model.AvailablePriceList
import ru.tensor.sbis.mvp.interactor.crudinterface.command.*
import ru.tensor.sbis.pricing.generated.*

interface AvailablePriceListComponent {

    fun getAvailablePriceListModelManager(): DependencyProvider<AvailablePricelistFacade>
    fun getAvailablePriceListModelRepository(): AvailablePriceListRepository
    fun getAvailablePriceListModelCommandWrapper(): AvailablePriceListCommandWrapper

    fun getAvailablePriceListModelMapper(): BaseModelMapper<AvailablePricelistModel, AvailablePriceList>

    fun getAvailablePriceListModelListCommand(): BaseListObservableCommand<PagedListResult<AvailablePriceList>, AvailablePricelistFilter, DataRefreshedAvailablePricelistFacadeCallback>
    fun getAvailablePriceListModelListMapper(): BaseModelMapper<ListResultOfAvailablePricelistModelMapOfStringString, PagedListResult<AvailablePriceList>>
    fun getAvailablePriceListModelListFilter(): AvailablePriceListFilter
}