package ru.tensor.sbis.wheel_time_picker.fragment

import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.design_dialogs.dialogs.content.ContentCreatorParcelable
import ru.tensor.sbis.wheel_time_picker.data.TimePickerParameters

/**
 * Выполняет создание [PeriodPickerDialogContent] для использования в шторке.
 *
 * @author us.bessonov
 */
@Parcelize
internal class PeriodPickerMovablePanelContentCreator(
    private val parameters: TimePickerParameters
) : ContentCreatorParcelable {

    override fun createFragment() = PeriodPickerDialogContent.newInstance(parameters)
}