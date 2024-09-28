package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.dialogs.information.di

import dagger.BindsInstance
import dagger.Component
import ru.tensor.sbis.communicator.common.di.CommunicatorCommonComponent
import ru.tensor.sbis.communicator.themes_registry.contract.ThemesRegistryDependency
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.dialogs.information.DialogInformationContract
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.dialogs.information.DialogInformationFragment
import ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.di.ThemeParticipantsComponent
import ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.di.ThemeParticipantsModule
import java.util.*
import javax.inject.Named

/**
 * Di компонент екрана информации о диалоге
 *
 * @author da.zhukov
 */
@DialogInformationScope
@Component(
    dependencies = [
        CommunicatorCommonComponent::class,
        ThemesRegistryDependency::class
    ],
    modules = [
        DialogInformationModule::class,
        ThemeParticipantsModule::class
    ]
)
internal interface DialogInformationComponent :
    ThemeParticipantsComponent {

    fun inject(fragment: DialogInformationFragment)

    fun getPresenter(): DialogInformationContract.Presenter

    @Component.Builder
    interface Builder {

        fun communicatorCommonComponent(component: CommunicatorCommonComponent): Builder

        fun communicatorThemeRegistryDependency(dependency: ThemesRegistryDependency): Builder

        @BindsInstance
        fun conversationUuid(conversationUuid: UUID): Builder

        @BindsInstance
        fun isNewDialog(@Named("isNewDialog") isNewDialog: Boolean): Builder

        @BindsInstance
        fun conversationName(@Named(CONVERSATION_NAME) conversationName: String): Builder

        @BindsInstance
        fun videoCallParticipants(participantsUuids: ArrayList<UUID>?): Builder

        fun build(): DialogInformationComponent
    }
}

internal const val CONVERSATION_NAME = "CONVERSATION_NAME"