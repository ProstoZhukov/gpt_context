package ru.tensor.sbis.communicator.communicator_crm_chat_list.ui

import android.content.Context
import androidx.annotation.StringRes
import ru.tensor.sbis.communicator.design.R
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design_notification.SbisPopupNotification
import ru.tensor.sbis.design_notification.popup.SbisPopupNotificationStyle
import ru.tensor.sbis.design_notification.popup.state_machine.util.DisplayDuration

/**
 * Вспомогательный класс для работы с уведомлениями на экране чатов CRM.
 *
 * @author da.zhukov
 */
internal class CRMChatListNotificationHelper(private val context: Context) {

    fun showSbisPopupNotification(
        type: SbisPopupNotificationStyle,
        @StringRes message: Int,
        icon: String? = null,
        duration: DisplayDuration = DisplayDuration.Default
    ) {
        SbisPopupNotification.push(type, context.getString(message), icon, duration)
    }

    fun showNetworkError() {
        showSbisPopupNotification(
            SbisPopupNotificationStyle.ERROR,
            R.string.communicator_sync_error_message,
            SbisMobileIcon.Icon.smi_WiFiNone.character.toString()
        )
    }
}