/**
 * Пакет с функциями-расширениями для подписки на соответсвующий контроллер
 */
@file:JvmName("SubscriptionToRxUtils")
package ru.tensor.sbis.communicator.sbis_conversation.utils

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import ru.tensor.sbis.communicator.contacts_declaration.controller.ContactsControllerWrapper
import ru.tensor.sbis.communicator.contacts_declaration.model.ContactsDataRefreshCallback
import timber.log.Timber

/** @SelfDocumented */
fun ContactsControllerWrapper.subscribeDataRefresh(): Observable<Unit> {
    val subject = PublishSubject.create<Unit>().toSerialized()
    val subscription = setDataRefreshCallback(object : ContactsDataRefreshCallback {
        override fun execute(params: HashMap<String, String>) {
            try {
                subject.onNext(Unit)
            }
            catch (e: Exception) {
                Timber.e(e)
            }
        }
    })

    return subject.doOnDispose {
        subscription.disable()
    }
}
