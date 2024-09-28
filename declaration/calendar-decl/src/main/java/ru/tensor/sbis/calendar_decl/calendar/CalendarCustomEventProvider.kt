package ru.tensor.sbis.calendar_decl.calendar

import androidx.fragment.app.Fragment
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import ru.tensor.sbis.calendar_decl.calendar.events.VacationTypeInfo
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.regulation.generated.Regulation
import java.util.Date
import java.util.UUID

/**
 * Интерфейс получения кастомных фрагментов при создании события
 */
interface CalendarCustomEventProvider : Feature {

    /**
     * Метод получения фрагмента для создания кастомного события [eventType] на указанную дату [date]
     * для сотрудника [profileUUID] автором события [ownerUUID]. [vacationTypeInfo] служит для создания отпуска,
     * [regulation] - регламент события
     */
    fun getCalendarCreateEventFragment(
        eventType: String,
        date: LocalDate?,
        regulation: Regulation?,
        vacationTypeInfo: VacationTypeInfo?,
        profileUUID: UUID?,
        ownerUUID: UUID,
    ): Fragment? = null

    /**
     * Метод получения фрагмента для создания кастомного события [eventType] на указанную дату [date]
     * для сотрудника [profileUUID] автором события [ownerUUID]
     */
    fun getCalendarCreateEventFragment(eventType: String, date: LocalDate, profileUUID: UUID?, ownerUUID: UUID): Fragment?

    /**
     * Метод получения фрагмента для создания кастомного события [eventType] с указанием точного времени
     * начала [timeStart] и завершения [timeEnd] для сотрудника [profileUUID] автором события [ownerUUID].
     * @param hasActualStartAndEndTime - событие создается, имея актуальное время начало и конца
     * @param profileDepartmentUUID - департамент текущего пользователя
     */
    fun getCalendarCreateEventFragment(
        eventType: String,
        timeStart: LocalDateTime,
        timeEnd: LocalDateTime,
        profileUUID: UUID?,
        ownerUUID: UUID,
        profileDepartmentUUID: UUID?,
        hasActualStartAndEndTime: Boolean
    ): Fragment?

    /**
     * Метод получения фрагмента для создания кастомного события [eventType] с указанием точного времени
     * начала [timeStart] и завершения [timeEnd] для сотрудника [profileUUID] автором события [ownerUUID].
     * [vacationTypeInfo] служит для создания отпуска, [regulation] - регламент события
     * @param hasActualStartAndEndTime - событие создается, имея актуальное время начало и конца
     * @param profileDepartmentUUID - департамент текущего пользователя
     */
    fun getCalendarCreateEventFragment(
        eventType: String,
        regulation: Regulation?,
        vacationTypeInfo: VacationTypeInfo?,
        timeStart: LocalDateTime,
        timeEnd: LocalDateTime,
        profileUUID: UUID?,
        ownerUUID: UUID,
        profileDepartmentUUID: UUID?,
        hasActualStartAndEndTime: Boolean
    ): Fragment? = null

    /**
     * Метод получения фрагмента для отображения кастомного события [eventType] с идентификатором [eventUuid] у сотрудника [profileUUID]
     * (опционально для некоторых событий передается id документа [docId])
     * если событие открыто из пуша, то передается [eventDateFromNotification]
     */
    fun getCalendarShowEventFragment(
        eventType: String,
        eventUuid: UUID,
        profileUUID: UUID?,
        eventDateFromNotification: String?,
        docId: Long?,
        openInEditMode: Boolean,
    ): Fragment?

    /**
     * Метод получения фрагмента для отображения карточки записи к мастеру событие [eventType]
     * и идентификатором записи в салон [saleId]
     */
    fun getCalendarShowBeautySalonOrderFragment(eventType: String, saleId: Long): Fragment? = null

    /** Метод получения фрагмента для создания корректировки командировки */
    fun getBusinessTripCorrectionFragment(
        correctionUuid: UUID,
        correctionCancelled: Boolean,
        correctionReason: String,
        correctionDateStart: Date?,
        correctionDateEnd: Date?,
        correctionRegulation: Regulation,
        profileUUID: UUID?,
    ): Fragment? = null
}