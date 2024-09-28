package ru.tensor.sbis.calendar.date.view.year

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.joda.time.DateTimeConstants
import org.joda.time.LocalDate
import ru.tensor.sbis.calendar.date.R
import ru.tensor.sbis.calendar.date.data.Day
import ru.tensor.sbis.calendar.date.data.EventType
import ru.tensor.sbis.calendar.date.utils.asMonthString
import ru.tensor.sbis.calendar.date.view.DayViewHolder
import ru.tensor.sbis.calendar.date.view.SelectedDayDrawable
import ru.tensor.sbis.calendar.date.view.day.DayView
import ru.tensor.sbis.calendar.date.view.selector.AdapterNotifier
import ru.tensor.sbis.calendar.date.view.selector.NoSelector
import ru.tensor.sbis.calendar.date.view.selector.Selector
import ru.tensor.sbis.design.utils.delegatePropertyMT
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Адаптер для отображения скролящегося в обе стороны календаря
 * В реальности он не бесконечен, а начинается с 1990 года, от это точки идёт отсчёт дней и смещений дней
 *
 *  Для каждой позиции вычисляется дата, с учётом дат, пустых мест и заголовков
 *
 *  Август'20               <- 0 position
 *  𝚡𝚡|𝚡𝚡|𝚡𝚡|𝚡𝚡|𝚡𝚡|𝟶𝟷|𝟶𝟸    <- 1-7 position
 *  𝟶𝟹|𝟶𝟺|𝟶𝟻|𝟶𝟼|𝟶𝟽|𝟶𝟾|𝟶𝟿
 *  𝟷𝟶|𝟷𝟷|𝟷𝟸|𝟷𝟹|𝟷𝟺|𝟷𝟻|𝟷𝟼
 *  𝟷𝟽|𝟷𝟾|𝟷𝟿|𝟸𝟶|𝟸𝟷|𝟸𝟸|𝟸𝟹
 *  𝟸𝟺|𝟸𝟻|𝟸𝟼|𝟸𝟽|𝟸𝟾|𝟸𝟿|𝟹𝟶
 *  31|𝚡𝚡|𝚡𝚡|𝚡𝚡|𝚡𝚡|𝚡𝚡|𝚡𝚡    <- 29-35 position
 *  Сентябрь'20             <- 36 position
 *  𝚡𝚡|01|02|03|04|05|06    <- 37... position
 *
 * @see YearAdapterDayPositionCalculator
 */
