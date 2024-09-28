package ru.tensor.sbis.event_bus

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

/**
 * Метод, который отправляет ивент в [EventBus] через глобальный скоуп.
 * Необходим для уменьшения избыточности использования EventBus через Java.
 */
fun postEventOnEventBusScope(event: Any) {
    EventBus.scope.launch {
        EventBus.post(event)
    }
}

/**
 * Метод, подписки на ивент в [EventBus] через глобальный скоуп.
 * Необходим для уменьшения избыточности использования EventBus через Java.
 */
inline fun <T> subscribeOnEventBusScope(clazz: Class<T>, crossinline onEvent: (T) -> Unit) {
    EventBus.scope.launch {
        EventBus.subscribe(clazz, onEvent)
    }
}

/**
 * Метод, подписки на ивент в [EventBus] через [LifecycleOwner].
 * Необходим для уменьшения избыточности использования EventBus через Java.
 */
inline fun <T> subscribeOnLifecycleScope(clazz: Class<T>, viewLifecycleOwner: LifecycleOwner, crossinline onEvent: (T) -> Unit) {
    viewLifecycleOwner.lifecycleScope.launch {
        EventBus.subscribe(clazz, onEvent)
    }
}