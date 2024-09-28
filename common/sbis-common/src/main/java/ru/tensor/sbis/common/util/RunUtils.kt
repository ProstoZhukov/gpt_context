package ru.tensor.sbis.common.util

import android.os.Handler
import android.os.Looper
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

/**
 * Запускает action на UI.
 */
fun runOnUi(action: () -> Unit) {
    if (Looper.myLooper() === Looper.getMainLooper())
        action()
    else
        Handler(Looper.getMainLooper()).post(action)
}

/**
 * Запускает action на UI.
 */
fun runOnUiThread(action: () -> Unit): Disposable =
    Completable.fromRunnable { action() }
        .subscribeOn(AndroidSchedulers.mainThread())
        .subscribe()

private const val DELAY_RUN_MS = 150L

/**
 * Запускает отложенно action.
 */
fun delayRun(delay: Long = DELAY_RUN_MS, action: () -> Unit): Disposable =
    Completable.timer(delay, TimeUnit.MILLISECONDS)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe { action() }

/**
 * Запускает отложенно action.
 */
fun CoroutineScope.delayRun(delay: Long = DELAY_RUN_MS, action: () -> Unit) {
    launch {
        delay(delay)
        action()
    }
}