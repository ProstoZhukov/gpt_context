package ru.tensor.sbis.design.period_picker.decl

import com.arkivanov.essenty.parcelable.Parcelable
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.design.theme.res.SbisColor

/**
 * ---
 *
 * @author da.zolotarev
 */
@Parcelize
data class SbisPeriodPickerDayCustomTheme(
    val backgroundColor: SbisColor? = null,
    val dayOfWeekColor: SbisColor? = null
) : Parcelable