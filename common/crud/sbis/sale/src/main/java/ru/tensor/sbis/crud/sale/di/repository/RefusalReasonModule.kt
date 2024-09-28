package ru.tensor.sbis.crud.sale.di.repository

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.common.modelmapper.BaseModelMapper
import ru.tensor.sbis.crud.sale.crud.refusal_reason.RefusalReasonCommandWrapper
import ru.tensor.sbis.crud.sale.crud.refusal_reason.RefusalReasonCommandWrapperImpl
import ru.tensor.sbis.crud.sale.crud.refusal_reason.RefusalReasonListFilter
import ru.tensor.sbis.crud.sale.crud.refusal_reason.RefusalReasonRepository
import ru.tensor.sbis.crud.sale.crud.refusal_reason.RefusalReasonRepositoryImpl
import ru.tensor.sbis.crud.sale.crud.refusal_reason.command.RefusalReasonListCommand
import ru.tensor.sbis.crud.sale.crud.refusal_reason.mapper.RefusalReasonListMapper
import ru.tensor.sbis.crud.sale.crud.refusal_reason.mapper.RefusalReasonMapper
import ru.tensor.sbis.crud.sale.model.RefusalReason
import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.mvp.interactor.crudinterface.command.ListObservableCommand
import ru.tensor.sbis.sale.mobile.generated.RefusalReasonFacade
import ru.tensor.sbis.sale.mobile.generated.RefusalReasonFilter
import ru.tensor.sbis.sale.mobile.generated.RefusalReasonListResult
import ru.tensor.sbis.sale.mobile.generated.SaleMobileService
import ru.tensor.sbis.sale.mobile.generated.RefusalReasonModel as ControllerRefund

@Module
class RefusalReasonModule {

    @Provides
    internal fun provideFilter():
            RefusalReasonListFilter =
            RefusalReasonListFilter()

    @Provides
    internal fun provideManager(service: DependencyProvider<SaleMobileService>):
            DependencyProvider<RefusalReasonFacade> =
            DependencyProvider.create { service.get().refusalReason() }

    @Provides
    internal fun provideRepository(manager: DependencyProvider<RefusalReasonFacade>):
            RefusalReasonRepository = RefusalReasonRepositoryImpl(manager)


    @Provides
    internal fun provideCommandWrapper(repository: RefusalReasonRepository,
                                       listCommand: ListObservableCommand<PagedListResult<RefusalReason>, RefusalReasonFilter>):
            RefusalReasonCommandWrapper =
            RefusalReasonCommandWrapperImpl(repository, listCommand)


    @Provides
    internal fun provideMapper(context: Context):
            BaseModelMapper<ControllerRefund, RefusalReason> =
            RefusalReasonMapper(context)

    @Provides
    internal fun provideListCommand(repository: RefusalReasonRepository,
                                    mapper: BaseModelMapper<RefusalReasonListResult, PagedListResult<RefusalReason>>):
            ListObservableCommand<PagedListResult<RefusalReason>, RefusalReasonFilter> =
            RefusalReasonListCommand(repository, mapper)

    @Provides
    internal fun provideListMapper(context: Context):
            BaseModelMapper<RefusalReasonListResult, PagedListResult<RefusalReason>> =
            RefusalReasonListMapper(context)
}