package ru.tensor.sbis.crud.sale.di.repository

import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.mvp.interactor.crudinterface.command.ListObservableCommand
import ru.tensor.sbis.common.modelmapper.BaseModelMapper
import ru.tensor.sbis.crud.sale.crud.refusal_reason.RefusalReasonCommandWrapper
import ru.tensor.sbis.crud.sale.crud.refusal_reason.RefusalReasonListFilter
import ru.tensor.sbis.crud.sale.crud.refusal_reason.RefusalReasonRepository
import ru.tensor.sbis.crud.sale.model.RefusalReason
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.sale.mobile.generated.RefusalReasonFacade
import ru.tensor.sbis.sale.mobile.generated.RefusalReasonFilter
import ru.tensor.sbis.sale.mobile.generated.RefusalReasonListResult
import ru.tensor.sbis.sale.mobile.generated.RefusalReasonModel as ControllerRefusalReason

/**@SelfDocumented */
interface RefusalReasonComponent : Feature {

    /**@SelfDocumented */
    fun getRefusalReasonManager(): DependencyProvider<RefusalReasonFacade>

    /**@SelfDocumented */
    fun getRefusalReasonRepository(): RefusalReasonRepository

    /**@SelfDocumented */
    fun getRefusalReasonCommandWrapper(): RefusalReasonCommandWrapper

    /**@SelfDocumented */
    fun getRefusalReasonMapper(): BaseModelMapper<ControllerRefusalReason, RefusalReason>

    /**@SelfDocumented */
    fun getRefusalReasonListCommand(): ListObservableCommand<PagedListResult<RefusalReason>, RefusalReasonFilter>

    /**@SelfDocumented */
    fun getRefusalReasonListMapper(): BaseModelMapper<RefusalReasonListResult, PagedListResult<RefusalReason>>

    /**@SelfDocumented */
    fun getRefusalReasonListFilter(): RefusalReasonListFilter
}