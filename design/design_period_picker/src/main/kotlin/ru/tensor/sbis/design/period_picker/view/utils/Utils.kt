package ru.tensor.sbis.design.period_picker.view.utils

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.common.util.dateperiod.DatePeriod
import ru.tensor.sbis.common.util.dateperiod.PeriodType
import ru.tensor.sbis.design.container.R
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerMode
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerRange
import ru.tensor.sbis.design.period_picker.view.models.CalendarStorage
import ru.tensor.sbis.design.period_picker.view.models.SelectionType
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.theme.global_variables.Offset
import ru.tensor.sbis.design.theme.global_variables.TextColor
import ru.tensor.sbis.design.utils.KeyboardUtils
import ru.tensor.sbis.design.view.input.mask.date.DateInputView
import java.util.Calendar
import kotlin.math.min
import kotlin.math.roundToInt
import ru.tensor.sbis.design.R as RDesign
import ru.tensor.sbis.design.period_picker.R as RPeriodPicker

/**@SelfDocumented*/
internal fun getParamsFromArgs(arguments: Bundle?, key: String, clazz: Class<*>): Any? {
    return arguments?.let {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            it.getParcelable(key, clazz)
        } else {
            it.getParcelable(key)
        }
    }
}

/**
 * Получить позицию первого видимого элемента адаптера, если она отличается от текущей,
 * в противном случае вернуть null.
 */
internal fun getFirstVisibleItemPosition(
    layoutManager: LinearLayoutManager?,
    adapter: RecyclerView.Adapter<*>,
    currentPosition: Int
): Int? {
    val firstVisiblePos = layoutManager?.findFirstVisibleItemPosition()
    return if (
        firstVisiblePos != null &&
        firstVisiblePos > 0 &&
        currentPosition != firstVisiblePos &&
        firstVisiblePos in 0 until adapter.itemCount
    ) {
        firstVisiblePos
    } else {
        null
    }
}

/** Получить первый доступный для отображения период. */
internal fun getFirstDisplayedRange(displayedRanges: List<SbisPeriodPickerRange>?): SbisPeriodPickerRange {
    val dR = displayedRanges?.get(0) ?: SbisPeriodPickerRange()
    return SbisPeriodPickerRange(dR.startDate?.removeTime(), dR.endDate?.removeTime())
}

/** Настроить цвет текста для SbisTextView. */
internal fun SbisTextView.setSbisTextViewColor(
    color: TextColor,
    context: Context? = null
) {
    this.setTextColor(color.getValue(context ?: this.context))
}

/** Обновить значение для DateInputView. */
internal fun DateInputView.updateDate(newDate: Calendar?) {
    if (newDate == null) {
        this.value = ""
    } else {
        this.updateValue(newDate)
    }
}

internal fun SbisTextView.updateDate(newStartDate: Calendar?, newEndDate: Calendar?) {
    if (newStartDate == null || newEndDate == null) {
        text = context.resources.getString(RPeriodPicker.string.all_period)
        return
    }
    val periodType = DatePeriod(newStartDate, newEndDate).type

    val currYear = Calendar.getInstance().get(Calendar.YEAR)

    val isCurrYear = newStartDate.get(Calendar.YEAR) == currYear && newEndDate.get(Calendar.YEAR) == currYear

    text = when (periodType) {
        PeriodType.YEAR -> newStartDate.year.toString()
        PeriodType.HALF_YEAR -> formatHalfYearText(newStartDate, isCurrYear)
        PeriodType.QUARTER -> formatQuarterText(newStartDate, isCurrYear)
        PeriodType.MONTH -> formatMonthText(newStartDate, isCurrYear)
        PeriodType.DAY -> formatDayText(newStartDate, isCurrYear)
        else -> ""
    }
}

/** Скрыть клавиатуру. */
internal fun hideKeyboard(view: View) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        view.windowInsetsController?.hide(WindowInsetsCompat.Type.ime())
    } else {
        KeyboardUtils.hideKeyboard(view)
    }
}

/** Получить предварительно выбранный период. */
internal fun getPresetPeriod(start: Calendar?, newDate: Calendar): Pair<Calendar, Calendar?> {
    val startDate = start ?: newDate
    val endDate: Calendar? = if (start == null) null else newDate
    return startDate to endDate
}

/** Получить новый выбранный период. */
internal fun getNewPeriod(
    start: Calendar,
    end: Calendar?,
    handleEnd: ((Calendar) -> Calendar)? = null
): Pair<Calendar, Calendar?> {
    var startPeriod = start
    var endPeriod = end

    if (end != null) {
        val isReversedDate = start > end
        startPeriod = if (isReversedDate) end else start
        endPeriod = if (isReversedDate) start else end
        handleEnd?.let {
            val preEnd = endPeriod!!
            endPeriod = it.invoke(preEnd)
        }
    }

    return startPeriod to endPeriod
}

/**
 * Проверить, было ли выделение периода, и сбросить, если было.
 * Для случаев, когда период выбран полностью.
 */
internal fun checkAndResetPreviousSelection(
    currentSelectionType: SelectionType,
    previousSelectionType: SelectionType,
    startPeriod: Calendar?,
    endPeriod: Calendar?,
    calendarStorage: CalendarStorage
): Boolean {
    return if (currentSelectionType != previousSelectionType || (startPeriod != null && endPeriod != null)) {
        calendarStorage.deselectPeriod(startPeriod, endPeriod)
        true
    } else {
        false
    }
}

