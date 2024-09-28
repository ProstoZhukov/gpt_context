package ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.channels.ui

import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.asFlow
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.arkivanov.mvikotlin.core.utils.diff
import com.arkivanov.mvikotlin.core.view.BaseMviView
import com.arkivanov.mvikotlin.core.view.ViewRenderer
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.asFlow
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.communication_decl.crm.CrmChannelFilterType
import ru.tensor.sbis.communication_decl.crm.CrmChannelListCase
import ru.tensor.sbis.communicator.communicator_crm_chat_list.databinding.CommunicatorCrmChannelsFragmentBinding
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.channels.ui.CRMChannelsView.*
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.channels.ui.helper.CRMChannelsItemClickHelper
import ru.tensor.sbis.communicator.communicator_crm_chat_list.utils.launchAndCollect
import ru.tensor.sbis.communicator.communicator_crm_chat_list.utils.toChannelName
import ru.tensor.sbis.consultations.generated.ChannelGroupType
import ru.tensor.sbis.consultations.generated.ChannelHeirarchyItemType
import ru.tensor.sbis.design.topNavigation.api.SbisTopNavigationContent
import ru.tensor.sbis.design.view.input.searchinput.SearchColorType
import java.util.UUID

/**
 * @author da.zhukov
 */
internal class CRMChannelsViewImpl(
    private val binding: CommunicatorCrmChannelsFragmentBinding,
    listComponentFactory: CRMChannelsListComponentFactory,
    private val crmReassignItemClickHelper: CRMChannelsItemClickHelper,
    case: CrmChannelListCase,
    private val firstItemHolderHelper: FirstItemHolderHelper
) : BaseMviView<Model, Event>(), CRMChannelsView {

    private val isFilterRegistryCase =
        case is CrmChannelListCase.CrmChannelFilterCase && case.type == CrmChannelFilterType.REGISTRY

    override val renderer: ViewRenderer<Model> = diff {
        diff(
            get = Model::query,
            set = {
                if (isFilterRegistryCase) {
                    binding.crmReassignSearchFilterPanel.setSearchText(it ?: StringUtils.EMPTY)
                } else {
                    binding.crmReassignToolbar.searchInput?.setSearchText(it ?: StringUtils.EMPTY)
                }
            }
        )
        diff(
            get = Model::currentFolderViewIsVisible,
            set = {
                binding.crmReassignCurrentFolderView.isVisible = it
                firstItemHolderHelper.run {
                    if (it) hideFirstItem() else showFirstItem()
                }
            }
        )
        diff(
            get = Model::folderTitle,
            set = { binding.crmReassignCurrentFolderView.setTitle(it) }
        )
    }

    private val queryChangeFlow
        get() = if (isFilterRegistryCase) {
            binding.crmReassignSearchFilterPanel.searchQueryChangedObservable().asFlow()
        } else {
            binding.crmReassignToolbar.searchInput?.searchQueryChangedObservable()?.asFlow()
        }

    private val cancelSearchFlow
        get() = if (isFilterRegistryCase) {
            binding.crmReassignSearchFilterPanel.cancelSearchObservable().asFlow()
        } else {
            binding.crmReassignToolbar.searchInput?.cancelSearchObservable()?.asFlow()
        }

    private val searchActionsFlow
        get() = if (isFilterRegistryCase) {
            binding.crmReassignSearchFilterPanel.searchFieldEditorActionsObservable().asFlow()
        } else {
            binding.crmReassignToolbar.searchInput?.searchFieldEditorActionsObservable()?.asFlow()
        }

    private val folderCrumbs = mutableListOf<Triple<UUID?, String, ChannelGroupType>>()

    init {
        val lifecycleOwner: LifecycleOwner? = binding.root.findViewTreeLifecycleOwner()
        val scope = lifecycleOwner?.lifecycleScope

        with(binding) {
            if (isFilterRegistryCase){
                crmReassignSearchFilterPanel.isVisible = true
            } else {
                crmReassignToolbar.apply {
                    content = SbisTopNavigationContent.SearchInput
                    showBackButton = true
                    searchInput?.apply {
                        setHasFilter(false)
                        setSearchColor(SearchColorType.ADDITIONAL)
                    }
                    backBtn?.setOnClickListener {
                        scope?.launch {
                            dispatch(Event.BackButtonClick)
                        }
                    }
                    isVisible = true
                }
            }
            crmReassignCurrentFolderView.setOnClickListener {
                scope?.launch {
                    if (folderCrumbs.size == 1) {
                        dispatch(
                            Event.CurrentFolderViewClick(
                                parentId = null,
                                parentName = StringUtils.EMPTY,
                                groupType = null,
                                needShowFolder = false
                            )
                        )
                        folderCrumbs.clear()
                    } else {
                        val parentFolderIndex = folderCrumbs.lastIndex - 1
                        val parentFolder = folderCrumbs[parentFolderIndex]
                        dispatch(
                            Event.CurrentFolderViewClick(
                                parentId = parentFolder.first,
                                parentName = parentFolder.second,
                                groupType = parentFolder.third,
                                needShowFolder = true
                            )
                        )
                        folderCrumbs.removeAt(parentFolderIndex)
                    }
                }
            }
        }

        scope?.launchWhenStarted {
            queryChangeFlow?.let {
                launchAndCollect(it) { query ->
                    dispatch(Event.EnterSearchQuery(query))
                }
            }
            cancelSearchFlow?.let {
                launchAndCollect(it) {
                    dispatch(Event.EnterSearchQuery(null))
                }
            }
            searchActionsFlow?.let { flow ->
                launchAndCollect(flow) {
                    if (it == EditorInfo.IME_ACTION_NEXT || it == EditorInfo.IME_ACTION_SEARCH) {
                        binding.crmReassignToolbar.searchInput?.hideKeyboard()
                    }
                }
            }
            launchAndCollect(crmReassignItemClickHelper.onItemSuccessIconFlow) {
                dispatch(
                    Event.OnItemSuccessClick(
                        id = it.first,
                        parentId = it.second,
                        channelName = it.third
                    )
                )
            }
            launchAndCollect(crmReassignItemClickHelper.onItemCheckedFlow) {
                dispatch(Event.OnItemCheckClick(it))
            }
            listComponentFactory.create(binding.crmReassignList).apply {
                launchAndCollect(onItemClick.asFlow()) {
                    val channelId = if (it.itemType == ChannelHeirarchyItemType.OPEN_LINE) it.parentId else it.id
                    val operatorGroupId = if (it.itemType == ChannelHeirarchyItemType.OPEN_LINE) it.id else null
                    val id = it.id
                    val channelName = if (it.itemType == ChannelHeirarchyItemType.CHANNEL_GROUP_TYPE) {
                        it.groupType.toChannelName(binding.root.context)
                    } else {
                        it.name
                    }

                    if (it.isGroup) {
                        folderCrumbs.add(Triple(it.id, channelName, it.groupType))
                    }
                    val needOpenFolder = if (case is CrmChannelListCase.CrmChannelFilterCase && case.type == CrmChannelFilterType.OPERATOR) {
                        it.isGroup && it.itemType == ChannelHeirarchyItemType.CHANNEL_FOLDER_GROUP
                    } else {
                        it.isGroup
                    }

                    dispatch(
                        Event.OnItemClick(
                            id = id,
                            channelId = channelId,
                            operatorGroupId = operatorGroupId,
                            name = channelName,
                            needOpenFolder = needOpenFolder,
                            itemType = it.itemType,
                            groupType = it.groupType
                        )
                    )
                    val needShowCurrentFolder = it.itemType == ChannelHeirarchyItemType.CHANNEL_FOLDER
                            || it.itemType == ChannelHeirarchyItemType.CHANNEL_FOLDER_GROUP
                            || it.itemType == ChannelHeirarchyItemType.CHANNEL_GROUP_TYPE

                    if (it.isGroup || it.itemType == ChannelHeirarchyItemType.CHANNEL_GROUP_TYPE) dispatch(Event.UpdateCurrentFolderView(channelName, needShowCurrentFolder))
                }
            }
        }
    }
}