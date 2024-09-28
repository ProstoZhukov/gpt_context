package ru.tensor.sbis.mvp.interactor.crudinterface.event

import androidx.annotation.WorkerThread
import ru.tensor.sbis.platform.generated.Subscription

/**
 * Фабрика для создания подписки на события микросервиса
 *
 * @author ev.grigoreva
 */
@Deprecated("Устаревший подход, переходим на mvi_extension")
fun interface ServiceSubscriptionFactory {

    /**
     * Создание подписки на события от конкретного микросервиса
     *
     * @param eventName имя события для подписки
     * @param onEventAction обработка события
     */
    @WorkerThread
    fun createSubscription(eventName: String, onEventAction: (EventData) -> Unit): Subscription
}