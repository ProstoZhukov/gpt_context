package ru.tensor.sbis.design.period_picker.view.utils

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.container.SbisContainer
import ru.tensor.sbis.design.period_picker.R
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerDayCountersRepository
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerDayType
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerFeature.Companion.periodPickerRequestKey
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerFeature.Companion.periodPickerResultKey
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerRange
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerSelectionType
import ru.tensor.sbis.design.period_picker.view.listener.CalendarGlobalLayoutListener
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerMode
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerMode.*
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.delegate.CalendarRequestResultKeysDelegate
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.delegate.CalendarUpdateDelegate
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.MonthModePeriodPickerFragment
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.YearModePeriodPickerFragment
import java.util.Calendar

/**
 * Управлять сменой фрагментов для Большого выбора периода.
 *
 * Если фрагмент одного режима календаря показывается, то фрагмент другого режима скрывается.
 */
internal fun manageFragmentTransaction(
    fragmentManager: FragmentManager,
    mode: SbisPeriodPickerMode,
    startPeriod: Calendar?,
    endPeriod: Calendar?,
    startPresetPeriod: Calendar?,
    endPresetPeriod: Calendar?,
    isEnabled: Boolean,
    selectionType: SbisPeriodPickerSelectionType,
    displayedRange: SbisPeriodPickerRange,
    isBottomPosition: Boolean,
    anchorDate: Calendar?,
    requestKey: String,
    resultKey: String,
    dayCountersFactory: SbisPeriodPickerDayCountersRepository.Factory?
) {
    val fragment = fragmentManager.findFragmentByTag(mode.tag)
    if (fragment != null) {
        fragmentManager.beginTransaction().show(fragment).commit()
    } else {
        fragmentManager.beginTransaction().add(
            R.id.calendar_container,
            getModeFragment(
                mode.tag,
                startPeriod,
                endPeriod,
                startPresetPeriod,
                endPresetPeriod,
                isEnabled,
                selectionType,
                displayedRange,
                isBottomPosition,
                anchorDate,
                requestKey,
                resultKey,
                dayCountersFactory
            ),
            mode.tag
        ).commit()
    }
    val prevTag = if (mode == MONTH) YEAR else MONTH
    val prevFragment = fragmentManager.findFragmentByTag(prevTag.tag)
    if (prevFragment != null) {
        fragmentManager.beginTransaction().hide(prevFragment).commit()
    }
}

/** Привести фрагмент к типу CalendarUpdateDelegate, в противном случае вернуть null. */
internal fun Fragment.getCalendarUpdateDelegate(): CalendarUpdateDelegate? {
    return this as? CalendarUpdateDelegate
}

/** Найти фрагмент по тегу и привести фрагмент к типу CalendarUpdateDelegate, в противном случае вернуть null. */
internal fun Fragment.findCalendarUpdateDelegate(tag: String): CalendarUpdateDelegate? {
    val child = this.childFragmentManager.findFragmentByTag(tag)
    return child?.getCalendarUpdateDelegate()
}

/** Получить View радительского фрагмента, в противном случае, вернуть null. */
internal fun Fragment.getParentRootView(isSingle: Boolean) = getParentFragment(isSingle)?.view

/** Получить радительский фрагмент, в противном случае, вернуть null. */
internal fun Fragment.getParentFragment(isSingle: Boolean) =
    if (isSingle) this.parentFragment else this.parentFragment?.parentFragment

/** Проверить, является ли родительский фрагмент контейнером. */
internal fun Fragment.isSbisContainerParent(isSingle: Boolean) = this.getParentFragment(isSingle) is SbisContainer

/** Отправить результат выбора периода в вызывающий фрагмент. */
internal fun Fragment?.sendResult(
    range: SbisPeriodPickerRange,
    requestKey: String,
    resultKey: String
) {
    this?.setFragmentResult(requestKey, bundleOf(resultKey to range))
}

/** @SelfDocumented */
internal fun Fragment.getRequestKey() =
    (this as? CalendarRequestResultKeysDelegate)?.requestKey ?: periodPickerRequestKey

/** @SelfDocumented */
internal fun Fragment.getResultKey() =
    (this as? CalendarRequestResultKeysDelegate)?.resultKey ?: periodPickerResultKey

