package ru.tensor.sbis.business.common.ui.utils

import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import ru.tensor.sbis.business.common.ui.utils.RoundUtils.getMaxFactor
import ru.tensor.sbis.common.testing.params

/**
 * Тест аналогичный проверке столбчатой диаграммы https://online.sbis.ru/doc/492a1565-5fad-452d-96c4-7ecf4fac8486
 */
@RunWith(JUnitParamsRunner::class)
class RoundUtilsParametrizedTest {

    @Test
    @Parameters(method = "factors")
    fun `Return correct max factor`(
        expected: Double,
        factor: Double
    ) {
        assertEquals(expected, getMaxFactor(factor), 0.0)
    }

    @Suppress("unused")
    private fun factors() = params {
        add(1.0, 100.49)
        add(1.0, 999.99)
        add(1_000.0, 1_000.0)
        add(1_000.0, 99_999.49)
        add(1_000.0, 999_999.99)
        add(1_000_000.0, 999_999_999.99)
        add(1_000_000.0, -999_999_999.99)
        add(1_000_000_000.0, 999_999_999_999.99)
        add(1_000_000_000_000.0, 100_000_000_000_000.00)
    }
}