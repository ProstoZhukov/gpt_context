package ru.tensor.sbis.common.util

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.SerialDisposable
import timber.log.Timber

fun Disposable.storeIn(compositeDisposable: CompositeDisposable) {
    compositeDisposable.add(this)
}

fun Disposable.storeIn(serialDisposable: SerialDisposable) {
    serialDisposable.set(this)
}

fun <T> Single<T>.logAndIgnoreError(): Observable<T> = toObservable().logAndIgnoreError()

fun <T> Observable<T>.logAndIgnoreError(): Observable<T> = onErrorResumeNext { error: Throwable ->
    Timber.e(error)
    Observable.empty()
}

/**
 * Возвращает [Single] от применения функции [mapper] к результату завершения [Maybe].
 * Результат будет `null` при завершении [Maybe] через `onComplete`.
 * Отличается от [Maybe.flatMapSingle] тем, что не кидает [NoSuchElementException] при завершении пустого [Maybe]
 */
fun <T, R> Maybe<T>.flatMapSingleNullable(mapper: (maybeValue: T?) -> Single<R>): Single<R> =
    materialize()
        .flatMap { notification ->
            if (notification.isOnError)
                Single.error(notification.error)
            else
                mapper(notification.value)
        }

/**
 * Возвращает [Completable] от применения функции [mapper] к результату завершения [Maybe].
 * Отличается от [Maybe.flatMapCompletable] тем, что [mapper] выполняется
 * не только для завершения через `onSuccess`, но и для завершения через `onComplete` (со значением null).
 */
fun <T> Maybe<T>.flatMapCompletableNullable(mapper: (maybeValue: T?) -> Completable): Completable =
    materialize()
        .flatMapCompletable { notification ->
            if (notification.isOnError)
                Completable.error(notification.error)
            else
                mapper(notification.value)
        }