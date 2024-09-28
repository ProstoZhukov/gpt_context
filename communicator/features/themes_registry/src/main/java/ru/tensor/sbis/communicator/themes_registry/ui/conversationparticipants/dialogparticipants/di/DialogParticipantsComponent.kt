package ru.tensor.sbis.communicator.themes_registry.ui.conversationparticipants.dialogparticipants.di

import dagger.BindsInstance
import dagger.Component
import ru.tensor.sbis.communicator.common.di.CommunicatorCommonComponent
import ru.tensor.sbis.communicator.themes_registry.contract.ThemesRegistryDependency
import ru.tensor.sbis.communicator.themes_registry.ui.conversationparticipants.dialogparticipants.contract.DialogParticipantsViewContract
import ru.tensor.sbis.communicator.themes_registry.ui.conversationparticipants.dialogparticipants.presentation.view.DialogParticipantsFragment
import ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.di.ThemeParticipantsComponent
import ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.di.ThemeParticipantsModule
import java.util.*
import javax.inject.Named

/** @SelfDocumented */
@DialogParticipantsScope
@Component(
    dependencies = [
        CommunicatorCommonComponent::class,
        ThemesRegistryDependency::class
    ],
    modules = [
        DialogParticipantsModule::class,
        ThemeParticipantsModule::class
    ]
)
/** @SelfDocumented */
internal interface DialogParticipantsComponent :
        ThemeParticipantsComponent {

    // injects here
    fun inject(fragment: DialogParticipantsFragment)

    // getters here
    fun getPresenter(): DialogParticipantsViewContract.Presenter

    @Component.Builder
    interface Builder {

        fun communicatorCommonComponent(component: CommunicatorCommonComponent): Builder

        fun communicatorDialogChatDependency(dependency: ThemesRegistryDependency): Builder

        @BindsInstance
        fun conversationUuid(conversationUuid: UUID): Builder

        @BindsInstance
        fun isNewDialog(@Named("isNewDialog") isNewDialog: Boolean): Builder

        @BindsInstance
        fun videoCallParticipants(participantsUuids: ArrayList<UUID>?): Builder

        @BindsInstance
        fun isFromCollage(@Named("isFromCollage") isFromCollage: Boolean): Builder

        fun build(): DialogParticipantsComponent
    }
}