package ru.tensor.sbis.calendar.date.view.day.subviews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.Gravity
import androidx.appcompat.widget.AppCompatTextView
import ru.tensor.sbis.calendar.date.utils.getCalendarDateViewStyleId
import ru.tensor.sbis.calendar.date.view.day.beans.ColorsProvider
import kotlin.properties.Delegates.observable
import ru.tensor.sbis.design.R as RDesign

private const val CURRENT_DATE_DRAWING_COEFFICIENT_TABLET = .85f
private const val CURRENT_DATE_DRAWING_COEFFICIENT_PHONE = .95f

/**
 * View для отображения даты в пикере календаря.
 *
 * @author ae.noskov
 */
internal open class DateView
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : AppCompatTextView(context, attrs, defStyle) {

    /**
     * Является ли день текущим.
     */
    var isCurrent by observable(false) { _, _, newValue ->
        setTextColor(
            if (newValue) colorsProvider.colorDateTextSelected else colorsProvider.colorDateText
        )
    }

    /**
     * Является ли день праздничным.
     */
    var isHoliday by observable(false) { _, _, newValue ->
        if(newValue && !isCurrent) {
            setTextColor(colorsProvider.colorHolidayText)
        }
    }
    protected val colorsProvider = ColorsProvider(context)
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        context.theme.applyStyle(context.getCalendarDateViewStyleId(), true)
        gravity = Gravity.CENTER
        includeFontPadding = false
    }

    override fun onDraw(canvas: Canvas) {
        if (isCurrent) {
            val isTablet = resources.getBoolean(RDesign.bool.is_tablet)
            paint.color = colorsProvider.colorCurrentDayCircle
            val circleRadius = (height / 2f) * (if (isTablet) CURRENT_DATE_DRAWING_COEFFICIENT_TABLET else CURRENT_DATE_DRAWING_COEFFICIENT_PHONE)
            canvas.drawCircle(width / 2f, height / 2f, circleRadius, paint)
        }
        super.onDraw(canvas)
    }
}