package ru.tensor.sbis.design_notification.popup

import android.content.Context
import android.view.View

/**
 * Реализация [SbisNotificationFactory], поддерживающая создание стандартной панели-информера в одном из
 * предусмотренных стилей.
 *
 * @param type стиль оформления панели
 * @param message отображаемое сообщение
 * @param icon опциональная иконка, отображаемая слева
 *
 * @author us.bessonov
 */
data class SbisInfoNotificationFactory(
    private val type: SbisPopupNotificationStyle,
    private val message: String,
    private val icon: String?
) : SbisNotificationFactory {

    override fun createView(context: Context, closeCallback: (() -> Unit)?): View =
        SbisInfoView(context, null, type.styleAttr, type.defaultStyle, message, icon, closeCallback)
}