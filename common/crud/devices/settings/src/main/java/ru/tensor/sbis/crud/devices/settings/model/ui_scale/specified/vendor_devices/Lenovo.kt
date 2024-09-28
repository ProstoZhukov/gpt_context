package ru.tensor.sbis.crud.devices.settings.model.ui_scale.specified.vendor_devices

import ru.tensor.sbis.crud.devices.settings.model.ui_scale.InterfaceScale
import ru.tensor.sbis.crud.devices.settings.model.ui_scale.InterfaceScale.S
import ru.tensor.sbis.crud.devices.settings.model.ui_scale.specified.SpecifiedDevices

/** Девайсы выпускаемые под брендом 'LENOVO'. */
internal sealed interface LENOVO : SpecifiedDevices {

    /** Девайсы категории 'LARGE'. */
    sealed class LARGE(override val deviceNames: List<String>) : LENOVO {

        /** Планшет 'Lenovo TB-8504F'. */
        object TB_8504F : LARGE(listOf("Lenovo TB-8504F")) {
            override fun getSpecifiedScaleFactor(
                currentThemeScale: InterfaceScale,
                currentThemeScaleValue: Float
            ): Float =
                when (currentThemeScale) {
                    S -> 1.32F
                    else -> currentThemeScaleValue
                }

            /* Не меняем стандартное масштабирование, но оставляем только 3 темы оформления. */
            override fun isCompactDevice(): Boolean = true
        }
    }
}