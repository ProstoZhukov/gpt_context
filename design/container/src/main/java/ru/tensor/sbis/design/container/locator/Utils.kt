/**
 * @author ma.kolpakov
 */
package ru.tensor.sbis.design.container.locator

import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DimenRes
import ru.tensor.sbis.design.container.locator.calculator.AnchorPositionCalculator

/**
 * Возвращает прямоугольник видимости  для вью относительно родителя
 * @param parent - родительская вью в иерархии которой находится целевая вью
 */
internal fun View.getRectDescendantParent(parent: ViewGroup): Rect {
    val anchorRect = Rect()
    getDrawingRect(anchorRect)
    parent.offsetDescendantRectToMyCoords(this, anchorRect)
    return anchorRect
}

/**
 * Настраивает локаторы для работы в переписке.
 * Если не поместилось сверху и снизу расположить контейнер сбоку по внешней стороне элемента с заданным отступом
 */
fun configureForConversationRegistry(
    verticalLocator: AnchorVerticalLocator,
    horizontalLocator: AnchorHorizontalLocator,
    @DimenRes offsetRes: Int
) {
    val vertAnchorLocator = verticalLocator.locator as AnchorLocator
    (vertAnchorLocator.positionCalculator as AnchorPositionCalculator).onCantPlaceListener = {
        val horAnchorLocator = horizontalLocator.locator as AnchorLocator
        val newAlignment = horAnchorLocator.alignment.invert()
        with(horAnchorLocator.positionCalculator as AnchorPositionCalculator) {
            alignmentPriority = listOf(newAlignment)
            srcAnchorData.innerPosition = false
        }
        horAnchorLocator.offsetRes = offsetRes
    }
}