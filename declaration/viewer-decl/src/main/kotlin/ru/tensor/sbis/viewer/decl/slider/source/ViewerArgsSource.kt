package ru.tensor.sbis.viewer.decl.slider.source

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.viewer.decl.viewer.ViewerArgs

typealias IndexedViewerArgs = IndexedValue<ViewerArgs>

/**
 * Источник аргументов просмотрщиков в слайдере
 *
 * @author sa.nikitin
 */
sealed class ViewerArgsSource : Parcelable {

    /**
     * Источник в виде фиксированного набора
     *
     * @param argsList           Список аргументов просмотрщиков
     * @param initialPosition    Позиция просмотрщика, который следует отобразить первым
     */
    @Parcelize
    class Fixed(val argsList: List<ViewerArgs>, val initialPosition: Int) : ViewerArgsSource()

    /**
     * Источник с наблюдаемой коллекцией для динамического обновления
     *
     * @param initialArgsList       Начальный список аргументов просмотрщиков
     * @param initialPosition       Позиция просмотрщика в [initialArgsList], который следует отобразить первым
     * @param collectionFactory     Фабрика наблюдаемой коллекции [ViewerSliderCollection]
     */
    @Parcelize
    class Collection(
        val initialArgsList: List<ViewerArgs>,
        val initialPosition: Int,
        val collectionFactory: ViewerSliderCollectionFactory
    ) : ViewerArgsSource()
}