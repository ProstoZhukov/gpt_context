package ru.tensor.sbis.calendar.date.view

import ru.tensor.sbis.calendar.date.data.EventType
import ru.tensor.sbis.calendar.date.view.day.beans.ColorsProvider

/** Вернуть цвет из провайдера [colorsProvider], основываясь на типе дня */
fun EventType.resolveBackgroundColorWithColorsProvider(colorsProvider: ColorsProvider): Int {
    return when (this) {
        EventType.WORKDAY -> colorsProvider.colorWorkday
        EventType.DAY_OFF -> colorsProvider.colorDayOff
        EventType.TRUANCY -> colorsProvider.colorTruancy
        EventType.BUSINESS_TRIP -> colorsProvider.colorBusinessTrip
        EventType.PLAN_VACATION -> colorsProvider.colorPlanVacation
        EventType.PLAN_VACATION_ON_AGREEMENT -> colorsProvider.colorPlanVacationOnAgreement
        EventType.PLAN_VACATION_ON_DELETION -> colorsProvider.colorPlanVacationOnDeletion
        EventType.FACT_VACATION -> colorsProvider.colorFactVacation
        EventType.FACT_VACATION_WITHOUT_PAY -> colorsProvider.colorFactVacation
        EventType.FACT_VACATION_MOBILIZATION -> colorsProvider.colorFactVacation
        EventType.SICK_LEAVE -> colorsProvider.colorSickLeave
        EventType.DOWNTIME -> colorsProvider.colorDowntime
        EventType.BIRTHDAY -> colorsProvider.colorBirthday
        EventType.BABY_CARE -> colorsProvider.colorSickLeave
        EventType.NOT_HIRED -> colorsProvider.notHired
        EventType.REPORT -> colorsProvider.colorReport
        EventType.TIME_OFF -> colorsProvider.colorTimeOff
    }
}