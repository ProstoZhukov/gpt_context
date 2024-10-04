package ru.tensor.sbis.design_notification.popup

import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import ru.tensor.sbis.design_notification.R

/**
 * Стили [View] панели-информера для создания посредством [SbisInfoNotificationFactory]
 *
 * @author us.bessonov
 */
enum class SbisPopupNotificationStyle(
    @AttrRes internal val styleAttr: Int,
    @StyleRes internal val defaultStyle: Int
) {
    /**
     * Акцентный стиль для информационных сообщений
     */
    INFORMATION(
        R.attr.sbisPopupNotificationInfoTheme,
        R.style.SbisPopupNotificationInfoTheme
    ),
    /**
     * Стиль для сообщений об успешном действии
     */
    SUCCESS(R.attr.sbisPopupNotificationSuccessTheme, R.style.SbisPopupNotificationSuccessTheme),
    /**
     * Стиль для предупреждений о проблеме
     */
    WARNING(
        R.attr.sbisPopupNotificationWarningTheme,
        R.style.SbisPopupNotificationWarningTheme
    ),
    /**
     * Стиль для сообщений об ошибке, проблеме
     */
    ERROR(
        R.attr.sbisPopupNotificationErrorTheme,
        R.style.SbisPopupNotificationErrorTheme
    ),
    /**
     * Стиль для уведомлений
     */
    NOTIFICATION(
        R.attr.sbisPopupNotificationNotificationTheme,
        R.style.SbisPopupNotificationNotificationTheme
    )
}