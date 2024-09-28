package ru.tensor.sbis.business.common.domain.result

import ru.tensor.sbis.business.common.ui.base.Error
import ru.tensor.sbis.business.common.data.ViewModelProvider
import ru.tensor.sbis.mvp.data.model.PagedListResult

/**
 * Ответ со списочными данными и полезной нагрузкой
 *
 * @param list списочные данные
 * @param extra метаданные
 * @param hasMore статус доступности данных еще (обычно ненадежен, прогнозный)
 */
open class PayloadPagedListResult<EXTRA : ViewModelProvider?, DATA : ViewModelProvider>(
    list: List<DATA>,
    val extra: EXTRA?,
    hasMore: Boolean,
    override var error: Throwable? = null,
    override var fromRefreshedCache: Boolean = false
) : PagedListResult<DATA>(list, hasMore),
    PayloadResult {

    override val isEmpty: Boolean
        get() = dataList.isEmpty()

    /**
     * true если предположительно еще будут данные
     * Поле может быть переопределено под специфику ответа со списочными данными
     */
    open val hasMore: Boolean
        get() = mHasMore

    /**
     * true если предположительно больше не будет данных
     * Поле может быть переопределено под специфику ответа со списочными данными
     */
    open val hasNoMore: Boolean
        get() = hasMore.not()

    /**
     * true если предположительно больше не будет данных после синхронизации [fromRefreshedCache]
     */
    val hasNoMoreAfterRefresh: Boolean
        get() = hasNoMore && fromRefreshedCache

    /**
     * true если ошибка [error] связана с отсутствием прав доступа
     */
    val isPermissionError: Boolean
        get() = error is Error.NoPermissionsError
}