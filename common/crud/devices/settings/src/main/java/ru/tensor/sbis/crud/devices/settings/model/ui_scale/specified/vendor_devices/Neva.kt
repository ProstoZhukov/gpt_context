package ru.tensor.sbis.crud.devices.settings.model.ui_scale.specified.vendor_devices

import ru.tensor.sbis.crud.devices.settings.model.ui_scale.InterfaceScale
import ru.tensor.sbis.crud.devices.settings.model.ui_scale.InterfaceScale.L
import ru.tensor.sbis.crud.devices.settings.model.ui_scale.specified.SpecifiedDevices

/** Девайсы выпускаемые под брендом 'НЕВА'. */
internal sealed interface NEVA : SpecifiedDevices {

    /** Девайсы категории 'LARGE'. */
    sealed class LARGE(override val deviceNames: List<String>) : NEVA {

        /** Устройство-касса 'Нева-01-Ф' (Development/Release devices). */
        object F01 : LARGE(
            /* Информация взята из офф. ответа от "Интеграция ПО СБИС с Нева-01". */
            deviceNames = listOf(
                "NEVA-01-F-DEV",
                "NEVA-01-F",
                "NEVA-01-F-LTB",
                "TPS570Q"
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

            /*
             * На "Нева 01 Ф" должно быть доступно только 3 темы.
             *
             * Обсуждалось здесь:
             * https://online.sbis.ru/opendoc.html?guid=7f928b93-3503-470a-ae63-26d7876f3b5d&client=3
             */
            override fun isCompactDevice(): Boolean = true
        }
    }
}