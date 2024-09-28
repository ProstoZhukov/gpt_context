package ru.tensor.sbis.controller_utils.sync

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.platform.generated.Subscription
import ru.tensor.sbis.platform.sync.generated.AreaStatus
import ru.tensor.sbis.platform.sync.generated.AreaSyncInformer
import ru.tensor.sbis.platform.sync.generated.AreaSyncStatusChangedCallback
import ru.tensor.sbis.platform.sync.generated.SyncType

/**
 * Вспомогательный клас для подписки на изменения статусов синхронизации с облаком.
 *
 * @author rv.krohalev
 */
interface AreaSyncStatusPublisher {

    /**
     * Подписка на события инкрементальной синхронизации, которая вызывается при первоначальной загрузке данных,
     * при инициализации обновления данных со стороны пользователя по pull to refresh или по stomp событиям.
     * Подписка происходит на [Schedulers.io] планировщике.
     * [area] - имя области для которой синхронизируются данные.
     */
    fun incrementalSyncStatus(area: String): Observable<AreaStatus>

    /**
     * Подписка на события порционной синхронизации, которая вызывается при подгрузке данных при пагинации.
     * [area] - имя области для которой синхронизируются данные.
     * Подписка происходит на [Schedulers.io] планировщике.
     */
    fun partialSyncStatus(area: String): Observable<AreaStatus>
}

/**
 * Имплементация интерфейса [AreaSyncStatusPublisher].
 * [areaSyncInformer] - Класс контроллера, оповещающий о завершении работы синхронизаторов,
 * связанных с активными областями ui.
 */
class ControllerAreaSyncStatusPublisher(
    private val areaSyncInformer: DependencyProvider<AreaSyncInformer>
) : AreaSyncStatusPublisher {

    /** Смотри [AreaSyncStatusPublisher.incrementalSyncStatus] */
    override fun incrementalSyncStatus(area: String): Observable<AreaStatus> {
        return createAreaStatusObservable(area, SyncType.INCREMENTAL)
            .subscribeOn(Schedulers.io())
    }

    /** Смотри [AreaSyncStatusPublisher.partialSyncStatus] */
    override fun partialSyncStatus(area: String): Observable<AreaStatus> {
        return createAreaStatusObservable(area, SyncType.PARTIAL)
            .subscribeOn(Schedulers.io())
    }

    private fun createAreaStatusObservable(area: String, syncType: SyncType): Observable<AreaStatus> {
        return Observable.create { emitter ->
            var subscription: Subscription? = areaSyncInformer.get().areaSyncStatusChanged(area, syncType)
                .subscribe(object : AreaSyncStatusChangedCallback() {

                    override fun onEvent(syncStatus: AreaStatus) {
                        if (!emitter.isDisposed) {
                            emitter.onNext(syncStatus)
                        }
                    }
                })
                .also { it.enable() }

            emitter.setCancellable {
                subscription?.disable()
                subscription = null
            }

            val initialValue = areaSyncInformer.get().areaSyncStatus(area, syncType)
            if (!emitter.isDisposed) {
                emitter.onNext(initialValue)
            }
        }
    }
}
