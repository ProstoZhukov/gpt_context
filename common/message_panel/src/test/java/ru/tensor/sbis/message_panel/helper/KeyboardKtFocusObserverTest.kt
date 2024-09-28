package ru.tensor.sbis.message_panel.helper

import org.mockito.kotlin.whenever
import io.reactivex.Observable
import io.reactivex.schedulers.TestScheduler
import io.reactivex.subjects.PublishSubject
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import java.util.concurrent.TimeUnit

/**
 * Тесты метода для запроса и ожидания фокуса
 *
 * @author vv.chekurda
 * @since 1/15/2020
 */
@RunWith(MockitoJUnitRunner.StrictStubs::class)
class KeyboardKtFocusObserverTest : AbstractKeyboardTest() {

    private val immediatelySampler = Observable.just(Unit)
    private val requestSource = PublishSubject.create<Unit>()

    @Test
    fun `When view has focus, then result should be true`() {
        val focusObserver = requestSource.toFocusRequestObservable(view, immediatelySampler).test()
        whenever(inputMethodManager.isActive(view)).thenReturn(true)

        requestSource.onNext(Unit)

        focusObserver.assertValue(true)
    }

    @Test
    fun `When view can't obtain focus, then result should be false`() {
        val focusObserver = requestSource.toFocusRequestObservable(view, immediatelySampler).test()
        whenever(inputMethodManager.isActive(view)).thenReturn(false)

        requestSource.onNext(Unit)

        focusObserver.assertValue(false)
    }

    @Test
    fun `When view obtained focus with delay, result should be true`() {
        val scheduler = TestScheduler()
        val sampler = Observable.interval(FOCUS_CHECK_PERIOD, TimeUnit.MILLISECONDS, scheduler)
        val focusObserver = requestSource.toFocusRequestObservable(view, sampler).test()
        whenever(inputMethodManager.isActive(view)).thenReturn(false)

        requestSource.onNext(Unit)
        // немедленный запрос фокуса не дал результатов
        focusObserver.assertNoValues()

        // ждём
        scheduler.advanceTimeBy(FOCUS_CHECK_PERIOD, TimeUnit.MILLISECONDS)
        // всё ещё нет фокуса -> нет результатов, продолжаем ждать
        focusObserver.assertNoValues()

        // фокус получен
        whenever(inputMethodManager.isActive(view)).thenReturn(true)
        // на следующей итерации фокус будет доступен, ждём ещё
        scheduler.advanceTimeBy(FOCUS_CHECK_PERIOD, TimeUnit.MILLISECONDS)

        focusObserver.assertValue(true)
    }
}