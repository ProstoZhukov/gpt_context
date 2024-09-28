package ru.tensor.sbis.crud.devices.settings.model.ui_scale.specified.vendor_devices

import ru.tensor.sbis.crud.devices.settings.model.ui_scale.InterfaceScale
import ru.tensor.sbis.crud.devices.settings.model.ui_scale.InterfaceScale.L
import ru.tensor.sbis.crud.devices.settings.model.ui_scale.specified.SpecifiedDevices

/** Девайсы выпускаемые под брендом 'MSPOS'. */
internal sealed interface MSPOS : SpecifiedDevices {

    /** Девайсы категории 'XLARGE'. */
    sealed class XLARGE(override val deviceNames: List<String>) : MSPOS {

        /** Устройство-касса 'MSPOS-Т-Ф'. */
        object MSPOS_T_F : XLARGE(listOf("T1mini-G", "D2mini")) {

            override fun getSpecifiedScaleFactor(
                currentThemeScale: InterfaceScale,
                currentThemeScaleValue: Float
            ): Float =
                when (currentThemeScale) {
                    L -> 1.78F
                    else -> currentThemeScaleValue
                }

            override fun isCompactDevice(): Boolean = false
        }
    }
}