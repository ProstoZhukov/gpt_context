package ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.channels.di

import android.view.View
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelStoreOwner
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import dagger.Module
import dagger.Provides
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.communication_decl.crm.CrmChannelFilterType
import ru.tensor.sbis.communication_decl.crm.CrmChannelListCase
import ru.tensor.sbis.communicator.communicator_crm_chat_list.databinding.CommunicatorCrmChannelsFragmentBinding
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.channels.data.CRMChannelsCollectionWrapper
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.channels.data.CRMChannelsCollectionWrapperImpl
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.channels.data.CRMChannelsFilterHolder
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.channels.mapper.CRMChannelsMapper
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.channels.store.CRMChannelsInteractor
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.channels.store.CRMChannelsInteractorImpl
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.channels.store.CRMChannelsStoreFactory
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.channels.ui.CRMChannelsListComponentFactory
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.channels.ui.CRMChannelsView
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.channels.ui.CRMChannelsViewImpl
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.channels.ui.CrmChannelsListSectionClickDelegate
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.channels.ui.CrmChannelsListViewSectionFactory
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.channels.ui.FirstItemHolderHelper
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.channels.ui.helper.CRMChannelsItemClickHelper
import ru.tensor.sbis.communicator.communicator_crm_chat_list.ui.CRMChatListNotificationHelper
import ru.tensor.sbis.communicator.communicator_crm_chat_list.utils.HighlightsColorProvider
import ru.tensor.sbis.consultations.generated.ChannelHeirarchyCollectionMode.CHANNEL_FOLDERS_AND_OPEN_LINES
import ru.tensor.sbis.consultations.generated.ChannelHeirarchyCollectionMode.CHANNEL_FOLDERS_ONLY
import ru.tensor.sbis.consultations.generated.ChannelHeirarchyCollectionMode.CHANNEL_TYPES_AND_CONTACTS
import ru.tensor.sbis.consultations.generated.ChannelHierarchyCollectionFilter
import ru.tensor.sbis.consultations.generated.ChannelHierarchyCollectionProvider
import ru.tensor.sbis.consultations.generated.ConsultationService
import ru.tensor.sbis.mvi_extension.AndroidStoreFactory
import ru.tensor.sbis.plugin_struct.utils.SbisThemedContext
import java.util.UUID

/**
 * DI модуль.
 *
 * @author da.zhukov
 */
@Module
internal class CRMChannelsModule {

    @Provides
    @CRMChannelsScope
    fun provideViewFactory(
        listComponentFactory: CRMChannelsListComponentFactory,
        crmChannelsItemClickHelper: CRMChannelsItemClickHelper,
        case: CrmChannelListCase,
        firstItemHolderHelper: FirstItemHolderHelper
    ): (View) -> CRMChannelsView {
        return {
            CRMChannelsViewImpl(
                CommunicatorCrmChannelsFragmentBinding.bind(it),
                listComponentFactory,
                crmChannelsItemClickHelper,
                case,
                firstItemHolderHelper
            )
        }
    }

    @Provides
    @CRMChannelsScope
    fun provideStoreFactory(): StoreFactory {
        return AndroidStoreFactory(DefaultStoreFactory())
    }

    @Provides
    @CRMChannelsScope
    fun provideCRMChannelsStoreFactory(
        storeFactory: StoreFactory,
        listComponentFactory: CRMChannelsListComponentFactory,
        filterHolder: CRMChannelsFilterHolder,
        crmChannelsInteractor: CRMChannelsInteractor,
        case: CrmChannelListCase,
        notificationHelper: CRMChatListNotificationHelper
    ): CRMChannelsStoreFactory {
        return CRMChannelsStoreFactory(
            storeFactory,
            listComponentFactory,
            filterHolder,
            crmChannelsInteractor,
            case,
            notificationHelper
        )
    }

