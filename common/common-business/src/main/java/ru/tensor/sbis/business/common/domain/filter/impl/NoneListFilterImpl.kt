package ru.tensor.sbis.business.common.domain.filter.impl

import ru.tensor.sbis.business.common.data.HashFilterProvider
import ru.tensor.sbis.business.common.domain.filter.CursorBuilder
import ru.tensor.sbis.business.common.domain.filter.navigation.NavigationType
import ru.tensor.sbis.business.common.domain.filter.navigation.NoneNavigation
import timber.log.Timber

/**
 * Реализация списочного фильтра БЕЗ навигации
 * Используется когда фасад микросервиса отдает все записи без навигации
 *
 * @property CPP_FILTER тип фильтра контроллера
 */
abstract class NoneListFilterImpl<CPP_FILTER : Any>(hashProvider: HashFilterProvider) :
    BaseListFilterImpl<CPP_FILTER, Nothing>(hashProvider) {

    override val shownPage = false

    override val isFirstPageToSync = true

    override val isFirstPageOnlySynced = true

    override val navigationType = NavigationType.NULL

    override fun incPage() = Unit

    override fun reset() = resetSyncedState()

    override fun getNavigation() = NoneNavigation()

    override fun getLastSyncNavigation(lastFilter: CPP_FILTER) = NoneNavigation()

    override fun incPosition(newPosition: CursorBuilder<Nothing>, offset: Int, forceInc: Boolean) =
        Timber.e("Метод не должен быть использован при отсутствии навигации")
}