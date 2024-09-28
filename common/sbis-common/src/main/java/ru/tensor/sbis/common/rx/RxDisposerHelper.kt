package ru.tensor.sbis.common.rx

import io.reactivex.*
import io.reactivex.internal.disposables.DisposableContainer
import io.reactivex.disposables.Disposable
import org.reactivestreams.Publisher
import timber.log.Timber

/**
 * @author Subbotenko Dmitry
 */
operator fun DisposableContainer.plusAssign(d: Disposable) {
    add(d)
}

/**
 * Позволяет использовать подписки с Nullable данными
 */
open class RxContainer<out A>(val value: A?, val fromView: Boolean = false)

/**
 * Функция логирует и позворяет увидеть вживую жизненный цикл подписки
 */
@Deprecated("Использовать только для отладки, удалять перед мерджем!", ReplaceWith(" // отладочный лог удален"))
fun <T : Any> Observable<T>.log(tag: String) = compose(LoggerTransformer(tag))!!

/**
 * Функция логирует и позворяет увидеть вживую жизненный цикл подписки
 */
@Deprecated("Использовать только для отладки, удалять перед мерджем!", ReplaceWith(" // отладочный лог удален"))
fun <T : Any> Single<T>.log(tag: String) = compose(LoggerTransformer(tag))!!

/**
 * Функция логирует и позворяет увидеть вживую жизненный цикл подписки
 */
@Deprecated("Использовать только для отладки, удалять перед мерджем!", ReplaceWith(" // отладочный лог удален"))
fun <T : Any> Maybe<T>.log(tag: String) = compose(LoggerTransformer(tag))!!

/**
 * Функция логирует и позворяет увидеть вживую жизненный цикл подписки
 */
@Deprecated("Использовать только для отладки, удалять перед мерджем!", ReplaceWith(" // отладочный лог удален"))
fun <T : Any> Flowable<T>.log(tag: String) = compose(LoggerTransformer(tag))!!

class LoggerTransformer<T>(val tag: String) :
        ObservableTransformer<T, T>,
        SingleTransformer<T, T>,
        MaybeTransformer<T, T>,
        FlowableTransformer<T, T> {
    override fun apply(upstream: Single<T>): SingleSource<T> = upstream
            .doOnSubscribe { Timber.d("$tag doOnSubscribe ") }
            .doOnSuccess { Timber.d("$tag doOnSuccess $it") }
            .doOnDispose { Timber.d("$tag doOnDispose ") }
            .doOnError { Timber.d("$tag doOnError $it"); Timber.d(it) }

    override fun apply(upstream: Maybe<T>): MaybeSource<T> = upstream
            .doOnSubscribe { Timber.d("$tag doOnSubscribe ") }
            .doOnSuccess { Timber.d("$tag doOnSuccess $it") }
            .doOnDispose { Timber.d("$tag doOnDispose ") }
            .doOnError { Timber.d("$tag doOnError $it"); Timber.d(it) }
            .doOnComplete { Timber.d("$tag doOnComplete ") }

    override fun apply(upstream: Flowable<T>): Publisher<T> = upstream
            .doOnSubscribe { Timber.d("$tag doOnSubscribe ") }
            .doOnComplete { Timber.d("$tag doOnComplete ") }
            .doOnNext { Timber.d("$tag doOnNext $it") }
            .doOnTerminate { Timber.d("$tag doOnTerminate ") }
            .doOnError { Timber.d("$tag doOnError $it"); Timber.d(it) }
            .doOnCancel { Timber.d("$tag doOnCancel ") }
            .doOnRequest { Timber.d("$tag doOnRequest $it") }

    override fun apply(upstream: Observable<T>): ObservableSource<T> = upstream
            .doOnSubscribe { Timber.d("$tag doOnSubscribe ") }
            .doOnComplete { Timber.d("$tag doOnComplete ") }
            .doOnNext { Timber.d("$tag doOnNext $it") }
            .doOnTerminate { Timber.d("$tag doOnTerminate ") }
            .doOnDispose { Timber.d("$tag doOnDispose ") }
            .doOnError { Timber.d("$tag doOnError $it"); Timber.d(it) }
}