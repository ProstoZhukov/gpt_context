package ru.tensor.sbis.communicator.communicator_crm_chat_list.data

import ru.tensor.sbis.communicator.declaration.crm.model.CRMChatFilterModel
import ru.tensor.sbis.communicator.declaration.crm.model.CRMRadioButtonFilterType
import ru.tensor.sbis.consultations.generated.ConsultationCounterFilter
import ru.tensor.sbis.consultations.generated.ConsultationGroupType
import ru.tensor.sbis.consultations.generated.ConsultationListFilter
import ru.tensor.sbis.consultations.generated.ConsultationOperatorFilterMode
import java.util.UUID

/**
 * Холдер фильтра чатов CRM.
 *
 * @author da.zhukov
 */
internal class CRMChatListFilterHolder(
    private val filter: ConsultationListFilter,
    private val folderFilter: ConsultationCounterFilter = ConsultationCounterFilter()
): () -> ConsultationListFilter {

    override fun invoke(): ConsultationListFilter {
        return filter
    }

    val viewId: UUID
        get() = filter.viewId ?: let {
            filter.viewId = UUID.randomUUID()
            filter.viewId!!
        }

    private var currentFilterModel: CRMChatFilterModel = CRMChatFilterModel()
    private var currentFiltersTitle: List<String> = listOf()

    /** @SelfDocumented */
    fun getCurrentFolderFilter(): ConsultationCounterFilter {
       return folderFilter
    }

    /** @SelfDocumented */
    fun getCurrentFilterModel(): CRMChatFilterModel {
        return currentFilterModel
    }

    /** @SelfDocumented */
    fun getCurrentFilters(): List<String> {
        return currentFiltersTitle
    }

    /** @SelfDocumented */
    fun setQuery(query: String?) {
        filter.searchStr = query
    }

    /** @SelfDocumented */
    fun setGroupType(groupType: ConsultationGroupType) {
        filter.groupType = groupType
        folderFilter.consultationGroupType = groupType
    }

    /** @SelfDocumented */
    fun applyFilterTitle(title: List<String>) {
        currentFiltersTitle = title
    }

    fun applyFilter(filterModel: CRMChatFilterModel) {
        filter.apply {
            expired = filterModel.isExpired
            operatorFilter = filterModel.type.toConsultationOperatorFilterMode()
            sourceIds = filterModel.sourceIds.first
            channelIds = filterModel.channelIds.first
            clientIds = filterModel.clientIds.first
            operatorIds = filterModel.operatorIds.first
        }
        folderFilter.apply {
            operatorFilter = filterModel.type.toConsultationOperatorFilterMode()
            sourceIds = filterModel.sourceIds.first
            channelIds = filterModel.channelIds.first
            clientIds = filterModel.clientIds.first
            operatorIds = filterModel.operatorIds.first
        }
        currentFilterModel = filterModel
    }

    private fun CRMRadioButtonFilterType.toConsultationOperatorFilterMode(): ConsultationOperatorFilterMode =
        when (this) {
            CRMRadioButtonFilterType.ALL -> ConsultationOperatorFilterMode.ALL
            CRMRadioButtonFilterType.MY -> ConsultationOperatorFilterMode.MINE
            CRMRadioButtonFilterType.FROM_MY_CHANNELS -> ConsultationOperatorFilterMode.MY_CHANNELS
            CRMRadioButtonFilterType.DEFINED_OPERATORS -> ConsultationOperatorFilterMode.DEFINED_OPERATORS
            else -> ConsultationOperatorFilterMode.ALL
        }
}