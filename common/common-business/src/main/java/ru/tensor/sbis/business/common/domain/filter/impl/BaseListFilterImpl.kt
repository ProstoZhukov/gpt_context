package ru.tensor.sbis.business.common.domain.filter.impl

import androidx.annotation.VisibleForTesting
import ru.tensor.sbis.business.common.data.HashFilterProvider
import ru.tensor.sbis.business.common.domain.filter.ListFilter
import ru.tensor.sbis.business.common.domain.filter.navigation.Navigation
import ru.tensor.sbis.business.common.domain.filter.navigation.NavigationType

/**
 * Базовый фильтр для получения списочных данных с пагинацией
 */
abstract class BaseListFilterImpl<
        CPP_FILTER : Any,
        CPP_CURSOR : Any>(hashProvider: HashFilterProvider) :
    BaseFilterImpl<CPP_FILTER>(hashProvider),
    ListFilter<CPP_FILTER, CPP_CURSOR> {

    override var limit = DEFAULT_LIMIT

    override val hasCertainFilter = false

    override val isPositionType: Boolean
        get() = navigationType == NavigationType.POSITION

    override val isPageType: Boolean
        get() = navigationType == NavigationType.PAGE

    /**
     * Получить текущую навигацию для фильтра
     */
    protected abstract fun getNavigation(): Navigation

    /**
     * Получить навигацию из последнего синхронизируемого фильтра
     */
    protected abstract fun getLastSyncNavigation(lastFilter: CPP_FILTER): Navigation

    companion object {
        const val DEFAULT_LIMIT = 30
        /** Ключ с настройкой для итеративного поиска */
        const val ITERATIVE_KEY = "searchSettings"
    }
}