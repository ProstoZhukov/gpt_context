package ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month

import com.arkivanov.mvikotlin.core.view.MviView
import ru.tensor.sbis.design.period_picker.view.models.CalendarStorage
import ru.tensor.sbis.design.period_picker.view.period_picker.details.store.Intent
import ru.tensor.sbis.design.period_picker.view.utils.CalendarDayRange
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.MonthModePeriodPickerView.Model
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.MonthModePeriodPickerView.Event
import java.util.Calendar

/**
 * Контракт представления.
 *
 * @author mb.kruglova
 */
internal interface MonthModePeriodPickerView : MviView<Model, Event> {

    /**
     * События, генерируемые представлением.
     */
    sealed interface Event {

        /** Маппинг евента в интент */
        fun eventToIntent(): Intent

        /** Событие клика по дню в календаре. */
        data class ClickDay(val date: Calendar) : Event {
            override fun eventToIntent(): Intent = Intent.SelectDay(date)
        }

        /** Событие клика по месяцу в календаре. */
        data class ClickMonthPeriod(val dateFrom: Calendar, val dateTo: Calendar) : Event {
            override fun eventToIntent(): Intent = Intent.SelectMonthPeriod(dateFrom, dateTo)
        }

        /** Событие обновления счётчиков. */
        data class UpdateCounters(val range: CalendarDayRange) : Event {
            override fun eventToIntent(): Intent = Intent.UpdateCounters(range)
        }

        /** Событие сброса выделения периода. */
        object ResetSelection : Event {
            override fun eventToIntent(): Intent = Intent.ResetSelection
        }

        /** Событие обновления позиции календаря на текущую дату. */
        object UpdateScrollToCurrentDay : Event {
            override fun eventToIntent(): Intent = Intent.UpdateScrollToCurrentDay
        }

        /** Событие дозагрузки календаря. */
        data class ReloadCalendar(val isNextPage: Boolean) : Event {
            override fun eventToIntent(): Intent = Intent.ReloadCalendar(isNextPage)
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
     * @param counters счетчики по дням.
     * @param isSingleClick является ли режим выбора - выбор одного дня.
     */
    data class Model(
        private val calendarStorage: CalendarStorage = CalendarStorage(),
        private val startPeriod: Calendar? = null,
        private val endPeriod: Calendar? = null,
        private val counters: Map<Calendar, Int>? = null,
        private val isSingleClick: Boolean = true
    ) {

        /** @SelfDocumented */
        internal fun getStartPeriod() = startPeriod

        /** @SelfDocumented */
        internal fun getEndPeriod() = endPeriod

        /** @SelfDocumented */
        internal fun getCalendar() = calendarStorage.dayGrid
    }
}