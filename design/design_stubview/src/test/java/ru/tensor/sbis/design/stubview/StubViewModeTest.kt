package ru.tensor.sbis.design.stubview

import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import ru.tensor.sbis.common.testing.params
import kotlin.random.Random

/**
 * @author ma.kolpakov
 */
@RunWith(JUnitParamsRunner::class)
class StubViewModeTest {

    @Test
    @Parameters(method = "fromIdSupportedParams")
    fun `Given supported id, when fromId() called, then return enum value `(id: Int, expected: StubViewMode) {
        assertEquals(expected, StubViewMode.fromId(id))
    }

    @Suppress("unused")
    private fun fromIdSupportedParams() = params {
        add(0, StubViewMode.BASE)
        add(1, StubViewMode.BLOCK)
    }

    @Test(expected = IllegalStateException::class)
    fun `Given unsupported id, when fromId() called, then throw illegal state`() {
        val id = getRandomUnsupportedId()

        println("Unsupported id: $id")

        StubViewMode.fromId(id)
    }

    private fun getRandomUnsupportedId(): Int {
        return if (Random.nextBoolean() /* Положительное или отрицательное значение */)
            Random.nextInt(1, Int.MAX_VALUE)
        else
            Random.nextInt(Int.MIN_VALUE, 0)
    }
}
