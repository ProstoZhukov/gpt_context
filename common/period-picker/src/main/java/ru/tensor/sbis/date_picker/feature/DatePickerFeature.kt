package ru.tensor.sbis.date_picker.feature

import ru.tensor.sbis.date_picker.DatePickerDialogFragment
import ru.tensor.sbis.date_picker.DatePickerParams
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Фичи модуля выбора периода
 *
 * @author mb.kruglova
 */
interface DatePickerFeature : Feature {

    /**
     * Универсальный компонент выбора периода
     */
    fun createDatePickerDialogFragment(params: DatePickerParams): DatePickerDialogFragment
}