package ru.tensor.sbis.platform_event_manager

import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.toolbox_decl.eventmanager.EventManagerServiceSubscriberFactory
import ru.tensor.sbis.platform.generated.EventManagerService

/**
 * Фабрика для создания подписчика на основе платформенного [EventManagerService]
 *
 * @author unknown
 */
internal class EventManagerServiceSubscriberFactoryImpl(private val service: DependencyProvider<EventManagerService>) :
    EventManagerServiceSubscriberFactory {
    override fun create() = EventManagerServiceSubscriberImpl(service)
}