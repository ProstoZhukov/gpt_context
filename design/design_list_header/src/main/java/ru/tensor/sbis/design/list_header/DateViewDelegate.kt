package ru.tensor.sbis.design.list_header

import android.util.AttributeSet
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.annotation.StyleableRes
import androidx.core.content.res.use
import ru.tensor.sbis.design.R
import ru.tensor.sbis.design.list_header.format.FormattedDateTime
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.utils.getDimenPx

/**
 * Делегат, реализующий BaseDateView
 *
 * @author ra.petrov
 */
internal class DateViewDelegate(
    @StyleableRes private val modeAttr: Int
) : BaseDateView {

    override lateinit var dateViewMode: DateViewMode

    /**
     * textView, которое отображает дату
     */
    private lateinit var textView: SbisTextView

    internal fun init(
        textView: SbisTextView,
        attrs: AttributeSet?,
        @AttrRes defStyleAttr: Int,
        @StyleRes defStyleRes: Int
    ) {
        this.textView = textView
        textView.context.getDimenPx(R.attr.fontSize_xs_scaleOff).toFloat().let {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, it)
        }
        textView.height = textView.context.resources.getDimensionPixelSize(R.dimen.design_list_header_height)

        dateViewMode = textView.context.obtainStyledAttributes(attrs, intArrayOf(modeAttr), defStyleAttr, defStyleRes)
            .use { array ->
                array.getInteger(0, DateViewMode.DATE_TIME.ordinal).let { DateViewMode.values()[it] }
            }
    }

    override fun setFormattedDateTime(formattedDateTime: FormattedDateTime?) {
        val dateTimeText = formattedDateTime?.let {
            when (dateViewMode) {
                DateViewMode.DATE_TIME -> "${it.date} ${it.time}".trim()
                DateViewMode.DATE_ONLY -> it.date
                DateViewMode.TIME_ONLY -> it.time
            }
        }
        textView.text = dateTimeText
    }
}