package ru.tensor.sbis.review.triggers

import android.content.SharedPreferences
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import ru.tensor.sbis.common.testing.params

/**
 * @author ma.kolpakov
 */
@RunWith(JUnitParamsRunner::class)
class ParentTriggersTests {
    private val mockSharedPreferences: SharedPreferences = mock()

    @Test
    @Parameters(method = "paramsForAndTrigger")
    fun `When child trigger checked, then AndTrigger check with correct logical value`(
        triggers: List<Boolean>,
        expected: Boolean
    ) {
        val triggerMocks = mutableListOf<Trigger>()
        triggers.forEach {
            val mockTrigger = mock<CountTrigger>()
            triggerMocks.add(mockTrigger)
            whenever(mockTrigger.checkEvent(mockSharedPreferences)).thenReturn(it)
        }

        val andTrigger = AndTrigger(*triggerMocks.toTypedArray())

        assertEquals(expected, andTrigger.checkEvent(mockSharedPreferences))
    }

    @Test
    @Parameters(method = "paramsForOrTrigger")
    fun `When child trigger checked, then OrTrigger check with correct logical value`(
        triggers: List<Boolean>,
        expected: Boolean
    ) {
        val triggerMocks = mutableListOf<Trigger>()
        triggers.forEach {
            val mockTrigger = mock<CountTrigger>()
            triggerMocks.add(mockTrigger)
            whenever(mockTrigger.checkEvent(mockSharedPreferences)).thenReturn(it)
        }

        val andTrigger = OrTrigger(*triggerMocks.toTypedArray())

        assertEquals(expected, andTrigger.checkEvent(mockSharedPreferences))
    }

    @Suppress("unused")
    private fun paramsForAndTrigger() = params {
        add(listOf(true), true)
        add(listOf(true, false), false)
        add(listOf(true, true), true)
        add(listOf(true, true, false), false)
    }

    @Suppress("unused")
    private fun paramsForOrTrigger() = params {
        add(listOf(true), true)
        add(listOf(true, false), true)
        add(listOf(true, true), true)
        add(listOf(true, true, false), true)
        add(listOf(false, false, false), false)
    }
}