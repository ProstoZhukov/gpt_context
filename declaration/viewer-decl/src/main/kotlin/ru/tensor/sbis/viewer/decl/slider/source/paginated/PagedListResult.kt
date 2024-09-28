package ru.tensor.sbis.viewer.decl.slider.source.paginated

/**
 * Обертка данных с информацией о пагинации для просмотрщика.
 *
 * @author am.boldinov, vv.malyhin
 */
data class PagedListResult<DATA>(
    val dataList: List<DATA>,
    val hasMore: Boolean,
    val metaData: Map<String, String>
)
