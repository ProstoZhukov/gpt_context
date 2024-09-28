package ru.tensor.sbis.calendar.date.view

import androidx.annotation.IntRange
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import org.joda.time.LocalDate
import ru.tensor.sbis.calendar.date.data.Day
import ru.tensor.sbis.design.utils.ProviderSubject

/**
 * Способ выбора даты или дат
 * @param mode режим селекта
 * @param selectedDayDrawable отрисовка ячеек выбранных дат
 */
data class DatePickerBaseProperties(
    val mode: Int = NO_SELECTOR,
    var selectedDayDrawable: SelectedDayDrawable
){
    companion object {
        const val NO_SELECTOR = 0
        const val ONE_SELECTOR = 1
        const val MULTIPLE_SELECTOR = 2
        const val ONE_SELECTOR_WITH_SEVERAL_DAYS = 3
    }
}

/**
 * @param data - данные для отображения в календаре, отпуска, прогулы и т.д.
 * @param loadMonthPeriod - период зашгрузки исчисляется в месяцах, минимум 1
 */
class DatePickerLifeData(
    val hide: ProviderSubject<Boolean>,
    val month: ProviderSubject<LocalDate>,
    var selectedDates: ProviderSubject<Pair<LocalDate?, LocalDate?>?>,
    val selectedDatesWithNoTimePickerSubscribe: PublishSubject<Pair<LocalDate?, LocalDate?>?> = PublishSubject.create(),
    val data: Observable<Map<LocalDate, Day>>,
    @IntRange(from = 1) var loadMonthPeriod: Int = 1,
    val expandMode: HidableMonthPickerExpandMode = HidableMonthPickerExpandMode.EXPANDABLE,
    var selectedDatesFunc: ((Pair<LocalDate?, LocalDate?>) -> Unit)? = null
)

enum class HidableMonthPickerExpandMode {
    EXPANDABLE,
    ALWAYS_EXPANDED
}
