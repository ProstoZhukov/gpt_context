package ru.tensor.sbis.business.common.domain

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import ru.tensor.sbis.business.common.di.PerFragment
import ru.tensor.sbis.business.common.ui.base.event.ToastEvent
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Менеджер тостов раздела "Деньги"
 */
@PerFragment
class ToastHelper @Inject constructor() {
    private var subject: Subject<ToastEvent> = PublishSubject.create()
    private val exclusions: MutableList<Class<*>> = mutableListOf()
    private var postponedEvent: ToastEvent? = null

    fun post(
        message: String,
        error: Throwable? = null,
    ) {
        post(ToastEvent(text = message, error = error))
    }

    fun post(message: String, receiverId: String) {
        post(ToastEvent(receiverId, message))
    }

    /**
     * Подписаться на событие отображения всплывающего сообщения
     * Нет необходимости использовать [observe] c receiverId если скоупы используются верно
     *
     * @param receiverId id получателя результата
     */
    fun observe(receiverId: String = ""): Observable<ToastEvent> {
        if (subject.hasComplete() || subject.hasThrowable()) {
            subject = PublishSubject.create()
        }
        return subject
            .mergeWith(postPostponed())
            .throttleFirst(ON_TOAST_WINDOW_DELAY_SEC, TimeUnit.SECONDS)
            .filter { filterEvent(receiverId, it) }
            .observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * Добавить исключение из обработки менеджера тостов
     *
     * @param exclusion событие исключение
     */
    fun addExclusion(exclusion: Class<*>) {
        if (exclusions.contains(exclusion).not()) {
            exclusions.add(exclusion)
        }
    }

    fun clearExclusions() {
        exclusions.clear()
    }

    private fun filterEvent(
        receiverId: String,
        event: ToastEvent,
    ): Boolean {
        val fineReceiver = receiverId.isEmpty() || event.receiverId == receiverId
        val notExclude = event.error?.let { exclusions.contains(it::class.java).not() } ?: true
        return fineReceiver && notExclude
    }

    private fun post(event: ToastEvent) =
        if (subject.hasObservers()) {
            subject.onNext(event)
        } else {
            postponedEvent = event
        }

    private fun postPostponed(): Observable<ToastEvent> =
        if (postponedEvent != null) {
            val event = postponedEvent
            postponedEvent = null
            Observable.just(event)
        } else Observable.never()

    private companion object {
        const val ON_TOAST_WINDOW_DELAY_SEC = 3L
    }
}