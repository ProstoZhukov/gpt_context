package ru.tensor.sbis.calendar_decl.calendar

import android.content.Intent
import ru.tensor.sbis.plugin_struct.feature.Feature
import java.util.*

/** Провайдер активности карточки комплекта */
interface CalendarComplectCardActivityProvider : Feature {

    /**
     * Получение интента для запуска активити карточки комплекта
     * @param complectUuid Идентификатор записи
     * @param eventStateId Идентификатор состояния события, необходим для получения списка действий и изменения состояния
     */
    fun getCalendarComplectCardActivityIntent(complectUuid: UUID, eventStateId: UUID?): Intent

    /**
     * Получение интента для запуска активити карточки комплекта
     * @param id Идентификатор записи
     * @param caption Текст на записи (название отчета)
     * @param inspCaption Название направления сдачи
     * @param iconName Иконка гос. инспекции
     * @param iconColor цвет иконки гос. органищации
     * @param orgsCaption Названия организаций одной строкой
     * @param date Дата
     * @param eventStateId Идентификатор состояния события, необходим для получения списка действий и изменения состояния
     */
    fun getCalendarComplectCardActivityIntent(
        id: UUID,
        caption: String?,
        inspCaption: String?,
        iconName: String?,
        iconColor: String?,
        orgsCaption: String?,
        date: Date,
        eventStateId: UUID?
    ): Intent
}