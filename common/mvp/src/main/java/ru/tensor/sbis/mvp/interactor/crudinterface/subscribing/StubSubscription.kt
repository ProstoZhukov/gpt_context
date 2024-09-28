package ru.tensor.sbis.mvp.interactor.crudinterface.subscribing

import ru.tensor.sbis.platform.generated.Subscription

/**
 * Моковая подписка.
 */
@Deprecated("Устаревший подход, переходим на mvi_extension")
object StubSubscription {
    fun stub(): Subscription = MockSubscription()
}

private class MockSubscription : Subscription() {
    override fun enable() = Unit
    override fun disable() = Unit
}
