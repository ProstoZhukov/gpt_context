package ru.tensor.sbis.calendar_decl.calendar

import android.content.Intent
import ru.tensor.sbis.plugin_struct.feature.Feature
import java.util.*

/** Интерфейс получения [Intent] экрана группы отчетов */
interface CalendarReportingGroupActivityProvider: Feature {

    /**
     * Получить [Intent] экрана группы отчетов
     * @param caption Заголовок группы
     * @param docType Тип документа
     * @param docSubType Подтип документа
     * @param periodYear Год периода
     * @param periodCode Код периода
     * @param eventDate Дата события
     */
    fun getCalendarReportingGroupActivityIntent(
        caption: String?,
        docType: String?,
        docSubType: String?,
        periodYear: Short,
        periodCode: Short,
        eventDate: Date
    ): Intent
}