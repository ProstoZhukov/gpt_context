package ru.tensor.sbis.design.container.locator.calculator

import android.view.Gravity
import ru.tensor.sbis.design.container.locator.LocatorAlignment
import ru.tensor.sbis.design.container.locator.LocatorCalculatedData
import ru.tensor.sbis.design.container.locator.LocatorSrcData

/**
 * Класс для вычисления позиции контейнера относительно экрана
 *
 * @author ma.kolpakov
 */
internal open class ScreenPositionCalculator(private val alignment: LocatorAlignment) : PositionCalculator {
    internal var currentGravity = LocatorAlignment.CENTER
    override var srcData: LocatorSrcData = LocatorSrcData()

    override fun calculate(isVertical: Boolean): LocatorCalculatedData {
        val availableSize = getAvailableSize()
        // Если контент больше чем доступный размер, показываем в начале экрана, в доступном размере, с учетом отступов
        return if (srcData.contentSize > availableSize) {
            srcData.contentSize = availableSize
            LocatorCalculatedData(startOfPosition(), srcData.contentSize, getGravity(isVertical))
        } else {
            LocatorCalculatedData(calculatePosition(), 0, getGravity(isVertical))
        }
    }

    protected fun startOfPosition(): Int {
        currentGravity = LocatorAlignment.START
        return srcData.marginStart + srcData.boundsPos
    }

    protected fun endOfPosition(): Int {
        currentGravity = LocatorAlignment.END
        // Если есть ограничивающая область необходимо прибавить расстояние между правым краем области и экраном
        return srcData.marginEnd + if (srcData.boundsSize > 0) {
            srcData.rootSize - (srcData.boundsPos + srcData.boundsSize)
        } else 0
    }

    private fun centerOfPosition(): Int {
        currentGravity = LocatorAlignment.CENTER
        return if (srcData.boundsSize > 0) {
            val boundsCenter = srcData.boundsPos + srcData.boundsSize / 2
            val rootCenter = srcData.rootSize / 2

            boundsCenter - rootCenter
        } else 0
    }

    private fun getGravity(isVertical: Boolean): Int {
        return if (isVertical) {
            when (currentGravity) {
                LocatorAlignment.START -> Gravity.TOP
                LocatorAlignment.CENTER -> Gravity.CENTER_VERTICAL
                LocatorAlignment.END -> Gravity.BOTTOM
            }
        } else {
            when (currentGravity) {
                LocatorAlignment.START -> Gravity.START
                LocatorAlignment.CENTER -> Gravity.CENTER_HORIZONTAL
                LocatorAlignment.END -> Gravity.END
            }
        }
    }

    open fun calculatePosition(): Int {
        return when (alignment) {
            LocatorAlignment.START -> {
                startOfPosition()
            }
            LocatorAlignment.CENTER -> {
                centerOfPosition()
            }
            LocatorAlignment.END -> {
                endOfPosition()
            }
        }
    }

    private fun getAvailableSize(): Int {
        return srcData.rootSize - srcData.marginEnd - srcData.marginStart
    }

}