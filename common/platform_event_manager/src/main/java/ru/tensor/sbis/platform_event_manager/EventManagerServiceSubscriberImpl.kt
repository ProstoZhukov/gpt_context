package ru.tensor.sbis.platform_event_manager

import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.toolbox_decl.eventmanager.EventManagerServiceSubscriber
import ru.tensor.sbis.platform.generated.EventCallback
import ru.tensor.sbis.platform.generated.EventManagerService
import ru.tensor.sbis.platform.generated.Subscription
import timber.log.Timber
import java.lang.ref.WeakReference
import java.util.*

/**
 * Реализация интерфейса [EventManagerServiceSubscriber] подписчика для платформенного [EventManagerService]
 *
 * @author unknown
 */
internal class EventManagerServiceSubscriberImpl(private val eventManagerService: DependencyProvider<EventManagerService>) :
    EventManagerServiceSubscriber {

    private val subscriptions = LinkedList<Subscription>()

    override fun subscribeOnEvent(eventKey: String, eventCallback: (HashMap<String, String>) -> Unit) {
        try {
            val eventCallbackRef = WeakReference(eventCallback)
            subscriptions.add(eventManagerService.get().addEventCallback(eventKey, object : EventCallback() {
                override fun onEvent(p0: String, p1: HashMap<String, String>) {
                    eventCallbackRef.get()?.invoke(p1)
                }
            }))
        } catch(ex: Exception) {
            Timber.e(ex, "Failed to subscribe on event $eventKey")
        }
    }

    override fun enableSubscriptions() {
        subscriptions.forEach { subscription -> subscription.enable()}
    }

    override fun disableSubscriptions() {
        subscriptions.forEach { subscription -> subscription.disable()}
    }

}