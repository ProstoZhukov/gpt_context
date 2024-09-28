package ru.tensor.sbis.platform_event_manager

import dagger.BindsInstance
import dagger.Component
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.platform.generated.EventManagerService
import javax.inject.Singleton

/**
 * Интерфейс используется библиотекой 'Dagger2' для предоставления зависимостей [EventManagerService].
 *
 * @author unknown
 */
@Singleton
@Component
internal interface EventManagerServiceComponent {

    fun getEventManagerService(): DependencyProvider<EventManagerService>

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun withEventManagerService(eventManagerService: DependencyProvider<EventManagerService>): Builder

        fun build(): EventManagerServiceComponent

    }

}