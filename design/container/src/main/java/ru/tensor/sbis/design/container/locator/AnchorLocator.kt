package ru.tensor.sbis.design.container.locator

import android.graphics.Rect
import android.view.View
import androidx.annotation.DimenRes
import androidx.annotation.IdRes
import androidx.core.content.res.ResourcesCompat
import io.reactivex.Completable
import ru.tensor.sbis.design.container.locator.calculator.AnchorPositionCalculator
import ru.tensor.sbis.design.container.locator.calculator.PositionCalculator
import ru.tensor.sbis.design.container.locator.watcher.AnchorWatcher

/**
 * Локатор позволяющий задать позиционирования относительно якорной вью с учетом ограничивающей вью
 * @param alignment - Выравнивание контейнера
 * @param boundsViewId - Id ограничивающей вью
 * @param offsetRes - дополнительный отступ от вызывающего элемента (передается ID ресурса с размером)
 * @param force - Влияет на параметр alignment: если true всегда будет показывать контейнер
 * с выбранной в alignment стороны. Если false то в случае не хватки места для отображения контейнера
 * он будет показан с противоположной стороны
 * @param innerPosition - Указывает относительно внутренней или внешней стороны якорной вью будет
 * произведено выравнивание
 * @author ma.kolpakov
 */
internal class AnchorLocator(
    internal var alignment: LocatorAlignment,
    @IdRes private val boundsViewId: Int = View.NO_ID,
    internal var force: Boolean = true,
    internal var innerPosition: Boolean = false,
    @DimenRes
    internal var offsetRes: Int = ResourcesCompat.ID_NULL,
    var anchorWatcher: AnchorWatcher? = null
) : ScreenLocator(alignment, boundsViewId) {

    override var positionCalculator: PositionCalculator = AnchorPositionCalculator(alignment)

    override fun prepareLocator(): Completable {
        val anchorData = AnchorLocatorSrcData()
        return super.prepareLocator().andThen(
            anchorWatcher?.getAnchor(parent)?.doOnSuccess { newAnchorRect ->
                if (isVertical) {
                    anchorData.anchorPosition = newAnchorRect.top
                    anchorData.anchorSize = newAnchorRect.height()
                } else {
                    anchorData.anchorPosition = newAnchorRect.left
                    anchorData.anchorSize = newAnchorRect.width()
                }

                anchorData.pixelOffset =
                    if (offsetRes != ResourcesCompat.ID_NULL)
                        parent.context.resources.getDimensionPixelSize(offsetRes)
                    else
                        0
                anchorData.force = force
                anchorData.innerPosition = innerPosition

                (positionCalculator as AnchorPositionCalculator).srcAnchorData = anchorData
            }?.onErrorReturn {
                // Если произошла ошибка при поиске якоря то показываем контейнер по середине экрана
                val anchorPositionCalculator = positionCalculator as AnchorPositionCalculator
                anchorData.force = true
                anchorData.innerPosition = true
                anchorData.anchorPosition = 0
                anchorData.anchorSize = anchorPositionCalculator.srcData.rootSize
                anchorPositionCalculator.alignmentPriority = listOf(LocatorAlignment.CENTER)
                anchorPositionCalculator.srcAnchorData = anchorData
                Rect()
            }?.ignoreElement()
        )
    }

    override fun dispose() {
        anchorWatcher?.dispose()
    }
}
