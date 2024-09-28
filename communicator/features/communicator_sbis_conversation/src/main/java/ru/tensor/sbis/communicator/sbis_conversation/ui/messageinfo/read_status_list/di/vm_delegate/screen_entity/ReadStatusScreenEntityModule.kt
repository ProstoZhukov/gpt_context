package ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.di.vm_delegate.screen_entity

import dagger.Binds
import dagger.Module
import dagger.Provides
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.content.entity.screen.ReadStatusScreenEntity
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.content.entity.screen.ReadStatusScreenEntityFactoryImpl
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.di.ReadStatusListViewScope
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.di.vm_delegate.filter.ReadStatusListFilterModule
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.di.vm_delegate.paging.ReadStatusListPagingEntityModule
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.utils.ReadStatusScreenEntityFactory
import ru.tensor.sbis.list.base.domain.entity.ListScreenEntity

/**
 * Di модуль view списка статусов прочитанности сообщения
 * для провайда моделей слоя [ListScreenEntity]
 * @see [ReadStatusListPagingEntityModule]
 * @see [ReadStatusListFilterModule]
 *
 * @author vv.chekurda
 */
@Module(includes = [
    ReadStatusListPagingEntityModule::class,
    ReadStatusListFilterModule::class,
    ReadStatusScreenEntityModule.BindsDiModule::class
])
internal class ReadStatusScreenEntityModule {

    @Provides
    @ReadStatusListViewScope
    fun provideReadStatusListScreenEntity(
        factory: ReadStatusScreenEntityFactory
    ): ReadStatusScreenEntity =
        factory.createEntity()

    @Suppress("unused")
    @Module
    interface BindsDiModule {

        @Binds
        @ReadStatusListViewScope
        fun asReadStatusListScreenEntityFactory(impl: ReadStatusScreenEntityFactoryImpl): ReadStatusScreenEntityFactory
    }
}