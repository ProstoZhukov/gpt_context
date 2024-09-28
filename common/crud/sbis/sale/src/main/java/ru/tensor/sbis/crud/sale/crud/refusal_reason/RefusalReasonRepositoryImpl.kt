package ru.tensor.sbis.crud.sale.crud.refusal_reason

import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.sale.mobile.generated.*

/**@SelfDocumented */
internal class RefusalReasonRepositoryImpl(private val controller: DependencyProvider<RefusalReasonFacade>) :
        RefusalReasonRepository {

    override fun create(name: String, type: RefusalReasonType, isWriteOff: Boolean): RefusalReasonModel =
            controller.get().create(name, type, isWriteOff)

    override fun update(entity: RefusalReasonModel): RefusalReasonModel =
            controller.get().update(entity)

    override fun delete(id: Long): Boolean =
            controller.get().delete(id)

    override fun read(id: Long): RefusalReasonModel? =
            controller.get().read(id)

    override fun list(filter: RefusalReasonFilter): RefusalReasonListResult =
            controller.get().list(filter)

    override fun refresh(filter: RefusalReasonFilter): RefusalReasonListResult =
            controller.get().refresh(filter)

    override fun setDataRefreshCallback(callback: RefusalReasonDataRefreshCallback): RefusalReasonSubscription {
        return controller.get().setDataRefreshCallback(callback)
    }
}