package ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.di.vm_delegate.filter

import dagger.Binds
import dagger.Module
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.utils.ReadStatusFilterAndPageProvider
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.content.filter.factory.ReadStatusListFilterFactory
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.content.filter.factory.ReadStatusListFilterFactoryImpl
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.content.filter.ReadStatusFilterAndPageProviderDelegate
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.di.ReadStatusListViewScope

/**
 * Di модуль view списка статусов прочитанности сообщения
 * для провайда моделей, отвечающий за создание фильтра
 *
 * @author vv.chekurda
 */
@Module(includes = [ReadStatusListFilterModule.BindsDiModule::class])
internal class ReadStatusListFilterModule {

    @Module
    interface BindsDiModule {

        @Suppress("unused")
        @Binds
        @ReadStatusListViewScope
        fun asReadStatusFilterAndPageProvider(impl: ReadStatusFilterAndPageProviderDelegate): ReadStatusFilterAndPageProvider

        @Suppress("unused")
        @Binds
        @ReadStatusListViewScope
        fun asReadStatusListFilterFactory(impl: ReadStatusListFilterFactoryImpl): ReadStatusListFilterFactory
    }
}