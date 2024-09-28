package ru.tensor.sbis.mvp.presenter

import android.content.Context
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design_notification.SbisPopupNotification
import ru.tensor.sbis.design_notification.popup.SbisPopupNotificationStyle
import ru.tensor.sbis.common.R as RCommon

/**
 * Утилита для показа всплывающих сообщений "Панель-информер"
 * http://axure.tensor.ru/MobileStandart8/#p=%D0%BF%D0%B0%D0%BD%D0%B5%D0%BB%D1%8C-%D0%B8%D0%BD%D1%84%D0%BE%D1%80%D0%BC%D0%B5%D1%80_v2&g=1
 *
 * @author ev.grigoreva
 */
@Deprecated("Устаревший подход, переходим на mvi_extension")
object PopupNotification {

    @JvmStatic
    fun showError(uiContext: Context, message: String) {
        SbisPopupNotification.push(
            uiContext,
            SbisPopupNotificationStyle.ERROR,
            message,
            SbisMobileIcon.Icon.smi_alert.character.toString()
        )
    }

    @JvmStatic
    fun showError(uiContext: Context, @StringRes errorTextResId: Int) {
        showError(uiContext, uiContext.getString(errorTextResId))
    }

    @JvmStatic
    fun showNetworkError(uiContext: Context, message: String) {
        SbisPopupNotification.push(
            uiContext,
            SbisPopupNotificationStyle.ERROR,
            message,
            SbisMobileIcon.Icon.smi_WiFiNone.character.toString()
        )
    }

    @JvmStatic
    fun showNetworkError(uiContext: Context, @StringRes messageResId: Int) {
        showNetworkError(uiContext, uiContext.getString(messageResId))
    }

    @JvmStatic
    fun showNetworkError(uiContext: Context) {
        showNetworkError(uiContext, RCommon.string.common_no_network_available_check_connection)
    }

    @JvmStatic
    fun showInfoMessage(uiContext: Context, message: String, icon: String?) {
        SbisPopupNotification.push(
            uiContext,
            SbisPopupNotificationStyle.INFORMATION,
            message,
            icon
        )
    }
}

/**
 * Получить делегат для показа ошибок загрузки в [SbisPopupNotification]
 */
@Deprecated("Устаревший подход, переходим на mvi_extension")
fun Fragment.popupErrorDelegate(): DisplayErrorDelegate {
    return PopupNotificationErrorDelegate(this)
}

private class PopupNotificationErrorDelegate(
    private val fragment: Fragment
) : DisplayErrorDelegate {

    override fun showLoadingError(errorTextResId: Int) {
        showLoadingError(fragment.getString(errorTextResId))
    }

    override fun showLoadingError(errorText: String) {
        if (fragment.isResumed) {
            if (errorText.isNotEmpty()) {
                PopupNotification.showNetworkError(fragment.requireContext(), errorText)
            } else {
                PopupNotification.showNetworkError(fragment.requireContext())
            }
        }
    }
}
