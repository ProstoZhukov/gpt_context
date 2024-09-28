package ru.tensor.sbis.pushnotification_utils

import android.content.Context
import android.util.TypedValue
import android.view.ContextThemeWrapper
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat

import ru.tensor.sbis.pushnotification_utils.R as RPushNotificationUtils
import ru.tensor.sbis.design.R as RDesign

/**
 * Поставщик ресурсов для стилизации пуш уведомлений.
 *
 * @author ev.grigoreva
 */
object PushThemeProvider {

    private var themeRes = ResourcesCompat.ID_NULL
    private var colorRes = ResourcesCompat.ID_NULL
    private var smallIconRes = ResourcesCompat.ID_NULL

    /**
     * @return цвет в пуш уведомлении см. [NotificationCompat.Builder.setColor]
     */
    @JvmStatic
    @ColorInt
    fun getColor(context: Context): Int {
        if (colorRes == ResourcesCompat.ID_NULL) {
            colorRes = context.prepareContext().let {
                it.getThemeRes(it.getPushTheme(), RPushNotificationUtils.attr.pushNotificationColor)
                    .takeIf { res -> res != ResourcesCompat.ID_NULL } ?: RDesign.color.color_primary
            }
        }
        return ContextCompat.getColor(context, colorRes)
    }

    /**
     * @return ресурс маленькой иконки в пуш уведомлении см. [NotificationCompat.Builder.setSmallIcon]
     */
    @JvmStatic
    @DrawableRes
    fun getSmallIconRes(context: Context): Int {
        if (smallIconRes == ResourcesCompat.ID_NULL) {
            smallIconRes = context.prepareContext().let {
                it.getThemeRes(it.getPushTheme(), RPushNotificationUtils.attr.pushNotificationSmallIcon)
                    .takeIf { res -> res != ResourcesCompat.ID_NULL }
                    ?: RPushNotificationUtils.drawable.push_notification_utils_push_icon_bird
            }
        }
        return smallIconRes
    }

    /**
     * Возвращает контекст, подходящий для работы с ресурсами во вложенных темах
     *
     * При работе с applicationContext тема, заданная через атрибут, при инициализации
     * не видит ресурсы, заданные ссылками на атрибуты, определенные вне текущей темы.
     * Причина в том, что тема в applicationContext не проинициализирована.
     *
     * @return текущий контекст или обертку над applicationContext
     */
    private fun Context.prepareContext(): Context {
        return if (this == applicationContext) {
            ContextThemeWrapper(this, applicationInfo.theme)
        } else {
            this
        }
    }

    /**
     * Достать атрибут из темы
     *
     * @param theme ид ресурса темы
     * @param attr название атрибута
     * @return ид ресурса, который содержится в теме под указанным атрибутом, либо [ResourcesCompat.ID_NULL]
     */
    private fun Context.getThemeRes(theme: Int, @AttrRes attr: Int): Int {
        return theme.takeIf { it != ResourcesCompat.ID_NULL }?.let {
            with(TypedValue()) {
                ContextThemeWrapper(this@getThemeRes, it).theme.resolveAttribute(attr, this, true)
                resourceId
            }
        } ?: ResourcesCompat.ID_NULL
    }

    /**
     * Достать из темы приложения атрибут темы для стилизации пушей
     *
     * @return ид ресурса темы пушей
     */
    private fun Context.getPushTheme(): Int {
        if (themeRes == ResourcesCompat.ID_NULL) {
            themeRes = getThemeRes(applicationInfo.theme, RPushNotificationUtils.attr.pushNotificationTheme)
        }
        return themeRes
    }
}