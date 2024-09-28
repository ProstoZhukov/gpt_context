package ru.tensor.sbis.business.common.domain

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import ru.tensor.sbis.business.common.di.PerFragment
import ru.tensor.sbis.business.common.ui.base.event.PopupNotificationEvent
import ru.tensor.sbis.design_notification.popup.SbisPopupNotificationStyle
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Менеджер информеров.
 *
 * @see <a http://axure.tensor.ru/MobileStandart8/#p=панель-информер_v2&g=1">Панель-информер</a>
 *
 * @author aa.kobeleva
 */
@PerFragment
class PopupNotificationHelper @Inject constructor() {
    private var subject: Subject<PopupNotificationEvent> = PublishSubject.create()
    private val exclusions: MutableList<Class<*>> = mutableListOf()
    private var postponedEvent: PopupNotificationEvent? = null

    /** @SelfDocumented */
    fun post(
        message: String,
        style: SbisPopupNotificationStyle = SbisPopupNotificationStyle.ERROR,
        error: Throwable? = null,
        icon: String? = null
    ) = post(PopupNotificationEvent(message, style, error, icon))

    /**
     * Подписаться на событие отображения всплывающего сообщения.
     */
    fun observe(): Observable<PopupNotificationEvent> {
        if (subject.hasComplete() || subject.hasThrowable()) {
            subject = PublishSubject.create()
        }
        return subject
            .mergeWith(postPostponed())
            .throttleFirst(ON_POPUP_NOTIFICATION_WINDOW_DELAY_SEC, TimeUnit.SECONDS)
            .filter(::filterEvent)
            .observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * Добавить исключение из обработки менеджера информеров.
     *
     * @param exclusion событие исключение
     */
    fun addExclusion(exclusion: Class<*>) {
        if (exclusions.contains(exclusion).not()) {
            exclusions.add(exclusion)
        }
    }

    private fun post(event: PopupNotificationEvent) =
        if (subject.hasObservers()) {
            subject.onNext(event)
        } else {
            postponedEvent = event
        }

    private fun postPostponed(): Observable<PopupNotificationEvent> =
        if (postponedEvent != null) {
            val event = postponedEvent
            postponedEvent = null
            Observable.just(event)
        } else {
            Observable.never()
        }

    private fun filterEvent(
        event: PopupNotificationEvent,
    ): Boolean {
        return event.error?.let { exclusions.contains(it::class.java).not() } ?: true
    }

    private companion object {
        const val ON_POPUP_NOTIFICATION_WINDOW_DELAY_SEC = 3L
    }
}
