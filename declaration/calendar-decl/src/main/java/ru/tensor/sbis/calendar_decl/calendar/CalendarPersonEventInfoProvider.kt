package ru.tensor.sbis.calendar_decl.calendar

import io.reactivex.Observable
import kotlinx.coroutines.flow.Flow
import ru.tensor.sbis.calendar_decl.calendar.data.PersonCurrentEventInfo
import ru.tensor.sbis.plugin_struct.feature.Feature
import java.util.*

/** Провайдер информации о пользователе */
interface CalendarPersonEventInfoProvider : Feature {

    /**
     * Получить информацию о событии пользователя на выбранную дату
     * @param uuid UUID пользователя
     * @param date дата события. Если передано null, то вернется событие на текущее время
     * @return Observable<PersonCurrentEventInfo>
     */
    fun getPersonCurrentEventInfoObservable(
        uuid: UUID,
        date: Date? = null
    ): Observable<PersonCurrentEventInfo>

    /**
     * Проверить доступность календаря пользователя
     * @param uuid UUID пользователя
     */
    fun getPersonCalendarAvailabilityObservable(uuid: UUID): Observable<Boolean>

    /** Проверить доступность активности календаря */
    fun getPersonCalendarDayActivityAvailability(): Flow<Boolean>
}