package ru.tensor.sbis.common.util

import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(JUnitParamsRunner::class)
class ApplicationDiskSpaceUsageKtTest {

    @Test
    @Parameters(method = "getValues")
    fun convertMegabytesToInterval(
            value: Long,
            result: String
    ) {
        assertEquals(
                result,
                convertMegabytesToInterval(value)
        )
    }

    @Suppress("unused")
    private fun getValues() = arrayOf(
            arrayOf(
                    150,
                    "0-199MB"
            ),
            arrayOf(
                    199,
                    "0-199MB"
            ),
            arrayOf(
                    200,
                    "200-299MB"
            ),
            arrayOf(
                    299,
                    "200-299MB"
            ),
            arrayOf(
                    999,
                    "900-999MB"
            ),
            arrayOf(
                    1000,
                    "1000-1249MB"
            ),
            arrayOf(
                    1249,
                    "1000-1249MB"
            ),
            arrayOf(
                    1250,
                    "1250-1499MB"
            ),
            arrayOf(
                    1499,
                    "1250-1499MB"
            ),
            arrayOf(
                    1500,
                    "1500-1749MB"
            ),
            arrayOf(
                    1750,
                    "1750-1999MB"
            ),
            arrayOf(
                    2000,
                    "2000-2249MB"
            ),
            arrayOf(
                    2750,
                    "2750-2999MB"
            ),
            arrayOf(
                    3000,
                    "3000-3249MB"
            )
    )
}