package ru.tensor.sbis.crud.devices.settings.model.ui_scale.specified.vendor_devices

import ru.tensor.sbis.crud.devices.settings.model.ui_scale.InterfaceScale
import ru.tensor.sbis.crud.devices.settings.model.ui_scale.InterfaceScale.L
import ru.tensor.sbis.crud.devices.settings.model.ui_scale.InterfaceScale.M
import ru.tensor.sbis.crud.devices.settings.model.ui_scale.InterfaceScale.S
import ru.tensor.sbis.crud.devices.settings.model.ui_scale.InterfaceScale.XS
import ru.tensor.sbis.crud.devices.settings.model.ui_scale.InterfaceScale.XXS
import ru.tensor.sbis.crud.devices.settings.model.ui_scale.specified.SpecifiedDevices

/** Девайсы выпускаемые под брендом 'ATOL'. */
internal sealed interface ATOL : SpecifiedDevices {

    /** Девайсы категории 'NORMAL'. */
    sealed class NORMAL(override val deviceNames: List<String>) : ATOL {

        /** Устройство-касса 'ATOL STRIKE'. */
        object STRIKE : NORMAL(listOf("QUAD-CORE A64 p3")) {
            override fun getSpecifiedScaleFactor(
                currentThemeScale: InterfaceScale,
                currentThemeScaleValue: Float
            ): Float =
                when (currentThemeScale) {
                    XXS -> 0.9F
                    XS -> 1F
                    S, M, L -> 1.08F
                    else -> currentThemeScaleValue
                }

            override fun isCompactDevice(): Boolean = true
        }

        /** Устройство-касса 'Атол СТБ 5'. */
        object STB_5 : NORMAL(listOf("PT-5F")) {
            override fun getSpecifiedScaleFactor(
                currentThemeScale: InterfaceScale,
                currentThemeScaleValue: Float
            ): Float = when (currentThemeScale) {
                XXS -> 0.9F
                XS -> 1F
                S, M, L -> 1.05F
                else -> currentThemeScaleValue
            }

            override fun isCompactDevice(): Boolean = true
        }
    }

    /** Девайсы категории 'XLARGE'. */
    sealed class XLARGE(override val deviceNames: List<String>) : ATOL {

        /** Устройство-касса 'ATOL 150Ф' (10.1 дюймов). */
        object F150_10_1 : XLARGE(listOf("sigma10wl")) {
            override fun getSpecifiedScaleFactor(
                currentThemeScale: InterfaceScale,
                currentThemeScaleValue: Float
            ): Float =
                when (currentThemeScale) {
                    L -> 1.7F
                    else -> currentThemeScaleValue
                }

            override fun isCompactDevice(): Boolean = false
        }

        /** Устройство-касса 'ATOL 150Ф' (8.0 дюймов). */
        object F150_8_0 : XLARGE(listOf("sigma8wl")) {
            /* Берем значение коэффициентов аналогично устройству - 'F150_10_1'. */
            override fun getSpecifiedScaleFactor(
                currentThemeScale: InterfaceScale,
                currentThemeScaleValue: Float
            ): Float =
                when (currentThemeScale) {
                    L -> 1.72F
                    else -> currentThemeScaleValue
                }

            override fun isCompactDevice(): Boolean = false
        }
    }
}