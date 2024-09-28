package ru.tensor.sbis.list.base.data.utils

/**
 * Необходим для тестов для делегирования создания [SubscriptionHolder].
 */
internal class CreateSubscriptionHolder internal constructor(
) : Function1<Any, SubscriptionHolder> {

    override fun invoke(subscription: Any): SubscriptionHolder {
        return SubscriptionHolder(subscription)
    }
}