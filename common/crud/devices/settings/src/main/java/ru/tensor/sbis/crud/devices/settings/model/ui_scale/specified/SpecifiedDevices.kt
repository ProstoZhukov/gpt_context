package ru.tensor.sbis.crud.devices.settings.model.ui_scale.specified

import android.os.Build
import ru.tensor.sbis.crud.devices.settings.model.ui_scale.InterfaceScale
import ru.tensor.sbis.crud.devices.settings.model.ui_scale.specified.vendor_devices.ATOL
import ru.tensor.sbis.crud.devices.settings.model.ui_scale.specified.vendor_devices.AZUR
import ru.tensor.sbis.crud.devices.settings.model.ui_scale.specified.vendor_devices.DIGMA
import ru.tensor.sbis.crud.devices.settings.model.ui_scale.specified.vendor_devices.LENOVO
import ru.tensor.sbis.crud.devices.settings.model.ui_scale.specified.vendor_devices.MSPOS
import ru.tensor.sbis.crud.devices.settings.model.ui_scale.specified.vendor_devices.NEVA
import ru.tensor.sbis.crud.devices.settings.model.ui_scale.specified.vendor_devices.SAMSUNG

/**
 * Девайсы со специфичным масштабированием интерфейса.
 *
 * Важно! Т.к. мы не используем рефлексию, то после добавления очередного девайса
 * в таблицу, не забудьте указать его в списке 'supportedSpecialDevices'.
 *
 * Приложение #1. "Реальные параметры дисплеев розничных девайсов".
 * https://online.sbis.ru/shared/disk/d3e86f9e-ddcc-4089-88bf-f91db971f135
 */
internal interface SpecifiedDevices : SpecifiedScaleFactor {

    /**
     * Название устройства, зашитое производителем в [Build.MODEL].
     *
     * UPD #1: выяснилось, что производитель может выпускать одно и
     * то же устройство с разными [Build.MODEL].
     */
    val deviceNames: List<String>

    companion object {

        /* Намеренно не используем SpecifiedDevices::class.sealedSubclasses, т.к. требует подключения рефлексии. */
        private val supportedSpecialDevices: List<SpecifiedDevices>
            get() = listOf(
                /* Atol. */
                ATOL.NORMAL.STRIKE,
                ATOL.NORMAL.STB_5,
                ATOL.XLARGE.F150_8_0,
                ATOL.XLARGE.F150_10_1,

                /* Azur. */
                AZUR.NORMAL.F01,

                /* Neva. */
                NEVA.LARGE.F01,

                /* Lenovo. */
                LENOVO.LARGE.TB_8504F,

                /* Digma. */
                DIGMA.LARGE.CITI_10,

                /* Samsung. */
                SAMSUNG.LARGE.GALAXY_TAB_A_10_1,
                SAMSUNG.LARGE.GALAXY_TAB_A_7_0,

                /* MSPOS. */
                MSPOS.XLARGE.MSPOS_T_F
            )

        /**
         * Получение специфичных настроек масштабирования интерфейса, если устройство входит в указанный список.
         * Передаем [defaultScaleFactorValue] для случаев, когда переопределить масштабирование нужно только в
         * конкретных темах.
         */
        fun InterfaceScale.getSpecifiedScaleFactorOrDefault(defaultScaleFactorValue: Float): Float =
            supportedSpecialDevices.firstOrNull { it.deviceNames.any { deviceName -> deviceName == Build.MODEL } }
                ?.getSpecifiedScaleFactor(currentThemeScale = this, currentThemeScaleValue = defaultScaleFactorValue)
                ?: defaultScaleFactorValue

        /**
         * Если возвращает true, необходимо применить настройки для "компактных" устройств.
         * Для таких устройств уменьшены коэффициенты масштабирования и урезана линейка масштабов.
         */
        fun useAsCompactDevices(): Boolean =
            supportedSpecialDevices.firstOrNull {
                it.deviceNames.any { deviceName -> deviceName == Build.MODEL }
            }?.isCompactDevice() ?: false
    }
}