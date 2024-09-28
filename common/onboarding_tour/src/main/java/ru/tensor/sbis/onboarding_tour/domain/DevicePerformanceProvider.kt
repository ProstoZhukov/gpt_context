package ru.tensor.sbis.onboarding_tour.domain

import android.app.ActivityManager
import android.content.Context
import ru.tensor.sbis.verification_decl.onboarding_tour.DevicePerformanceProvider
import javax.inject.Inject

/**
 * Поставщик конфигурации производительности устройства.
 */
internal class DevicePerformanceProviderImpl @Inject constructor(
    private val context: Context
) : DevicePerformanceProvider {

    override fun isLowPerformanceDevice(): Boolean {
        return !matchesConditions()
    }

    override fun matchesConditions(
        atLeastNumberCPUs: Int,
        systemMemoryLimitInMb: Int
    ): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        return Runtime.getRuntime().availableProcessors() >= atLeastNumberCPUs &&
            !activityManager.isLowRamDevice &&
            activityManager.memoryClass >= systemMemoryLimitInMb
    }
}