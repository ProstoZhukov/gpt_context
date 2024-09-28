package ru.tensor.sbis.crud.devices.settings.model.ui_scale.specified.vendor_devices

import ru.tensor.sbis.crud.devices.settings.model.ui_scale.InterfaceScale
import ru.tensor.sbis.crud.devices.settings.model.ui_scale.InterfaceScale.L
import ru.tensor.sbis.crud.devices.settings.model.ui_scale.InterfaceScale.M
import ru.tensor.sbis.crud.devices.settings.model.ui_scale.InterfaceScale.S
import ru.tensor.sbis.crud.devices.settings.model.ui_scale.specified.SpecifiedDevices

/** Девайсы выпускаемые под брендом 'Samsung'. */
internal sealed interface SAMSUNG : SpecifiedDevices {

    /** Девайсы категории 'LARGE'. */
    sealed class LARGE(override val deviceNames: List<String>) : SAMSUNG {

        /** Планшет 'Galaxy Tab A' (10.1 дюймов). */
        object GALAXY_TAB_A_10_1 : LARGE(listOf("SM-T515")) {
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

        /** Планшет 'Galaxy Tab A' (7.0 дюймов). */
        object GALAXY_TAB_A_7_0 : LARGE(listOf("SM-T285")) {
            /* Берем значение коэффициентов аналогично устройству - 'GALAXY_TAB_A_10_1'. */
            override fun getSpecifiedScaleFactor(
                currentThemeScale: InterfaceScale,
                currentThemeScaleValue: Float
            ): Float =
                when (currentThemeScale) {
                    S -> 1.3F
                    M -> 1.36F
                    L -> 1.42F
                    else -> currentThemeScaleValue
                }

            override fun isCompactDevice(): Boolean = false
        }
    }
}