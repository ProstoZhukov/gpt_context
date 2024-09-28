package ru.tensor.sbis.common_views.notification

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import androidx.annotation.AttrRes
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.theme.global_variables.FontSize
import ru.tensor.sbis.design.theme.global_variables.StyleColor

/**
 * Вью для отображения даты уведомления с индикацией статуса прочитанности.
 *
 * @author am.boldinov
 */
class NotificationDateView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0
) : SbisTextView(context, attrs, defStyleAttr) {

    init {
        setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            FontSize.X3S.getScaleOffDimen(context)
        )
        setIsRead(false)
    }

    /**
     * Установить строку с датой уведомления.
     */
    fun setDate(date: String) {
        text = date
    }

    /**
     * Установить статус прочитанности.
     */
    fun setIsRead(isRead: Boolean) {
        val color = if (isRead) {
            StyleColor.UNACCENTED.getTextColor(context)
        } else {
            StyleColor.PRIMARY.getTextColor(context)
        }
        setTextColor(color)
    }

}