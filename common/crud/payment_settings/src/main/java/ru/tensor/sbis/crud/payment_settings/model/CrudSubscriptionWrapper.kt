package ru.tensor.sbis.crud.payment_settings.model

import ru.tensor.sbis.platform.generated.Subscription

/**
 * Wrapper над CRUD подпиской
 */
class CrudSubscriptionWrapper(private val crudSubscription: Subscription) {
    /** @SelfDocumented */
    fun enable() = crudSubscription.enable()

    /** @SelfDocumented */
    fun disable() = crudSubscription.disable()
}
