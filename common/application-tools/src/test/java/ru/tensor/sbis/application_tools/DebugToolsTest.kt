package ru.tensor.sbis.application_tools

import android.app.Application
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Test
import org.junit.runner.RunWith
import ru.tensor.sbis.application_tools.initializer.AnalyticsInitializer
import ru.tensor.sbis.application_tools.initializer.CrashAnalyticsInitializer
import ru.tensor.sbis.application_tools.initializer.CrashlyticsInitializer
import ru.tensor.sbis.application_tools.initializer.LocalDebugUtilsInitializer

@RunWith(JUnitParamsRunner::class)
class DebugToolsTest {

    private val mockApplication = mock<Application> {
        on { cacheDir } doReturn mock()
    }
    private val mockLocalDebugUtilsInitializer = mock<LocalDebugUtilsInitializer>()
    private val mockCrashAnalyticsInitializer = mock<CrashAnalyticsInitializer>()
    private val mockCrashlyticsInitializer = mock<CrashlyticsInitializer>()
    private val mockAnalyticsInitializer = mock<AnalyticsInitializer>()

    /**
     * Инициализация всех инструментов не должна происходить до вызова метода [DebugTools.init], т.к. при создании
     * класса DebugTools приложение еще не проинициализировалось.
     */
    @Test
    fun `No invocation before init`() {
        getDebugTools()

        verify(mockLocalDebugUtilsInitializer, never())()
        verify(mockCrashAnalyticsInitializer, never())()
        verify(mockAnalyticsInitializer, never())()
    }

    @Test
    fun `Should run tools`() {
        val debugTools = getDebugTools()

        debugTools.init()

        verify(mockLocalDebugUtilsInitializer).invoke()
    }

    @Test
    fun `Should run crash analytics`() {
        val debugTools = getDebugTools()

        debugTools.init()

        verify(mockCrashAnalyticsInitializer).invoke()
    }

    @Test
    @Parameters(
        value = [
            "true, 1",
            "false, 0"
        ]
    )
    fun `Should init Crashlytics if enabled`(enableCrashlytics: Boolean, times: Int) {
        val debugTools = getDebugTools(enableCrashlytics = enableCrashlytics)

        debugTools.init()

        verify(
            mockCrashlyticsInitializer,
            times(times)
        ).invoke()
    }

    @Test
    @Parameters(
        value = [
            "true, 1",
            "false, 0"
        ]
    )
    fun `Should init Analitics if enabled`(publicBuild: Boolean, times: Int) {
        val debugTools = getDebugTools(publicBuild = publicBuild)

        debugTools.init()

        verify(
            mockAnalyticsInitializer,
            times(times)
        ).invoke()
    }

    private fun getDebugTools(enableCrashlytics: Boolean = false, publicBuild: Boolean = false): DebugTools {
        return DebugTools(
            mockApplication,
            enableLocalDebugUtils = true,
            stopApplicationOnRxCrash = true,
            appId = "",
            versionName = "",
            flavor = "",
            versionCode = 0,
            enableLeakCanary = false,
            enableCrashlytics = enableCrashlytics,
            publicBuild = publicBuild,
            localDebugUtilsInitializer = mockLocalDebugUtilsInitializer,
            crashAnalyticsInitializer = mockCrashAnalyticsInitializer,
            crashlyticsInitializer = mockCrashlyticsInitializer,
            analyticsInitializer = mockAnalyticsInitializer,
            processNameProvider = mock()
        )
    }
}