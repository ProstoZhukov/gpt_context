package ru.tensor.sbis.design.period_picker.view.short_period_picker.adapter.utils

import android.content.res.Resources
import android.util.DisplayMetrics
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.isVisible
import ru.tensor.sbis.design.period_picker.R
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerRange
import ru.tensor.sbis.design.period_picker.decl.SbisShortPeriodPickerVisualParams
import ru.tensor.sbis.design.period_picker.view.short_period_picker.models.PeriodPickerSelectionParams
import ru.tensor.sbis.design.period_picker.view.short_period_picker.models.PeriodPickerParams
import ru.tensor.sbis.design.period_picker.view.models.SelectionType
import ru.tensor.sbis.design.period_picker.view.short_period_picker.adapter.ShortPeriodPickerItem
import ru.tensor.sbis.design.period_picker.view.utils.checkRangeBelonging
import ru.tensor.sbis.design.period_picker.view.utils.firstDay
import ru.tensor.sbis.design.period_picker.view.utils.halfYearStep
import ru.tensor.sbis.design.period_picker.view.utils.quarterStep
import ru.tensor.sbis.design.period_picker.view.utils.yearStep
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.theme.global_variables.Offset
import ru.tensor.sbis.design.theme.global_variables.TextColor
import java.util.Calendar
import java.util.GregorianCalendar
import kotlin.math.roundToInt

/**
 * Получить параметры для выделения периода.
 */
internal fun getShortPeriodPickerSelection(startDate: Calendar, endDate: Calendar): PeriodPickerParams {
    // день игнорируется
    val sYear = startDate.get(Calendar.YEAR)
    val sMonth = startDate.get(Calendar.MONTH)
    val eYear = endDate.get(Calendar.YEAR)
    val eMonth = endDate.get(Calendar.MONTH)

    return when {
        // Если год начального и конечного значений периода совпадают,
        // разница между номерами месяцов равна 2,
        // и номер месяца начала периода кратен 3,
        // то выделяем квартал
        sYear == eYear && eMonth - sMonth == quarterStep && sMonth % 3 == 0 -> {
            PeriodPickerParams(startDate, SelectionType.QUARTER)
        }
        // Если год начального и конечного значений периода совпадают,
        // разница между номерами месяцов равна 5,
        // и номер месяца начала периода кратен 6,
        // то выделяем полугодие
        sYear == eYear && eMonth - sMonth == halfYearStep && sMonth % 6 == 0 -> {
            PeriodPickerParams(startDate, SelectionType.HALF_YEAR)
        }
        // Если год начального и конечного значений периода совпадают,
        // разница между номерами месяцов равна 11,
        // и номер месяца начала периода кратен 12,
        // то выделяем год
        sYear == eYear && eMonth - sMonth == yearStep && sMonth % 12 == 0 -> {
            PeriodPickerParams(startDate, SelectionType.YEAR)
        }
        // Во всех остальных случая (в том числе если значения для начального и конечного значений периода некорректны)
        // выделяем месяц
        else -> {
            PeriodPickerParams(startDate, SelectionType.MONTH)
        }
    }
}

/**@SelfDocumented*/
internal fun convertDpToPixel(dp: Int): Int {
    val metrics: DisplayMetrics = Resources.getSystem().displayMetrics
    val px = dp * (metrics.densityDpi / 160f)
    return px.roundToInt()
}

/**
 * Настроить слушателя для выбора кванта.
 */
internal fun View.setQuantumListener(params: PeriodPickerSelectionParams) {
    this.setOnClickListener {
        val dateStart: Calendar = GregorianCalendar(params.item.year, params.startMonth, firstDay)
        val dateEnd: Calendar = GregorianCalendar(params.item.year, params.endMonth, firstDay)
        val maxDayOfMonth = dateEnd.getActualMaximum(Calendar.DAY_OF_MONTH)
        dateEnd.set(Calendar.DAY_OF_MONTH, maxDayOfMonth)
        params.listener(params.item, params.position, SbisPeriodPickerRange(dateStart, dateEnd))
        updateSelection(
            params.selectionView,
            params.rootContainerLayout,
            this.id,
            params.horizontalViewId,
            params.isParentConstraint
        )
    }
}

