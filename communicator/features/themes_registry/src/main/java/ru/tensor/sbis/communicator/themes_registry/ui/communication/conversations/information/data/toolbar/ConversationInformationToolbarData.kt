package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.data.toolbar

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.apache.commons.lang3.StringUtils.EMPTY
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.data.toolbar.ConversationInformationToolbarState.DEFAULT
import ru.tensor.sbis.design.profile_decl.person.PhotoData

/**
 * Данные для отображения тулбара на экране информации диалога/канала.
 *
 * @author dv.baranov
 */
@Parcelize
internal data class ConversationInformationToolbarData(
    val title: CharSequence = EMPTY,
    val subtitle: CharSequence = EMPTY,
    val photoDataList: List<PhotoData> = emptyList(),
    val toolbarState: ConversationInformationToolbarState = DEFAULT,
    val isGroup: Boolean = false,
    val isChat: Boolean = false
) : Parcelable

/**
 * Состояние тулбара на экране информации диалога/канала.
 */
enum class ConversationInformationToolbarState {
    EDITING,
    SEARCHING,
    DEFAULT
}
