package ru.tensor.sbis.communicator.communicator_crm_chat_list.ui

import androidx.annotation.StringRes
import com.arkivanov.mvikotlin.core.view.MviView
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.communication_decl.crm.CRMConsultationParams
import ru.tensor.sbis.communicator.communicator_crm_chat_list.utils.swipe_menu.MenuItem
import ru.tensor.sbis.consultations.generated.ConsultationGroupType
import ru.tensor.sbis.design_notification.popup.SbisPopupNotificationStyle

/**
 * @author da.zhukov
 */
internal interface CRMChatListView : MviView<CRMChatListView.Model, CRMChatListView.Event> {

    /**@SelfDocumented*/
    sealed interface Event {
        /**@SelfDocumented*/
        data class EnterSearchQuery(val query: String?) : Event

        /**@SelfDocumented*/
        data class FolderChanged(
            val groupType: ConsultationGroupType,
            val folderTitle: String
        ) : Event

        /**@SelfDocumented*/
        data class SwipeMenuItemClicked(val menuItem: MenuItem) : Event

        /**@SelfDocumented*/
        data class OpenConsultation(val consultationParams: CRMConsultationParams) : Event

        /**@SelfDocumented*/
        data class ShowInformer(
            @StringRes val msg: Int,
            val style: SbisPopupNotificationStyle,
            val icon: String?
        ) : Event

        /**@SelfDocumented*/
        object OpenSearchPanel : Event

        /**@SelfDocumented*/
        object ClickFilterIcon : Event

        /**@SelfDocumented*/
        object CheckShowTakeOldestFab : Event

        /**@SelfDocumented*/
        object TakeOldestConsultation : Event

        /**@SelfDocumented*/
        data class TakeOldestFabVisibilityChanged(val isVisible: Boolean) : Event
    }

    /**
     * Модель для отображения.
     */
    data class Model(
        val query: String? = null,
        val groupType: ConsultationGroupType = ConsultationGroupType.UNKNOWN,
        val currentFolderViewIsVisible: Boolean = false,
        val folderTitle: String = StringUtils.EMPTY,
        val searchPanelIsOpen: Boolean = false,
        val filters: List<String>,
        val fabVisible: Boolean = false,
    )
}
