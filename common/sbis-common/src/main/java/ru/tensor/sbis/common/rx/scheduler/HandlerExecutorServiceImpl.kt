package ru.tensor.sbis.common.rx.scheduler

import android.os.Handler
import java.util.concurrent.AbstractExecutorService
import java.util.concurrent.Callable
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import kotlin.jvm.Throws

/** A [HandlerExecutorService] implementation.
 *
 * Копия из facebook core (fresco)
 */
class HandlerExecutorServiceImpl(
    private val mHandler: Handler
) : AbstractExecutorService(), HandlerExecutorService {

    override fun shutdown() {
        throw UnsupportedOperationException()
    }

    override fun shutdownNow(): List<Runnable> {
        throw UnsupportedOperationException()
    }

    override fun isShutdown(): Boolean {
        return false
    }

    override fun isTerminated(): Boolean {
        return false
    }

    @Throws(InterruptedException::class)
    override fun awaitTermination(timeout: Long, unit: TimeUnit): Boolean {
        throw UnsupportedOperationException()
    }

    override fun execute(command: Runnable) {
        mHandler.post(command)
    }

    override fun <T> newTaskFor(runnable: Runnable, value: T?): ScheduledFutureImpl<T> {
        return ScheduledFutureImpl(mHandler, runnable, value)
    }

    override fun <T> newTaskFor(callable: Callable<T>): ScheduledFutureImpl<T> {
        return ScheduledFutureImpl(mHandler, callable)
    }

    override fun submit(task: Runnable): ScheduledFuture<*> {
        return submit<Void?>(task, null as Void?)
    }

    override fun <T> submit(task: Runnable?, result: T?): ScheduledFuture<T> {
        if (task == null) throw NullPointerException()
        val future = newTaskFor(task, result)
        execute(future)
        return future
    }

    override fun <T> submit(task: Callable<T>?): ScheduledFuture<T> {
        if (task == null) throw NullPointerException()
        val future = newTaskFor(task)
        execute(future)
        return future
    }

    override fun schedule(command: Runnable, delay: Long, unit: TimeUnit): ScheduledFuture<*> {
        val future: ScheduledFutureImpl<*> = newTaskFor<Any?>(command, null)
        mHandler.postDelayed(future, unit.toMillis(delay))
        return future
    }

    override fun <V> schedule(callable: Callable<V>, delay: Long, unit: TimeUnit): ScheduledFuture<V> {
        val future = newTaskFor(callable)
        mHandler.postDelayed(future, unit.toMillis(delay))
        return future
    }

    override fun scheduleAtFixedRate(
        command: Runnable, initialDelay: Long, period: Long, unit: TimeUnit
    ): ScheduledFuture<*> {
        throw UnsupportedOperationException()
    }

    override fun scheduleWithFixedDelay(
        command: Runnable, initialDelay: Long, delay: Long, unit: TimeUnit
    ): ScheduledFuture<*> {
        throw UnsupportedOperationException()
    }

    override fun quit() {
        mHandler.looper.quit()
    }

    override fun isHandlerThread(): Boolean {
        return Thread.currentThread() === mHandler.looper.thread
    }

}