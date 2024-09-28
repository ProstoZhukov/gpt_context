package ru.tensor.sbis.communicator.send_message.di

import android.content.Context
import dagger.Component
import ru.tensor.sbis.common.di.CommonSingletonComponent
import ru.tensor.sbis.communicator.declaration.send_message.SendMessageManager
import ru.tensor.sbis.communicator.send_message.contract.SendMessageDependency
import ru.tensor.sbis.communicator.declaration.send_message.SendMessageUseCase
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.pushnotification.controller.notification.base.PushIntentHelper

/**
 * DI компонент отправки сообщений в фоне.
 *
 * @author dv.baranov
 */
@SendMessageScope
@Component(
    dependencies = [CommonSingletonComponent::class, SendMessageDependency::class],
    modules = [SendMessageModule::class]
)
internal interface SendMessageComponent : Feature {
    val context: Context

    val dependency: SendMessageDependency

    val sendMessageManager: SendMessageManager
    val sendMessageUseCase: SendMessageUseCase
    val pushIntentHelper: PushIntentHelper

    @Component.Factory
    interface Factory {
        fun create(
            dependency: SendMessageDependency,
            commonSingletonComponent: CommonSingletonComponent
        ): SendMessageComponent
    }
}
