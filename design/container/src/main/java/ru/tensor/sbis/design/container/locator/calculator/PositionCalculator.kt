package ru.tensor.sbis.design.container.locator.calculator

import ru.tensor.sbis.design.container.locator.LocatorCalculatedData
import ru.tensor.sbis.design.container.locator.LocatorSrcData
/**
 * Класс для вычисления позиции контейнера
 *
 * @author ma.kolpakov
 */
internal interface PositionCalculator {
    fun calculate(isVertical: Boolean = false): LocatorCalculatedData
    var srcData: LocatorSrcData
}