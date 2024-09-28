package ru.tensor.sbis.counter_provider

import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import ru.tensor.sbis.platform.generated.Subscription
import ru.tensor.sbis.toolbox_decl.counters.CounterProvider

/**
 * Реализация по умолчанию поставщика данных для счетчиков
 *
 * @param <COUNTER>    модель счетчика
 * @param <REPOSITORY> модель источника данных
 *
 * @author mb.kruglova
 */
abstract class BaseAbstractCounterProvider<COUNTER, DATA_REFRESH_CALLBACK, REPOSITORY : BaseCounterRepository<out COUNTER, out DATA_REFRESH_CALLBACK>> @JvmOverloads constructor(
    protected val repository: REPOSITORY,
    scheduler: Scheduler = Schedulers.io()
) : CounterProvider<COUNTER> {

    private val eventBehaviorSubject = BehaviorSubject.create<COUNTER>()
    protected val eventSubject: Subject<COUNTER> = eventBehaviorSubject.toSerialized()
    protected val eventObservable: Observable<COUNTER>

    /**
     * Подписка на обновление счётчика в контроллере
     */

    protected fun onDataRefreshed(params: HashMap<String, String>)
    {
        eventSubject.onNext(counter)
    }
    protected abstract val controllerSubscription: Subscription

    init {
        eventObservable = eventSubject
            .doOnSubscribe {
                controllerSubscription.enable()
                eventSubject.onNext(counter)
            }
            .doOnDispose { controllerSubscription.disable() }
            .subscribeOn(scheduler)
            .replay(1)
            .refCount()
    }

    override fun getCounter(): COUNTER = repository.counter

    override fun getCounterEventObservable(): Observable<COUNTER> = eventObservable

    /**
     * Вернет текущее значение счетчика, если оно есть. Без накладных расходов в виде похода в контроллер или rx подписок.
     */
    fun getCounterValue() = eventBehaviorSubject.value
}
