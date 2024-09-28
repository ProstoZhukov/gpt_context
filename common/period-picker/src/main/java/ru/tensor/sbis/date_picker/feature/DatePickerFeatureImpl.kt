package ru.tensor.sbis.date_picker.feature

import ru.tensor.sbis.date_picker.DatePickerDialogFragment
import ru.tensor.sbis.date_picker.DatePickerParams

/**
 * Реализация фичи модуля выбора периода
 *
 * @author mb.kruglova
 */
internal class DatePickerFeatureImpl : DatePickerFeature {

    override fun createDatePickerDialogFragment(params: DatePickerParams): DatePickerDialogFragment {
        return DatePickerDialogFragment.newInstance(params)
    }
}