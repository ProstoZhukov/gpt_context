package ru.tensor.sbis.mvp.interactor.crudinterface.subscribing

import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import ru.tensor.sbis.platform.generated.Subscription

/**
 * Хранитель подписок
 * @author am.boldinov
 */
internal class SubscriptionHolder {

    /**
     * Коллекция именованных подписок.
     */
    private val namedSubscriptions = mutableMapOf<String, Pair<Subscription, Boolean>>()

    /**
     * Коллекция неименованных подписок.
     */
    private val unnamedSubscriptions = mutableListOf<Pair<Subscription, Boolean>>()

    /**
     * Создать ожидатель подписки.
     */
    fun createWaiter(params: SubscriptionParams): Completable {
        return params.subscriptionSource
            .observeOn(AndroidSchedulers.mainThread())
            .map {
                params.subscriptionName?.let { name ->
                    namedSubscriptions[name] = Pair(it, params.permanent)
                } ?: run {
                    unnamedSubscriptions.add(Pair(it, params.permanent))
                }
                return@map it
            }.ignoreElements()
    }

    /**
     * Возобновить все подписки.
     */
    fun resumeSubscriptions() {
        for (subscription in namedSubscriptions.values) {
            subscription.first.enable()
        }
        for (subscription in unnamedSubscriptions) {
            subscription.first.enable()
        }
    }

    /**
     * Приостановить все подписки.
     */
    fun pauseSubscriptions() {
        for (subscription in namedSubscriptions.values) {
            if (!subscription.second) {
                subscription.first.disable()
            }
        }
        for (subscription in unnamedSubscriptions) {
            if (!subscription.second) {
                subscription.first.disable()
            }
        }
    }

    /**
     * Возобновить подписку с указанным названием.
     */
    fun resumeSubscription(subscriptionName: String) {
        namedSubscriptions[subscriptionName]?.first?.enable()
    }

    /**
     * Приостановить подписку с указанным названием.
     */
    fun pauseSubscription(subscriptionName: String) {
        val subscription = namedSubscriptions[subscriptionName]
        if (subscription != null && !subscription.second) {
            subscription.first.disable()
        }
    }

    /**
     * Освободить ресурсы, занимаемые менеджером.
     */
    fun dispose() {
        pauseSubscriptions()
        namedSubscriptions.clear()
        unnamedSubscriptions.clear()
    }
}
