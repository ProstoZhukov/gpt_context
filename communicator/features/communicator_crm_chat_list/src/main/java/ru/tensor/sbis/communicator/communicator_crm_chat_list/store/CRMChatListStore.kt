package ru.tensor.sbis.communicator.communicator_crm_chat_list.store

import android.os.Parcelable
import androidx.annotation.StringRes
import com.arkivanov.mvikotlin.core.store.Store
import kotlinx.parcelize.Parcelize
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.communication_decl.crm.CRMConsultationParams
import ru.tensor.sbis.communicator.communicator_crm_chat_list.utils.swipe_menu.MenuItem
import ru.tensor.sbis.communicator.declaration.crm.model.CRMChatFilterModel
import ru.tensor.sbis.consultations.generated.ConsultationGroupType
import ru.tensor.sbis.design_notification.popup.SbisPopupNotificationStyle

/**
 * Стор чатов CRM.
 *
 * @author da.zhukov
 */
internal interface CRMChatListStore :
    Store<CRMChatListStore.Intent, CRMChatListStore.State, CRMChatListStore.Label> {

    /**
     * Намерения стора чатов CRM.
     */
    sealed interface Intent {
        data class InitialLoading(
            val query: String? = null,
            val groupType: ConsultationGroupType = ConsultationGroupType.UNKNOWN
        ) : Intent
        data class SearchQuery(val query: String?) : Intent
        data class ChangeCurrentFolder(val groupType: ConsultationGroupType, val folderTitle: String) : Intent
        data class HandleSwipeMenuItemClick(val menuItem: MenuItem) : Intent
        data class OpenConsultation(val consultationParams: CRMConsultationParams) : Intent
        data class CreateConsultation(val consultationParams: CRMConsultationParams) : Intent
        data class ApplyFilter(val filterModel: CRMChatFilterModel, val filters: List<String>) : Intent
        object OpenSearchPanel : Intent
        data class ShowInformer(
            @StringRes val msg: Int,
            val style: SbisPopupNotificationStyle,
            val icon: String?
        ) : Intent
        data class OpenFilters(val filterModel: CRMChatFilterModel) : Intent
        data class CheckShowTakeOldestFab(val isTablet: Boolean) : Intent
        data class TakeOldestConsultation(val needBackButton: Boolean) : Intent
        data class TakeOldestFabVisibilityChanged(val isVisible: Boolean) : Intent
    }

    /**
     * События стора чатов CRM.
     */
    sealed interface Label {
        /** Показать информер в стиле [style] с сообщением [msg] */
        data class ShowInformer(
            @StringRes val msg: Int,
            val style: SbisPopupNotificationStyle,
            val icon: String?
        ) : Label
        data class OpenConsultation(val consultationParams: CRMConsultationParams) : Label
        data class CreateConsultation(val consultationParams: CRMConsultationParams) : Label
        data class OpenFilters(val filterModel: CRMChatFilterModel) : Label
        data class ChangeTakeOldestFabVisibility(val isVisible: Boolean) : Label
    }

    /**
     * Состояние стора чатов CRM.
     */
    @Parcelize
    data class State(
        val query: String? = null,
        val groupType: ConsultationGroupType = ConsultationGroupType.UNKNOWN,
        val folderTitle: String = StringUtils.EMPTY,
        val searchPanelIsOpen: Boolean = false,
        val filterModel: CRMChatFilterModel,
        val filters: List<String>,
        val fabVisible: Boolean = false,
    ) : Parcelable
}
