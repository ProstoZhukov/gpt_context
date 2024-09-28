package ru.tensor.sbis.mvp.interactor.crudinterface.subscribing

import io.reactivex.Observable
import ru.tensor.sbis.platform.generated.Subscription

/**
 * Параметры для оформления подписки.
 *
 * @author am.boldinov
 */
internal class SubscriptionParams(
    /**
     * Название подписки для дальнейшего управления ей.
     */
    val subscriptionName: String?,
    /**
     * Источник асинхронной подписки.
     */
    val subscriptionSource: Observable<Subscription>,
    /**
     * Если true - подписка работает постоянно и не реагирует на вызовы pause.
     */
    val permanent: Boolean
)
