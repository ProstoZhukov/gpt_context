package ru.tensor.sbis.viewer.decl.slider.source.paginated

import android.content.Context
import ru.tensor.sbis.viewer.decl.viewer.ViewerArgs
import java.io.Serializable

/**
 * Фабрика компонентов для постраничной загрузки элементов слайдера просмотрщиков
 * Реализация не должна захватывать что-либо несериализуемое
 *
 * @author sa.nikitin
 */
interface PaginatedViewerComponentsFactory<FILTER : Any, DATA : ViewerArgs> : Serializable {

    fun createFilterFactory(appContext: Context): PaginatedSliderFilterFactory<FILTER, DATA>

    fun createInteractor(appContext: Context): PaginatedSliderInteractor<FILTER, DATA>
}