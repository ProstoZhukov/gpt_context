package ru.tensor.sbis.pushnotification.util.counters

import android.annotation.SuppressLint
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.tensor.sbis.common.lifecycle.AppLifecycleTracker
import java.util.concurrent.atomic.AtomicInteger

/**
 * Предназначен для обновления значения счётчика уведомлений у иконки приложения.
 * Необходим для Launcher'ов, в которых значение счётчика не формируется на основе числа активных уведомлений, а должно
 * быть задано явно
 */
class AppIconCounterUpdater(
    private val notificationBadge: NotificationBadge,
    private val lifecycleTracker: AppLifecycleTracker
) {

    private val counter = AtomicInteger(0)

    private val scope = CoroutineScope(Dispatchers.Default)

    init {
        scope.launch {
            subscribeToForegroundEvents()
        }
    }

    @SuppressLint("CheckResult")
    private suspend fun subscribeToForegroundEvents() {
        lifecycleTracker.appForegroundStateFlow
            .collectLatest { appIsInForeground ->
                if (appIsInForeground) {
                    removeCounter()
                }
            }
    }

    /**
     * Увеличивает текущее значение счётчика, если это возможно для используемого Launcher'а
     */
    fun incrementCounter() {
        tryToUpdateCounter(counter.incrementAndGet())
    }

    /**
     * Сбрасывает текущее значение счётчика
     */
    fun removeCounter() {
        counter.set(0)
        updateCounter(0)
    }

    /**
     * Обновление счетчика событий для иконки приложения, выполняется только если приложение находится в свернутом
     * состоянии
     */
    private fun tryToUpdateCounter(count: Int) {
        if (!lifecycleTracker.isAppInForeground) {
            updateCounter(count)
        }
    }

    private fun updateCounter(count: Int) {
        Completable
            .fromAction { notificationBadge.applyCount(count) }
            .subscribeOn(Schedulers.single())
            .subscribe()
    }
}