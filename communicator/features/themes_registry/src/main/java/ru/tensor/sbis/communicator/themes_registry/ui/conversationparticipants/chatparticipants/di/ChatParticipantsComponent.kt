package ru.tensor.sbis.communicator.themes_registry.ui.conversationparticipants.chatparticipants.di

import dagger.BindsInstance
import dagger.Component
import ru.tensor.sbis.communicator.common.di.CommunicatorCommonComponent
import ru.tensor.sbis.communicator.themes_registry.contract.ThemesRegistryDependency
import ru.tensor.sbis.communicator.themes_registry.ui.chatrecipientselection.chatAdministrators.di.ChatAdministratorsComponent
import ru.tensor.sbis.communicator.themes_registry.ui.chatrecipientselection.chatAdministrators.di.ChatAdministratorsModule
import ru.tensor.sbis.communicator.themes_registry.ui.conversationparticipants.chatparticipants.contract.ChatParticipantsViewContract
import ru.tensor.sbis.communicator.themes_registry.ui.conversationparticipants.chatparticipants.presentation.view.ChatParticipantsFragment
import ru.tensor.sbis.communicator.themes_registry.ui.conversationparticipants.chatparticipants.presentation.viewmodel.ChatParticipantsViewModel
import ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.di.ThemeParticipantsComponent
import ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.di.ThemeParticipantsModule
import ru.tensor.sbis.communicator.generated.Permissions
import ru.tensor.sbis.communicator.themes_registry.ui.conversationparticipants.chatparticipants.presentation.router.ChatParticipantsRouter
import ru.tensor.sbis.verification_decl.login.LoginInterface
import java.util.*

/** @SelfDocumented */
@ChatParticipantsScope
@Component(
    dependencies = [
        CommunicatorCommonComponent::class,
        ThemesRegistryDependency::class
    ],
    modules = [
        ChatParticipantsModule::class,
        ThemeParticipantsModule::class,
        ChatAdministratorsModule::class
    ]
)
/** @SelfDocumented */
internal interface ChatParticipantsComponent :
        ThemeParticipantsComponent,
        ChatAdministratorsComponent {

    // injects here
    fun inject(fragment: ChatParticipantsFragment)

    // getters here
    fun getPresenter(): ChatParticipantsViewContract.Presenter

    fun getViewModel(): ChatParticipantsViewModel

    fun getLoginInterface(): LoginInterface

    fun getRouter(): ChatParticipantsRouter

    @Component.Builder
    interface Builder {

        fun communicatorCommonComponent(component: CommunicatorCommonComponent): Builder

        fun communicatorDialogChatDependency(dependency: ThemesRegistryDependency): Builder

        @BindsInstance
        fun viewModel(viewModel: ChatParticipantsViewModel): Builder

        @BindsInstance
        fun router(router: ChatParticipantsRouter): Builder

        @BindsInstance
        fun conversationUuid(conversationUuid: UUID): Builder

        @BindsInstance
        fun chatPermissions(chatPermissions: Permissions): Builder

        fun build(): ChatParticipantsComponent
    }
}