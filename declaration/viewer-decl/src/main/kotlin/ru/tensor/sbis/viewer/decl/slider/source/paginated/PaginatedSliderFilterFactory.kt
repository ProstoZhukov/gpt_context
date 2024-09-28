package ru.tensor.sbis.viewer.decl.slider.source.paginated

import ru.tensor.sbis.viewer.decl.viewer.ViewerArgs
import java.io.Serializable

/**
 * Фабрика фильтров для паджинируемого просмотрщика вложений
 *
 * @author vv.chekurda
 */
interface PaginatedSliderFilterFactory<FILTER, DATA : ViewerArgs> : Serializable {

    fun createFilter(
        dataList: List<DATA>,
        direction: ViewerUpdatingDirection,
        pageSize: Int
    ): FILTER
}