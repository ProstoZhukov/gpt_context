package ru.tensor.sbis.common.rx.consumer

import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.annotations.CheckReturnValue
import io.reactivex.annotations.SchedulerSupport
import io.reactivex.functions.Consumer
import ru.tensor.sbis.common.util.AppConfig
import timber.log.Timber

/**
 * Обработчик ошибок, предоставляющий возможность определения действия
 * при возникновении непредвиденных ошибкок в релизной версии приложения.
 *
 * @param errorMessage - более детальный месседж ошибки (надеюсь)
 * @author Subbotenko Dmitry
 */
@Deprecated("Использование потеряло актуальность, используйте обычный subscribe")
open class KFallbackErrorConsumer @JvmOverloads constructor(
        private val errorMessage: String? = null,
        private val additionalStackTrace: Array<StackTraceElement>? = null
) : Consumer<Throwable> {

    companion object DEFAULT : KFallbackErrorConsumer(), (Throwable) -> Unit {
        override fun invoke(throwable: Throwable) = fallback(throwable)

        val tensorStackTrace
            get() = Throwable().stackTrace
                    .filter { it.className.contains("ru.tensor") }
                    .toTypedArray()
    }

    @Throws(Exception::class)
    override fun accept(throwable: Throwable) = fallback(throwable, errorMessage)

    /**
     * Бросить ошибку. Проверяет отладочную версию и позволяет выставить специфическое сообщение.
     * Также мдифицирует трейс вызова, для того чтобы из стека можно было понять откуда была вызвана
     * ошибка (для RX)
     *
     * @param throwable возникшая ошибка
     * @param errorMessage более детальный месседж ошибки
     */
    @JvmOverloads
    fun fallback(throwable: Throwable, errorMessage: String? = null) {
        if (!additionalStackTrace.isNullOrEmpty())
            throwable.stackTrace = additionalStackTrace + throwable.stackTrace

        if (AppConfig.isDebug()) {
            debugFallback(throwable, errorMessage)
        } else {
            releaseFallback(throwable, errorMessage)
        }
    }

    private fun debugFallback(throwable: Throwable, errorMessage: String? = null): Nothing =
            throw RuntimeException(errorMessage
                    ?: KFallbackErrorConsumer::class.java.simpleName, throwable)

    private fun releaseFallback(throwable: Throwable, errorMessage: String? = null) {
        errorMessage?.run {
            Timber.e(throwable, errorMessage)
        } ?: Timber.e(throwable)
    }
}

@CheckReturnValue
@SchedulerSupport(SchedulerSupport.NONE)
@Deprecated("Использование потеряло актуальность, используйте обычный subscribe")
fun <T> Observable<T>.subscribeWithFallback(onNext: Consumer<in T>) = subscribe(onNext, KFallbackErrorConsumer(additionalStackTrace = KFallbackErrorConsumer.tensorStackTrace))!!

@CheckReturnValue
@SchedulerSupport(SchedulerSupport.NONE)
@Deprecated("Использование потеряло актуальность, используйте обычный subscribe")
fun <T> Observable<T>.subscribeWithFallback(onNext: (T) -> Unit) = subscribe(Consumer(onNext), KFallbackErrorConsumer(additionalStackTrace = KFallbackErrorConsumer.tensorStackTrace))!!

@CheckReturnValue
@SchedulerSupport(SchedulerSupport.NONE)
@Deprecated("Использование потеряло актуальность, используйте обычный subscribe")
fun <T> Single<T>.subscribeWithFallback(onNext: Consumer<in T>) = subscribe(onNext, KFallbackErrorConsumer(additionalStackTrace = KFallbackErrorConsumer.tensorStackTrace))

@CheckReturnValue
@SchedulerSupport(SchedulerSupport.NONE)
@Deprecated("Использование потеряло актуальность, используйте обычный subscribe")
fun <T> Single<T>.subscribeWithFallback(onNext: (T) -> Unit) = subscribe(Consumer(onNext), KFallbackErrorConsumer(additionalStackTrace = KFallbackErrorConsumer.tensorStackTrace))

@CheckReturnValue
@SchedulerSupport(SchedulerSupport.NONE)
@Deprecated("Использование потеряло актуальность, используйте обычный subscribe")
fun <T> Maybe<T>.subscribeWithFallback(onNext: Consumer<in T>) = subscribe(onNext, KFallbackErrorConsumer(additionalStackTrace = KFallbackErrorConsumer.tensorStackTrace))!!

@CheckReturnValue
@SchedulerSupport(SchedulerSupport.NONE)
@Deprecated("Использование потеряло актуальность, используйте обычный subscribe")
fun <T> Maybe<T>.subscribeWithFallback(onNext: (T) -> Unit) = subscribe(Consumer(onNext), KFallbackErrorConsumer(additionalStackTrace = KFallbackErrorConsumer.tensorStackTrace))!!

@CheckReturnValue
@SchedulerSupport(SchedulerSupport.NONE)
@Deprecated("Использование потеряло актуальность, используйте обычный subscribe")
fun <T> Flowable<T>.subscribeWithFallback(onNext: Consumer<in T>) = subscribe(onNext, KFallbackErrorConsumer(additionalStackTrace = KFallbackErrorConsumer.tensorStackTrace))!!

@CheckReturnValue
@SchedulerSupport(SchedulerSupport.NONE)
@Deprecated("Использование потеряло актуальность, используйте обычный subscribe")
fun <T> Flowable<T>.subscribeWithFallback(onNext: (T) -> Unit) = subscribe(Consumer(onNext), KFallbackErrorConsumer(additionalStackTrace = KFallbackErrorConsumer.tensorStackTrace))!!
