package ru.tensor.sbis.crud.sbis.pricing.di.repository

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.common.modelmapper.BaseModelMapper
import ru.tensor.sbis.crud.sbis.pricing.crud.available_price_list.AvailablePriceListCommandWrapper
import ru.tensor.sbis.crud.sbis.pricing.crud.available_price_list.AvailablePriceListCommandWrapperImpl
import ru.tensor.sbis.crud.sbis.pricing.crud.available_price_list.AvailablePriceListFilter
import ru.tensor.sbis.crud.sbis.pricing.crud.available_price_list.AvailablePriceListRepository
import ru.tensor.sbis.crud.sbis.pricing.crud.available_price_list.AvailablePriceListRepositoryImpl
import ru.tensor.sbis.crud.sbis.pricing.crud.available_price_list.mapper.AvailablePriceListModelListMapper
import ru.tensor.sbis.crud.sbis.pricing.crud.available_price_list.mapper.AvailablePriceListModelMapper
import ru.tensor.sbis.crud.sbis.pricing.model.AvailablePriceList
import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.mvp.interactor.crudinterface.command.BaseListCommand
import ru.tensor.sbis.mvp.interactor.crudinterface.command.BaseListObservableCommand
import ru.tensor.sbis.pricing.generated.AvailablePricelistFacade
import ru.tensor.sbis.pricing.generated.AvailablePricelistFilter
import ru.tensor.sbis.pricing.generated.AvailablePricelistModel
import ru.tensor.sbis.pricing.generated.DataRefreshedAvailablePricelistFacadeCallback
import ru.tensor.sbis.pricing.generated.ListResultOfAvailablePricelistModelMapOfStringString

@Module
class AvailablePriceListModule {

    @Provides
    internal fun provideManager():
            DependencyProvider<AvailablePricelistFacade> =
            DependencyProvider.create { AvailablePricelistFacade.instance() }

    @Provides
    internal fun provideRepository(manager: DependencyProvider<AvailablePricelistFacade>):
            AvailablePriceListRepository = AvailablePriceListRepositoryImpl(manager)

    @Provides
    internal fun provideCommandWrapper(repository: AvailablePriceListRepository,
                                       listCommand: BaseListObservableCommand<PagedListResult<AvailablePriceList>, AvailablePricelistFilter, DataRefreshedAvailablePricelistFacadeCallback>):
            AvailablePriceListCommandWrapper =
            AvailablePriceListCommandWrapperImpl(
                    repository,
                    listCommand)

    @Provides
    internal fun provideMapper(context: Context):
            BaseModelMapper<AvailablePricelistModel, AvailablePriceList> =
            AvailablePriceListModelMapper(context)

    @Provides
    internal fun provideFilter():
            AvailablePriceListFilter =
            AvailablePriceListFilter()

    @Provides
    internal fun provideListCommand(repository: AvailablePriceListRepository,
                                    mapper: BaseModelMapper<ListResultOfAvailablePricelistModelMapOfStringString, PagedListResult<AvailablePriceList>>):
            BaseListObservableCommand<PagedListResult<AvailablePriceList>, AvailablePricelistFilter, DataRefreshedAvailablePricelistFacadeCallback> =
            BaseListCommand(repository, mapper)

    @Provides
    internal fun provideListMapper(context: Context):
            BaseModelMapper<ListResultOfAvailablePricelistModelMapOfStringString, PagedListResult<AvailablePriceList>> =
            AvailablePriceListModelListMapper(context)
}