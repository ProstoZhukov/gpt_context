package ru.tensor.sbis.platform_event_manager

import dagger.Component
import ru.tensor.sbis.common.util.di.PerActivity
import ru.tensor.sbis.toolbox_decl.eventmanager.EventManagerProvider
import ru.tensor.sbis.platform.generated.EventManagerService

/**
 * Интерфейс используется библиотекой 'Dagger2' для предоставления зависимостей [EventManagerService].
 *
 * @author unknown
 */
@PerActivity
@Component(dependencies = [(EventManagerServiceComponent::class)], modules = [(EventManagerModule::class)])
interface EventManagerComponent : EventManagerProvider