package ru.tensor.sbis.communicator.send_message.di

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.tensor.sbis.communicator.declaration.send_message.SendMessageManager
import ru.tensor.sbis.communicator.declaration.send_message.SendMessageUseCase
import ru.tensor.sbis.communicator.send_message.interactor.use_case.SendMessageUseCaseImpl
import ru.tensor.sbis.communicator.send_message.manager.SendMessageManagerImpl
import ru.tensor.sbis.pushnotification.controller.notification.base.PushIntentHelper

/**
 * DI модуль отправки сообщений в фоне.
 *
 * @author dv.baranov
 */
@Module
internal class SendMessageModule {

    @SendMessageScope
    @Provides
    internal fun provideSendMessagesManager(
        context: Context
    ): SendMessageManager =
        SendMessageManagerImpl(context)

    @SendMessageScope
    @Provides
    internal fun provideSendMessageUseCase(): SendMessageUseCase = SendMessageUseCaseImpl()

    @SendMessageScope
    @Provides
    internal fun providePushIntentHelper(context: Context): PushIntentHelper = PushIntentHelper(context)
}
