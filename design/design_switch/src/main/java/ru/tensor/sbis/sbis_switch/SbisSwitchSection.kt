package ru.tensor.sbis.sbis_switch

import androidx.annotation.StyleableRes

/**
 * Секции переключателя.
 *
 * @author mb.kruglova
 */
internal enum class SbisSwitchSection(
    @StyleableRes val checkedColor: Int,
    @StyleableRes val uncheckedColor: Int,
    @StyleableRes val disabledCheckedColor: Int,
    @StyleableRes val disabledUncheckedColor: Int
) {
    THUMB(
        R.styleable.SbisSwitchView_SbisSwitchView_thumbCheckedColor,
        R.styleable.SbisSwitchView_SbisSwitchView_thumbUncheckedColor,
        R.styleable.SbisSwitchView_SbisSwitchView_thumbDisabledCheckedColor,
        R.styleable.SbisSwitchView_SbisSwitchView_thumbDisabledUncheckedColor
    ),
    TRACK(
        R.styleable.SbisSwitchView_SbisSwitchView_trackCheckedColor,
        R.styleable.SbisSwitchView_SbisSwitchView_trackUncheckedColor,
        R.styleable.SbisSwitchView_SbisSwitchView_trackDisabledCheckedColor,
        R.styleable.SbisSwitchView_SbisSwitchView_trackDisabledUncheckedColor
    )
}