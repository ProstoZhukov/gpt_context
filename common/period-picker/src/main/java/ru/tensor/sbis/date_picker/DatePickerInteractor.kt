package ru.tensor.sbis.date_picker

import io.reactivex.Observable
import ru.tensor.sbis.date_picker.free.DatePickerRepository
import ru.tensor.sbis.date_picker.free.items.HistoryPeriod
import java.util.*

/**
 * @author mb.kruglova
 */
class DatePickerInteractor(
    private val repository: DatePickerRepository?,
    private val monthMarkersRepository: MonthMarkersRepository?,
) {

    fun getRecentPeriods(key: String): Observable<List<HistoryPeriod>> {
        return Observable.fromCallable { repository?.getRecentPeriods(key) ?: emptyList() }
    }

    fun savePeriod(key: String, period: Period) = repository?.savePeriod(key, period)

    fun getHolidays(period: Period): Observable<List<Calendar>> =
        repository?.createDaysOffObservable(period) ?: Observable.just(emptyList())

    fun getUnavailablePeriods(): Observable<List<Period>> {
        return Observable.just(repository?.getUnavailablePeriods() ?: emptyList())
    }

    fun getMarkedMonths(): Observable<List<Calendar>> {
        return Observable.fromCallable { monthMarkersRepository?.getMarkedMonths() ?: emptyList() }
    }
}