/**
 * Проверить, было ли выделение периода, и сбросить, если было.
 * Для случаев, когда период выбран не полностью в режиме Год (когда визуально есть выделение кванта).
 */
internal fun checkAndResetPreviousSelection(
    currentSelectionType: SelectionType,
    previousSelectionType: SelectionType,
    startPeriod: Calendar?,
    endPeriod: Calendar?,
    handleEndPeriod: (() -> Calendar?),
    calendarStorage: CalendarStorage
): Boolean {
    return if ((currentSelectionType != previousSelectionType || endPeriod != null) && startPeriod != null) {
        val endQuantum = endPeriod ?: handleEndPeriod()
        calendarStorage.deselectPeriod(startPeriod, endQuantum)
        true
    } else {
        false
    }
}

/** Проверить, закрыть ли выбор периода.. */
internal fun isClosePeriodPicker(isSingleClick: Boolean, endPeriod: Calendar?): Boolean {
    return isSingleClick || endPeriod != null
}

/** Получить период из двух дат. */
internal fun getPeriod(date1: Calendar?, date2: Calendar?): SbisPeriodPickerRange {
    val start: Calendar?
    val end: Calendar?
    if (date1 != null && date2 != null && date1 > date2) {
        start = date2
        end = date1
    } else {
        start = date1 ?: date2
        end = date2 ?: date1
    }

    return SbisPeriodPickerRange(start, end)
}

/**
 * Получить режим календаря в зависимости от выбранного периода.
 * Если выбранный период не кратен кванту, меньше или равен месяцу,
 * то открывается календарь в режиме Месяц.
 */
internal fun getCalendarMode(
    start: Calendar?,
    end: Calendar?,
    isOneDaySelection: Boolean,
    defaultMode: SbisPeriodPickerMode
) = when {
    isOneDaySelection || (
        start != null && end != null &&
            (
                (
                    start.year == end.year && start.month == end.month &&
                        end.dayOfMonth - start.dayOfMonth < end.lastDayOfMonth - 1
                    ) ||
                    start.dayOfMonth != 1 || end.dayOfMonth != end.lastDayOfMonth
                )
        )
    -> SbisPeriodPickerMode.MONTH

    start != null && end != null -> SbisPeriodPickerMode.YEAR
    else -> defaultMode
}

/** Обновить высоту контента в контейнере. */
internal fun View.updateContentLayoutParams(
    fraction: Double = heightFraction
) {
    this.apply {
        updateLayoutParams<ViewGroup.LayoutParams> {
            val offset =
                resources.getDimension(R.dimen.container_margin_top) + resources.getDimension(
                    R.dimen.container_margin_bottom
                ) + 2 * resources.getDimension(
                    R.dimen.container_content_padding
                )

            // Берем минимальное значение между необходимой высотой контента и доступной высотой контейнера.
            val contentHeight = min(
                (context.resources.displayMetrics.heightPixels * fraction).roundToInt(),
                context.resources.displayMetrics.heightPixels - offset.roundToInt()
            )
            height = contentHeight
            updatePadding(top = Offset.M.getDimenPx(context))
        }
    }
}

/** Обновить стратегию восстановления состояния адаптера. */
internal fun updateStateRestorationPolicy(adapter: RecyclerView.Adapter<*>) {
    adapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
}

private fun SbisTextView.formatDayText(newStartDate: Calendar, isCurrYear: Boolean) =
    "${newStartDate.get(Calendar.DAY_OF_MONTH)} ${
        context.resources.getStringArray(RDesign.array.design_months_names)[
            newStartDate.get(Calendar.MONTH)
        ].lowercase()
    }${getLast2DigitsIfNotCurrYear(newStartDate.year, isCurrYear)}"

private fun SbisTextView.formatMonthText(newStartDate: Calendar, isCurrYear: Boolean) =
    "${resources.getString(mapMonthToStringResId(newStartDate.month))}${
        getLast2DigitsIfNotCurrYear(newStartDate.year, isCurrYear)
    }"

private fun SbisTextView.formatQuarterText(newStartDate: Calendar, isCurrYear: Boolean) =
    "${
        when {
            newStartDate.month <= Calendar.MARCH -> "I"
            newStartDate.month in Calendar.APRIL..Calendar.JUNE -> "II"
            newStartDate.month in Calendar.JULY..Calendar.SEPTEMBER -> "III"
            else -> "IV"
        }
    } ${
        context.resources.getString(RPeriodPicker.string.quarter)
    }${getLast2DigitsIfNotCurrYear(newStartDate.year, isCurrYear)}"

private fun SbisTextView.formatHalfYearText(newStartDate: Calendar, isCurrYear: Boolean) =
    "${if (newStartDate.month <= Calendar.JUNE) "I" else "II"} " +
        context.resources.getString(RPeriodPicker.string.half_year) +
        getLast2DigitsIfNotCurrYear(newStartDate.year, isCurrYear)

private fun getLast2DigitsIfNotCurrYear(year: Int, isCurrYear: Boolean) =
    if (isCurrYear) "" else "'${year.toString().takeLast(2)}"