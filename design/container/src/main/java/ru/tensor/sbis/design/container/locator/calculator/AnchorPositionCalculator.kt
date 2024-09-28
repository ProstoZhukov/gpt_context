package ru.tensor.sbis.design.container.locator.calculator

import androidx.annotation.Px
import ru.tensor.sbis.design.container.locator.AnchorLocatorSrcData
import ru.tensor.sbis.design.container.locator.LocatorAlignment

/**
 * Класс для вычисления позиции контейнера относительно вызывающего элемента
 *
 * @author ma.kolpakov
 */
internal class AnchorPositionCalculator(alignment: LocatorAlignment) : ScreenPositionCalculator(alignment) {
    var onCantPlaceListener: (() -> Unit)? = null

    var alignmentPriority = listOf(alignment, alignment.invert(), alignment)

    var finalAlignment: LocatorAlignment = alignment

    var srcAnchorData = AnchorLocatorSrcData()
        set(value) {
            field = value
            value.anchorPosition += srcData.rootOffset
        }

    override fun calculatePosition(): Int {
        check(alignmentPriority.isNotEmpty()) { "alignmentPriority can't be empty" }
        // по умолчанию у нас всегда позиция задается в середине
        var newPosition = getPosition(LocatorAlignment.CENTER)
        var inParent: HitInParent = isInParent(newPosition)
        // Если заданны приоритеты используем их
        alignmentPriority.forEachIndexed { index, value ->
            if (index == alignmentPriority.size - 1) {
                onCantPlaceListener?.invoke()
            }
            newPosition = getPosition(value)
            inParent = isInParent(newPosition)
            if (inParent == HitInParent.HIT) {
                finalAlignment = value
                // возвращаем значение если куда то поместились
                return newPosition
            } else {
                finalAlignment = value
                // или если задана строгая привязка
                if (srcAnchorData.force)
                    return if (inParent == HitInParent.UNDERSHOOT) startOfPosition()
                    else endOfPosition()
            }

        }
        // Ну и если ни куда не попали прижимаемся к краю по данным из последнего приоритета выравнивания
        return when (inParent) {
            HitInParent.UNDERSHOOT -> startOfPosition()
            HitInParent.HIT -> newPosition
            HitInParent.OVERSHOOT -> endOfPosition()
        }
    }

    private fun isInParent(@Px newPosition: Int): HitInParent = when (currentGravity) {
        LocatorAlignment.START -> {
            when {
                newPosition < startOfAvailableSpace() -> HitInParent.UNDERSHOOT
                newPosition + srcData.contentSize > endOfAvailableSpace() -> HitInParent.OVERSHOOT
                else -> HitInParent.HIT
            }
        }
        LocatorAlignment.END -> {
            when {
                srcData.rootSize - newPosition - srcData.contentSize < startOfAvailableSpace() -> HitInParent.UNDERSHOOT
                srcData.rootSize - newPosition > endOfAvailableSpace() -> HitInParent.OVERSHOOT
                else -> HitInParent.HIT
            }
        }
        LocatorAlignment.CENTER -> {
            when {
                srcData.rootSize / 2 + newPosition - srcData.contentSize / 2 < startOfAvailableSpace()
                -> HitInParent.UNDERSHOOT
                srcData.rootSize / 2 + newPosition + srcData.contentSize / 2 > endOfAvailableSpace()
                -> HitInParent.OVERSHOOT
                else -> HitInParent.HIT
            }
        }
    }

    private fun endOfAvailableSpace() =
        srcData.rootSize - srcData.marginStart - if (srcData.boundsSize > 0)
            srcData.rootSize - srcData.boundsSize - srcData.boundsPos
        else 0

    private fun startOfAvailableSpace() = srcData.boundsPos + srcData.marginStart

    private fun getPosition(alignment: LocatorAlignment): Int {
        val selfOffset = if (alignment != LocatorAlignment.CENTER) srcAnchorData.anchorSize else 0

        return if (srcAnchorData.innerPosition) {
            currentGravity = alignment
            getPositionForGravity(currentGravity) + srcAnchorData.pixelOffset - selfOffset
        } else {
            currentGravity = alignment.invert()
            getPositionForGravity(currentGravity) + srcAnchorData.pixelOffset
        }
    }

    private fun getPositionForGravity(gravity: LocatorAlignment) = when (gravity) {
        LocatorAlignment.START -> {
            srcAnchorData.anchorPosition + srcAnchorData.anchorSize
        }
        LocatorAlignment.CENTER -> {
            srcAnchorData.anchorPosition - srcData.rootSize / 2 + srcAnchorData.anchorSize / 2
        }
        LocatorAlignment.END -> {
            srcData.rootSize - srcAnchorData.anchorPosition
        }

    }

    /**
     * Признак того помещается ли контейнер в родителя
     */
    private enum class HitInParent {
        /**
         * недолет - пересекли границу родителя в начале
         */
        UNDERSHOOT,

        /**
         * попали - помещается внутри родителя
         */
        HIT,

        /**
         * перелет - пересекли границу родителя в конце
         */
        OVERSHOOT
    }
}