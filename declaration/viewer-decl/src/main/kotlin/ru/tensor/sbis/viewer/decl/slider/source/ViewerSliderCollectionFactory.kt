package ru.tensor.sbis.viewer.decl.slider.source

import android.os.Parcelable
import ru.tensor.sbis.viewer.decl.viewer.ViewerArgs

/**
 * Фабрика наблюдаемой коллекции [ViewerSliderCollection] для слайдера просмотрщиков
 *
 * @author sa.nikitin
 */
interface ViewerSliderCollectionFactory : Parcelable {

    /**
     * Создать [ViewerSliderCollection] с:
     *  Опциональным якорем [anchor]
     *  Количеством элементов на странице [itemsOnPage]
     *  Начальным направлением [direction]
     */
    fun createCollection(
        anchor: ViewerArgs?,
        itemsOnPage: Long,
        direction: ViewerSliderDirection
    ): ViewerSliderCollection
}