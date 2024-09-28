package ru.tensor.sbis.business.common.domain.interactor.impl

import android.annotation.SuppressLint
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import ru.tensor.sbis.business.common.data.base.BaseCrudListRepository
import ru.tensor.sbis.business.common.domain.filter.HashFilter
import ru.tensor.sbis.business.common.domain.filter.SearchListFilter
import ru.tensor.sbis.business.common.domain.interactor.SearchInteractor
import ru.tensor.sbis.business.common.domain.result.PayloadPagedListResult
import ru.tensor.sbis.business.common.ui.base.Error

/**
 * Базовая реализация интерактора получения данных с CRUD фасада [BaseRequestListInteractor]
 * дополненная функционалом базового Поиска без кэширования с блокирующим получением всего результата по выборке
 */
abstract class BaseListSearchInteractor<CPP_DATA : Any, DATA : PayloadPagedListResult<*, *>, CPP_FILTER : Any, FILTER>(
    filter: FILTER,
    repository: BaseCrudListRepository<*, *, CPP_DATA, CPP_FILTER>,
    mapper: Function<CPP_DATA, DATA>
) : BaseRequestListInteractor<CPP_DATA, DATA, CPP_FILTER, FILTER>(filter, repository, mapper),
    SearchInteractor<DATA, FILTER> where FILTER : HashFilter, FILTER : SearchListFilter<CPP_FILTER, *> {

    override fun searchData(searchQuery: String): Observable<Result<DATA>> =
        search(filter.searchQuery, filter)
            .map(Result.Companion::success)
            .onErrorResumeNext { e: Throwable -> Observable.just(Result.failure(e)) }

    /** @SelfDocumented **/
    protected open fun isSearchDataEmpty(cppResult: CPP_DATA?): Boolean = cppResult == null

    @SuppressLint("VisibleForTests")
    private fun search(
        searchQuery: String,
        filter: FILTER
    ): Observable<DATA> = Observable.create(ObservableOnSubscribe<DATA> { emitter ->
        if (networkUtils.isConnected.not()) {
            emitter.onError(Error.NoInternetConnection())
        }
        val cppFilter = filter.build()
        val data: CPP_DATA? = repository.search(searchQuery, cppFilter)
        if (isSearchDataEmpty(data)) {
            emitter.onError(Error.NoSearchDataError())
        } else {
            onFetchProcessor(filter, data, true)
            emitter.onNext(mapper.apply(data!!))
        }
    })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
}