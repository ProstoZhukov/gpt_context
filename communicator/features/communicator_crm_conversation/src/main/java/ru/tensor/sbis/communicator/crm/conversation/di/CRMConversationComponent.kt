package ru.tensor.sbis.communicator.crm.conversation.di

import androidx.lifecycle.ViewModelStoreOwner
import dagger.BindsInstance
import dagger.Component
import ru.tensor.sbis.communicator.crm.conversation.contract.CRMConversationDependency
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.CRMConversationContract
import ru.tensor.sbis.communicator.crm.conversation.data.CRMCoreConversationInfo
import ru.tensor.sbis.communicator.crm.conversation.di.singleton.CRMConversationSingletonComponent
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.viewmodel.CRMConversationViewModel
import ru.tensor.sbis.design.list_header.ListDateViewUpdater
import ru.tensor.sbis.design.message_view.utils.MessageViewPool
import javax.inject.Scope

/**
 * DI компонент экрана чата CRM.
 *
 * @author da.zhukov
 */
@Scope
@Retention
internal annotation class CRMConversationScope

@CRMConversationScope
@Component(
    dependencies = [CRMConversationSingletonComponent::class],
    modules = [CRMConversationModule::class]
)
internal interface CRMConversationComponent {
    val dependency: CRMConversationDependency

    val crmConversationPresenter: CRMConversationContract.CRMConversationPresenterContract
    val messageViewPool: MessageViewPool
    val listDateViewUpdater: ListDateViewUpdater

    @Component.Builder
    interface Builder {

        fun crmConversationSingletonComponent(component: CRMConversationSingletonComponent): Builder

        @BindsInstance
        fun viewModelStoreOwner(viewModelStoreOwner: ViewModelStoreOwner): Builder

        @BindsInstance
        fun conversationData(coreConversationInfo: CRMCoreConversationInfo): Builder

        @BindsInstance
        fun viewModel(coreConversationInfo: CRMConversationViewModel): Builder

        fun build(): CRMConversationComponent
    }
}