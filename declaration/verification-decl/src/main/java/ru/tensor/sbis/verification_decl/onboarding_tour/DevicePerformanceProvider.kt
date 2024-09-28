package ru.tensor.sbis.verification_decl.onboarding_tour

import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Поставщик конфигурации производительности устройства.
 *
 * @author as.chadov
 */
interface DevicePerformanceProvider : Feature {

    /** Является ли устройство низкопроизводительным. */
    fun isLowPerformanceDevice(): Boolean

    /**
     * Проверить производительность устройства на соответствие условиям.
     *
     * @param atLeastNumberCPUs мин. количество процессоров доступных JVM
     * @param systemMemoryLimitInMb ограничение памяти в мегабайтах которого должно придерживаться приложение чтобы соблюдать ограничения системы текущего устройства
     */
    fun matchesConditions(
        atLeastNumberCPUs: Int = MIN_NUMBER_OF_PROCESSOR,
        systemMemoryLimitInMb: Int = MIN_PER_APP_MEMORY_MB
    ): Boolean

    private companion object {
        const val MIN_NUMBER_OF_PROCESSOR = 2
        const val MIN_PER_APP_MEMORY_MB = 64
    }
}