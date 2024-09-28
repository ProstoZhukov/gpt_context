package ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.di

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.Provides
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.di.vm_delegate.ReadStatusListScreenVmModule
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.di.vm_delegate.search_input.ReadStatusSearchInputModule
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.navigation.ReadStatusListRouter
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.navigation.ReadStatusListRouterImpl
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.vm.ReadStatusListViewModel
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.vm.ReadStatusListViewModelFactory
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.vm.ReadStatusListViewModelImpl
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.vm.live_data.ReadStatusListVMLiveData
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.vm.live_data.ReadStatusListVMLiveDataImpl
import javax.inject.Named

/**
 * Di модуль view списка статусов прочитанности сообщения
 * @see [ReadStatusListScreenVmModule]
 * @see [ReadStatusSearchInputModule]
 *
 * @author vv.chekurda
 */
@Module(includes = [
    ReadStatusListViewModule.BindsDIModule::class,
    ReadStatusListScreenVmModule::class,
    ReadStatusSearchInputModule::class
])
internal class ReadStatusListViewModule {

    @Provides
    @ReadStatusListViewScope
    fun provideReadStatusListViewModel(
        fragment: Fragment,
        @Named(LIST_VIEW_MODEL) factory: ViewModelProvider.Factory
    ): ReadStatusListViewModel =
        ViewModelProvider(
            fragment,
            factory
        ).get(ReadStatusListViewModelImpl::class.java)

    @Suppress("unused")
    @Module
    interface BindsDIModule {

        @Binds
        @ReadStatusListViewScope
        @Named(LIST_VIEW_MODEL)
        fun asViewModelProviderFactory(impl: ReadStatusListViewModelFactory): ViewModelProvider.Factory

        @Binds
        @ReadStatusListViewScope
        fun asReadStatusListVMLiveData(impl: ReadStatusListVMLiveDataImpl): ReadStatusListVMLiveData

        @Binds
        @ReadStatusListViewScope
        fun asReadStatusListRouter(impl: ReadStatusListRouterImpl): ReadStatusListRouter
    }
}

private const val LIST_VIEW_MODEL = "read_status_list_view_model"