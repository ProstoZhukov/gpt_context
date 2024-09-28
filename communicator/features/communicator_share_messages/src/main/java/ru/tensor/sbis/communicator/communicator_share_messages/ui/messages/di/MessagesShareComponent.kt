package ru.tensor.sbis.communicator.communicator_share_messages.ui.messages.di

import androidx.fragment.app.Fragment
import dagger.BindsInstance
import dagger.Component
import dagger.assisted.AssistedFactory
import ru.tensor.sbis.common.di.CommonSingletonComponent
import ru.tensor.sbis.communicator.communicator_share_messages.ui.messages.ui.MessagesShareController
import ru.tensor.sbis.communicator.communicator_share_messages.ui.messages.ui.MessagesShareView
import ru.tensor.sbis.communicator.declaration.send_message.SendMessageManager
import ru.tensor.sbis.communicator.declaration.send_message.SendMessageUseCase
import ru.tensor.sbis.toolbox_decl.share.ShareData

/**
 * Di-компонент экрана шаринга в сообщения.
 *
 * @author dv.baranov
 */
@MessagesShareScope
@Component(
    dependencies = [CommonSingletonComponent::class],
    modules = [MessagesShareModule::class]
)
internal interface MessagesShareComponent {

    fun injector(): Injector

    val viewFactory: MessagesShareView.Factory

    @Component.Factory
    interface Factory {
        fun create(
            commonSingletonComponent: CommonSingletonComponent,
            @BindsInstance viewFactory: MessagesShareView.Factory,
            @BindsInstance sendMessageManager: SendMessageManager,
            @BindsInstance sendMessageUseCase: SendMessageUseCase,
            @BindsInstance shareData: ShareData,
            @BindsInstance quickShareKey: String?
        ): MessagesShareComponent
    }

    @AssistedFactory
    interface Injector {
        fun inject(fragment: Fragment): MessagesShareController
    }
}
