package ru.tensor.sbis.viper.informer

import android.content.Context
import androidx.annotation.StringRes
import ru.tensor.sbis.design_notification.SbisPopupNotification
import ru.tensor.sbis.design_notification.popup.SbisInfoNotificationFactory
import ru.tensor.sbis.design_notification.popup.SbisPopupNotificationStyle
import ru.tensor.sbis.design_notification.popup.SbisPopupNotificationStyle.ERROR
import ru.tensor.sbis.design_notification.popup.SbisPopupNotificationStyle.INFORMATION
import ru.tensor.sbis.design_notification.popup.SbisPopupNotificationStyle.SUCCESS
import ru.tensor.sbis.viper.R
import ru.tensor.sbis.design.R as RDesign

internal class GlobalInformerInterfaceImpl(private val context: Context) : GlobalInformerInterface {

    override fun showGlobalInformer(data: InformerData) {
        val msg = data.message.takeIf { it.isNotBlank() } ?: context.resources.getString(data.messageResId)
        when (data.type) {
            InformerType.FAILURE ->
                showPanelInformer(context = context, ERROR, message = msg, icon = data.iconResId)

            InformerType.SUCCESS ->
                showPanelInformer(context = context, SUCCESS, message = msg, icon = data.iconResId)

            InformerType.ACCENT ->
                showPanelInformer(
                    context = context,
                    INFORMATION,
                    message = msg,
                    icon = data.iconResId ?: RDesign.string.design_mobile_icon_info
                )
        }
    }

    override fun showNetworkErrorInformer() {
        val message = context.resources.getString(R.string.viper_network_error)
        showGlobalInformer(
            InformerData(
                InformerType.FAILURE,
                message,
                iconResId = RDesign.string.design_mobile_icon_wifi_none
            )
        )
    }

    override fun showServiceUnavailableErrorInformer() {
        showGlobalInformer(
            InformerData(
                InformerType.ACCENT,
                context.resources.getString(R.string.viper_unavailable_service_error)
            )
        )
    }

    //Метод для отображения панели информера
    private fun showPanelInformer(
        context: Context,
        type: SbisPopupNotificationStyle,
        message: String,
        @StringRes icon: Int?,
    ) {
        SbisPopupNotification.push(SbisInfoNotificationFactory(type, message, icon?.let { context.getString(it) }))
    }
}