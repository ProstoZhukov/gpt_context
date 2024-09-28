package ru.tensor.sbis.crud.devices.settings.model.ui_scale.specified.vendor_devices

import ru.tensor.sbis.crud.devices.settings.model.ui_scale.InterfaceScale
import ru.tensor.sbis.crud.devices.settings.model.ui_scale.InterfaceScale.L
import ru.tensor.sbis.crud.devices.settings.model.ui_scale.specified.SpecifiedDevices

/** Девайсы выпускаемые под брендом 'DIGMA'. */
internal sealed interface DIGMA : SpecifiedDevices {

    /** Девайсы категории 'LARGE'. */
    sealed class LARGE(override val deviceNames: List<String>) : DIGMA {

        /** Планшет 'Digma Citi 10'. */
        object CITI_10 : LARGE(
            deviceNames = listOf(
                "CITI 10 E402 4G CS1235PL",
                "CS1235PL",
                "CITI 10"
            )
        ) {

            override fun getSpecifiedScaleFactor(
                currentThemeScale: InterfaceScale,
                currentThemeScaleValue: Float
            ): Float =
                when (currentThemeScale) {
                    L -> 1.76F
                    else -> currentThemeScaleValue
                }

            override fun isCompactDevice(): Boolean = false
        }
    }
}