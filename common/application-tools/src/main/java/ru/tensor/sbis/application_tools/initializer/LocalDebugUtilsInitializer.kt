package ru.tensor.sbis.application_tools.initializer

import android.app.Application
import ru.tensor.sbis.application_tools.leak.deployLeakCanary
import ru.tensor.sbis.application_tools.leak.disableLeakCanary

/**
 * @author du.bykov
 *
 * Инициализатор отладочных утилит не продакшен сборки.
 */
class LocalDebugUtilsInitializer(
    private val application: Application,
    private val enableLeakCanary: Boolean,
    private val enableCrashlytics: Boolean,
    private val enableStrictMode: Boolean,
    private val anrTimberInitializer: AnrTimberInitializer = AnrTimberInitializer(enableCrashlytics),
    private val leakCanaryInitializer: () -> Unit = {
        deployLeakCanary(application)
    },
    private val leakCanaryDisabler: () -> Unit = ::disableLeakCanary,
    private val strictModeInitializer: StrictModeInitializer = StrictModeInitializer(enableCrashlytics)
) : () -> Unit {

    override fun invoke() {
        anrTimberInitializer()
        if (enableLeakCanary) leakCanaryInitializer()
        else leakCanaryDisabler()
        if (enableStrictMode) strictModeInitializer()
    }
}