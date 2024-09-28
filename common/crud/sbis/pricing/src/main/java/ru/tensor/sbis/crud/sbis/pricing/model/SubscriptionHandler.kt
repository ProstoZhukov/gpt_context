package ru.tensor.sbis.crud.sbis.pricing.model

import ru.tensor.sbis.common.generated.ITableSubscriptionHandler

/**
 * Обертка над [ITableSubscriptionHandler]
 */
class SubscriptionHandler(var subscriptionHandler: ITableSubscriptionHandler?) {

    fun disable() {
        subscriptionHandler = null
    }
}