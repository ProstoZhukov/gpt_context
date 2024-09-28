@file:JvmName("SubscriptionToRxUtils")
package ru.tensor.sbis.communicator.core.utils

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import ru.tensor.sbis.communicator.common.crud.ThemeRepository
import ru.tensor.sbis.communicator.generated.DataRefreshedThemeControllerCallback
import ru.tensor.sbis.communicator.generated.TypingNotificatorCallback
import java.util.*
import kotlin.collections.HashMap

/**
 * Класс-утилита с функциями-расширениями для подписки на соответсвующий контроллер
 */

/** @SelfDocumented */
fun ThemeRepository.subscribeDataRefresh(): Observable<HashMap<String, String>> {
    val subject = PublishSubject.create<HashMap<String, String>>().toSerialized()
    val subscription = subscribeDataRefreshedEvent(object : DataRefreshedThemeControllerCallback() {
        override fun onEvent(params: HashMap<String, String>) {
            subject.onNext(params)
        }
    })

    return subject.doOnDispose {
        subscription.disable()
    }
}

/** @SelfDocumented */
fun ThemeRepository.subscribeTypingUsers(): Observable<List<String>> {
        val subject = PublishSubject.create<List<String>>().toSerialized()
        val subscription = subscribeTypingUsers(object : TypingNotificatorCallback() {
            override fun onEvent(typingUsers: ArrayList<String>) {
                subject.onNext(typingUsers)
            }
        })

        return subject.doOnDispose {
            subscription.disable()
        }
    }
