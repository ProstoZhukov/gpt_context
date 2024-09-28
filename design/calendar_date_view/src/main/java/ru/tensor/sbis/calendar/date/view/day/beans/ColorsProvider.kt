package ru.tensor.sbis.calendar.date.view.day.beans

import android.content.Context
import androidx.annotation.ColorInt
import ru.tensor.sbis.calendar.date.R
import ru.tensor.sbis.design.utils.getThemeColorInt

/**
 * Провайдер цветов для пикера календаря.
 *
 * @author im.zheglov
 */
class ColorsProvider(context: Context) {
    @ColorInt val colorWorkday = context.getThemeColorInt(R.attr.calendar_date_view_color_workday_background)
    @ColorInt val colorDayOff = context.getThemeColorInt(R.attr.calendar_date_view_color_day_off_background)
    @ColorInt val colorHolidayCrossVac = context.getThemeColorInt(R.attr.calendar_date_view_color_cross_vacation_holiday_background)
    @ColorInt val colorTimeOff = context.getThemeColorInt(R.attr.calendar_date_view_color_time_off_background)
    @ColorInt val colorTruancy = context.getThemeColorInt(R.attr.calendar_date_view_color_truancy_background)
    @ColorInt val colorBusinessTrip = context.getThemeColorInt(R.attr.calendar_date_view_color_business_trip_background)
    @ColorInt val colorPlanVacation = context.getThemeColorInt(R.attr.calendar_date_view_color_plan_vacation_background)
    @ColorInt val colorPlanVacationOnAgreement = context.getThemeColorInt(R.attr.calendar_date_view_color_plan_vacation_on_agreement_background)
    @ColorInt val colorPlanVacationOnDeletion = context.getThemeColorInt(R.attr.calendar_date_view_color_plan_vacation_on_deletion_background)
    @ColorInt val colorFactVacation = context.getThemeColorInt(R.attr.calendar_date_view_color_fact_vacation_background)
    @Suppress("unused")
    @ColorInt val colorCrossedVacation = context.getThemeColorInt(R.attr.calendar_date_view_color_crossed_vacation_hatch)
    @ColorInt val colorSickLeave = context.getThemeColorInt(R.attr.calendar_date_view_color_sick_leave_background)
    @ColorInt val colorReport = context.getThemeColorInt(R.attr.calendar_date_view_color_report)
    @ColorInt val colorDowntime = context.getThemeColorInt(R.attr.calendar_date_view_color_downtime_background)
    @ColorInt val colorBirthday = context.getThemeColorInt(R.attr.calendar_date_view_color_birthday_background)
    @ColorInt val notHired = context.getThemeColorInt(R.attr.calendar_date_view_not_hired_background)
    @ColorInt val colorDateText = context.getThemeColorInt(R.attr.calendar_date_view_color_date_text)
    @ColorInt val colorDateTextSelected = context.getThemeColorInt(R.attr.calendar_date_view_color_date_text_selected)
    @ColorInt val colorCurrentDayBorder = context.getThemeColorInt(R.attr.calendar_date_view_color_current_day_border)
    @ColorInt val colorCurrentDayCircle = context.getThemeColorInt(R.attr.calendar_date_view_color_current_day_circle)
    @ColorInt val colorEventsMarkReport = context.getThemeColorInt(R.attr.calendar_date_view_color_busy_level_report)
    @ColorInt val colorEventsMarkDefault = context.getThemeColorInt(R.attr.calendar_date_view_color_busy_level_default)
    @ColorInt val arrowBack = context.getThemeColorInt(R.attr.calendar_date_view_color_arrow_back)
    @ColorInt val divider = context.getThemeColorInt(R.attr.calendar_date_view_color_divider)
    @ColorInt val firstDayText = context.getThemeColorInt(R.attr.calendar_date_view_color_first_day_text)
    @ColorInt val eventsCountText = context.getThemeColorInt(R.attr.calendar_date_view_color_events_count_text)
    @ColorInt val firstDayBackground = context.getThemeColorInt(R.attr.calendar_date_view_color_first_day_background)
    @ColorInt val busyLevel = context.getThemeColorInt(R.attr.calendar_date_view_color_busy_level_default)
    @ColorInt val workdayText = context.getThemeColorInt(R.attr.calendar_date_view_color_workday_text)
    @ColorInt val firstDayWorkdayText = context.getThemeColorInt(R.attr.calendar_date_view_color_first_day_workday_text)
    @ColorInt val firstDayHolidayText = context.getThemeColorInt(R.attr.calendar_date_view_color_first_day_holiday_text)
    @ColorInt val colorHolidayText = context.getThemeColorInt(R.attr.calendar_date_view_color_holiday_text)
}
