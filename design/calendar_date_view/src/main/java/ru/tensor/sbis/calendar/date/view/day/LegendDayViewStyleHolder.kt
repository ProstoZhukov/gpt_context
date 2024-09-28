package ru.tensor.sbis.calendar.date.view.day

import android.content.Context
import android.util.AttributeSet
import ru.tensor.sbis.calendar.date.R
import androidx.core.content.withStyledAttributes
import ru.tensor.sbis.calendar.date.view.day.beans.ColorsProvider
import ru.tensor.sbis.calendar.date.view.day_legend.LegendViewDimensions
import ru.tensor.sbis.design.custom_view_tools.utils.sp
import ru.tensor.sbis.design.theme.global_variables.FontSize
import ru.tensor.sbis.design.utils.getDimen

/** Првоайдер стиля для [LegendDayView] */
internal class LegendDayViewStyleHolder(
    var viewHeight: Int = 0,
    var viewWidth: Int = 0,
    var dateTextBaseline: Float = 0f,
    var dayOfWeekTextBaseline: Float = 0f,
    var dateTextSize: Float = 0f,
    var dayOfWeekTextSize: Float = 0f,
    var eventsCountTextSize: Float = 0f,
    var eventsCountTextMarginEnd: Float = 0f,
    var eventsCountTextMarginTop: Float = 0f,
    var dividerWidth: Int = 0,
    var colorsProvider: ColorsProvider,
) {
    /** @SelfDocumented */
    fun load(context: Context, attrs: AttributeSet?) {
        context.withStyledAttributes(attrs, R.styleable.LegendView) {
            viewHeight = resources.sp(LegendViewDimensions.HEIGHT)
            viewWidth = resources.sp(LegendViewDimensions.DAY_WIDTH)
            dateTextBaseline = resources.sp(LegendViewDimensions.DATE_BASELINE).toFloat()
            dayOfWeekTextBaseline = resources.sp(LegendViewDimensions.DAY_OF_WEEK_BASELINE).toFloat()
            dateTextSize = FontSize.X2L.getScaleOnDimen(context)
            dayOfWeekTextSize = FontSize.X3S.getScaleOnDimen(context)
            eventsCountTextSize = context.getDimen(R.attr.calendar_date_view_events_count_text_size)
            eventsCountTextMarginEnd = resources.sp(LegendViewDimensions.EVENTS_COUNT_MARGIN_END).toFloat()
            eventsCountTextMarginTop = resources.sp(LegendViewDimensions.EVENTS_COUNT_MARGIN_TOP).toFloat()
            dividerWidth = resources.getDimensionPixelSize(ru.tensor.sbis.design.R.dimen.common_separator_medium_size)
        }
    }
}