package ru.tensor.sbis.common.util

import android.view.View
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

/**
 * Помощники предобработки кликов
 */
fun View.subscribeThrottleClick(timeout: Long = THROTTLE_BUTTON): Observable<Any> =
    RxView.clicks(this)
        .throttleFirst(timeout, TimeUnit.MILLISECONDS)
        .observeOn(AndroidSchedulers.mainThread())

/**@SelfDocumented*/
fun <T> Observable<T>.subscribeThrottleClick(timeout: Long = THROTTLE_BUTTON, function: (T) -> Unit): Disposable =
    throttleFirst(timeout, TimeUnit.MILLISECONDS)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(function)

/**
 * Константы времени для тротлинга в мс
 */
const val THROTTLE_BUTTON = 1000L
const val THROTTLE_IMAGE = 2000L