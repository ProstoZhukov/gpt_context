package ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.di.vm_delegate.domain

import androidx.lifecycle.Lifecycle
import dagger.Binds
import dagger.Module
import dagger.Provides
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.communicator.generated.MessageReceiverReadStatusController
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.content.service.ReadStatusListServiceWrapperImpl
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.di.ReadStatusListViewScope
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.utils.*
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.utils.ReadStatusListInteractor
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.utils.ReadStatusListRepository
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.utils.ReadStatusListServiceWrapper
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.utils.ReadStatusScreenEntityFactory
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.vm.helper.ReadStatusLifeCycleHolder
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.vm.helper.ReadStatusLifeCycleHolderImpl
import ru.tensor.sbis.list.base.domain.ListInteractorImpl

/**
 * Di модуль view списка статусов прочитанности сообщения
 * для провайда моделей слоя бизнес-логики
 *
 * @author vv.chekurda
 */
@Module(includes = [ReadStatusListDomainModule.BindsDiModule::class])
internal class ReadStatusListDomainModule {

    @Provides
    @ReadStatusListViewScope
    fun provideReadStatusListInteractor(
        repository: ReadStatusListRepository
    ): ReadStatusListInteractor = ListInteractorImpl(repository)

    @Provides
    @ReadStatusListViewScope
    fun provideReadStatusListRepository(
        screenEntityFactory: ReadStatusScreenEntityFactory,
        serviceWrapper: ReadStatusListServiceWrapper
    ): ReadStatusListRepository =
        ReadStatusListRepositoryImpl(
            screenEntityFactory,
            serviceWrapper
        )

    @Provides
    @ReadStatusListViewScope
    fun provideReadStatusListController(): DependencyProvider<MessageReceiverReadStatusController> =
        DependencyProvider.create(MessageReceiverReadStatusController::instance)

    @Provides
    @ReadStatusListViewScope
    fun provideFragmentLifeCycle(
        lifeCycleHolder: ReadStatusLifeCycleHolder
    ) : Lifecycle =
        lifeCycleHolder.lifecycle

    @Provides
    @ReadStatusListViewScope
    fun provideReadStatusLifeCycleHolder() : ReadStatusLifeCycleHolder =
        ReadStatusLifeCycleHolderImpl()

    @Module
    interface BindsDiModule {

        @Suppress("unused")
        @Binds
        @ReadStatusListViewScope
        fun asReadStatusListServiceWrapper(impl: ReadStatusListServiceWrapperImpl): ReadStatusListServiceWrapper
    }
}