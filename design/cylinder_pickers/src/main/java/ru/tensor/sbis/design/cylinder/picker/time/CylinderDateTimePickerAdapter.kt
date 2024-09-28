package ru.tensor.sbis.design.cylinder.picker.time

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.joda.time.*
import org.joda.time.format.DateTimeFormat
import ru.tensor.sbis.design.cylinder.picker.R
import ru.tensor.sbis.design.cylinder.picker.cylinder.CylinderViewHolder
import ru.tensor.sbis.design.theme.global_variables.TextColor
import ru.tensor.sbis.design.utils.LocaleUtils

/**
 * @author Subbotenko Dmitry
 */
internal class CylinderDateTimePickerAdapter(
    private val viewType: CylinderViewType,
    private val minutesStep: Int = 5
) : RecyclerView.Adapter<CylinderViewHolder>() {

    private var fmtShort = DateTimeFormat.forPattern("EE dd MMM").withLocale(LocaleUtils.getDefaultLocale)!!
    private var fmtLong = DateTimeFormat.forPattern("EE dd MMMM").withLocale(LocaleUtils.getDefaultLocale)!!

    private val LocalDate.daysFromEra get() = Period(LocalDate(0), this, PeriodType.days()).days

    // стартовая позиция не меняется и всегда указывает на initDate для барабана День
    // для барабана час и минуты указывает на 0
    val startPosition: Int = Int.MAX_VALUE / 2

    /** Отображать полночь как 24 часа */
    var showMidnightAs24: Boolean = false

    var startPeriod: ReadablePeriod = when (viewType) {
        CylinderViewType.DAY -> Days.days(0)
        CylinderViewType.HOUR -> Hours.hours(0)
        CylinderViewType.MINUTE -> Minutes.minutes(0)
    }

    var initDate = LocalDateTime()

    fun getDateForPosition(position: Int): ReadablePeriod {
        return when (viewType) {
            CylinderViewType.DAY -> Days.days(position - startPosition)
            CylinderViewType.HOUR -> Hours.hours((24 + (position - startPosition) % 24) % 24)
            CylinderViewType.MINUTE -> Minutes.minutes((60 + ((position - startPosition) * minutesStep) % 60) % 60)
        }
    }

    fun getPositionForDate(date: LocalDateTime): Int =
        when (viewType) {
            CylinderViewType.DAY -> date.toLocalDate().daysFromEra - initDate.toLocalDate().daysFromEra
            CylinderViewType.HOUR -> date.hourOfDay
            CylinderViewType.MINUTE -> date.minuteOfHour / minutesStep
        } + startPosition

    override fun getItemCount(): Int = Int.MAX_VALUE

    override fun onCreateViewHolder(parent: ViewGroup, type: Int): CylinderViewHolder =
        CylinderViewHolder(parent)

    override fun onBindViewHolder(holder: CylinderViewHolder, position: Int) {
        val elementTextColor =
            (if (showMidnightAs24) TextColor.READ_ONLY else TextColor.DEFAULT).getValue(holder.itemView.context)
        val dateForPosition = getDateForPosition(position)
        val showDate = initDate.withHourOfDay(0).withMinuteOfHour(0).plus(dateForPosition)

        holder.bind(
            when (viewType) {
                CylinderViewType.DAY -> {
                    if (showDate.toLocalDate().daysFromEra == LocalDate().daysFromEra)
                        holder.itemView.resources.getString(R.string.cylinder_picker_now_string)
                    else
                        formatDate(showDate.toLocalDate().monthOfYear).print(showDate)
                }
                CylinderViewType.HOUR -> {
                    if (showMidnightAs24 && showDate.hourOfDay == 0 && showDate.minuteOfHour == 0)
                        24.toString()
                    else
                        addZeroIfNeed(showDate.hourOfDay)
                }
                CylinderViewType.MINUTE -> addZeroIfNeed(showDate.minuteOfHour)
            },
            elementTextColor
        )
    }

    private fun formatDate(monthNumber: Int) =
        when (monthNumber) {
            3, 6, 7 -> fmtLong
            else -> fmtShort
        }

    private fun addZeroIfNeed(number: Int): String =
        if (number < 10) {
            StringBuilder().append("0").append(number.toString()).toString()
        } else {
            number.toString()
        }
}