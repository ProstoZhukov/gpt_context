package ru.tensor.sbis.common.util

import android.content.Context
import androidx.annotation.BoolRes
import androidx.fragment.app.Fragment
import ru.tensor.sbis.common.R
import ru.tensor.sbis.common.util.DeviceConfiguration.PHONE_LANDSCAPE
import ru.tensor.sbis.common.util.DeviceConfiguration.PHONE_PORTRAIT
import ru.tensor.sbis.common.util.DeviceConfiguration.TABLET_LANDSCAPE
import ru.tensor.sbis.common.util.DeviceConfiguration.TABLET_PORTRAIT

/**
 * Предоставляяет сведения о том, соответствует ли текущая конфигурация устройства планшетному отображению и ландшафтной
 * ориентации.
 */
object DeviceConfigurationUtils {

    /**
     * Получить конфигурацию устройства [DeviceConfiguration].
     *
     * @param tabletResQualifier квалификатор определения планшета
     */
    @JvmStatic
    @JvmOverloads
    fun getDeviceConfiguration(
        context: Context,
        @BoolRes tabletResQualifier: Int = R.bool.is_tablet
    ): DeviceConfiguration {
        val isTablet = context.resources.getBoolean(tabletResQualifier)
        val isLandscape = context.resources.getBoolean(R.bool.is_landscape)
        return if (isTablet)
            if (isLandscape) TABLET_LANDSCAPE
            else TABLET_PORTRAIT
        else if (isLandscape) PHONE_LANDSCAPE
        else PHONE_PORTRAIT
    }

    /**
     * Соответствует ли текущая конфигурация устройства планшетному отображению.
     */
    @JvmStatic
    fun isTablet(context: Context): Boolean {
        return context.resources.getBoolean(R.bool.is_tablet)
    }

    /**
     * Соответствует ли текущая конфигурация устройства ландшафтной ориентации.
     */
    @JvmStatic
    fun isLandscape(context: Context): Boolean {
        val deviceConfiguration = getDeviceConfiguration(context)
        return deviceConfiguration == PHONE_LANDSCAPE || deviceConfiguration == TABLET_LANDSCAPE
    }
}

/**
 * Соответствует ли текущая конфигурация устройства планшетному отображению.
 */
val Fragment.isTablet get() = DeviceConfigurationUtils.isTablet(requireContext())