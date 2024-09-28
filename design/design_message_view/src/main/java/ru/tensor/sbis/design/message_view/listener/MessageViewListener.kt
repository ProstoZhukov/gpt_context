package ru.tensor.sbis.design.message_view.listener

import ru.tensor.sbis.design.message_view.listener.events.EventListener
import ru.tensor.sbis.design.message_view.listener.events.MessageViewEvent
import ru.tensor.sbis.design.message_view.ui.MessageView
import kotlin.reflect.KClass

/**
 * Подписка на события [MessageView].
 *
 * @author vv.chekurda
 */
internal class MessageViewListener internal constructor() {

    private val eventListeners: MessageViewEventListeners = hashMapOf()

    /** Изменить слушатели [MessageView]. */
    fun changeEventListeners(changes: MessageViewListenerChanges) {
        MessageViewListenerConfig(eventListeners).apply(changes)
    }

    /** Проверить установлен ли слушатель. */
    fun check(vararg eventType: KClass<out MessageViewEvent>): Boolean =
        eventType.any { eventListeners.containsKey(it) }

    /** @SelfDocumented */
    fun onEvent(event: MessageViewEvent) {
        eventListeners[event::class]?.onEvent(event)
    }
}

internal typealias MessageViewEventListeners = HashMap<KClass<out MessageViewEvent>, EventListener<MessageViewEvent>>