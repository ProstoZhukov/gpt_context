package ru.tensor.sbis.period_picker_default

import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import ru.tensor.sbis.date_picker.Period
import ru.tensor.sbis.date_picker.free.DatePickerService
import ru.tensor.sbis.date_picker.generated.DataRefreshedCallback
import ru.tensor.sbis.date_picker.generated.DateInterval
import ru.tensor.sbis.date_picker.generated.DatePickerCallbackParameter
import ru.tensor.sbis.date_picker.generated.DatePickerController
import ru.tensor.sbis.date_picker.generated.DatePickerFilter
import ru.tensor.sbis.date_picker.toCalendar
import ru.tensor.sbis.platform.generated.Subscription
import java.util.Calendar
import java.util.Date

/**
 * Реализация сервиса, используемая компонентом выбора периода, см. [DatePickerService].
 * @param controllerInitializer инициализатор контроллера, см. [DatePickerController].
 *
 * @author aa.sviridov
 */
class DatePickerServiceImpl(
    controllerInitializer: () -> DatePickerController,
) : DatePickerService {

    private val controller by lazy(controllerInitializer)

    private var holidaysSubscription: Subscription? = null

    private val holidaysDataRefreshedCallback = object : DataRefreshedCallback() {

        override fun onEvent(param: DatePickerCallbackParameter) {
            val filter = param.datePickerFilter
            holidaysEmitters[filter]?.onNext(controller.refresh(filter).result.map { it.date.toCalendar() })
        }
    }

    private val holidaysEmitters = mutableMapOf<DatePickerFilter, ObservableEmitter<List<Calendar>>>()

    override fun saveHistory(key: String, period: Period) {
        controller.saveHistory(key, DateInterval(period.from, period.to))
    }

    override fun getHistory(key: String): List<Period> {
        return controller.getHistory(key).map { Period(it.dateFrom!!.toCalendar(), it.dateTo!!.toCalendar()) }
    }

    override fun getDaysOff(period: Period): List<Date> {
        return controller.list(DatePickerFilter(period.from!!, period.to!!)).result.map { it.date }
    }

    override fun createDaysOffObservable(period: Period): Observable<List<Calendar>> {
        val filter = DatePickerFilter(period.from!!, period.to!!)
        return Observable.create<List<Calendar>?> { emitter ->
            if (holidaysSubscription == null) {
                holidaysSubscription = controller.dataRefreshed().subscribe(holidaysDataRefreshedCallback)
            }
            holidaysEmitters[filter] = emitter
            emitter.onNext(controller.list(filter).result.map { it.date.toCalendar() })
        }.doFinally {
            holidaysEmitters.remove(filter)
            if (holidaysEmitters.isEmpty()) {
                holidaysSubscription?.disable()
                holidaysSubscription = null
            }
        }
    }
}