/** Настроить и вернуть CalendarGlobalLayoutListener. */
internal fun Fragment.setGlobalLayoutListener(
    isSingle: Boolean,
    calendar: RecyclerView,
    isBottomPosition: Boolean,
    handleAction: (Boolean) -> Unit
) = CalendarGlobalLayoutListener(this.getParentRootView(isSingle), calendar, isBottomPosition, handleAction)

/** Отписаться от CalendarGlobalLayoutListener после выполнения действия. */
internal fun RecyclerView.removeGlobalLayoutListener(
    listener: CalendarGlobalLayoutListener?,
    handleAction: () -> Boolean
) {
    if (handleAction()) {
        this.viewTreeObserver.removeOnGlobalLayoutListener(listener)
    }
}

/** Получить фрагмент режима для Большого выбора периода. */
private fun getModeFragment(
    tag: String,
    startPeriod: Calendar?,
    endPeriod: Calendar?,
    startPresetPeriod: Calendar?,
    endPresetPeriod: Calendar?,
    isEnabled: Boolean,
    selectionType: SbisPeriodPickerSelectionType,
    displayedRange: SbisPeriodPickerRange,
    isBottomPosition: Boolean,
    anchorDate: Calendar?,
    requestKey: String,
    resultKey: String,
    dayCountersFactory: SbisPeriodPickerDayCountersRepository.Factory?
): Fragment {
    return if (tag == MONTH.tag) {
        getMonthModeFragment(
            startPeriod,
            endPeriod,
            startPresetPeriod,
            endPresetPeriod,
            isEnabled,
            selectionType,
            displayedRange,
            isBottomPosition,
            anchorDate,
            requestKey,
            resultKey,
            dayCountersFactory
        )
    } else {
        getYearModeFragment(
            startPeriod,
            endPeriod,
            startPresetPeriod,
            endPresetPeriod,
            isEnabled,
            selectionType,
            displayedRange,
            isBottomPosition,
            anchorDate,
            requestKey,
            resultKey
        )
    }
}

/** Получить фрагмент режима Месяц. */
private fun getMonthModeFragment(
    startPeriod: Calendar?,
    endPeriod: Calendar?,
    startPresetPeriod: Calendar?,
    endPresetPeriod: Calendar?,
    isEnabled: Boolean,
    selectionType: SbisPeriodPickerSelectionType,
    displayedRange: SbisPeriodPickerRange,
    isBottomPosition: Boolean,
    anchorDate: Calendar?,
    requestKey: String,
    resultKey: String,
    dayCountersFactory: SbisPeriodPickerDayCountersRepository.Factory?
): MonthModePeriodPickerFragment {
    return MonthModePeriodPickerFragment.create(
        startValue = startPeriod,
        endValue = endPeriod,
        isEnabled = isEnabled,
        dayType = SbisPeriodPickerDayType.Simple,
        selectionType = selectionType,
        displayedRange = displayedRange,
        isCompact = false,
        isBottomPosition = isBottomPosition,
        presetStartValue = startPresetPeriod,
        presetEndValue = endPresetPeriod,
        anchorDate = anchorDate,
        requestKey = requestKey,
        resultKey = resultKey,
        factory = dayCountersFactory
    )
}

/** Получить фрагмент режима Год. */
private fun getYearModeFragment(
    startPeriod: Calendar?,
    endPeriod: Calendar?,
    startPresetPeriod: Calendar?,
    endPresetPeriod: Calendar?,
    isEnabled: Boolean,
    selectionType: SbisPeriodPickerSelectionType,
    displayedRange: SbisPeriodPickerRange,
    isBottomPosition: Boolean,
    anchorDate: Calendar?,
    requestKey: String,
    resultKey: String
): YearModePeriodPickerFragment {
    return YearModePeriodPickerFragment.create(
        startValue = startPeriod,
        endValue = endPeriod,
        isEnabled = isEnabled,
        selectionType = selectionType,
        displayedRange = displayedRange,
        isBottomPosition = isBottomPosition,
        presetStartValue = startPresetPeriod,
        presetEndValue = endPresetPeriod,
        anchorDate = anchorDate,
        requestKey = requestKey,
        resultKey = resultKey
    )
}