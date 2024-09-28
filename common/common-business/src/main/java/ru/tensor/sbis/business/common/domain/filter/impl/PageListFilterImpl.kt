package ru.tensor.sbis.business.common.domain.filter.impl

import androidx.annotation.VisibleForTesting
import ru.tensor.sbis.business.common.data.HashFilterProvider
import ru.tensor.sbis.business.common.domain.filter.CursorBuilder
import ru.tensor.sbis.business.common.domain.filter.navigation.Navigation
import ru.tensor.sbis.business.common.domain.filter.navigation.NavigationType
import ru.tensor.sbis.business.common.domain.filter.navigation.PageNavigation
import timber.log.Timber

/**
 * Реализация списочного фильтра с "Постраничной" навигацией
 *
 * @property CPP_FILTER тип фильтра контроллера
 *
 * @property pageNumber Номер текущей страницы/разворота
 * @property maxPageNumber Номер максимально получаемой ранее страницы/разворота
 */
abstract class PageListFilterImpl<CPP_FILTER : Any>(hashProvider: HashFilterProvider) :
    BaseListFilterImpl<CPP_FILTER, Nothing>(hashProvider) {

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var pageNumber: Int = 0

    override val shownPage: Boolean
        get() = maxPageNumber >= pageNumber

    override val isFirstPageToSync: Boolean
        get() = pageNumber == 0

    override val isFirstPageOnlySynced: Boolean
        get() {
            val cppFilter = lastCppFilter
            return cppFilter == null || getLastSyncNavigation(cppFilter).pageNumber == 0
        }

    val isFirstOrSecondPage: Boolean
        get() = pageNumber in 0..1

    override val navigationType = NavigationType.PAGE

    override fun incPage() {
        val cppFilter = lastCppFilter
        if (cppFilter == null) {
            return
        } else {
            val lastNavigation = getLastSyncNavigation(cppFilter)
            if (lastNavigation.pageNumber == pageNumber) {
                pageNumber++
            }
        }
    }

    override fun reset() {
        pageNumber = 0
        maxPageNumber = 0
        resetSyncedState()
    }

    override fun getNavigation() = PageNavigation(limit, pageNumber)

    /**
     * Получить постраничную навигацию из последнего синхронизируемого фильтра
     */
    abstract override fun getLastSyncNavigation(lastFilter: CPP_FILTER): Navigation

    override fun build(
        hash: String,
        markAsUsed: Boolean
    ): CPP_FILTER {
        if (markAsUsed) {
            maxPageNumber = pageNumber
        }
        return super.build(hash, markAsUsed)
    }

    private var maxPageNumber: Int = 0
        private set(value) {
            if (value >= 0) {
                field = value
            }
        }

    override fun incPosition(newPosition: CursorBuilder<Nothing>, offset: Int, forceInc: Boolean) =
        Timber.e("Метод не должен быть использован при постраничной навигации")
}