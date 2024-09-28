package ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.channels.mapper

import androidx.lifecycle.MutableLiveData
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.communication_decl.crm.CrmChannelFilterType
import ru.tensor.sbis.communication_decl.crm.CrmChannelListCase
import ru.tensor.sbis.communicator.common.util.castTo
import ru.tensor.sbis.communicator.communicator_crm_chat_list.R
import ru.tensor.sbis.communicator.communicator_crm_chat_list.utils.HighlightsColorProvider
import ru.tensor.sbis.communicator.communicator_crm_chat_list.utils.applySearchSpan
import ru.tensor.sbis.communicator.communicator_crm_chat_list.utils.icon
import ru.tensor.sbis.communicator.communicator_crm_chat_list.utils.toChannelName
import ru.tensor.sbis.consultations.generated.ChannelHeirarchyItemType
import ru.tensor.sbis.consultations.generated.ChannelHierarchyViewModel
import ru.tensor.sbis.crud3.domain.ItemInSectionMapper
import ru.tensor.sbis.list.view.binding.BindingItem
import ru.tensor.sbis.list.view.binding.DataBindingViewHolderHelper
import ru.tensor.sbis.list.view.binding.LayoutIdViewFactory
import ru.tensor.sbis.list.view.item.AnyItem
import ru.tensor.sbis.list.view.item.Options
import ru.tensor.sbis.plugin_struct.utils.SbisThemedContext
import java.util.UUID

/**
 * Реализация ItemMapper для crud3.
 *
 * @author da.zhukov.
 */
internal class CRMChannelsMapper(
    private val context: SbisThemedContext,
    private val highlightsColorProvider: HighlightsColorProvider,
    private val onSuccessAction: (UUID, UUID?, String) -> Unit = { _: UUID, _: UUID?, _: String -> },
    private val onCheckedAction: (UUID, String) -> Unit = { _: UUID, _: String -> },
    private val case: CrmChannelListCase,
    private val selectedItems: MutableLiveData<List<UUID>>
) : ItemInSectionMapper<ChannelHierarchyViewModel, AnyItem> {

    override fun map(
        item: ChannelHierarchyViewModel,
        defaultClickAction: (ChannelHierarchyViewModel) -> Unit
    ): AnyItem {
        val (icon, iconColor) = item.icon.icon(context)
        val arrowIconIsVisible = if (case is CrmChannelListCase.CrmChannelFilterCase && case.type == CrmChannelFilterType.OPERATOR) {
            item.isGroup && item.itemType == ChannelHeirarchyItemType.CHANNEL_FOLDER_GROUP
        } else {
            item.isGroup
        }

        val data = CRMChannelsViewModelBinding(
            id = item.id,
            name = item.getCorrectChannelName(),
            parentId = item.parentId,
            rootName = item.rootName ?: StringUtils.EMPTY,
            itemType = item.itemType,
            isGroup = item.isGroup,
            arrowIconIsVisible = arrowIconIsVisible,
            path = item.path,
            icon = icon,
            iconColor = iconColor,
            groupType = item.groupType,
            onSuccessAction = onSuccessAction,
            onCheckedAction = onCheckedAction,
            case = case,
            selectedItems = selectedItems,
            isParentVisible = item.rootName != null
        )
        return BindingItem(
            data = data,
            dataBindingViewHolderHelper = DataBindingViewHolderHelper(
                factory = LayoutIdViewFactory(R.layout.communicator_crm_channel_item)
            ),
            options = Options(
                clickAction = {
                    defaultClickAction(item)
                    if (case is CrmChannelListCase.CrmChannelFilterCase && !item.isGroup) {
                        data.onCheckedClick()
                    }
                },
                customBackground = true,
                customSidePadding = true
            )
        )
    }

    private fun ChannelHierarchyViewModel.getCorrectChannelName(): CharSequence {
        // Для CHANNEL_GROUP_TYPE приходит только тип подключения, и нам необходимо самим получить название канала.
        val channelName =  if (this.itemType == ChannelHeirarchyItemType.CHANNEL_GROUP_TYPE) {
            this.groupType.toChannelName(context)
        } else {
            this.name
        }
        return this.searchResult?.let {
            channelName.applySearchSpan(it.start, it.end, highlightsColorProvider)
        } ?: channelName
    }
}