package ru.tensor.sbis.calendar.date.data

import android.graphics.Color
import android.os.Parcelable
import androidx.annotation.ColorInt
import kotlinx.parcelize.Parcelize
import org.joda.time.DateTimeConstants
import org.joda.time.LocalDate
import ru.tensor.sbis.design.theme.res.SbisColor

/** День  */
@Parcelize
data class Day(

    /** Дата  */
    val date: LocalDate,

    /** Тип дня  */
    val type: EventType = if (date.isWeekend()) EventType.DAY_OFF else EventType.WORKDAY,

    /** Список для покраски  */
    val dayActivitiesPaintingList: ArrayList<ActivityForPainting> = arrayListOf(),

    /** Количество событий за день в подключенных календарях  */
    val payloadEventsCount: Int = 0,

    /** Число отчётов, сдаваемых в данный день */
    val reportsCount: Int? = null,

    /** Является ли праздником  */
    val isHoliday: Boolean = false,

    /** Является ли днем рождения  */
    val isBirthday: Boolean = false,

    /** Уровень занятости  */
    val busyLevel: ArrayList<Boolean> = arrayListOf(),

    @Deprecated(
        "Будет удалено по https://dev.saby.ru/opendoc.html?guid=a56e6ad7-0bdb-4785-8458-1e9b92a9a148&client=3",
        ReplaceWith("sbisBackgroundColor")
    )
    /** Цвет фона дня. Если передать пустую строку, то будет выбираться по типу дня. */
    val backgroundColor: String = "",

    /** Цвет фона дня уже отпарсенный */
    @ColorInt
    @Deprecated(
        "https://dev.saby.ru/opendoc.html?guid=a56e6ad7-0bdb-4785-8458-1e9b92a9a148&client=3",
        ReplaceWith("sbisBackgroundColor")
    )
    val parseBackgroundColor: Int = Color.WHITE,

    /** Является ли день выходным */
    val isWorkday: Boolean = false,

    /** Нежелательный день для отпуска */
    var isUnwantedVacDay: Boolean = false,

    /** Кастомный цвет фона; если null, то цвет фона будет выбран на основе [type] */
    val sbisBackgroundColor: SbisColor? = null,
) : Parcelable {

    /** Является ли день пересчением отпуска и праздника*/
    val isVacationOnHoliday: Boolean
        get() = isHoliday && (
            type == EventType.FACT_VACATION
                || type == EventType.FACT_VACATION_WITHOUT_PAY
                || type == EventType.PLAN_VACATION
                || type == EventType.PLAN_VACATION_ON_AGREEMENT
                || type == EventType.PLAN_VACATION_ON_DELETION
                || type == EventType.FACT_VACATION_MOBILIZATION
            )
}

private fun LocalDate.isWeekend() = this.dayOfWeek in arrayOf(DateTimeConstants.SATURDAY, DateTimeConstants.SUNDAY)
