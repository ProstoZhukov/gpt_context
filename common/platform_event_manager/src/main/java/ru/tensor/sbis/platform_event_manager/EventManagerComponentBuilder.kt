package ru.tensor.sbis.platform_event_manager

import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.platform.generated.EventManagerService

/**
 * Билдер для построения и получения экземпляра [EventManagerComponent].
 *
 * @author unknown
 */
fun build(): EventManagerComponent {

    val eventManagerService = DependencyProvider.create {
        EventManagerService.instance()
    }

    val eventManagerServiceComponent = DaggerEventManagerServiceComponent
            .builder()
            .withEventManagerService(eventManagerService)
            .build()

    return DaggerEventManagerComponent.builder()
            .eventManagerServiceComponent(eventManagerServiceComponent)
            .eventManagerModule(EventManagerModule())
            .build()
}