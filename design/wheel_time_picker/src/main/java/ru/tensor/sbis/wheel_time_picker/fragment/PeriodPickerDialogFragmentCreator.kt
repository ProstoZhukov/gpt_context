package ru.tensor.sbis.wheel_time_picker.fragment

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.design.container.ContentCreator
import ru.tensor.sbis.design.container.FragmentContent
import ru.tensor.sbis.wheel_time_picker.R
import ru.tensor.sbis.wheel_time_picker.data.DurationMode
import ru.tensor.sbis.wheel_time_picker.data.PeriodPickerMode
import ru.tensor.sbis.wheel_time_picker.data.TimePickerParameters

/**
 * Creator для диалога выбора времени.
 */
@Parcelize
internal class PeriodPickerDialogFragmentCreator(
    private val parameters: TimePickerParameters
) : ContentCreator<FragmentContent>, Parcelable {

    override fun createContent(): FragmentContent = PeriodPickerDialogContainer(parameters)
}