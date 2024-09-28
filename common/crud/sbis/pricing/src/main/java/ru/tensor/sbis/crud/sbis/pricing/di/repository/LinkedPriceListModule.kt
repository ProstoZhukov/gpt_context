package ru.tensor.sbis.crud.sbis.pricing.di.repository

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.common.modelmapper.BaseModelMapper
import ru.tensor.sbis.crud.sbis.pricing.crud.linked_price_list.LinkedPriceListCommandWrapper
import ru.tensor.sbis.crud.sbis.pricing.crud.linked_price_list.LinkedPriceListCommandWrapperImpl
import ru.tensor.sbis.crud.sbis.pricing.crud.linked_price_list.LinkedPriceListFilter
import ru.tensor.sbis.crud.sbis.pricing.crud.linked_price_list.LinkedPriceListRepository
import ru.tensor.sbis.crud.sbis.pricing.crud.linked_price_list.LinkedPriceListRepositoryImpl
import ru.tensor.sbis.crud.sbis.pricing.crud.linked_price_list.mapper.LinkedPriceListModelListMapper
import ru.tensor.sbis.crud.sbis.pricing.crud.linked_price_list.mapper.LinkedPriceListModelMapper
import ru.tensor.sbis.crud.sbis.pricing.model.LinkedPriceList
import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.mvp.interactor.crudinterface.command.BaseListCommand
import ru.tensor.sbis.mvp.interactor.crudinterface.command.BaseListObservableCommand
import ru.tensor.sbis.pricing.generated.DataRefreshedLinkedPricelistFacadeCallback
import ru.tensor.sbis.pricing.generated.LinkedPricelistFacade
import ru.tensor.sbis.pricing.generated.LinkedPricelistFilter
import ru.tensor.sbis.pricing.generated.LinkedPricelistModel
import ru.tensor.sbis.pricing.generated.ListResultOfLinkedPricelistModelMapOfStringString

@Module
class LinkedPriceListModule {

    @Provides
    internal fun provideManager():
            DependencyProvider<LinkedPricelistFacade> =
            DependencyProvider.create { LinkedPricelistFacade.instance() }

    @Provides
    internal fun provideRepository(manager: DependencyProvider<LinkedPricelistFacade>):
            LinkedPriceListRepository = LinkedPriceListRepositoryImpl(manager)

    @Provides
    internal fun provideCommandWrapper(repository:LinkedPriceListRepository,
                                       listCommand: BaseListObservableCommand<PagedListResult<LinkedPriceList>, LinkedPricelistFilter, DataRefreshedLinkedPricelistFacadeCallback>):
            LinkedPriceListCommandWrapper =
            LinkedPriceListCommandWrapperImpl(repository, listCommand)

    @Provides
    internal fun provideMapper(context: Context):
            BaseModelMapper<LinkedPricelistModel, LinkedPriceList> =
            LinkedPriceListModelMapper(context)

    @Provides
    internal fun provideFilter():
            LinkedPriceListFilter =
            LinkedPriceListFilter()

    @Provides
    internal fun provideListCommand(repository: LinkedPriceListRepository,
                                    mapper: BaseModelMapper<ListResultOfLinkedPricelistModelMapOfStringString, PagedListResult<LinkedPriceList>>):
            BaseListObservableCommand<PagedListResult<LinkedPriceList>, LinkedPricelistFilter, DataRefreshedLinkedPricelistFacadeCallback> =
            BaseListCommand(repository, mapper)

    @Provides
    internal fun provideListMapper(context: Context):
            BaseModelMapper<ListResultOfLinkedPricelistModelMapOfStringString, PagedListResult<LinkedPriceList>> =
            LinkedPriceListModelListMapper(context)
}