package ru.tensor.sbis.calendar.date.view.day_legend.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.joda.time.*
import ru.tensor.sbis.calendar.date.EVENTS_COUNT_POSITION_TOP
import ru.tensor.sbis.calendar.date.SHOW_HINT_MODE_BUSY_LEVEL
import ru.tensor.sbis.calendar.date.data.Day
import ru.tensor.sbis.calendar.date.data.EventType
import ru.tensor.sbis.calendar.date.data.ModeSelectedDay
import ru.tensor.sbis.calendar.date.view.selector.AdapterNotifier
import java.util.*
import kotlin.properties.Delegates.observable
import kotlin.reflect.KProperty

internal class DayLegendAdapter(private val currentDateScrollLimit: Boolean) :
    RecyclerView.Adapter<DayHolder>(), AdapterNotifier {

    var showHintMode: Int = SHOW_HINT_MODE_BUSY_LEVEL

    /** Позиция счетчика событий */
    var eventsCountPosition: Int = EVENTS_COUNT_POSITION_TOP

    /** Если true, то будем рисовать полоску отчета при отсутствии событий */
    var showReportMarkForced: Boolean = false

    val data = TreeMap<LocalDate, Day>()
    var onDayClicked by observable(null as ((LocalDate) -> Unit)?, onChange)
    var onModeSelectDay by observable(null as ((ModeSelectedDay) -> Unit)?, onChange)

    /**
     * Установить коллекцию данных [days]. Если [cleanOldData] = true, то перетрутся старые данные,
     * в противном случае новые данные устанавливаются без удаления старых
     */
    fun setData(
        days: Collection<Day>,
        firstVisiblePos: Int,
        lastVisiblePos: Int,
        cleanOldData: Boolean = false,
    ) {
        if (cleanOldData) {
            data.clear()
            notifyItemRangeChanged(firstVisiblePos, lastVisiblePos)
        }
        days.forEach {
            if (data[it.date] != it) {
                data[it.date] = it
                if (positionOfDate(it.date, currentDateScrollLimit) in firstVisiblePos..lastVisiblePos)
                    notifyItemChanged(positionOfDate(it.date, currentDateScrollLimit))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, type: Int): DayHolder =
        DayLegendHolder(parent)

    override fun getItemCount(): Int = Int.MAX_VALUE
    override fun onBindViewHolder(holder: DayHolder, position: Int) {
        val date = startDateToCountFrom(currentDateScrollLimit).plusDays(position)
        val info =
            if (data.containsKey(date)) data[date]!!
            else {
                val isWorkday = date.dayOfWeek <= 5
                Day(
                    date = date,
                    type = if (!isWorkday) EventType.DAY_OFF else EventType.WORKDAY,
                    isWorkday = isWorkday,
                )
            }
        when {
            data.containsKey(date) -> holder.bind(
                info = info,
                onDayClicked = {
                    onModeSelectDay?.invoke(ModeSelectedDay.CLICK)
                    onDayClicked?.invoke(it)
                },
                showHintMode = showHintMode,
                eventsCountPosition = eventsCountPosition,
                showReportMarkForced = showReportMarkForced
            )

            else -> {
                holder.bind(
                    info = info,
                    onDayClicked = {
                        onModeSelectDay?.invoke(ModeSelectedDay.CLICK)
                        onDayClicked?.invoke(it)
                    },
                    showHintMode = showHintMode,
                    eventsCountPosition = eventsCountPosition,
                    showReportMarkForced = showReportMarkForced
                )
            }
        }
    }
}

fun positionOfDate(it: LocalDate, currentDateScrollLimit: Boolean) =
    Days.daysBetween(startDateToCountFrom(currentDateScrollLimit), it).days

internal fun startDateToCountFrom(currentDateScrollLimit: Boolean): LocalDate = if (currentDateScrollLimit) {
    LocalDate.now().plusDays(1)
} else {
    LocalDate(0)
}

private val DayLegendAdapter.onChange: (KProperty<*>, Any?, Any?) -> Unit
    get() = { _, oldValue, newValue ->
        if (newValue != oldValue)
            notifyDataSetChanged()
    }