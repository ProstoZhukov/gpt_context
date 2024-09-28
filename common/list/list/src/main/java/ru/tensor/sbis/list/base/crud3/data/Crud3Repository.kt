package ru.tensor.sbis.list.base.crud3.data

import io.reactivex.Observable
import io.reactivex.disposables.SerialDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import ru.tensor.sbis.list.base.data.filter.FilterAndPageProvider
import ru.tensor.sbis.list.base.data.filter.FilterProvider
import ru.tensor.sbis.list.base.domain.entity.EntityFactory
import ru.tensor.sbis.list.base.domain.entity.PagingListScreenEntity

/**
 * Реализация репозитория для работы с микросервисом контроллера, получает "бизнес модель"(БМ) списка
 * и подписывается на обновления данных от микросервиса.
 */
@Deprecated("Используй модуль crud3")
class Crud3Repository<ENTITY, COLLECTION_VIEW_MODEL : Any, ITEM, FILTER> constructor(
    private val entityFactory: EntityFactory<ENTITY, List<ITEM>>,
    private val serviceWrapper: Crud3Wrapper<COLLECTION_VIEW_MODEL, ITEM, FILTER>
) : CollectionRepository<ENTITY, ITEM, FILTER> where ENTITY : PagingListScreenEntity<FILTER>,
                                                     ENTITY : FilterProvider<FILTER> {

    private var disposable = SerialDisposable()

    private val subject = PublishSubject.create<Result<ITEM>>()
    private var collection: COLLECTION_VIEW_MODEL? = null

    @Synchronized
    override fun create(
        filterProvider: FilterAndPageProvider<FILTER>
    ): Observable<Result<ITEM>> {
        return subject
            .doOnSubscribe {
                createDisposable(filterProvider)
            }
    }

    private fun createDisposable(filterProvider: FilterAndPageProvider<FILTER>) {
        disposable.set(Observable.create<Result<ITEM>> { emitter ->
            collection = serviceWrapper.createCollection(filterProvider.getServiceFilter()).also {
                serviceWrapper.setObserver(it, Crud3ObserverCallback { res ->
                    emitter.onNext(res)
                })
            }

        }
            .subscribeOn(Schedulers.io())
            .subscribe {
                subject.onNext(it)
            })
    }

    override fun next(): Observable<Result<ITEM>> {
        return subject
            .doOnSubscribe {
                collection?.let {
                    serviceWrapper.goNext(it)
                }
            }
    }

    override fun prev(): Observable<Result<ITEM>> {
        return subject
            .doOnSubscribe {
                collection?.let {
                    serviceWrapper.goPrev(it)
                }
            }
    }

    override fun update(entity: ENTITY, value: List<ITEM>) {
        entityFactory.updateEntityWithData(0, entity, value)
    }

    override fun destroy() {
        disposable.dispose()
        collection?.let {
            serviceWrapper.dispose(it)
            collection = null
        }
    }
}