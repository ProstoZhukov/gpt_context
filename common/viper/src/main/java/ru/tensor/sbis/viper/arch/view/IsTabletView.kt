package ru.tensor.sbis.viper.arch.view

import android.content.Context
import ru.tensor.sbis.common.util.DeviceConfigurationUtils

/**
 * Интерфейс для определения, работает приложение на планшете или на телефоне.
 *
 * @author ga.malinskiy
 */
interface IsTabletView {

    /**
     * Функция для определения, работает приложение на планшете или на телефоне.
     *
     * @return true - то планшет, иначе - телефон.
     */
    fun isTablet(context: Context): Boolean =
            DeviceConfigurationUtils.isTablet(context)
}