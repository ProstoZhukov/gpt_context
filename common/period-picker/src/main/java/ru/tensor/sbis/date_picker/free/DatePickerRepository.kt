package ru.tensor.sbis.date_picker.free

import io.reactivex.Observable
import ru.tensor.sbis.common.util.ResourceProvider
import ru.tensor.sbis.date_picker.Period
import ru.tensor.sbis.date_picker.R
import ru.tensor.sbis.date_picker.free.items.HistoryPeriod
import ru.tensor.sbis.date_picker.prepareDateInterval
import java.util.*
import ru.tensor.sbis.common.R as RCommon

/**
 * Репозиторий для получения списка периодов и сохранения выбранных периодов
 *
 * @author mb.kruglova
 */
open class DatePickerRepository(
    private val datePickerService: DatePickerService,
    private val resourceProvider: ResourceProvider
) {

    fun getRecentPeriods(key: String): List<HistoryPeriod> {
        val halfYearTitles = resourceProvider.getStringArray(R.array.half_year).toList()
        val quartersTitles = resourceProvider.getStringArray(R.array.quarters).toList()
        val monthTitles = resourceProvider.getStringArray(RCommon.array.common_months).toList()

        return datePickerService.getHistory(key).map {
            prepareDateInterval(halfYearTitles, quartersTitles, monthTitles, it)
        }
    }

    fun savePeriod(key: String, period: Period) {
        datePickerService.saveHistory(key, period)
    }

    open fun getUnavailablePeriods(): List<Period> {
        return emptyList()
    }

    fun dayOffList(period: Period) = datePickerService.getDaysOff(period)

    fun createDaysOffObservable(period: Period): Observable<List<Calendar>> =
        datePickerService.createDaysOffObservable(period)
}