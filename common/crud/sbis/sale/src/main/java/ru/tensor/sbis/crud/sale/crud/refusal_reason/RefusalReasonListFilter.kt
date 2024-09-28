package ru.tensor.sbis.crud.sale.crud.refusal_reason

import ru.tensor.sbis.mvp.interactor.crudinterface.filter.AnchorPositionQueryBuilder
import ru.tensor.sbis.mvp.interactor.crudinterface.filter.ListFilter
import ru.tensor.sbis.crud.sale.model.RefusalReasonType
import ru.tensor.sbis.crud.sale.model.SaleSyncStatus
import ru.tensor.sbis.crud.sale.model.SaleVisibilityType
import ru.tensor.sbis.crud.sale.model.map
import ru.tensor.sbis.sale.mobile.generated.RefusalReasonFilter
import ru.tensor.sbis.sale.mobile.generated.SyncStatus
import java.io.Serializable
import ru.tensor.sbis.sale.mobile.generated.RefusalReasonType as ControllerRefusalReasonType
import ru.tensor.sbis.sale.mobile.generated.VisibilityType as ControllerVisibilityType

/**@SelfDocumented */
class RefusalReasonListFilter : Serializable, ListFilter() {

    private var byType: RefusalReasonType? = null

    private var byVisible: SaleVisibilityType = SaleVisibilityType.VISIBLE_ONLY

    private var byWriteOff: Boolean? = null

    private var byDeleted: Boolean? = null

    private var bySyncStatus: SaleSyncStatus? = null

    private var limit: Int? = null

    /**@SelfDocumented */
    var offset: Int? = null

    override fun queryBuilder(): Builder<*, *> =
        RefusalReasonFilterBuilder(
            byType?.map(),
            byVisible.map(),
            byWriteOff,
            byDeleted,
            bySyncStatus?.map(),
            limit,
            offset
        )
            .searchQuery(mSearchQuery)

    private class RefusalReasonFilterBuilder(
        private val byType: ControllerRefusalReasonType?,
        private val byVisible: ControllerVisibilityType,
        private val byWriteOff: Boolean?,
        private val byDeleted: Boolean?,
        private val bySyncStatus: SyncStatus?,
        private val limit: Int?,
        private val offset: Int?
    ) :
        AnchorPositionQueryBuilder<Any, RefusalReasonFilter>() {

        override fun build(): RefusalReasonFilter =
            RefusalReasonFilter(
                byType,
                byVisible,
                byWriteOff,
                byDeleted,
                bySyncStatus,
                limit,
                offset
            )
    }
}