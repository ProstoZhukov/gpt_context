package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.di

import dagger.BindsInstance
import dagger.Component
import ru.tensor.sbis.base_components.BaseFragment
import ru.tensor.sbis.common.util.FileUriUtil
import ru.tensor.sbis.communicator.common.di.CommunicatorCommonComponent
import ru.tensor.sbis.communicator.themes_registry.contract.ThemesRegistryDependency
import ru.tensor.sbis.communicator.themes_registry.ui.chatrecipientselection.chatAdministrators.di.ChatAdministratorsModule
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.ChatSettingsContract
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.ChatSettingsFragment
import ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.di.ThemeParticipantsModule
import ru.tensor.sbis.verification_decl.login.LoginInterface
import java.util.*
import javax.inject.Named

/**
 * DI компонент экрана настроек чата.
 *
 * @author da.zhukov
 */
@ChatSettingsScope
@Component(
    dependencies = [
        CommunicatorCommonComponent::class,
        ThemesRegistryDependency::class
    ],
    modules = [
        ChatSettingsModule::class,
        ThemeParticipantsModule::class,
        ChatAdministratorsModule::class
    ]
)
internal interface ChatSettingsComponent {

    fun inject(fragment: ChatSettingsFragment)

    fun getChatSettingsPresenter(): ChatSettingsContract.Presenter

    fun getLoginInterface(): LoginInterface

    fun getFileUriUtil(): FileUriUtil

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun setFragment(@Named("baseFragment") fragment: BaseFragment): Builder

        @BindsInstance
        fun newChat(@Named("newChat") isNewChat: Boolean): Builder

        @BindsInstance
        fun chatUuid(@Named("chatUuid") uuid: UUID?): Builder

        @BindsInstance
        fun draftChat(@Named("draftChat") isDraftChat: Boolean): Builder

        fun communicatorCommonComponent(component: CommunicatorCommonComponent): Builder

        fun communicatorDialogChatDependency(dependency: ThemesRegistryDependency): Builder

        fun build(): ChatSettingsComponent
    }
}
