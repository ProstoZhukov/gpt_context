package ru.tensor.sbis.onboarding.domain.util

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import ru.tensor.sbis.onboarding.di.HostScope
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Таймер для планирования / отсчета до события перелистывания страниц приветственного экрана
 *
 * @author as.chadov
 *
 * @property flipDelay интервал перелистывания в мс
 */
@HostScope
internal class FlipTimer @Inject constructor() {

    val flipDelay: Int
        get() = FLIP_INTERVAL.toInt()

    /**
     * @return [Observable] предоставляющий событие перелистывания [FlipEvent]
     */
    fun observeFlip(
        onSubscribe: () -> Unit = {},
        onDispose: () -> Unit = {}
    ): Observable<FlipEvent> =
        flipChannel
            .doOnSubscribe {
                onSubscribe()
                startCountdown()
            }
            .doOnDispose {
                stopCountdown()
                onDispose()
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    /**
     * @return [Observable] предоставляющий событие отсчета к событию перелистывания
     */
    private fun observeCountdown(): Observable<Unit> =
        Observable.intervalRange(
            0L,
            updateCount,
            FLIP_INIT_DELAY,
            PROGRESS_UPDATE_PERIOD,
            TimeUnit.MILLISECONDS
        )
            .map { step ->
                postCountdown((step * PROGRESS_UPDATE_PERIOD).toInt())
            }
            .subscribeOn(Schedulers.io())

    private fun startCountdown() {
        flipDisposable = observeCountdown()
            .doOnSubscribe {
                postCountdown(0)
            }
            .doOnComplete {
                flipChannel.onNext(FlipEvent(flipDelay, true))
                restartCountdown()
            }
            .subscribe()
    }

    private fun restartCountdown() {
        stopCountdown()
        startCountdown()
    }

    private fun stopCountdown() = flipDisposable?.dispose()

    private fun postCountdown(progress: Int) = flipChannel.onNext(FlipEvent(progress))

    private val updateCount: Long
        get() = FLIP_INTERVAL / PROGRESS_UPDATE_PERIOD

    private val flipChannel = PublishSubject.create<FlipEvent>()
    private var flipDisposable: Disposable? = null

    companion object {
        /**
         * Интервал перелистывания
         */
        private const val FLIP_INTERVAL = 7_000L
        /**
         * Задержка рестарта отсчета
         */
        private const val FLIP_INIT_DELAY = 500L
        /**
         * Интервал обновления прогресса
         */
        private const val PROGRESS_UPDATE_PERIOD = 20L
    }
}

/**
 * Событие перелистывания и отсчета к перелистыванию
 *
 * @param progress прогресс отсчета в интервале от 0 до [FlipTimer.flipDelay]
 * @param isCompleted перелистывание готово
 */
internal class FlipEvent(
    val progress: Int,
    val isCompleted: Boolean = false
)