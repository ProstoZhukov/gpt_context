package ru.tensor.sbis.crud.sbis.pricing.di.repository

import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.common.modelmapper.BaseModelMapper
import ru.tensor.sbis.crud.sbis.pricing.crud.linked_price_list.LinkedPriceListCommandWrapper
import ru.tensor.sbis.crud.sbis.pricing.crud.linked_price_list.LinkedPriceListFilter
import ru.tensor.sbis.crud.sbis.pricing.crud.linked_price_list.LinkedPriceListRepository
import ru.tensor.sbis.crud.sbis.pricing.model.LinkedPriceList
import ru.tensor.sbis.mvp.interactor.crudinterface.command.*
import ru.tensor.sbis.pricing.generated.*

interface LinkedPriceListComponent {

    fun getLinkedPriceListModelManager(): DependencyProvider<LinkedPricelistFacade>
    fun getLinkedPriceListModelRepository(): LinkedPriceListRepository
    fun getLinkedPriceListModelCommandWrapper(): LinkedPriceListCommandWrapper

    fun getLinkedPriceListModelMapper(): BaseModelMapper<LinkedPricelistModel, LinkedPriceList>

    fun getLinkedPriceListModelListCommand(): BaseListObservableCommand<PagedListResult<LinkedPriceList>, LinkedPricelistFilter, DataRefreshedLinkedPricelistFacadeCallback>
    fun getLinkedPriceListModelListMapper(): BaseModelMapper<ListResultOfLinkedPricelistModelMapOfStringString, PagedListResult<LinkedPriceList>>
    fun getLinkedPriceListModelListFilter(): LinkedPriceListFilter
}