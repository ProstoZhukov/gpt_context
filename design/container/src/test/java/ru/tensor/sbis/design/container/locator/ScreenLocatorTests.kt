package ru.tensor.sbis.design.container.locator

import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import ru.tensor.sbis.common.testing.params
import ru.tensor.sbis.design.container.locator.calculator.ScreenPositionCalculator

/**
 * @author ma.kolpakov
 */
@RunWith(JUnitParamsRunner::class)
class ScreenLocatorTests {

    @Test
    @Parameters(method = "getStartParams")
    internal fun `When locator at start of screen then calculate offset correctly`(
        locatorSrcData: LocatorSrcData,
        expectedOffset: Int
    ) {
        val positionCalculator = ScreenPositionCalculator(LocatorAlignment.START)
        positionCalculator.srcData = locatorSrcData
        assertEquals(expectedOffset, positionCalculator.calculatePosition())
    }

    @Suppress("unused")
    private fun getStartParams() = params {
        add(LocatorSrcData(marginStart = 0, boundsPos = 0), 0)
        add(LocatorSrcData(marginStart = 0, boundsPos = 1), 1)
        add(LocatorSrcData(marginStart = 1, boundsPos = 1), 2)
    }

    @Test
    @Parameters(method = "getCenterParams")
    internal fun `When locator at center of screen then calculate offset correctly`(
        locatorSrcData: LocatorSrcData,
        expectedOffset: Int
    ) {
        val positionCalculator = ScreenPositionCalculator(LocatorAlignment.CENTER)
        positionCalculator.srcData = locatorSrcData
        assertEquals(expectedOffset, positionCalculator.calculatePosition())
    }

    @Suppress("unused")
    private fun getCenterParams() = params {
        // Без ограничивающей области
        add(
            LocatorSrcData(
                marginStart = 1,
                rootSize = 1,
                contentSize = 1,
                boundsPos = 1,
                boundsSize = 1
            ),
            1
        )
        add(
            LocatorSrcData(
                marginStart = 0,
                rootSize = 0,
                contentSize = 0,
                boundsPos = 0,
                boundsSize = 0
            ),
            0
        )

    }

    @Test
    @Parameters(method = "getEndParams")
    internal fun `When locator at end of screen then calculate offset correctly`(
        locatorSrcData: LocatorSrcData,
        expectedOffset: Int
    ) {
        val positionCalculator = ScreenPositionCalculator(LocatorAlignment.END)
        positionCalculator.srcData = locatorSrcData
        assertEquals(expectedOffset, positionCalculator.calculatePosition())
    }

    @Suppress("unused")
    private fun getEndParams() = params {
        add(
            LocatorSrcData(
                rootSize = 100,
                contentSize = 10,
                boundsPos = 10,
                boundsSize = 50,
                marginEnd = 1
            ),
            41
        )
        add(
            LocatorSrcData(
                rootSize = 100,
                contentSize = 10,
                boundsPos = 0,
                boundsSize = 50,
                marginEnd = 0
            ),
            50
        )
    }

    @Test
    @Parameters(method = "getAlignmentParams")
    internal fun `When locator alignment then calculate offset correctly`(
        locatorAlignment: LocatorAlignment,
        locatorSrcData: LocatorSrcData,
        expectedOffset: Int
    ) {
        val positionCalculator = ScreenPositionCalculator(locatorAlignment)
        positionCalculator.srcData = locatorSrcData
        assertEquals(expectedOffset, positionCalculator.calculatePosition())
    }

    @Suppress("unused")
    private fun getAlignmentParams() = params {
        add(LocatorAlignment.START, LocatorSrcData(500, 0, 50, 10, 100), 10)
        add(LocatorAlignment.CENTER, LocatorSrcData(500, 0, 0, 0, 100), 0)
        add(LocatorAlignment.CENTER, LocatorSrcData(500, 0, 100, 250, 100), 50)
        add(LocatorAlignment.END, LocatorSrcData(500, 0, 10, 10, 100), 480)
    }

    @Test
    fun `When content more then available space, then position always as start`() {
        val screenLocator = ScreenPositionCalculator(LocatorAlignment.END)
        screenLocator.srcData = LocatorSrcData(contentSize = Int.MAX_VALUE)
        screenLocator.calculate(true)
        assertEquals(LocatorAlignment.START, screenLocator.currentGravity)
    }
}