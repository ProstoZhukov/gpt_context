package ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.store

import android.content.Context
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import ru.tensor.sbis.common.util.asArrayList
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.store.CrmChatFilterStore.Intent
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.store.CrmChatFilterStore.Label
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.store.CrmChatFilterStore.State
import ru.tensor.sbis.communicator.declaration.crm.model.CRMChatFilterModel
import ru.tensor.sbis.communicator.declaration.crm.model.CRMCheckableFilterType
import ru.tensor.sbis.communicator.declaration.crm.model.CRMOpenableFilterType
import ru.tensor.sbis.communicator.declaration.crm.model.CRMRadioButtonFilterType
import ru.tensor.sbis.mvi_extension.rx.RxJavaExecutor
import java.util.UUID

/**
 * Фабрика стора, основная бизнес логика экрана.
 *
 * @author da.zhukov
 */
internal class CrmChatFilterStoreFactory(
    private val storeFactory: StoreFactory,
    initFilterModel: CRMChatFilterModel,
    defFilterModel: CRMChatFilterModel,
    context: Context
) {

    private val filterHelper = CrmChatFilterHelper(
        context = context,
        initFilterModel = initFilterModel,
        defFilterModel = defFilterModel
    )

    /** @SelfDocumented */
    fun create(): CrmChatFilterStore {
        return object : CrmChatFilterStore,
            Store<Intent, State, Label> by storeFactory.create(
                name = "CrmChatFilterStore",
                initialState = State(
                    contentItems = mutableListOf(),
                    filterModel = filterHelper.initFilterModel(),
                    filters = filterHelper.initFilter()
                ),
                bootstrapper = SimpleBootstrapper(),
                executorFactory = { ExecutorImpl() },
                reducer = ReducerImpl(filterHelper = filterHelper)
            ) {}
    }

    private class ExecutorImpl : RxJavaExecutor<Intent, Action, State, Message, Label>() {

        override fun executeIntent(intent: Intent, getState: () -> State) {
            when (intent) {
                is Intent.Reset -> {
                    if (getState().contentIsOpen) {
                        publish(Label.ResetFilter)
                    } else {
                        dispatch(Message.ResetFilter)
                    }
                }
                is Intent.Apply -> publish(Label.Apply(getState().filterModel, getState().filters))
                is Intent.Back -> {
                    dispatch(Message.Back)
                    publish(Label.Back)
                }
                is Intent.SelectedFilterItem -> dispatch(Message.UpdateSelectedType(intent.type))
                is Intent.CheckedFilterItem -> dispatch(Message.UpdateCheckableType(intent.type))
                is Intent.OpenedFilterItem -> {
                    dispatch(Message.ContentIsOpen(intent.type, intent.showResetButton))
                    publish(Label.Open(intent.type))
                }
                is Intent.ContentItemIsSelected -> {
                    dispatch(Message.UpdateResetButtonState(true))
                    publish(Label.ContentItemIsSelected(intent.resultUuids, intent.resultTitles))
                }
                is Intent.ContentItemsIsApply -> dispatch(
                    Message.ContentItemsIsApply(
                        intent.resultUuids,
                        intent.resultTitles,
                        intent.type
                    )
                )
            }
        }
    }

    private class ReducerImpl(
        private val filterHelper: CrmChatFilterHelper
    ) : Reducer<State, Message> {

        override fun State.reduce(msg: Message): State =
            when (msg) {
                is Message.UpdateSelectedType -> {
                    copy(
                        filterModel = filterModel.copy(
                            type = msg.type,
                            operatorIds = if (msg.type != CRMRadioButtonFilterType.DEFINED_OPERATORS) {
                                arrayListOf<UUID>() to arrayListOf()
                            } else {
                                filterModel.operatorIds
                            }
                        ),
                        filters = filterHelper.filtersWithNewSelectedType(msg.type, filterModel.operatorIds.second)
                    )
                }
                is Message.UpdateCheckableType -> when (msg.type) {
                    CRMCheckableFilterType.EXPIRED -> copy(
                        filterModel = filterModel.copy(isExpired = !filterModel.isExpired),
                        filters = filterHelper.filtersWithNewExpiredState(!filterModel.isExpired)
                    )
                }
                is Message.ResetFilter -> copy(
                    filterModel = filterHelper.defFilterModel(),
                    filters = filterHelper.defFilter()
                )
                is Message.ContentIsOpen -> copy(
                    contentIsOpen = true,
                    headerTitle = msg.type.textRes,
                    needShowApplyButton = true,
                    needShowResetButton = msg.needShowResetButton
                )
                is Message.ContentItemsIsApply -> {
                    val resultUuids = msg.resultUuids.asArrayList()
                    val newFilterModel = filterModel.copy(
                        operatorIds = if (msg.type == CRMOpenableFilterType.RESPONSIBLE) resultUuids to msg.resultTitles else filterModel.operatorIds,
                        clientIds = if (msg.type == CRMOpenableFilterType.CLIENT) resultUuids to msg.resultTitles else filterModel.clientIds,
                        sourceIds = if (msg.type == CRMOpenableFilterType.SOURCE) resultUuids to msg.resultTitles else filterModel.sourceIds,
                        channelIds = if (msg.type == CRMOpenableFilterType.CHANNEL) resultUuids to msg.resultTitles else filterModel.channelIds,
                        type = if (msg.type == CRMOpenableFilterType.RESPONSIBLE) CRMRadioButtonFilterType.DEFINED_OPERATORS else filterModel.type
                    )
                    copy(
                        filterModel = newFilterModel,
                        filters = filterHelper.filtersWithNewEntity(
                            newFilterModel.isExpired,
                            newFilterModel.type,
                            newFilterModel.operatorIds.second,
                            newFilterModel.clientIds.second,
                            newFilterModel.channelIds.second,
                            newFilterModel.sourceIds.second
                        ),
                        contentIsOpen = false,
                        headerTitle = defFilterHeaderTitle,
                        needShowApplyButton = true,
                    )
                }
                is Message.UpdateResetButtonState -> {
                    copy(needShowResetButton = msg.needShowResetButton)
                }
                is Message.Back -> {
                    copy(
                        contentIsOpen = false,
                        headerTitle = defFilterHeaderTitle,
                        needShowApplyButton = true
                    )
                }
            }
    }

    private sealed interface Action

    private sealed interface Message {

        data class UpdateSelectedType(val type: CRMRadioButtonFilterType) : Message
        data class UpdateCheckableType(val type: CRMCheckableFilterType) : Message
        data class ContentItemsIsApply(
            val resultUuids: ArrayList<UUID>,
            val resultTitles: ArrayList<String>,
            val type: CRMOpenableFilterType
        ) : Message

        data class UpdateResetButtonState(val needShowResetButton: Boolean) : Message

        /**
         * Фильтр сброшен к дефолтному
         */
        object ResetFilter : Message
        data class ContentIsOpen(val type: CRMOpenableFilterType, val needShowResetButton: Boolean) : Message
        object Back : Message
    }
}