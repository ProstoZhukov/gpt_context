package ru.tensor.sbis.list.base.data

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import ru.tensor.sbis.list.base.data.filter.FilterAndPageProvider
import ru.tensor.sbis.list.base.data.filter.FilterProvider
import ru.tensor.sbis.list.base.data.utils.CreateSubscriptionHolder
import ru.tensor.sbis.list.base.data.utils.EMPTY_SUBSCRIPTION_HOLDER
import ru.tensor.sbis.list.base.domain.boundary.Repository
import ru.tensor.sbis.list.base.domain.entity.EntityFactory
import ru.tensor.sbis.list.base.domain.entity.PagingListScreenEntity

/**
 * Реализация репозитория для работы с микросервисом контроллера, получает "бизнес модель"(БМ) списка
 * и подписывается на обновления данных от микросервиса.
 *
 * @param SERVICE_RESULT "сырые" данные, получаемые из микросервиса методами list и refresh
 * @param ENTITY БМ экран списка.
 * @param FILTER класс пакета "*.generated"  - фильтра используемый микросервисом в методах list и refresh
 * @property entityFactory EntityFactory<ENTITY, SERVICE_RESULT>
 * @property serviceWrapper ServiceWrapper<SERVICE_RESULT, FILTER>
 */
class CrudRepository<SERVICE_RESULT, ENTITY, FILTER> constructor(
    private val entityFactory: EntityFactory<ENTITY, SERVICE_RESULT>,
    private val serviceWrapper: ServiceWrapper<SERVICE_RESULT, FILTER>,
    private val subject: PublishSubject<Map<String, String>> = PublishSubject.create()
) : Repository<ENTITY, FILTER>
        where ENTITY : PagingListScreenEntity<FILTER>,
              ENTITY : FilterProvider<FILTER> {

    private val disposable = Observable.create<Map<String, String>> { emitter ->
        val subscriptionHolder =
            serviceWrapper.setCallbackAndReturnSubscription {
                subject.onNext(it)
            }
                ?.run(CreateSubscriptionHolder())
                ?: EMPTY_SUBSCRIPTION_HOLDER
        emitter.setCancellable {
            subscriptionHolder.clear()
        }
    }
        .subscribeOn(Schedulers.computation())
        .subscribe()

    override fun update(
        entity: ENTITY,
        filterProvider: FilterAndPageProvider<FILTER>
    ): Observable<ENTITY> {
        return Observable.concat(
            Observable.fromCallable {
                listOf(
                    Pair(
                        filterProvider.getPageNumber(),
                        serviceWrapper.list(filterProvider.getServiceFilter())
                    )
                )
            },
            subject.map { params ->
                entity.getPageFilters().map {
                    Pair(
                        it.getPageNumber(),
                        serviceWrapper.refresh(it.getServiceFilter(), params)
                    )
                }
            }
        )
            .map {
                entityFactory.updateEntityWithData(
                    entity,
                    it
                )
                entity
            }
    }

    override fun destroy() = disposable.dispose()
}