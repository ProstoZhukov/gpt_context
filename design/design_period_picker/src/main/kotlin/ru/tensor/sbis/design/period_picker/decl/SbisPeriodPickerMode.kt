package ru.tensor.sbis.design.period_picker.decl

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Режим календаря.
 *
 * @author mb.kruglova
 */
@Parcelize
enum class SbisPeriodPickerMode(val tag: String) : Parcelable {
    MONTH("monthModeTag"),
    YEAR("yearModeTag");

    companion object {
        /**
         * Получить режим календаря по значению toggle-кнопки.
         */
        internal fun getMode(isToggleButtonChecked: Boolean): SbisPeriodPickerMode =
            if (isToggleButtonChecked) MONTH else YEAR

        /**
         * Получить противоположный режим календаря по значению тега.
         */
        internal fun getOppositeMode(tag: String): SbisPeriodPickerMode = if (tag == MONTH.tag) YEAR else MONTH
    }
}