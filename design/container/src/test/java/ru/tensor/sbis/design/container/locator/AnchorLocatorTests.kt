package ru.tensor.sbis.design.container.locator

import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import ru.tensor.sbis.common.testing.params
import ru.tensor.sbis.design.container.locator.calculator.AnchorPositionCalculator

/**
 * @author ma.kolpakov
 */
@RunWith(JUnitParamsRunner::class)
class AnchorLocatorTests {
    @Test
    @Parameters(method = "getStartParams")
    internal fun `When locator configured, then calculate offset correctly`(
        locatorAlignment: LocatorAlignment,
        locatorSrcData: LocatorSrcData,
        anchorLocatorSrcData: AnchorLocatorSrcData,
        expectedOffset: Int
    ) {
        val locator = AnchorPositionCalculator(locatorAlignment)
        locator.srcData = locatorSrcData
        locator.srcAnchorData = anchorLocatorSrcData

        assertEquals(expectedOffset, locator.calculatePosition())
    }

    @Suppress("unused")
    private fun getStartParams() = params {
        add(
            LocatorAlignment.START,
            LocatorSrcData(100, 10, 20, 10),
            AnchorLocatorSrcData(10, 10),
            80
        )
        add(
            LocatorAlignment.END,
            LocatorSrcData(100, 10, 20, 10),
            AnchorLocatorSrcData(10, 10),
            30
        )

        add(
            LocatorAlignment.CENTER,
            LocatorSrcData(100, 10, 10, 100, 40),
            AnchorLocatorSrcData(10, 10),
            100
        )

        add(
            LocatorAlignment.CENTER,
            LocatorSrcData(100, 10, 100, 10, 40),
            AnchorLocatorSrcData(10, 10),
            10
        )

        add(
            LocatorAlignment.START,
            LocatorSrcData(100, 0, 100, 0, 40),
            AnchorLocatorSrcData(50, 10),
            60
        )
    }
}