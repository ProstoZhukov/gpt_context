package ru.tensor.sbis.frescoutils

import com.facebook.common.memory.MemoryTrimType
import com.facebook.common.memory.MemoryTrimmable
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(JUnitParamsRunner::class)
class FrescoMemoryRegistryTest {

    private val frescoMemoryRegistry = FrescoMemoryRegistry()

    @Test
    @Parameters(method = "parameters")
    fun `Should trimm added and not removed trimmable`(memoryTrimType: MemoryTrimType) {
        val mockTrimmable0 = null
        val mockTrimmable = mock<MemoryTrimmable>()
        val mockTrimmable1 = mock<MemoryTrimmable>()
        val mockTrimmable2 = mock<MemoryTrimmable>()
        val mockTrimmable3 = mock<MemoryTrimmable>()
        val mockTrimmable4 = mock<MemoryTrimmable>()
        val mockTrimmable5 = null

        frescoMemoryRegistry.apply {
            registerMemoryTrimmable(mockTrimmable0)
            registerMemoryTrimmable(mockTrimmable)
            registerMemoryTrimmable(mockTrimmable1)
            registerMemoryTrimmable(mockTrimmable2)
            registerMemoryTrimmable(mockTrimmable3)
            registerMemoryTrimmable(mockTrimmable4)
            registerMemoryTrimmable(mockTrimmable5)

            registerMemoryTrimmable(mockTrimmable)
            registerMemoryTrimmable(mockTrimmable1)
            registerMemoryTrimmable(mockTrimmable2)
            registerMemoryTrimmable(mockTrimmable4)

            trim(memoryTrimType)
        }

        verify(mockTrimmable2).trim(memoryTrimType)
        verify(mockTrimmable3).trim(memoryTrimType)
    }

    @Suppress("unused")
    private fun parameters() = arrayOf(
            MemoryTrimType.OnSystemLowMemoryWhileAppInBackground,
            MemoryTrimType.OnAppBackgrounded
    )
}