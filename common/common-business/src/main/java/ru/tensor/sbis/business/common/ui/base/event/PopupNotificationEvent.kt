package ru.tensor.sbis.business.common.ui.base.event

import ru.tensor.sbis.design_notification.popup.SbisPopupNotificationStyle

/**
 * Событие отображения сообщения информера
 *
 * @author aa.kobeleva
 *
 * @property text текст для отображения
 * @property style стиль информационной панели
 * @property error ошибка для отображения
 * @property icon иконка для отображения
 */
data class PopupNotificationEvent(
    val text: String,
    val style: SbisPopupNotificationStyle,
    val error: Throwable? = null,
    val icon: String? = null
)