package ru.tensor.sbis.calendar_decl.calendar

import android.content.Intent
import ru.tensor.sbis.plugin_struct.feature.Feature

/** Провайдер активити добавления отчета */
interface CalendarAddReportActivityProvider: Feature {

    /**
     * Получить [Intent] экрана добавления отчета
     */
    fun getCalendarAddReportActivityIntent(): Intent
}