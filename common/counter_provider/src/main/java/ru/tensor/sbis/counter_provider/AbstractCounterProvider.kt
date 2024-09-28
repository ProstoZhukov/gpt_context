package ru.tensor.sbis.counter_provider

import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import ru.tensor.sbis.crud.generated.DataRefreshCallback
import ru.tensor.sbis.platform.generated.Subscription
import ru.tensor.sbis.toolbox_decl.counters.CounterProvider
import java.util.HashMap

/**
 * Реализация по умолчанию поставщика данных для счетчиков
 *
 * @param <COUNTER>    модель счетчика
 * @param <REPOSITORY> модель источника данных
 *
 * @author mb.kruglova
 */
open class AbstractCounterProvider<COUNTER, REPOSITORY : CounterRepository<out COUNTER>> @JvmOverloads constructor(
    repository: REPOSITORY,
    scheduler: Scheduler = Schedulers.io()
) : BaseAbstractCounterProvider< COUNTER, DataRefreshCallback, REPOSITORY>(repository, scheduler), CounterProvider<COUNTER> {

    /**
     * Подписка на обновление счётчика в контроллере
     */
    protected override val controllerSubscription: Subscription by lazy {
        repository.subscribe(object : DataRefreshCallback() {
            override fun execute(p0: HashMap<String, String>) {
                onDataRefreshed(p0)
            }
        })
    }
}
