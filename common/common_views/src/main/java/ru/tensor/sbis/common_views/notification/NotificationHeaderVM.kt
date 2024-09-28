package ru.tensor.sbis.common_views.notification

import androidx.annotation.AttrRes
import ru.tensor.sbis.common.util.CommonUtils
import ru.tensor.sbis.design.R

/**
 * Вью модель данных для цветного заголовка с датой.
 *
 * @property headerText текст заголовка
 * @property formattedDateText отформатированная строка, содержащая дату
 * @property isReaded нужно ли отобразить как прочитанное
 * @property headerColorAttr цвет текста заголовка (атрибут)
 * @property headerSizeAttr размер текста заголовка (атрибут)
 *
 * @author am.boldinov
 */
class NotificationHeaderVM @JvmOverloads constructor(
    val headerText: CharSequence?,
    val formattedDateText: String,
    var isReaded: Boolean = true,
    @AttrRes val headerColorAttr: Int = R.attr.unaccentedTextColor,
    @AttrRes val headerSizeAttr: Int = R.attr.fontSize_xs_scaleOff
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NotificationHeaderVM

        if (!CommonUtils.equals(headerText, other.headerText)) return false
        if (formattedDateText != other.formattedDateText) return false
        if (isReaded != other.isReaded) return false
        if (headerColorAttr != other.headerColorAttr) return false
        if (headerSizeAttr != other.headerSizeAttr) return false

        return true
    }

    override fun hashCode(): Int {
        var result = headerText?.hashCode() ?: 0
        result = 31 * result + formattedDateText.hashCode()
        result = 31 * result + isReaded.hashCode()
        result = 31 * result + headerColorAttr
        result = 31 * result + headerSizeAttr.hashCode()
        return result
    }
}