internal class YearAdapter(var drawBackgroundUsualDay: Boolean = true) : RecyclerView.Adapter<RecyclerView.ViewHolder>(),
    AdapterNotifier {

    var selectedDates by delegatePropertyMT({ selector.selectedDates }, { selector.selectedDates = it })
    var selector: Selector = NoSelector()

    lateinit var selectedDayDrawable: SelectedDayDrawable

    private val _data = ConcurrentHashMap<LocalDate, Day>()
    var data: Map<LocalDate, Day> = _data
        set(value) {
            _data.putAll(value)
        }

    private var yearAdapterDayPositionCalculator = YearAdapterDayPositionCalculator(initDays = DAYS_COUNT)
    private val now = LocalDate.now()

    /**
     * Предзаполнение кэшей класса [YearAdapterDayPositionCalculator]
     */
    fun preCountSpan() {
        yearAdapterDayPositionCalculator.preCountSpan()
    }

    override fun getItemViewType(position: Int): Int {
        yearAdapterDayPositionCalculator.monthByPosition(position).let {
            return if (position == it.daysBefore) VIEW_TYPE_MONTH_NAME else VIEW_TYPE_DAY
        }
    }

    override fun getItemCount() = yearAdapterDayPositionCalculator.maxDays
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_MONTH_NAME) {
            MonthNameViewHolder(parent)
        } else {
            DayViewHolder(DayView(parent.context).apply {
                layoutParams = RecyclerView.LayoutParams(
                    ViewGroup.MarginLayoutParams.MATCH_PARENT,
                    resources.getDimensionPixelSize(R.dimen.calendar_year_month_month_height)
                )
                dateTextSize = resources.getDimension(R.dimen.calendar_year_picker_day_text_size)
            })
        }
    }

    /**
     * Задать начальную дату [firstDate] адаптера
     */
    @SuppressLint("NotifyDataSetChanged")
    fun setFirstDate(firstDate: LocalDate, lastDate: LocalDate?) {
        yearAdapterDayPositionCalculator = YearAdapterDayPositionCalculator(firstDate, lastDate = lastDate)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val (year, monthOfYear, dayOfMonth, daysCount) = yearAdapterDayPositionCalculator.monthByPosition(position)
        val month = LocalDate(year, monthOfYear, dayOfMonth)

        if (getItemViewType(position) == VIEW_TYPE_MONTH_NAME) {  // label
            holder as MonthNameViewHolder
            holder.bind(
                month.asMonthString(now)
                    .replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                    }
            )
        } else {
            holder as DayViewHolder
            holder.drawBackgroundUsualDay = drawBackgroundUsualDay
            val offsetDays = month.dayOfWeek - 1
            val currentDay = position - daysCount - offsetDays

            when {
                currentDay > month.dayOfMonth().maximumValue -> {
                    holder.clear(); return
                }
                position < offsetDays                        -> {
                    holder.clear(); return
                }
                currentDay <= 0                              -> {
                    holder.clear(); return
                }
            }

            val localDate = LocalDate(year, monthOfYear, currentDay)
            val model = data[localDate]
            when {
                model != null -> holder.bind(model, selector, selectedDayDrawable)
                else -> holder.bind(
                    Day(
                        date = localDate,
                        type = if (localDate.dayOfWeek > DateTimeConstants.FRIDAY) EventType.DAY_OFF else EventType.WORKDAY
                    ),
                    selector,
                    selectedDayDrawable
                )
            }
        }
    }

    /**
     * Индекс позиции по дате [localDate]
     */
    fun indexOfMonth(localDate: LocalDate): Int = yearAdapterDayPositionCalculator.indexOfMonth(localDate)

    /**
     * Индекс позиции по дате [localDate]
     */
    fun indexOfDay(localDate: LocalDate): Int = yearAdapterDayPositionCalculator.indexOfDay(localDate)

    /**
     * Месяц по позиции [position]
     */
    fun monthByPosition(position: Int) = yearAdapterDayPositionCalculator.monthByPosition(position)

    /**
     * Обновить данные [map], уведомить только видимые холдеры
     * Начальная видимая позиция [firstVisible] и последняя видимая позиция [lastVisible]
     * Возвращает набор данных "первая позиция данных в адаптере", "кол-во элементов дял изменения", "список пар (позиция ячейки, надо ли обновить ячейку)"
     */
    fun updateData(map: Map<LocalDate, Day>, firstVisible: Int, lastVisible: Int): NotifyList {
        fun isNotDefaultCell(it: Day): Boolean =
            it.isHoliday
                    || it.type != EventType.WORKDAY
                    || it.backgroundColor != "#EDF4FC"
                    || it.dayActivitiesPaintingList.isNotEmpty()
                    || it.isUnwantedVacDay

        val keys = map.keys.toTypedArray()
        val firstIndex = yearAdapterDayPositionCalculator.indexOfDay(keys.first())
        val lastIndex = yearAdapterDayPositionCalculator.indexOfDay(keys.last())
        if (keys.isNotEmpty() && (firstIndex in firstVisible..lastVisible || lastIndex in firstVisible..lastVisible || firstIndex < firstVisible && lastIndex > lastVisible)) {
            val list = map.map { entry -> Pair(yearAdapterDayPositionCalculator.indexOfDay(entry.key), isNotDefaultCell(entry.value) || data[entry.key]?.backgroundColor != entry.value.backgroundColor || data[entry.key]?.type != entry.value.type) }
            data = map
            return NotifyList(true, list)
        }
        data = map
        return NotifyList(false, null)
    }

    companion object {
        const val YEARS_THRESHOLD = 10
        internal const val DAYS_COUNT = 2 * YEARS_THRESHOLD * 365 // ~20 лет
        const val VIEW_TYPE_MONTH_NAME = 0
        const val VIEW_TYPE_DAY = 1
    }

}