package ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.channels.ui

import com.arkivanov.mvikotlin.core.view.MviView
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.consultations.generated.ChannelGroupType
import ru.tensor.sbis.consultations.generated.ChannelHeirarchyItemType
import java.util.UUID

/**
 * @author da.zhukov
 */
internal interface CRMChannelsView : MviView<CRMChannelsView.Model, CRMChannelsView.Event> {

    /** @SelfDocumented */
    sealed interface Event {
        data class EnterSearchQuery(val query: String?) : Event
        data class OnItemSuccessClick(val id: UUID, val parentId: UUID?, val channelName: String) : Event
        data class OnItemCheckClick(val result: Pair<UUID, String>) : Event
        data class OnItemClick(
            val id: UUID,
            val channelId: UUID?,
            val operatorGroupId: UUID?,
            val name: String,
            val needOpenFolder: Boolean,
            val itemType: ChannelHeirarchyItemType,
            val groupType: ChannelGroupType
        ) : Event
        data class UpdateCurrentFolderView(val folderTitle: String, val currentFolderViewIsVisible: Boolean) : Event
        object BackButtonClick : Event
        data class CurrentFolderViewClick(
            val parentId: UUID?,
            val parentName: String,
            val groupType: ChannelGroupType?,
            val needShowFolder: Boolean) : Event
    }

    /** @SelfDocumented */
    data class Model(
        val query: String? = null,
        val currentFolderViewIsVisible: Boolean = false,
        val folderTitle: String = StringUtils.EMPTY
    )
}