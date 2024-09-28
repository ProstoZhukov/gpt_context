package ru.tensor.sbis.crud.sale.crud.refusal_reason

import ru.tensor.sbis.sale.mobile.generated.RefusalReasonSubscription

/**
 * Подписка на события розницы.
 */
internal object RefusalReasonStubSubscription {
    fun stub(): RefusalReasonSubscription = MockRefusalReasonSubscription()
}

private class MockRefusalReasonSubscription : RefusalReasonSubscription() {
    override fun enable() = Unit
    override fun disable() = Unit
}