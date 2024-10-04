package ru.tensor.sbis.design.message_panel.di.vm

import dagger.Module

/**
 * Модуль создаёт vm в два контура
 * - контур [MessagePanelViewModelFactoryModule] лениво создаёт vm внутри фабрики
 * - контур [MessagePanelViewModelFactoryComponent] получает vm из store или создаёт через первый контур
 *
 * @author ma.kolpakov
 */
@Module(
    subcomponents = [MessagePanelViewModelFactoryComponent::class],
    includes = [MessagePanelViewModelFactoryModule::class]
)
internal class MessagePanelViewModelModule
