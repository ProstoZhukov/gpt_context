package ru.tensor.sbis.communicator.communicator_share_messages

import ru.tensor.sbis.communication_decl.selection.recipient.RecipientSelectionProvider
import ru.tensor.sbis.communicator.communicator_share_messages.share_handlers.ChannelsShareHandler
import ru.tensor.sbis.communicator.communicator_share_messages.share_handlers.ContactsShareHandler
import ru.tensor.sbis.communicator.communicator_share_messages.share_handlers.DialogsShareHandler
import ru.tensor.sbis.communicator.declaration.send_message.SendMessageManager
import ru.tensor.sbis.communicator.declaration.send_message.SendMessageUseCase
import ru.tensor.sbis.communicator.declaration.theme.ThemesRegistryFragmentFactory
import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper
import ru.tensor.sbis.toolbox_decl.share.ShareHandler
import ru.tensor.sbis.verification_decl.login.LoginInterface

/**
 * Плагин шаринга в диалоги/каналы и новый диалог.
 *
 * @author da.zhukov
 */
object ShareMessagesPlugin : BasePlugin<ShareMessagesPlugin.CustomizationOptions>() {

    internal lateinit var themesRegistryFragmentFactoryProvider: FeatureProvider<ThemesRegistryFragmentFactory>
    internal lateinit var recipientSelectionProvider: FeatureProvider<RecipientSelectionProvider>
    internal lateinit var sendMessageManagerProvider: FeatureProvider<SendMessageManager.Provider>
    internal lateinit var sendMessageUseCaseProvider: FeatureProvider<SendMessageUseCase>
    internal lateinit var loginInterfaceProvider: FeatureProvider<LoginInterface>

    override val api: Set<FeatureWrapper<out Feature>> by lazy {
        setOfNotNull(
            createFeatureWrapper(ShareHandler::class.java, ::ContactsShareHandler) { customizationOptions.isContactsShareEnabled},
            createFeatureWrapper(ShareHandler::class.java, ::DialogsShareHandler) { customizationOptions.isDialogsShareEnabled },
            createFeatureWrapper(ShareHandler::class.java, ::ChannelsShareHandler) { customizationOptions.isChannelsShareEnabled }
        )
    } 

    override val dependency: Dependency by lazy {
        Dependency.Builder()
            .require(ThemesRegistryFragmentFactory::class.java) { themesRegistryFragmentFactoryProvider = it }
            .require(RecipientSelectionProvider::class.java) { recipientSelectionProvider = it }
            .require(SendMessageManager.Provider::class.java) { sendMessageManagerProvider = it }
            .require(SendMessageUseCase::class.java) { sendMessageUseCaseProvider = it }
            .require(LoginInterface::class.java) { loginInterfaceProvider = it }
            .build()
    }

    override val customizationOptions: CustomizationOptions = CustomizationOptions()

    /**
     * Опции кастомизации плагина.
     *
     * @property isContactsShareEnabled признак доступности фичи шаринга в контакты.
     * @property isDialogsShareEnabled признак доступности фичи шаринга в диалоги.
     * @property isChannelsShareEnabled признак доступности фичи шаринга в каналы.
     */
    class CustomizationOptions internal constructor(
        val isContactsShareEnabled: Boolean = true,
        val isDialogsShareEnabled: Boolean = true,
        val isChannelsShareEnabled: Boolean = true,
    )
    
    private fun <F : Feature> createFeatureWrapper(
        type: Class<F>,
        provider: FeatureProvider<out F>,
        isEnabled: () -> Boolean
    ): FeatureWrapper<F>? =
        FeatureWrapper(type, provider).takeIf { isEnabled() }
}