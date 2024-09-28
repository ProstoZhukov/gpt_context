package ru.tensor.sbis.crud.devices.settings.model.ui_scale.specified.vendor_devices

import ru.tensor.sbis.crud.devices.settings.model.ui_scale.InterfaceScale
import ru.tensor.sbis.crud.devices.settings.model.ui_scale.InterfaceScale.L
import ru.tensor.sbis.crud.devices.settings.model.ui_scale.InterfaceScale.M
import ru.tensor.sbis.crud.devices.settings.model.ui_scale.InterfaceScale.S
import ru.tensor.sbis.crud.devices.settings.model.ui_scale.InterfaceScale.XS
import ru.tensor.sbis.crud.devices.settings.model.ui_scale.InterfaceScale.XXS
import ru.tensor.sbis.crud.devices.settings.model.ui_scale.specified.SpecifiedDevices

/** Девайсы выпускаемые под брендом 'AZUR'. */
internal sealed interface AZUR : SpecifiedDevices {

    /** Девайсы категории 'NORMAL'. */
    sealed class NORMAL(override val deviceNames: List<String>) : AZUR {

        /** Устройство-касса 'Азур 01Ф'. */
        object F01 : NORMAL(listOf("KS8223")) {

            override fun getSpecifiedScaleFactor(
                currentThemeScale: InterfaceScale,
                currentThemeScaleValue: Float
            ): Float =
                when (currentThemeScale) {
                    XXS -> 0.9F
                    XS -> 1F
                    S, M, L -> 1.10F
                    else -> currentThemeScaleValue
                }

            override fun isCompactDevice(): Boolean = true
        }
    }
}