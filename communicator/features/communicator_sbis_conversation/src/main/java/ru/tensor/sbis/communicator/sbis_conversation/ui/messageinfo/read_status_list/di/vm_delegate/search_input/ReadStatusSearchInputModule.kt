package ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.di.vm_delegate.search_input

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.Provides
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.di.ReadStatusListViewScope
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.vm.search_input.ReadStatusSearchInputVM
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.vm.search_input.ReadStatusSearchInputVMFactory
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.vm.search_input.ReadStatusSearchInputVMImpl
import javax.inject.Named

/**
 * Di модуль view списка статусов прочитанности сообщения
 * для провайда вью-модели поисковой строки
 * @see [ReadStatusSearchInputVM]
 *
 * @author vv.chekurda
 */
@Module(includes = [ReadStatusSearchInputModule.BindsDIModule::class])
internal class ReadStatusSearchInputModule {

    @Provides
    @ReadStatusListViewScope
    fun provideReadStatusSearchInputVM(
        fragment: Fragment,
        @Named(SEARCH_INPUT_VIEW_MODEL) factory: ViewModelProvider.Factory
    ): ReadStatusSearchInputVM =
        ViewModelProvider(
            fragment,
            factory
        )[ReadStatusSearchInputVMImpl::class.java]

    @Suppress("unused")
    @Module
    interface BindsDIModule {

        @Binds
        @ReadStatusListViewScope
        @Named(SEARCH_INPUT_VIEW_MODEL)
        fun asViewModelProviderFactory(impl: ReadStatusSearchInputVMFactory): ViewModelProvider.Factory
    }
}

private const val SEARCH_INPUT_VIEW_MODEL = "read_status_search_input_view_model"