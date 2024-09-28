package ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year

import com.arkivanov.mvikotlin.core.view.MviView
import ru.tensor.sbis.design.period_picker.view.models.CalendarStorage
import ru.tensor.sbis.design.period_picker.view.models.SelectionType
import ru.tensor.sbis.design.period_picker.view.period_picker.details.store.Intent
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.YearModePeriodPickerView.Event
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.YearModePeriodPickerView.Model
import java.util.Calendar

/**
 * Контракт представления.
 *
 * @author mb.kruglova
 */
internal interface YearModePeriodPickerView : MviView<Model, Event> {

    /**
     * События, генерируемые представлением.
     */
    sealed interface Event {

        /** Маппинг евента в интент */
        fun eventToIntent(): Intent

        /** Событие клика по месяцу в календаре. */
        data class ClickMonth(val date: Calendar) : Event {
            override fun eventToIntent(): Intent = Intent.SelectMonth(date)
        }

        /** Событие клика по кварталу в календаре. */
        data class ClickQuarter(val dateFrom: Calendar, val dateTo: Calendar) : Event {
            override fun eventToIntent(): Intent = Intent.SelectQuantum(dateFrom, dateTo, SelectionType.QUARTER)
        }

        /** Событие клика по полугодию в календаре. */
        data class ClickHalfYear(val dateFrom: Calendar, val dateTo: Calendar) : Event {
            override fun eventToIntent(): Intent = Intent.SelectQuantum(dateFrom, dateTo, SelectionType.HALF_YEAR)
        }

        /** Событие клика по году в шапке календаря. */
        data class ClickYearLabel(val date: Calendar) : Event {
            override fun eventToIntent(): Intent = Intent.SelectYear(date)
        }

        /** Событие обновления года в шапке календаря. */
        data class UpdateYearLabel(val year: Int) : Event {
            override fun eventToIntent(): Intent = Intent.UpdateYearLabel(year)
        }

        /** Событие дозагрузки календаря. */
        data class ReloadCalendar(
            val isNextPage: Boolean,
            val year: Int? = null
        ) : Event {
            override fun eventToIntent(): Intent = Intent.ReloadCalendar(isNextPage, year)
        }

        /** Событие сброса выделения периода. */
        data class ResetSelection(val dateFrom: Calendar?, val dateTo: Calendar?) : Event {
            override fun eventToIntent(): Intent = Intent.ResetSelection
        }

        /** Событие выбора произвольного периода в календаре. */
        data class SelectPeriod(val dateFrom: Calendar, val dateTo: Calendar) : Event {
            override fun eventToIntent(): Intent = Intent.SelectPeriod(dateFrom, dateTo)
        }
    }

    /**
     * Модель представления, по которой отрисовывается выбранный период и счётчики.
     * @param calendarStorage хранилище данных для календаря.
     * @param startPeriod начало выбранного периода.
     * @param endPeriod конец выбранного периода.
     */
    data class Model(
        private val calendarStorage: CalendarStorage = CalendarStorage(),
        private val startPeriod: Calendar? = null,
        private val endPeriod: Calendar? = null
    ) {

        /** @SelfDocumented */
        internal fun getCalendarStorage() = calendarStorage

        /** @SelfDocumented */
        internal fun getStartPeriod() = startPeriod

        /** @SelfDocumented */
        internal fun getEndPeriod() = endPeriod
    }
}