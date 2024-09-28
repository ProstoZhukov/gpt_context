package ru.tensor.sbis.design.message_view.listener

import ru.tensor.sbis.design.message_view.listener.events.EventListener
import ru.tensor.sbis.design.message_view.listener.events.MessageViewEvent
import ru.tensor.sbis.design.message_view.ui.MessageView
import kotlin.reflect.KClass

/**
 * Конфигуратор слушателей событий [MessageView].
 *
 * @author vv.chekurda
 */
class MessageViewListenerConfig internal constructor(
    private val eventListeners: MessageViewEventListeners
) {

    /** @SelfDocumented */
    @Suppress("UNCHECKED_CAST")
    fun <E : MessageViewEvent> set(eventType: KClass<E>, listener: EventListener<E>) {
        eventListeners[eventType] = listener as EventListener<MessageViewEvent>
    }

    /** @SelfDocumented */
    fun <E : MessageViewEvent> remove(eventType: KClass<E>) {
        eventListeners.remove(eventType)
    }

    /** @SelfDocumented */
    fun clear() {
        eventListeners.clear()
    }
}

typealias MessageViewListenerChanges = MessageViewListenerConfig.() -> Unit