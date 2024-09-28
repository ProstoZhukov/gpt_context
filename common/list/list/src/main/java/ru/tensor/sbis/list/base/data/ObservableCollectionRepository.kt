package ru.tensor.sbis.list.base.data

import io.reactivex.Observable
import io.reactivex.disposables.SerialDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import ru.tensor.sbis.list.base.data.filter.FilterAndPageProvider
import ru.tensor.sbis.list.base.data.filter.FilterProvider
import ru.tensor.sbis.list.base.domain.boundary.Repository
import ru.tensor.sbis.list.base.domain.entity.EntityFactory
import ru.tensor.sbis.list.base.domain.entity.PagingListScreenEntity

/**
 * Реализация репозитория для работы с микросервисом контроллера, получает "бизнес модель"(БМ) списка
 * и подписывается на обновления данных от микросервиса.
 */
class ObservableCollectionRepository<ENTITY, COLLECTION_VIEW_MODEL : Any, ITEM, FILTER> constructor(
    private val entityFactory: EntityFactory<ENTITY, List<ITEM>>,
    private val serviceWrapper: ObservableCollectionWrapper<COLLECTION_VIEW_MODEL, ITEM, FILTER>
) : Repository<ENTITY, FILTER>
        where ENTITY : PagingListScreenEntity<FILTER>,
              ENTITY : FilterProvider<FILTER> {

    private var disposable = SerialDisposable()

    private val subject = PublishSubject.create<List<ITEM>>()
    lateinit var collection: COLLECTION_VIEW_MODEL

    @Synchronized
    override fun update(
        entity: ENTITY,
        filterProvider: FilterAndPageProvider<FILTER>
    ): Observable<ENTITY> {

        val reset = filterProvider.getPageNumber() == 0

        /**Страница всегда самая первая*/
        val page = 0
        return subject.map {
            entityFactory.updateEntityWithData(entity, listOf(Pair(page, it)))
            entity
        }.doOnSubscribe {
            if (reset) {
                createDisposable(filterProvider)
            } else {
                serviceWrapper.setFilter(collection, filterProvider.getServiceFilter())
            }
        }
    }

    private fun createDisposable(filterProvider: FilterAndPageProvider<FILTER>) {
        disposable.set(Observable.create<List<ITEM>> { emitter ->
            collection = serviceWrapper.createCollection(filterProvider.getServiceFilter())
            serviceWrapper.setObserver(collection, CollectionObserverCallback {
                emitter.onNext(it)
            })

            emitter.setCancellable {
                serviceWrapper.removeObserver(collection)
            }
        }.subscribeOn(Schedulers.io()).subscribe {
            subject.onNext(it)
        })
    }

    override fun destroy() {
        disposable.dispose()
    }
}