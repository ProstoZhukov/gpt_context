package ru.tensor.sbis.communicator.communicator_crm_chat_list.filter

import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.arkivanov.mvikotlin.core.binder.BinderLifecycleMode
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import ru.tensor.sbis.clients_feature.ClientsMultiSelectionContract
import ru.tensor.sbis.common.util.asArrayList
import ru.tensor.sbis.communication_decl.selection.recipient.data.RecipientDepartmentId
import ru.tensor.sbis.communicator.common.util.castTo
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.CrmChatFilterView.Model
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.di.DEFAULT_FILTER
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.di.INITIAL_FILTER
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.models.CheckableFilterItem
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.models.OpenableFilterItem
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.models.SelectableFilterItem
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.store.CrmChatFilterStore
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.store.CrmChatFilterStore.Intent
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.store.CrmChatFilterStore.State
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.store.CrmChatFilterStoreFactory
import ru.tensor.sbis.communicator.communicator_crm_chat_list.ui.CRMChatListFragment
import ru.tensor.sbis.communicator.communicator_crm_chat_list.utils.launchAndCollect
import ru.tensor.sbis.communicator.declaration.crm.model.CRMChatFilterModel
import ru.tensor.sbis.communicator.declaration.crm.model.CRMCheckableFilterType
import ru.tensor.sbis.communicator.declaration.crm.model.CRMOpenableFilterType
import ru.tensor.sbis.communicator.declaration.crm.model.CRMRadioButtonFilterType
import ru.tensor.sbis.consultations.generated.ConsultationService
import ru.tensor.sbis.design_selection.contract.customization.selected.panel.SelectedData
import ru.tensor.sbis.design_selection.contract.data.SelectionItem
import ru.tensor.sbis.design_selection.contract.data.SelectionPersonItem
import ru.tensor.sbis.mvi_extension.attachRxJavaBinder
import ru.tensor.sbis.mvi_extension.provideStore
import ru.tensor.sbis.mvi_extension.router.navigator.WeakLifecycleNavigator
import ru.tensor.sbis.mvi_extension.rx.observableEvents
import ru.tensor.sbis.mvi_extension.rx.observableLabels
import ru.tensor.sbis.mvi_extension.rx.observableStates
import ru.tensor.sbis.profiles.generated.EmployeeProfileController
import java.util.UUID
import javax.inject.Named

/**
 * Контроллер, обеспечивающий связку компонентов Android с компонентами MVI.
 *
 * @author da.zhukov
 */
