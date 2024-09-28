package ru.tensor.sbis.business.common.domain.interactor.impl

import io.reactivex.functions.Function
import ru.tensor.sbis.business.common.data.base.BaseCrudListRepository
import ru.tensor.sbis.business.common.data.base.BaseCrudRepository
import ru.tensor.sbis.business.common.domain.filter.Filter
import ru.tensor.sbis.business.common.domain.filter.HashFilter
import ru.tensor.sbis.business.common.domain.result.PayloadResult

/**
 * Базовая реализация интерактора получения данных через CRUD фасад.
 *
 * @param repository репозиторий CRUD фасада.
 * Как источник получения данных используется простой репозиторий [BaseCrudRepository]
 */
abstract class BaseRequestInteractor<CPP_DATA : Any, DATA : PayloadResult, CPP_FILTER : Any, FILTER>(
    filter: FILTER,
    repository: BaseCrudRepository<CPP_DATA, CPP_FILTER>,
    mapper: Function<CPP_DATA, DATA>
) : AbstractRequestInteractor<BaseCrudRepository<CPP_DATA, CPP_FILTER>, CPP_DATA, DATA, CPP_FILTER, FILTER>(
    filter = filter,
    repository = repository,
    mapper = mapper
) where FILTER : HashFilter, FILTER : Filter<CPP_FILTER> {

    override fun requestCppData(cppFilter: CPP_FILTER, sync: Boolean): CPP_DATA? =
        if (sync) {
            repository.readWithRefresh(cppFilter)
        } else {
            repository.read(cppFilter).takeIf(::matchUsableCondition)
        }
}

/**
 * Базовая реализация интерактора получения данных через списочный CRUD фасад.
 *
 * @param repository репозиторий CRUD фасада.
 * Как источник получения данных используется списочный [BaseCrudListRepository]
 */
abstract class BaseRequestListInteractor<CPP_DATA : Any, DATA : PayloadResult, CPP_FILTER : Any, FILTER>(
    filter: FILTER,
    repository: BaseCrudListRepository<*, *, CPP_DATA, CPP_FILTER>,
    mapper: Function<CPP_DATA, DATA>
) : AbstractRequestInteractor<BaseCrudListRepository<*, *, CPP_DATA, CPP_FILTER>, CPP_DATA, DATA, CPP_FILTER, FILTER>(
    filter = filter,
    repository = repository,
    mapper = mapper
) where FILTER : HashFilter, FILTER : Filter<CPP_FILTER> {

    override fun requestCppData(cppFilter: CPP_FILTER, sync: Boolean): CPP_DATA? =
        if (sync) {
            repository.readListWithRefresh(cppFilter)
        } else {
            repository.readList(cppFilter).takeIf(::matchUsableCondition)
        }
}

