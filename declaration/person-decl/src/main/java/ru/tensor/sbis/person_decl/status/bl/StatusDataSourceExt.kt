package ru.tensor.sbis.person_decl.status.bl

import io.reactivex.Observable
import ru.tensor.sbis.person_decl.status.model.ServiceAccessErrorStatus
import ru.tensor.sbis.person_decl.status.model.Status
import java.util.concurrent.Callable

/**
 * Подписка на обновление в списке статусов
 *
 * @param [fetchFunction] функция будет вызвана при обновлении и во время подписки, если [prefetch]==true
 * @param [prefetch] true, если нужно вызвать [fetchFunction] при подписке
 *
 * @author us.bessonov
 */
fun <T : Any> StatusDataSource.observe(
    fetchFunction: Callable<T>,
    prefetch: Boolean = false
): Observable<T> {
    return Observable.create<T> { emitter ->
        val updateListener =
            Runnable { emitter.takeIf { !it.isDisposed }?.onNext(fetchFunction.call()) }
        addOnChangeCallback(updateListener)
        emitter.setCancellable { removeOnChangeCallback(updateListener) }
        if (prefetch) updateListener.run()
    }
}

/**
 * Подписка на событие об ошибке установки статуса посредством функции [callback]
 */
fun StatusDataSource.observeError(callback: (Int, String) -> Unit): Observable<Unit> {
    return Observable.create { emitter ->
        addOnSetCurrentErrorCallback(callback)
        emitter.setCancellable { removeOnSetCurrentErrorCallback(callback) }
    }
}

/**
 * Подписка на событие об ошибке сервиса посредством функции [callback]
 */
fun StatusDataSource.observeServiceAccessError(callback: (ServiceAccessErrorStatus, String) -> Unit): Observable<Unit> {
    return Observable.create { emitter ->
        addServiceAccessErrorCallback(callback)
        emitter.setCancellable { removeServiceAccessErrorCallback(callback) }
    }
}

/**
 * Подписка на событие изменения типа статуса
 */
fun StatusDataSource.observeStatusTypeChanged(): Observable<Pair<Boolean, Long>> {
    return Observable.create { emitter ->
        val updateListener =
            { isWorking: Boolean, time: Long -> emitter.takeIf { !it.isDisposed }?.onNext(isWorking to time) }
        addOnStatusTypeChangedCallback(updateListener)
        emitter.setCancellable { removeOnStatusTypeChangedCallback(updateListener) }
    }
}

/**
 * Подписка на событие изменения доступности смены статуса
 */
fun StatusDataSource.observeStatusChangeabilitySettingChanged(): Observable<Boolean> {
    return Observable.create { emitter ->
        val updateListener =
            { statusChangeAvailable: Boolean -> emitter.takeIf { !it.isDisposed }?.onNext(statusChangeAvailable) }
        addStatusChangeabilitySettingChangeCallback(updateListener)
        emitter.setCancellable { removeStatusChangeabilitySettingChangeCallback(updateListener) }
    }
}

/**
 * Получение нового объекта [Observable] для подписки на обновление активного статуса. При подписке
 * будет доставлен статус из кэша, а следом - актуальный статус из облака
 *
 * @author us.bessonov
 */
val StatusDataSource.currentStatusObservable: Observable<Status>
    get() = observe({ getCurrentStatusCallback() }, true).startWith(getStubStatus())

/**
 * Получение нового объекта [Observable] для проверки возможности изменения статуса
 */
val StatusDataSource.statusChangeAvailabilityObservable: Observable<Boolean>
    get() = Observable.fromCallable { this.getStatusChangeAvailability() }.startWith(false)

/**
 * Получение нового объекта [Observable] для отслеживания изменения типа статуса
 */
val StatusDataSource.statusTypeChangedObservable: Observable<Pair<Boolean, Long>>
    get() = observeStatusTypeChanged()

/**
 * Получение нового объекта [Observable] для отслеживания изменения доступности смены статуса
 */
val StatusDataSource.statusChangeabilitySettingChangedObservable: Observable<Boolean>
    get() = observeStatusChangeabilitySettingChanged()