internal class CrmChatFilterController @AssistedInject constructor(
    @Assisted private val fragment: Fragment,
    viewFactory: (View) -> CrmChatFilterView,
    private val storeFactory: CrmChatFilterStoreFactory,
    @Named(INITIAL_FILTER) private val initFilter: CRMChatFilterModel,
    @Named(DEFAULT_FILTER)private val defFilter: CRMChatFilterModel
) {

    private val store = fragment.provideStore {
        storeFactory.create()
    }

    private var currentFilter: CRMChatFilterModel = initFilter

    private val router = CrmChatFilterRouter(
        crmChatFilterController = this
    )

    private val consultationService by lazy { ConsultationService.instance() }
    private val isInQueueAndMine by lazy { consultationService.getIsInQueueAndMine() }
    private val isCurrentUserLinkedChannels by lazy { consultationService.getIsCurrentUserLinkedChannels() }

    private val selectedItems = mutableSetOf<Pair<UUID, String>>()
    private var lastSelectedContentItemType: CRMOpenableFilterType? = null

    val scope = fragment.lifecycleScope

    init {
        router.attachNavigator(WeakLifecycleNavigator(fragment))
        fragment.attachRxJavaBinder(BinderLifecycleMode.CREATE_DESTROY, viewFactory) { view ->
            view.observableEvents() bind {
                when (it) {
                    CrmChatFilterView.Event.ApplyClick -> store.accept(Intent.Apply)
                    CrmChatFilterView.Event.ResetClick -> {
                        store.accept(Intent.Reset)
                    }
                    CrmChatFilterView.Event.BackClick -> store.accept(Intent.Back)
                    is CrmChatFilterView.Event.ContentItemIsSelected -> {
                        store.accept(Intent.ContentItemIsSelected(it.resultUuids, it.resultTitles))
                    }
                }
            }
            store.observableLabels() bind {
                when (it) {
                    is CrmChatFilterStore.Label.Apply -> {
                        scope.launch {
                            if (lastSelectedContentItemType != null) {
                                contentItemIsApply()
                                lastSelectedContentItemType = null
                                router.back()
                                selectedItems.clear()
                            } else {
                                if (currentFilter != it.filterModel) {
                                    currentFilter = it.filterModel
                                } else if (initFilter == it.filterModel) {
                                    router.back()
                                    return@launch
                                }
                                fragment.parentFragment?.parentFragmentManager?.setFragmentResult(
                                    CRMChatListFragment.REQUEST,
                                    bundleOf(
                                        CRMChatListFragment.RESULT_FILTER_MODEL to it.filterModel,
                                        CRMChatListFragment.RESULT_FILTER_NAMES to it.filters.asArrayList()
                                    )
                                )
                                router.back()
                            }
                        }
                    }
                    is CrmChatFilterStore.Label.Back -> {
                        scope.launch {
                            lastSelectedContentItemType = null
                            selectedItems.clear()
                            router.back()
                        }
                    }
                    is CrmChatFilterStore.Label.Open -> {
                        addInitSelectedItems(it.type)
                        lastSelectedContentItemType = it.type
                        router.onOpen(it.type)
                    }
                    is CrmChatFilterStore.Label.ContentItemIsSelected -> {
                        // необходимо почистить список, т.к. в этих сценариях всегда приходят только те эелементы которые необходимо добавить
                        // в других сценариях нам приходят эелементы и на удаление и на добавление.
                        if (lastSelectedContentItemType == CRMOpenableFilterType.CLIENT || lastSelectedContentItemType == CRMOpenableFilterType.RESPONSIBLE) {
                            selectedItems.clear()
                        }
                        for (i in 0 until it.resultUuids.size) {
                            updateSelectedItem(it.resultUuids[i] to it.resultTitles[i])
                        }
                    }
                    is CrmChatFilterStore.Label.ResetFilter -> {
                        selectedItems.clear()
                        fun contentFragments(): List<Fragment> = fragment.childFragmentManager.fragments
                        when (lastSelectedContentItemType) {
                            CRMOpenableFilterType.CHANNEL,
                            CRMOpenableFilterType.SOURCE -> {
                                contentFragments().findLast { content ->
                                    content is CrmChatFilterContentContract
                                }?.castTo<CrmChatFilterContentContract>()?.onResetButtonClick()
                            }
                            CRMOpenableFilterType.CLIENT -> {
                                contentFragments().findLast { content ->
                                    content is ClientsMultiSelectionContract.ActionHandler
                                }?.castTo<ClientsMultiSelectionContract.ActionHandler>()?.onReset()
                            }
                            else -> Unit
                        }
                    }
                }
            }
            store.observableStates().map { it.toModel() } bindTo view
        }
    }

    private fun State.toModel(): Model {
        return Model(
            contentItems = mutableListOf<Any>().apply {
                add(
                    CheckableFilterItem(
                        type = CRMCheckableFilterType.EXPIRED,
                        titleRes = CRMCheckableFilterType.EXPIRED.textRes,
                        isSelected = filterModel.isExpired,
                        clickAction = {
                            (it as? CRMCheckableFilterType)?.let { type ->
                                store.accept(Intent.CheckedFilterItem(type))
                            }
                        }
                    )
                )
                add(
                    createSelectableFilterItem(
                        CRMRadioButtonFilterType.ALL,
                        filterModel
                    )
                )
                // если по дефолту стоит фильтр мои - то пользователь точно оператор.
                if (defFilter.type == CRMRadioButtonFilterType.MY) {
                    add(
                        createSelectableFilterItem(
                            CRMRadioButtonFilterType.MY,
                            filterModel
                        )
                    )
                }
                if (isCurrentUserLinkedChannels && !isInQueueAndMine) {
                    add(
                        createSelectableFilterItem(
                            CRMRadioButtonFilterType.FROM_MY_CHANNELS,
                            filterModel
                        )
                    )
                }
                if (!isInQueueAndMine) {
                    add(
                        createOpenableFilterItem(
                            CRMOpenableFilterType.RESPONSIBLE,
                            filterModel.operatorIds.second.toSet().joinToString()
                        )
                    )
                }
                add(
                    createOpenableFilterItem(
                        CRMOpenableFilterType.CLIENT,
                        filterModel.clientIds.second.joinToString()
                    )
                )
                add(
                    createOpenableFilterItem(
                    CRMOpenableFilterType.CHANNEL,
                        filterModel.channelIds.second.joinToString()
                    )
                )
                add(
                    createOpenableFilterItem(
                    CRMOpenableFilterType.SOURCE,
                        filterModel.sourceIds.second.joinToString()
                    )
                )
            },
            isChanged = if (contentIsOpen) needShowResetButton else filterModel.isChanged(),
            contentIsOpen = contentIsOpen,
            headerTitle = headerTitle,
        )
    }

    private fun contentItemIsApply() {
            val resultFilters = selectedItems.mapTo(ArrayList()) { selectedItems -> selectedItems.second }
            val resultUuids = selectedItems.mapTo(ArrayList()) { selectedItems -> selectedItems.first }
            updateCurrentFilter(lastSelectedContentItemType!!, resultUuids, resultFilters)

            store.accept(
                Intent.ContentItemsIsApply(
                    resultUuids,
                    resultFilters,
                    lastSelectedContentItemType!!
                )
            )
    }

    private fun updateCurrentFilter(
        type: CRMOpenableFilterType,
        resultUuids: ArrayList<UUID>,
        resultFilterNames: ArrayList<String>
    ) {
        currentFilter = when (type) {
            CRMOpenableFilterType.RESPONSIBLE -> {
                currentFilter.copy(operatorIds = resultUuids to resultFilterNames)
            }
            CRMOpenableFilterType.CLIENT -> {
                currentFilter.copy(clientIds = resultUuids to resultFilterNames)
            }
            CRMOpenableFilterType.CHANNEL -> {
                currentFilter.copy(channelIds = resultUuids to resultFilterNames)
            }
            CRMOpenableFilterType.SOURCE -> {
                currentFilter.copy(sourceIds = resultUuids to resultFilterNames)
            }
        }
    }

    private fun onSelectableFilterItemClick(type: CRMRadioButtonFilterType) {
        store.accept(Intent.SelectedFilterItem(type))
    }

    private fun onOpenableFilterItemClick(type: CRMOpenableFilterType) {
        store.accept(Intent.OpenedFilterItem(type, needShowResetButton(type, currentFilter)))
    }

    private fun updateSelectedItem(newItem: Pair<UUID, String>) {
        selectedItems.apply {
            if (contains(newItem)) {
                remove(newItem)
            } else {
                add(newItem)
            }
        }
    }

    private fun createSelectableFilterItem(type: CRMRadioButtonFilterType, filterModel: CRMChatFilterModel): SelectableFilterItem =
        SelectableFilterItem(
            type = type,
            titleRes = type.textRes,
            isSelected = type == filterModel.type,
            clickAction = {
                (it as? CRMRadioButtonFilterType)?.let { type ->
                    onSelectableFilterItemClick(type)
                }
            }
        )

    private fun createOpenableFilterItem(type: CRMOpenableFilterType, value: String): OpenableFilterItem {
        return OpenableFilterItem(
            type = type,
            titleRes = type.textRes,
            clickAction = {
                (it as? CRMOpenableFilterType)?.let { type ->
                    onOpenableFilterItemClick(type)
                }
            },
            value = value
        )
    }

    private fun addInitSelectedItems(type: CRMOpenableFilterType) {
        fun addInitItems(items: Pair<ArrayList<UUID>, ArrayList<String>>) {
            val (uuids, names) = items

            if (uuids.size != names.size) return
            selectedItems.addAll(uuids.indices.map { index -> Pair(uuids[index], names[index]) })
        }

        when (type) {
            CRMOpenableFilterType.RESPONSIBLE -> {
                addInitItems(currentFilter.operatorIds)
            }
            CRMOpenableFilterType.CLIENT -> {
                addInitItems(currentFilter.clientIds)
            }
            CRMOpenableFilterType.CHANNEL -> {
                addInitItems(currentFilter.channelIds)
            }
            CRMOpenableFilterType.SOURCE -> {
                addInitItems(currentFilter.sourceIds)
            }
        }
    }

    private fun needShowResetButton(type: CRMOpenableFilterType, filterModel: CRMChatFilterModel): Boolean =
        when {
            type == CRMOpenableFilterType.CLIENT && filterModel.clientIds.first.isNotEmpty() -> true
            type == CRMOpenableFilterType.CHANNEL && filterModel.channelIds.first.isNotEmpty() -> true
            type == CRMOpenableFilterType.SOURCE && filterModel.sourceIds.first.isNotEmpty() -> true
            else -> false
        }

    /**
     * Проверка на то, что фильтр отличается от дефолтного
     */
    private fun CRMChatFilterModel.isChanged() =
        this != defFilter

    fun onBackPressed() {
        scope.launch {
            store.accept(Intent.Back)
        }
    }

    fun subscribeToSelectionResult(selectionFlow: Flow<SelectedData<SelectionItem>>) {
        val scope = fragment.lifecycleScope
        scope.launchAndCollect(selectionFlow) { selectedData ->
            if (selectedData.hasSelectedItems) {
                selectedItems.clear()
                selectedData.items.forEach { selectionItem ->
                    val selectedItemUuid = selectionItem.castTo<SelectionPersonItem>()?.photoData?.uuid
                        ?:  selectionItem.castTo<SelectionItem>()?.id?.castTo<RecipientDepartmentId>()?.uuid
                    if (selectionItem is SelectionPersonItem) {
                        selectedItemUuid?.let { updateSelectedItem(selectedItemUuid to selectionItem.title)}
                    } else {
                        scope.launch {
                            try {
                                EmployeeProfileController.instance().getEmployeeProfilesByGroupId(selectedItemUuid!!).forEach {
                                    updateSelectedItem(it.person.uuid to selectionItem.title)
                                }
                            } catch  (ex: Exception){}
                        }
                    }
                }
            } else {
                selectedItems.clear()
            }
        }
    }

    fun getCurrentFilter() = currentFilter
}