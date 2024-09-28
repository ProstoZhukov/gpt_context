package ru.tensor.sbis.date_picker

import io.reactivex.Observable
import java.util.*

/**
 * Репозиторий, предоставляющий счётчики дней
 *
 * @author mb.kruglova
 */
interface DayCountersRepository {

    /**
     * Возвращает подписку на обновление счётчиков.
     * @param range диапазон дат, по которым нужно получать счётчики
     * @return Observable, излучающие набор соответствий счётчика с датой дня
     */
    fun getDayCountersUpdatesObservable(range: ClosedRange<Calendar>): Observable<Map<Calendar, Int>>
}

/**
 * @author mb.kruglova
 */
object NoDayCountersRepository : DayCountersRepository {

    override fun getDayCountersUpdatesObservable(range: ClosedRange<Calendar>): Observable<Map<Calendar, Int>> =
        Observable.empty()
}