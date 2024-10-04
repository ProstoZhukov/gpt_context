package ru.tensor.sbis.calendar_date_icon

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.custom_view_tools.utils.textHeight
import timber.log.Timber

/**
 * Контроллер иконки календаря
 *
 * @author da.zolotarev
 */
internal class CalendarDateIconController(override var size: Float) : CalendarDateIconApi {

    private var icon: Drawable? = null
    private var numberLayout: TextLayout? = null

    internal fun attach(icon: Drawable?, numberLayout: TextLayout?) {
        this.icon = icon
        this.numberLayout = numberLayout
    }

    override var dayNumber: Int? = null
        set(value) {
            if (field != value) {
                field = value?.let { formatDate(it) }
                numberLayout?.configure {
                    text = field?.let { it.toString() } ?: ""
                }
                icon?.invalidateSelf()
            }
        }

    override fun getNumberHeight(): Int = numberLayout?.textPaint?.textHeight ?: 0

    @SuppressLint("BinaryOperationInTimber")
    private fun formatDate(value: Int) = if (value < CalendarDateIcon.MIN_DATE || value > CalendarDateIcon.MAX_DATE) {
        Timber.e(
            "День должен быть в промежутке от " +
                "${CalendarDateIcon.MIN_DATE} до ${CalendarDateIcon.MAX_DATE} включительно"
        )
        null
    } else value
}