@file:JvmName("SubscriptionToRxUtils")
package ru.tensor.sbis.communicator.contacts_registry.utils

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import ru.tensor.sbis.communicator.contacts_declaration.controller.ContactsControllerWrapper
import ru.tensor.sbis.communicator.contacts_declaration.model.ContactsDataRefreshCallback
import ru.tensor.sbis.communicator.generated.DataRefreshedMessageControllerCallback
import ru.tensor.sbis.communicator.generated.MessageController
import ru.tensor.sbis.profile_service.controller.profile_settings.ProfileSettingsControllerWrapper

/**
 * Подписаться на контроллер контактов [ContactsControllerWrapper] для получения события обновления.
 */
fun ContactsControllerWrapper.subscribeDataRefresh(): Observable<Unit> {
    val subject = PublishSubject.create<Unit>().toSerialized()
    val subscription = setDataRefreshCallback(object : ContactsDataRefreshCallback {
        override fun execute(params: HashMap<String, String>) {
            subject.onNext(Unit)
        }
    })

    return subject
        .doOnSubscribe { subscription.enable() }
        .doOnDispose { subscription.disable() }
}

fun MessageController.subscribeMessageSentEvents(): Observable<Unit> {
    val subject = PublishSubject.create<Unit>().toSerialized()
    val subscription = this.dataRefreshed().subscribe(object : DataRefreshedMessageControllerCallback() {
        override fun onEvent(param: HashMap<String, String>) {
            if (param["message_status"] == "sent") {
                subject.onNext(Unit)
            }
        }
    })

    return subject
        .doOnSubscribe { subscription.enable() }
        .doOnDispose { subscription.disable() }
}

/** @SelfDocumented */
fun ProfileSettingsControllerWrapper.subscribeProfileSettingsEvents(): Observable<Unit> {
    val subject = PublishSubject.create<Unit>().toSerialized()
    val subscription = this.subscribeDataRefreshedEvent { _: HashMap<String, String> ->
        subject.onNext(Unit)
    }
    return subject
        .doOnSubscribe { subscription.enable() }
        .doOnDispose { subscription.disable() }
}