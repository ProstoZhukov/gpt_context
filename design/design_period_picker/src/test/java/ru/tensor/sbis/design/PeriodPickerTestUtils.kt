package ru.tensor.sbis.design

import android.content.res.Resources
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.testing.launchFragmentInContainer
import ru.tensor.sbis.design.period_picker.view.period_picker.big.PeriodPickerFragment
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.MonthModePeriodPickerFragment
import ru.tensor.sbis.design.period_picker.view.short_period_picker.ShortPeriodPickerFragment
import ru.tensor.sbis.design.period_picker.view.utils.mapMonthToStringResId

/** Найти View Большого выбора периода. */
internal fun findPeriodPickerView(fragment: Fragment): View {
    fragment.childFragmentManager.executePendingTransactions()
    val periodPickerFragment = fragment.childFragmentManager.fragments.first()
        .childFragmentManager.fragments.first() as PeriodPickerFragment
    return periodPickerFragment.requireView()
}

/** Найти View Компактного выбора периода. */
internal fun findCompactPeriodPickerView(fragment: Fragment): View {
    fragment.childFragmentManager.executePendingTransactions()
    val periodPickerFragment = fragment.childFragmentManager.fragments.first()
        .childFragmentManager.fragments.first() as MonthModePeriodPickerFragment
    return periodPickerFragment.requireView()
}

/** Найти View фрагмента Компактного выбора периода. */
internal fun findCompactPeriodPickerFragmentView(fragment: Fragment): View {
    fragment.childFragmentManager.executePendingTransactions()
    val periodPickerFragment = fragment.childFragmentManager.fragments.first() as MonthModePeriodPickerFragment
    return periodPickerFragment.requireView()
}

/** Найти View Быстрого выбора периода. */
internal fun findShortPeriodPickerView(fragment: Fragment): View {
    fragment.childFragmentManager.executePendingTransactions()
    val periodPickerFragment = fragment.childFragmentManager.fragments.first()
        .childFragmentManager.fragments.first() as ShortPeriodPickerFragment
    return periodPickerFragment.requireView()
}

/**
 * Запуск тестового фрагмента в контейнере.
 */
internal fun launchTestFragmentFragmentInContainer() =
    launchFragmentInContainer(themeResId = R.style.AppTheme) { PeriodPickerTestFragment() }

/** Получить заголовок месяца. */
internal fun Resources.getMonthLabel(month: Int): String = this.getString(mapMonthToStringResId(month))