package ru.tensor.sbis.platform_event_manager

import dagger.Module
import dagger.Provides
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.toolbox_decl.eventmanager.EventManagerServiceSubscriberFactory
import ru.tensor.sbis.platform.generated.EventManagerService

/**
 * Класс используется библиотекой 'Dagger2', отвечает за
 * создание объектов, которые предоставляются интерфейсом [EventManagerComponent]
 *
 * @author unknown
 */
@Module
internal class EventManagerModule {

    @Provides
    fun provideEventManagerServiceSubscriberFactory(service: DependencyProvider<EventManagerService>): EventManagerServiceSubscriberFactory {
        return EventManagerServiceSubscriberFactoryImpl(service)
    }

}