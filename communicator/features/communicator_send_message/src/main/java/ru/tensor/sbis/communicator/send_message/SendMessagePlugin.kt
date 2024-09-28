package ru.tensor.sbis.communicator.send_message

import ru.tensor.sbis.attachments.decl.action.AddAttachmentsUseCase
import ru.tensor.sbis.common.di.CommonSingletonComponent
import ru.tensor.sbis.communicator.declaration.send_message.SendMessageManager
import ru.tensor.sbis.communicator.declaration.send_message.SendMessageUseCase
import ru.tensor.sbis.communicator.send_message.contract.SendMessageDependency
import ru.tensor.sbis.communicator.send_message.di.DaggerSendMessageComponent
import ru.tensor.sbis.communicator.send_message.di.SendMessageComponent
import ru.tensor.sbis.communicator.send_message.worker.SendMessageWorker
import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper
import ru.tensor.sbis.verification_decl.login.LoginInterface
import ru.tensor.sbis.verification_decl.login.event.AuthEvent

/**
 * Плагин модуля отправки сообщений в фоне.
 *
 * @author dv.baranov
 */
object SendMessagePlugin : BasePlugin<Unit>() {

    private val sendMessageManager by lazy {
        SendMessageManager.Provider { sendMessageComponent.sendMessageManager }
    }

    private lateinit var addAttachmentsUseCaseProvider: FeatureProvider<AddAttachmentsUseCase>
    private lateinit var commonSingletonComponentProvider: FeatureProvider<CommonSingletonComponent>
    private var loginInterface: FeatureProvider<LoginInterface>? = null

    override val api: Set<FeatureWrapper<out Feature>> = setOf(
        FeatureWrapper(SendMessageManager.Provider::class.java) { sendMessageManager },
        FeatureWrapper(SendMessageUseCase::class.java) { sendMessageComponent.sendMessageUseCase }
    )

    override val dependency: Dependency = Dependency.Builder()
        .require(CommonSingletonComponent::class.java) { commonSingletonComponentProvider = it }
        .require(AddAttachmentsUseCase::class.java) { addAttachmentsUseCaseProvider = it }
        .optional(LoginInterface::class.java) { loginInterface = it }
        .build()

    override val customizationOptions: Unit = Unit

    internal val sendMessageComponent: SendMessageComponent by lazy {
        val dependency = object : SendMessageDependency,
            AddAttachmentsUseCase by addAttachmentsUseCaseProvider.get() {}
        DaggerSendMessageComponent.factory().create(dependency, commonSingletonComponentProvider.get())
    }

    override fun doAfterInitialize() {
        subscribeOnLogoutEvent()
    }

    private fun subscribeOnLogoutEvent() = loginInterface?.let {
        it.get().eventsObservable
            .subscribe { authEvent ->
                if (authEvent.eventType == AuthEvent.EventType.LOGOUT) {
                    SendMessageWorker.cancelAllMessagesSending(application.applicationContext)
                }
            }
    }
}
