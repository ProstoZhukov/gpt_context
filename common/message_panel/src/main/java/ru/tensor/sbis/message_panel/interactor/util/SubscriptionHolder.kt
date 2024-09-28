package ru.tensor.sbis.message_panel.interactor.util

import io.reactivex.functions.Cancellable
import ru.tensor.sbis.platform.generated.Subscription

/**
 * Предназначен для избежания утечки памяти при использовании подписки на обновление данных контроллером в RxJava.
 * При вызове [cancel] очищает ссылку на [Subscription]
 *
 * @author us.bessonov
 */
internal class SubscriptionHolder(subscription: Subscription) : Cancellable {

    private var subscriptionRef: Subscription? = subscription

    override fun cancel() {
        subscriptionRef = null
    }
}
