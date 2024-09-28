package ru.tensor.sbis.common.util

import android.app.Application

/**
 * Предоставляяет сведения о том, соответствует ли текущая конфигурация устройства планшетному отображению.
 *
 * [application] используется для получения ресурсов системы, зависимых от конфигурации.
 */
class DeviceConfigurationInfo(private val application: Application) {

    /**
     * Соответствует ли текущая конфигурация устройства планшетному отображению.
     */
    fun isTablet() = DeviceConfigurationUtils.isTablet(application)
}