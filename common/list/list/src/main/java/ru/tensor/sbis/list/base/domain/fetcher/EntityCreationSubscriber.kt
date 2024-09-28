package ru.tensor.sbis.list.base.domain.fetcher

import androidx.annotation.CheckResult
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import ru.tensor.sbis.list.base.domain.boundary.View
import ru.tensor.sbis.list.base.domain.entity.ListScreenEntity
import timber.log.Timber

/**
 * Создатель подписки на обновления "бизнес модели"(БМ).
 * @param ENTITY : ListScreenEntity
 * @property updateSubject Subject<Boolean> по сигналу от сабжекта будет происходить обновление подписки полученной
 * методом [subscribeOnFetchingResult].
 * @property createErrorEntity CreateErrorEntity создать БМ заглушки.
 * @param showStubsImmediate Позволяет отображать заглушки без задержки даже если от контроллера пришел пустой список
 * @constructor
 */
internal class EntityCreationSubscriber<ENTITY : ListScreenEntity>(
    private val updateSubject: Subject<Unit> = PublishSubject.create(),
    private val createErrorEntity: CreateErrorEntity = CreateErrorEntity(),
    private val mainThreadScheduler: Scheduler = AndroidSchedulers.mainThread(),
    private val showStubsImmediate: Boolean = false
) {
    /**
     * Комбинирует событие обновления [updateSubject] и источник данных [entityCreation].
     * Выполняет на [view] действия соответствующий результату получения данных.
     * @param view View
     * @param entityCreation Observable<ENTITY>
     * @return Disposable
     */
    @CheckResult
    fun subscribeOnFetchingResult(
        entityCreation: Observable<ENTITY>,
        view: View<ENTITY>
    ): Disposable {
        return updateSubject
            .startWith(Unit)
            .switchMap {
                entityCreation
                    .observeOn(mainThreadScheduler)
                    .onErrorResumeNext { throwable: Throwable ->
                        Timber.d(
                            IllegalStateException(
                                "Unable to load data for SbisList",
                                throwable
                            )
                        )
                        view.showStub(createErrorEntity(view, updateSubject), showStubsImmediate)
                        return@onErrorResumeNext Observable.empty<ENTITY>()
                    }
            }
            .subscribeOn(Schedulers.io())
            .subscribe { entity ->
                synchronized(entity) {
                    if (entity.isStub()) view.showStub(entity, showStubsImmediate)
                    else if (entity.isData()) view.showData(entity)
                }
            }
    }
}