package ru.tensor.sbis.communicator.crud.di

import dagger.Component
import ru.tensor.sbis.communicator.common.di.CommunicatorCommonComponent
import javax.inject.Scope

@Scope
@kotlin.annotation.Retention
internal annotation class CommunicatorCrudScope

@CommunicatorCrudScope
@Component(
    dependencies = [(CommunicatorCommonComponent::class)],
    modules = [(CommunicatorCrudModule::class)]
)
internal interface CommunicatorCrudComponent