    @Provides
    @CRMChannelsScope
    fun provideCRMChannelsListComponentFactory(
        viewModelStoreOwner: ViewModelStoreOwner,
        listCollectionWrapper: CRMChannelsCollectionWrapper,
        listMapper: CRMChannelsMapper,
        case: CrmChannelListCase,
        listViewSectionFactory: CrmChannelsListViewSectionFactory
    ): CRMChannelsListComponentFactory {
        return CRMChannelsListComponentFactory(
            viewModelStoreOwner,
            listCollectionWrapper,
            listMapper,
            case,
            listViewSectionFactory
        )
    }

    @Provides
    @CRMChannelsScope
    fun provideCrmChannelsListViewSectionFactory(
        firstItemHolderHelper: FirstItemHolderHelper,
        clickDelegate: CrmChannelsListSectionClickDelegate
    ) = CrmChannelsListViewSectionFactory(clickDelegate, firstItemHolderHelper)

    @Provides
    @CRMChannelsScope
    fun provideFirstItemHolderHelper(context: SbisThemedContext) =
        FirstItemHolderHelper(context)

    @Provides
    @CRMChannelsScope
    fun provideCRMChannelsMapper(
        context: SbisThemedContext,
        highlightsColorProvider: HighlightsColorProvider,
        crmChannelsItemClickHelper: CRMChannelsItemClickHelper,
        case: CrmChannelListCase,
        selectedItems: MutableLiveData<List<UUID>>
    ): CRMChannelsMapper =
        CRMChannelsMapper(
            context,
            highlightsColorProvider,
            crmChannelsItemClickHelper.onItemSuccessIconClick,
            crmChannelsItemClickHelper.onItemCheckedClick,
            case,
            selectedItems
        )

    @Provides
    @CRMChannelsScope
    fun provideCRMChannelsCollectionWrapper(
        channelHierarchyCollectionProvider: DependencyProvider<ChannelHierarchyCollectionProvider>,
        filterHolder: CRMChannelsFilterHolder
    ): CRMChannelsCollectionWrapper {
        return CRMChannelsCollectionWrapperImpl(channelHierarchyCollectionProvider, filterHolder)
    }

    @Provides
    @CRMChannelsScope
    fun provideChannelHierarchyCollectionProvider(): DependencyProvider<ChannelHierarchyCollectionProvider> =
        DependencyProvider.create(ChannelHierarchyCollectionProvider::instance)

    @Provides
    @CRMChannelsScope
    fun provideFilterHolder(case: CrmChannelListCase): CRMChannelsFilterHolder {
        return CRMChannelsFilterHolder(
            ChannelHierarchyCollectionFilter().apply {
                parentIds = arrayListOf()
                when (case) {
                    is CrmChannelListCase.CrmChannelFilterCase -> {
                        collectionMode = if (case.type == CrmChannelFilterType.REGISTRY) {
                            CHANNEL_FOLDERS_ONLY
                        } else {
                            CHANNEL_FOLDERS_AND_OPEN_LINES
                        }
                    }
                    is CrmChannelListCase.CrmChannelReassignCase -> collectionMode = CHANNEL_FOLDERS_AND_OPEN_LINES
                    is CrmChannelListCase.CrmChannelConsultationCase -> {
                        collectionMode = CHANNEL_TYPES_AND_CONTACTS
                        authorId = case.consultationAuthorId
                    }
                }
                operatorVisibility = true
                clientVisibility = false
            }
        )
    }

    @Provides
    @CRMChannelsScope
    fun provideCRMChannelsInteractor(
        consultationServiceProvider: DependencyProvider<ConsultationService>,
        case: CrmChannelListCase
    ): CRMChannelsInteractor {
        return CRMChannelsInteractorImpl(consultationServiceProvider, case)
    }

    @Provides
    @CRMChannelsScope
    fun provideConsultationService(): DependencyProvider<ConsultationService> =
        DependencyProvider.create(ConsultationService::instance)

    @Provides
    @CRMChannelsScope
    fun provideCRMChannelsItemClickHelper(scope: LifecycleCoroutineScope): CRMChannelsItemClickHelper =
        CRMChannelsItemClickHelper(scope)

    @Provides
    @CRMChannelsScope
    fun provideHighlightsColorProvider(context: SbisThemedContext) =
        HighlightsColorProvider(context)
}