/**
 * Обновить выделение периода.
 */
internal fun updateSelection(
    selectionView: View,
    rootContainer: ConstraintLayout,
    viewIdTop: Int,
    viewIdHorizontal: Int,
    isParentConstraint: Boolean = false,
    viewIdBottom: Int? = null
) {
    if (!selectionView.isVisible) selectionView.visibility = View.VISIBLE

    val constraintSet = ConstraintSet()
    constraintSet.clone(rootContainer)
    constraintSet.connect(
        R.id.selection_view,
        ConstraintSet.TOP,
        viewIdTop,
        ConstraintSet.TOP,
        Offset.X2S.getDimenPx(selectionView.context)
    )
    constraintSet.connect(
        R.id.selection_view,
        ConstraintSet.BOTTOM,
        viewIdBottom ?: viewIdTop,
        ConstraintSet.BOTTOM,
        0
    )
    constraintSet.connect(
        R.id.selection_view,
        ConstraintSet.END,
        viewIdHorizontal,
        if (isParentConstraint) ConstraintSet.END else ConstraintSet.START,
        if (isParentConstraint) Offset.X2S.getDimenPx(selectionView.context) else 0
    )

    constraintSet.applyTo(rootContainer)
}

/**
 * Выделить год.
 */
internal fun selectYear(selectionView: View, rootContainer: ConstraintLayout, startViewId: Int, endViewId: Int) =
    updateSelection(selectionView, rootContainer, startViewId, rootContainer.id, true, endViewId)

/**
 * Обновить квант согласно его принадлежности к отображаемому диапазону.
 */
internal fun updateQuantum(
    limit: SbisPeriodPickerRange,
    month: Int,
    year: Int,
    step: Int,
    handleTextView: (() -> SbisTextView)
) {
    val checked = checkRangeBelonging(limit, month, year, step)
    if (!checked) {
        handleTextView().apply {
            isEnabled = false
            setOnClickListener(null)
            setTextColor(TextColor.READ_ONLY.getValue(this.context))
        }
    }
}

/**
 * Получить список полей для адаптера.
 */
internal fun getPeriodPickerItems(
    visualParams: SbisShortPeriodPickerVisualParams,
    startCalendarYear: Int,
    endCalendarYear: Int
): ArrayList<ShortPeriodPickerItem> {
    val list = arrayListOf<ShortPeriodPickerItem>()
    list.addItems(visualParams, startCalendarYear, endCalendarYear)
    return list
}

/**@SelfDocumented*/
private fun ArrayList<ShortPeriodPickerItem>.addItems(
    visualParams: SbisShortPeriodPickerVisualParams,
    startCalendarYear: Int,
    endCalendarYear: Int
) {
    for (year in startCalendarYear..endCalendarYear) {
        if (
            visualParams.chooseYears ||
            visualParams.chooseMonths ||
            visualParams.chooseQuarters ||
            visualParams.chooseHalfYears
        ) {
            this.add(ShortPeriodPickerItem.YearItem(year, visualParams.chooseYears, !visualParams.isYearMode()))
        }

        when {
            visualParams.chooseMonths -> this.add(
                ShortPeriodPickerItem.MonthItem(
                    year,
                    visualParams.chooseQuarters,
                    visualParams.chooseHalfYears
                )
            )

            visualParams.chooseQuarters -> this.add(
                ShortPeriodPickerItem.QuarterItem(
                    year,
                    visualParams.chooseHalfYears
                )
            )

            visualParams.chooseHalfYears -> this.add(
                ShortPeriodPickerItem.HalfYearItem(year)
            )

            visualParams.chooseYears -> Unit
        }
    }
}