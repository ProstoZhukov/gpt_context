package ru.tensor.sbis.application_tools.initializer

import android.app.Application
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import junitparams.custom.combined.CombinedParameters
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(JUnitParamsRunner::class)
class LocalDebugUtilsInitializerTest {

    private val mockLeakCanaryInitializer = mock<() -> Unit>()
    private val mockLeakCanaryDesabler = mock<() -> Unit>()
    private val mockAnrTimberInitializer = mock<AnrTimberInitializer>()
    private val mockStrictModeInitializer = mock<StrictModeInitializer>()
    private val mockApplication = mock<Application>()

    private fun getLocalDebugUtilsInitializer(
        enableLeakCanary: Boolean,
        enableCrashlytics: Boolean,
        enableStrictMode: Boolean
    ) = LocalDebugUtilsInitializer(
        application = mockApplication,
        enableLeakCanary = enableLeakCanary,
        enableCrashlytics = enableCrashlytics,
        enableStrictMode = enableStrictMode,
        anrTimberInitializer = mockAnrTimberInitializer,
        leakCanaryInitializer = mockLeakCanaryInitializer,
        leakCanaryDisabler = mockLeakCanaryDesabler,
        strictModeInitializer = mockStrictModeInitializer
    )

    @Test
    @CombinedParameters("true,false", "true,false")
    fun `Should initialize AnrWatch and Timber`(
        enableLeakCanary: Boolean,
        enableCrashlytics: Boolean
    ) {
        getLocalDebugUtilsInitializer(enableLeakCanary, enableCrashlytics, false)()

        verify(mockAnrTimberInitializer).invoke()
    }

    @Test
    @Parameters(value = ["true", "false"])
    fun `Should initialize LeakCanary`(enableCrashlytics: Boolean) {
        getLocalDebugUtilsInitializer(
            enableLeakCanary = true,
            enableStrictMode = false,
            enableCrashlytics = enableCrashlytics
        )()

        verify(mockLeakCanaryInitializer).invoke()
    }

    @Test
    @Parameters(value = ["true", "false"])
    fun `Should disable LeakCanary`(enableCrashlytics: Boolean) {
        getLocalDebugUtilsInitializer(
            enableLeakCanary = false,
            enableStrictMode = false,
            enableCrashlytics = enableCrashlytics
        )()

        verify(mockLeakCanaryDesabler).invoke()
    }

    @Test
    @Parameters(value = ["true|true", "true|false"])
    fun `Should initialize StrictMode`(
        enableStrictMode: Boolean,
        enableCrashlytics: Boolean,
    ) {
        getLocalDebugUtilsInitializer(false, enableCrashlytics, enableStrictMode)()

        verify(mockStrictModeInitializer).invoke()
    }
}