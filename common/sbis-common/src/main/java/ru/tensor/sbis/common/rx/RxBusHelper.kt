package ru.tensor.sbis.common.rx

import kotlinx.coroutines.launch
import ru.tensor.sbis.event_bus.EventBus

internal fun postEventOnEventBus(event: Any) {
    EventBus.scope.launch {
        EventBus.postWithoutRx(event)
    }
}