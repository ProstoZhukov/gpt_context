package ru.tensor.sbis.common.rx.scheduler

import android.os.Handler
import java.util.concurrent.*
import kotlin.jvm.Throws

/** A [ScheduledFuture] for [HandlerExecutorServiceImpl].
 *
 * Копия из facebook core (fresco)
 */
class ScheduledFutureImpl<V> : RunnableFuture<V>, ScheduledFuture<V> {
    private val mHandler: Handler
    private val mListenableFuture: FutureTask<V>

    constructor(handler: Handler, callable: Callable<V>) {
        mHandler = handler
        mListenableFuture = FutureTask(callable)
    }

    constructor(handler: Handler, runnable: Runnable, result: V?) {
        mHandler = handler
        mListenableFuture = FutureTask<V>(runnable, result)
    }

    override fun getDelay(unit: TimeUnit): Long {
        throw UnsupportedOperationException()
    }

    override fun compareTo(other: Delayed): Int {
        throw UnsupportedOperationException()
    }

    override fun run() {
        mListenableFuture.run()
    }

    override fun cancel(mayInterruptIfRunning: Boolean): Boolean {
        return mListenableFuture.cancel(mayInterruptIfRunning)
    }

    override fun isCancelled(): Boolean {
        return mListenableFuture.isCancelled
    }

    override fun isDone(): Boolean {
        return mListenableFuture.isDone
    }

    @Throws(InterruptedException::class, ExecutionException::class)
    override fun get(): V {
        return mListenableFuture.get()
    }

    @Throws(
        InterruptedException::class,
        ExecutionException::class,
        TimeoutException::class
    )
    override fun get(timeout: Long, unit: TimeUnit): V {
        return mListenableFuture.get(timeout, unit)
    }
}