package ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.di.vm_delegate

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.Provides
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.content.entity.screen.ReadStatusScreenEntity
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.content.reload.ReadStatusListActionsDelegate
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.content.reload.ReadStatusListUpdateActions
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.di.ReadStatusListViewScope
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.di.vm_delegate.domain.ReadStatusListDomainModule
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.di.vm_delegate.screen_entity.ReadStatusScreenEntityModule
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.utils.ReadStatusListInteractor
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.vm.list.ReadStatusListScreenVM
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.vm.list.ReadStatusListScreenVMFactory
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.vm.list.ReadStatusListScreenVMImpl
import ru.tensor.sbis.list.base.presentation.ListScreenVM
import ru.tensor.sbis.list.base.presentation.ListScreenVMImpl
import ru.tensor.sbis.list.base.presentation.getViewModel
import javax.inject.Named

/**
 * Di модуль view списка статусов прочитанности сообщения
 * для провайда моделей [ListScreenVM]
 * @see [ReadStatusListDomainModule]
 * @see [ReadStatusScreenEntityModule]
 *
 * @author vv.chekurda
 */
@Module(includes = [
    ReadStatusListDomainModule::class,
    ReadStatusScreenEntityModule::class,
    ReadStatusListScreenVmModule.BindsDiModule::class
])
internal class ReadStatusListScreenVmModule {

    @Provides
    @ReadStatusListViewScope
    fun provideReadStatusListScreenVM(
        fragment: Fragment,
        @Named(READ_STATUS_SCREEN_VM_FACTORY) factory: ViewModelProvider.Factory
    ) : ReadStatusListScreenVM =
        ViewModelProvider(
            fragment,
            factory
        )[ReadStatusListScreenVMImpl::class.java]

    @Provides
    @ReadStatusListViewScope
    fun provideReadStatusListScreenVmDelegate(
        interactor: ReadStatusListInteractor,
        entity: ReadStatusScreenEntity,
        fragment: Fragment,
    ): ListScreenVMImpl<ReadStatusScreenEntity> =
        fragment.getViewModel(interactor, entity)

    @Suppress("unused")
    @Module
    interface BindsDiModule {

        @Binds
        @ReadStatusListViewScope
        @Named(READ_STATUS_SCREEN_VM_FACTORY)
        fun asViewModelProviderFactory(impl: ReadStatusListScreenVMFactory): ViewModelProvider.Factory

        @Binds
        @ReadStatusListViewScope
        fun asReadStatusListUpdateActions(impl: ReadStatusListActionsDelegate): ReadStatusListUpdateActions
    }
}

private const val READ_STATUS_SCREEN_VM_FACTORY = "read_status_screen_vm_factory"