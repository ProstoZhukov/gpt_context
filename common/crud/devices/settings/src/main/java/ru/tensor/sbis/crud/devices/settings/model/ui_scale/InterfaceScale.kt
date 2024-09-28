package ru.tensor.sbis.crud.devices.settings.model.ui_scale

import android.content.Context
import android.content.res.Configuration
import ru.tensor.sbis.common.util.DeviceConfigurationUtils
import ru.tensor.sbis.crud.devices.settings.model.ui_scale.specified.SpecifiedDevices
import timber.log.Timber
import android.content.res.Configuration.SCREENLAYOUT_SIZE_LARGE as LARGE
import android.content.res.Configuration.SCREENLAYOUT_SIZE_NORMAL as NORMAL
import android.content.res.Configuration.SCREENLAYOUT_SIZE_SMALL as SMALL
import android.content.res.Configuration.SCREENLAYOUT_SIZE_XLARGE as XLARGE

/**
 * Перечисление размеров темы:
 * - 2XS=0.55(1 - без масштабирования)
 * - XS=0.65(1.18)
 * - S=0.75(1.36)
 * - M=0.85(1.54)
 * - L=1(1.82)
 *
 * https://online.sbis.ru/shared/disk/568846c1-6b4f-43af-8a28-4ee1ba8ba305
 *
 * Для разработки базовым размером(размером по умолчанию) устанавливается 2XS, коэффициент масштабирование 1(без масштабирования).
 * Платформенные компоненты создаются на 2XS.
 * Для проектирования базовым коэффициентом является L,
 * т.е. интерфейс проектируется с учетом максимальных размеров компонентов и масштабируется в меньшую сторону
 */
enum class InterfaceScale {
    XXS {
        override fun getScaleFactor(context: Context): Float =
            applySpecifiedScaleForDeviceOrDefault(
                defaultScaleFactorValue = when (getSizeType(context)) {
                    SMALL, NORMAL, LARGE -> 1F
                    XLARGE -> 1.1F
                    else -> undefinedScreenSize(1F)
                }
            )

        override fun getOldScaleFactor(): Float = 0.7F
    },
    XS {
        override fun getScaleFactor(context: Context): Float =
            applySpecifiedScaleForDeviceOrDefault(
                defaultScaleFactorValue = when (getSizeType(context)) {
                    SMALL, NORMAL -> 1.1F
                    LARGE -> 1.18F
                    XLARGE -> 1.2F
                    else -> undefinedScreenSize(1.18F)
                }
            )

        override fun getOldScaleFactor(): Float = 0.85F
    },
    S {
        override fun getScaleFactor(context: Context): Float =
            applySpecifiedScaleForDeviceOrDefault(
                defaultScaleFactorValue = when (getSizeType(context)) {
                    SMALL, NORMAL -> 1.15F
                    LARGE -> 1.36F
                    XLARGE -> 1.4F
                    else -> undefinedScreenSize(1.36F)
                }
            )

        override fun getOldScaleFactor(): Float = 1.0F
    },
    M {
        override fun getScaleFactor(context: Context): Float =
            applySpecifiedScaleForDeviceOrDefault(
                defaultScaleFactorValue = when (getSizeType(context)) {
                    SMALL, NORMAL -> 1.15F
                    LARGE -> 1.54F
                    XLARGE -> 1.6F
                    else -> undefinedScreenSize(1.54F)
                }
            )

        override fun getOldScaleFactor(): Float = 1.15F
    },
    L {
        override fun getScaleFactor(context: Context): Float =
            applySpecifiedScaleForDeviceOrDefault(
                defaultScaleFactorValue = when (getSizeType(context)) {
                    SMALL, NORMAL -> 1.15F
                    LARGE -> 1.82F
                    XLARGE -> 1.9F
                    else -> undefinedScreenSize(1.82F)
                }
            )

        override fun getOldScaleFactor(): Float = 1.3F
    },
    CUSTOM {
        override fun getScaleFactor(context: Context): Float {
            TODO("Not yet implemented")
        }

        override fun getOldScaleFactor(): Float {
            TODO("Not yet implemented")
        }
    };

    /** Возвращает коэффициент масштабирования. */
    abstract fun getScaleFactor(context: Context): Float

    /**
     * Возвращает коэффициент масштабирования соответствующий старой конфигурации приложения.
     * Удалить после миграции приложений на версию > 22.6146.
     */
    abstract fun getOldScaleFactor(): Float

    companion object {
        /**
         * Коэффициент масштабирования по умолчанию.
         *
         * Устанавливается среднее значение из линейки масштабов.
         * Для компактных устройств размер XS, для остальных S
         */
        fun getDefaultScale(context: Context): InterfaceScale =
            if (isCompactDevice(context) || !DeviceConfigurationUtils.isTablet(context)) XS else S

        /** Размер линейки масштабов. */
        fun getScaleStepsNumber(context: Context): Int =
            if (isCompactDevice(context) || !DeviceConfigurationUtils.isTablet(context)) 3 else values().size - 1

        private fun isCompactDevice(context: Context): Boolean {
            val isSmallSize = when (getSizeType(context)) {
                SMALL, NORMAL -> true
                else -> false
            }
            return isSmallSize || SpecifiedDevices.useAsCompactDevices()
        }

        private fun InterfaceScale.applySpecifiedScaleForDeviceOrDefault(defaultScaleFactorValue: Float): Float =
            SpecifiedDevices.run { getSpecifiedScaleFactorOrDefault(defaultScaleFactorValue) }

        private fun getSizeType(context: Context): Int =
            context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK

        private fun undefinedScreenSize(scale: Float): Float {
            Timber.e("Undefined screen size type")
            return scale
        }
    }
}