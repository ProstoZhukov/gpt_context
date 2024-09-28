package ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.store

import com.arkivanov.mvikotlin.core.store.Store
import ru.tensor.sbis.communicator.declaration.crm.model.CRMChatFilterModel
import ru.tensor.sbis.communicator.declaration.crm.model.CRMCheckableFilterType
import ru.tensor.sbis.communicator.declaration.crm.model.CRMOpenableFilterType
import ru.tensor.sbis.communicator.declaration.crm.model.CRMRadioButtonFilterType
import java.util.UUID
import ru.tensor.sbis.common.R as RCommon

/**
 * @author da.zhukov
 */
internal interface CrmChatFilterStore :
    Store<CrmChatFilterStore.Intent, CrmChatFilterStore.State, CrmChatFilterStore.Label> {

    /**
     * Намерение, отражающее действие со стороны пользователя. Подается в [Store] как входной параметр.
     *
     * @author da.zhukov
     */
    sealed interface Intent {
        data class SelectedFilterItem(val type: CRMRadioButtonFilterType) : Intent
        data class CheckedFilterItem(val type: CRMCheckableFilterType) : Intent
        data class OpenedFilterItem(val type: CRMOpenableFilterType, val showResetButton: Boolean) : Intent
        data class ContentItemIsSelected(
            val resultUuids: ArrayList<UUID>,
            val resultTitles: ArrayList<String>
        ) : Intent
        data class ContentItemsIsApply(
            val resultUuids: ArrayList<UUID>,
            val resultTitles: ArrayList<String>,
            val type: CRMOpenableFilterType
        ) : Intent
        object Reset : Intent
        object Apply : Intent
        object Back : Intent
    }

    /**
     * Выходной параметр [Store] для взаимодействия с сущностями вне MVI. Является ответом на пришедшим [Intent].
     *
     * @author da.zhukov
     */
    sealed interface Label {

        /**
         * Применить фильтры.
         */
        data class Apply(val filterModel: CRMChatFilterModel, val filters: Set<String>) : Label

        object Back : Label

        object ResetFilter : Label

        data class ContentItemIsSelected(
            val resultUuids: ArrayList<UUID>,
            val resultTitles: ArrayList<String>
        ) : Label

        /**
         * Открыть сторонний экран для выбора фильтра.
         */
        data class Open(val type: CRMOpenableFilterType) : Label
    }

    /**
     * Содержит текущее состояние.
     *
     * @property filterModel модель выбранных фильтров.
     * @property filters список выбранных фильтров.
     */
    data class State(
        val contentItems: MutableList<Any>,
        val filterModel: CRMChatFilterModel,
        val filters: Set<String>,
        val contentIsOpen: Boolean = false,
        val headerTitle: Int = RCommon.string.common_filter,
        val needShowApplyButton: Boolean = true,
        val needShowResetButton: Boolean = false
    ) {
        val defFilterHeaderTitle = RCommon.string.common_filter
    }
}
