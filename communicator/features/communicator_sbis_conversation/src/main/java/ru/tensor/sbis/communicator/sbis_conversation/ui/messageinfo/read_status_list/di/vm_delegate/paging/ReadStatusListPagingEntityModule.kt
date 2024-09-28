package ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.di.vm_delegate.paging

import androidx.recyclerview.widget.RecyclerView
import dagger.Binds
import dagger.Module
import dagger.Provides
import ru.tensor.sbis.communicator.sbis_conversation.R
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.content.entity.paging.READ_STATUS_LIST_MAX_PAGES_COUNT
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.content.entity.paging.ReadStatusPagingEntityFactory
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.content.helper.ReadStatusResultHelperImpl
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.content.holders.error.ReadStatusErrorItemFactory
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.content.holders.error.ReadStatusErrorItemFactoryImpl
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.content.holders.error.ReadStatusErrorViewHolderHelper
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.content.holders.vm.ReadStatusVM
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.content.holders.vm.factory.ReadStatusVMFactory
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.content.holders.vm.factory.ReadStatusVMFactoryImpl
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.content.mapper.ReadStatusListMapperImpl
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.content.mapper.item.ReadStatusBindingItemFactoryImpl
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.content.mapper.item.ReadStatusItemFactory
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.content.mapper.item.ReadStatusOptionsFactory
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.content.mapper.item.ReadStatusOptionsFactoryImpl
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.content.stub.ReadStatusStubContentProvider
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.di.ReadStatusListViewScope
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.utils.ReadStatusListMapper
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.utils.ReadStatusListPagingEntity
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.utils.ReadStatusListResult
import ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.read_status_list.utils.ReadStatusResultHelper
import ru.tensor.sbis.list.base.domain.entity.paging.PagingData
import ru.tensor.sbis.list.base.domain.entity.paging.PagingEntity
import ru.tensor.sbis.list.base.utils.stub.StubContentProvider
import ru.tensor.sbis.list.view.binding.DataBindingViewHolderHelper
import ru.tensor.sbis.list.view.binding.LayoutIdViewFactory
import ru.tensor.sbis.list.view.item.ViewHolderHelper

/**
 * Di модуль view списка статусов прочитанности сообщения
 * для провайда моделей слоя [PagingEntity]
 *
 * @author vv.chekurda
 */
@Module(includes = [ReadStatusListPagingEntityModule.BindsDiModule::class])
internal class ReadStatusListPagingEntityModule {

    @Provides
    @ReadStatusListViewScope
    fun provideReadStatusListPagingEntity(
        factory: ReadStatusPagingEntityFactory
    ): ReadStatusListPagingEntity =
        factory.createPagingEntity()

    @Provides
    @ReadStatusListViewScope
    fun provideReadStatusPagingData(
        helper: ReadStatusResultHelper
    ): PagingData<ReadStatusListResult> = PagingData(helper, READ_STATUS_LIST_MAX_PAGES_COUNT)

    @Provides
    @ReadStatusListViewScope
    fun provideDataBindingViewHolderHelper(
    ) = DataBindingViewHolderHelper<ReadStatusVM>(LayoutIdViewFactory(R.layout.communicator_read_status_list_content_item))

    @Suppress("unused")
    @Module
    interface BindsDiModule {

        @Binds
        @ReadStatusListViewScope
        fun asReadStatusResultHelper(impl: ReadStatusResultHelperImpl): ReadStatusResultHelper

        @Binds
        @ReadStatusListViewScope
        fun asReadStatusMapper(impl: ReadStatusListMapperImpl): ReadStatusListMapper

        @Binds
        @ReadStatusListViewScope
        fun asReadStatusItemMapper(impl: ReadStatusBindingItemFactoryImpl): ReadStatusItemFactory

        @Binds
        @ReadStatusListViewScope
        fun asReadStatusVMFactory(impl: ReadStatusVMFactoryImpl): ReadStatusVMFactory

        @Binds
        @ReadStatusListViewScope
        fun asReadStatusOptionsFactory(impl: ReadStatusOptionsFactoryImpl): ReadStatusOptionsFactory

        @Binds
        @ReadStatusListViewScope
        fun asAnyViewHolderHelper(impl: ReadStatusErrorViewHolderHelper): ViewHolderHelper<Any, RecyclerView.ViewHolder>

        @Binds
        @ReadStatusListViewScope
        fun asReadStatusErrorItemFactory(impl: ReadStatusErrorItemFactoryImpl): ReadStatusErrorItemFactory

        @Binds
        @ReadStatusListViewScope
        fun asStubContentProvider(impl: ReadStatusStubContentProvider): StubContentProvider<ReadStatusListResult>
    }
}