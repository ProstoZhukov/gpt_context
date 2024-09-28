package ru.tensor.sbis.calendar.date.view.day_legend

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import androidx.core.content.withStyledAttributes
import ru.tensor.sbis.calendar.date.R
import ru.tensor.sbis.calendar.date.view.day.beans.ColorsProvider
import ru.tensor.sbis.design.custom_view_tools.utils.dp
import ru.tensor.sbis.design.custom_view_tools.utils.sp
import ru.tensor.sbis.design.theme.global_variables.FontSize
import ru.tensor.sbis.design.utils.getDimen

/** Провайдер стиля для [LegendViewFirstDay] */
internal class LegendViewFirstDayStyleHolder(
    var arrowBackTextSize: Float = 0f,
    var dayOfWeekTextSize: Float = 0f,
    var dateTextSize: Float = 0f,
    var dateTextMarginEnd: Float = 0f,
    var monthTextSize: Float = 0f,
    var monthTextMarginEnd: Float = 0f,
    var arrowBackEndMargin: Float = 0f,
) {
    /** @SelfDocumented */
    fun load(context: Context, attrs: AttributeSet?) {
        context.withStyledAttributes(attrs, R.styleable.LegendView) {
            dateTextSize = FontSize.X4L.getScaleOnDimen(context)
            monthTextSize = FontSize.XL.getScaleOnDimen(context)
            dayOfWeekTextSize = FontSize.S.getScaleOnDimen(context)

            dateTextMarginEnd = resources.sp(LegendViewDimensions.DATE_TEXT_MARGIN_END).toFloat()
            arrowBackTextSize = FontSize.X2L.getScaleOnDimen(context)
            monthTextMarginEnd = resources.sp(LegendViewDimensions.MONTH_TEXT_MARGIN_END).toFloat()
            arrowBackEndMargin = resources.sp(LegendViewDimensions.ARROW_BACK_END_MARGIN).toFloat()
        }
    }
}