package ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.channels.store

import android.os.Parcelable
import com.arkivanov.mvikotlin.core.store.Store
import kotlinx.parcelize.Parcelize
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.communication_decl.crm.CrmChannelType
import ru.tensor.sbis.consultations.generated.ChannelGroupType
import ru.tensor.sbis.consultations.generated.ChannelHeirarchyItemType
import java.util.UUID

/**
 * Стор переназначения оператору.
 *
 * @author da.zhukov
 */
interface CRMChannelsStore :
    Store<CRMChannelsStore.Intent, CRMChannelsStore.State, CRMChannelsStore.Label> {

    /**
     * Намерения переназначения чата CRM.
     */
    sealed interface Intent {
        data class InitialLoading(
            val query: String? = null,
            val folderTitle: String = StringUtils.EMPTY,
            val currentFolderViewIsVisible: Boolean = false
        ) : Intent
        data class SearchQuery(val query: String?) : Intent
        data class OnItemSuccessClick(val id: UUID, val parentId: UUID?, val channelName: String) : Intent
        data class OnItemCheckClick(val result: Pair<UUID, String>) : Intent
        data class OnItemClick(
            val id: UUID,
            val channelId: UUID?,
            val operatorGroupId: UUID?,
            val name: String,
            val needOpenFolder: Boolean,
            val itemType: ChannelHeirarchyItemType,
            val groupType: ChannelGroupType
        ) :
            Intent
        data class UpdateCurrentFolderView(val folderTitle: String, val currentFolderViewIsVisible: Boolean) : Intent
        object BackButtonClick : Intent
        data class CurrentFolderViewClick(
            val parentId: UUID?,
            val parentName: String,
            val groupType: ChannelGroupType?,
            val needShowFolder: Boolean
        ) : Intent
    }

    /**
     * События переназначения чата CRM.
     */
    sealed interface Label {
        object BackButtonClick : Label
        data class OnItemCheckClick(val result: Pair<UUID, String>) : Label
        data class OnChannelConsultationItemClick(val result: Triple<UUID, UUID, CrmChannelType>) : Label
        data class OnChannelOperatorItemClick(val channelId: UUID?, val channelName: String) : Label
    }
    /**
     * Состояние стора переназначения чата CRM.
     */
    @Parcelize
    data class State(
        val query: String? = null,
        val currentFolderViewIsVisible: Boolean = false,
        val folderTitle: String = StringUtils.EMPTY
     ) : Parcelable
}