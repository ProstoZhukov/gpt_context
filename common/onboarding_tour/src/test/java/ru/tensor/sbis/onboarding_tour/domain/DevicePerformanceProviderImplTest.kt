package ru.tensor.sbis.onboarding_tour.domain

import android.app.ActivityManager
import android.content.Context
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import ru.tensor.sbis.common.testing.doReturn
import ru.tensor.sbis.verification_decl.onboarding_tour.DevicePerformanceProvider

internal class DevicePerformanceProviderImplTest {
    private val mockManager: ActivityManager = mock {
        on { isLowRamDevice } doAnswer { isLowRamDevice }
        on { memoryClass } doAnswer { appMemory }
    }
    private val mockContext: Context = mock {
        on { getSystemService(Context.ACTIVITY_SERVICE) } doReturn mockManager
    }

    private var appMemory = 100
    private var isLowRamDevice = false
    private lateinit var performanceProvider: DevicePerformanceProvider

    @Before
    fun setUp() {
        performanceProvider = DevicePerformanceProviderImpl(mockContext)
    }

    @Test
    fun `Consider device weak if there is little RAM`() {
        isLowRamDevice = true
        assertTrue(performanceProvider.isLowPerformanceDevice())
    }

    @Test
    fun `Consider device weak if app memory is not enough`() {
        appMemory = 32
        assertTrue(performanceProvider.isLowPerformanceDevice())
    }

    @Test
    fun `Consider device is high performance if the environment is by default`() {
        assertFalse(performanceProvider.isLowPerformanceDevice())
    }
}