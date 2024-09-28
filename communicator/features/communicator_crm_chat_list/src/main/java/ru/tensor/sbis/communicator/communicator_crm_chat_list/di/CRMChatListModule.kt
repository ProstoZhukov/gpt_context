package ru.tensor.sbis.communicator.communicator_crm_chat_list.di

import android.view.View
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.ViewModelStoreOwner
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import dagger.Module
import dagger.Provides
import io.reactivex.subjects.PublishSubject
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.communication_decl.crm.CRMChatListClientsParams
import ru.tensor.sbis.communication_decl.crm.CRMChatListHistoryParams
import ru.tensor.sbis.communication_decl.crm.CRMChatListMode
import ru.tensor.sbis.communication_decl.crm.CRMChatListParams
import ru.tensor.sbis.communicator.base_folders.list_section.BaseListFoldersViewModelFactory
import ru.tensor.sbis.communicator.base_folders.list_section.CommunicatorBaseListFolderViewSectionFactory
import ru.tensor.sbis.communicator.base_folders.list_section.CommunicatorBaseListViewFoldersHolderHelper
import ru.tensor.sbis.communicator.base_folders.list_section.FoldersViewHolderHelper
import ru.tensor.sbis.communicator.communicator_crm_chat_list.data.CRMChatListCollectionWrapper
import ru.tensor.sbis.communicator.communicator_crm_chat_list.data.CRMChatListCollectionWrapperImpl
import ru.tensor.sbis.communicator.communicator_crm_chat_list.data.CRMChatListFilterHolder
import ru.tensor.sbis.communicator.communicator_crm_chat_list.data.helper.CRMCollectionSynchronizeHelper
import ru.tensor.sbis.communicator.communicator_crm_chat_list.databinding.CommunicatorCrmChatListFragmentBinding
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.CRMChatFilterPreferences
import ru.tensor.sbis.communicator.communicator_crm_chat_list.mapper.CRMChatListMapper
import ru.tensor.sbis.communicator.communicator_crm_chat_list.store.CRMChatListInteractor
import ru.tensor.sbis.communicator.communicator_crm_chat_list.store.CRMChatListInteractorImpl
import ru.tensor.sbis.communicator.communicator_crm_chat_list.store.CRMChatListStoreFactory
import ru.tensor.sbis.communicator.communicator_crm_chat_list.ui.CRMChatListComponentFactory
import ru.tensor.sbis.communicator.communicator_crm_chat_list.ui.CRMChatListView
import ru.tensor.sbis.communicator.communicator_crm_chat_list.ui.CRMChatListViewImpl
import ru.tensor.sbis.communicator.communicator_crm_chat_list.ui.folders.CRMChatListFoldersInteractor
import ru.tensor.sbis.communicator.communicator_crm_chat_list.utils.CRMChatListOnScrollListener
import ru.tensor.sbis.communicator.communicator_crm_chat_list.utils.HighlightsColorProvider
import ru.tensor.sbis.communicator.communicator_crm_chat_list.utils.swipe_menu.CRMChatSwipeMenuHelper
import ru.tensor.sbis.communicator.communicator_crm_chat_list.utils.swipe_menu.CRMChatSwipeMenuProvider
import ru.tensor.sbis.communicator.declaration.crm.model.CRMChatFilterModel
import ru.tensor.sbis.communicator.declaration.crm.model.CRMRadioButtonFilterType
import ru.tensor.sbis.consultations.generated.ConsultationCollectionProvider
import ru.tensor.sbis.consultations.generated.ConsultationListFilter
import ru.tensor.sbis.consultations.generated.ConsultationListMode
import ru.tensor.sbis.consultations.generated.ConsultationService
import ru.tensor.sbis.design.folders.data.model.Folder
import ru.tensor.sbis.design.list_header.format.ListDateFormatter
import ru.tensor.sbis.mvi_extension.AndroidStoreFactory
import ru.tensor.sbis.plugin_struct.utils.SbisThemedContext
import java.util.UUID

/**
 * DI модуль чатов оператора.
 * @author da.zhukov
 */
@Module
internal class CRMChatListModule {

    @Provides
    @CRMChatListScope
    fun provideViewFactory(
        listComponentFactory: CRMChatListComponentFactory,
        foldersViewHolderHelper: FoldersViewHolderHelper,
        crmChatSwipeMenuHelper: CRMChatSwipeMenuHelper,
        crmChatListOnScrollListener: CRMChatListOnScrollListener,
        crmChatListParams: CRMChatListParams,
        filterHolder: CRMChatListFilterHolder
    ): (View) -> CRMChatListView {
        return {
            CRMChatListViewImpl(
                CommunicatorCrmChatListFragmentBinding.bind(it),
                listComponentFactory,
                foldersViewHolderHelper,
                crmChatSwipeMenuHelper,
                crmChatListOnScrollListener,
                crmChatListParams.crmListMode == CRMChatListMode.USER_HISTORY,
                filterHolder
            )
        }
    }

    @Provides
    @CRMChatListScope
    fun provideStoreFactory(): StoreFactory {
        return AndroidStoreFactory(DefaultStoreFactory())
    }

    @Provides
    @CRMChatListScope
    fun provideCRMChatListStoreFactory(
        storeFactory: StoreFactory,
        listComponentFactory: CRMChatListComponentFactory,
        filterHolder: CRMChatListFilterHolder,
        crmChatListInteractor: CRMChatListInteractor,
        collectionSynchronizeHelper: CRMCollectionSynchronizeHelper,
        crmChatListFoldersInteractor: CRMChatListFoldersInteractor
    ): CRMChatListStoreFactory {
        return CRMChatListStoreFactory(
            storeFactory,
            listComponentFactory,
            filterHolder,
            crmChatListInteractor,
            collectionSynchronizeHelper,
            crmChatListFoldersInteractor
        )
    }

    @Provides
    @CRMChatListScope
    fun provideFilterHolder(
        crmChatListParams: CRMChatListParams,
        crmChatFilterPreferences: CRMChatFilterPreferences
    ): CRMChatListFilterHolder {
        return CRMChatListFilterHolder(
            filter = ConsultationListFilter().apply {
                mode = when (crmChatListParams.crmListMode) {
                    CRMChatListMode.OPERATORS_CONSULTATION -> ConsultationListMode.OPERATORS_CONSULTAION
                    CRMChatListMode.USER_HISTORY -> ConsultationListMode.USER_HISTORY
                    else -> ConsultationListMode.OPERATORS_CONSULTAION
                }
                excludeId = crmChatListParams.excludeId
                userId = crmChatListParams.userId
                viewId = UUID.randomUUID()
            },
        ).apply {
            val filterModel = if (crmChatListParams is CRMChatListClientsParams) {
                crmChatFilterPreferences.currentState().copy(
                    clientIds = arrayListOf(crmChatListParams.clientId) to arrayListOf(),
                    type = CRMRadioButtonFilterType.ALL
                )
            } else {
                val defModelState = if (crmChatListParams.crmListMode == CRMChatListMode.USER_HISTORY) {
                    CRMChatFilterModel(type = CRMRadioButtonFilterType.ALL)
                } else {
                    crmChatFilterPreferences.currentState()
                }
                defModelState
            }
            applyFilter(filterModel)
            applyFilterTitle(listOf(crmChatFilterPreferences.currentFilterTitle()))
        }
    }

    @Provides
    @CRMChatListScope
    fun provideCRMChatListComponentFactory(
        viewModelStoreOwner: ViewModelStoreOwner,
        listCollectionWrapper: CRMChatListCollectionWrapper,
        listMapper: CRMChatListMapper,
        folderViewSectionFactory: CommunicatorBaseListFolderViewSectionFactory,
        crmChatListParams: CRMChatListParams
    ): CRMChatListComponentFactory {
        return CRMChatListComponentFactory(
            viewModelStoreOwner,
            listCollectionWrapper,
            listMapper,
            folderViewSectionFactory,
            crmChatListParams
        )
    }

    @Provides
    @CRMChatListScope
    fun provideCRMChatListMapper(
        context: SbisThemedContext,
        listDateFormatter: ListDateFormatter.DateTimeWithoutTodayStandard,
        highlightsColorProvider: HighlightsColorProvider,
        crmChatSwipeMenuProvider: CRMChatSwipeMenuProvider,
        crmChatListParams: CRMChatListParams
    ): CRMChatListMapper {
        return CRMChatListMapper(
            context,
            listDateFormatter,
            highlightsColorProvider,
            crmChatSwipeMenuProvider,
            crmChatListParams is CRMChatListHistoryParams
        )
    }

    @Provides
    @CRMChatListScope
    fun provideCRMChatListCollectionWrapperImpl(
        consultationCollectionProvider: DependencyProvider<ConsultationCollectionProvider>,
        filterHolder: CRMChatListFilterHolder,
        collectionSynchronizeHelper: CRMCollectionSynchronizeHelper
    ): CRMChatListCollectionWrapper {
        return CRMChatListCollectionWrapperImpl(
            consultationCollectionProvider,
            filterHolder,
            collectionSynchronizeHelper
        )
    }

    @Provides
    @CRMChatListScope
    fun provideConsultationCollectionProvider(): DependencyProvider<ConsultationCollectionProvider> =
        DependencyProvider.create(ConsultationCollectionProvider::instance)

    @Provides
    @CRMChatListScope
    fun provideCRMChatListFoldersInteractor(
        initialFolderListObservable: PublishSubject<List<Folder>>,
        crmChatListFilterHolder: CRMChatListFilterHolder
    ): CRMChatListFoldersInteractor {
        return CRMChatListFoldersInteractor(
            crmChatListFilterHolder,
            initialFolderListObservable
        )
    }

    @Provides
    @CRMChatListScope
    fun provideInitialFolderListSubject(): PublishSubject<List<Folder>> {
        return PublishSubject.create()
    }

    @Provides
    @CRMChatListScope
    fun provideBaseListFoldersViewModelFactory(
        foldersProvider: CRMChatListFoldersInteractor
    ): BaseListFoldersViewModelFactory {
        return BaseListFoldersViewModelFactory(
            foldersProvider,
            false
        )
    }

    @Provides
    @CRMChatListScope
    fun provideCommunicatorBaseListFolderViewSectionFactory(
        listViewFoldersHolderHelper: CommunicatorBaseListViewFoldersHolderHelper
    ) = CommunicatorBaseListFolderViewSectionFactory(listViewFoldersHolderHelper)

    @Provides
    @CRMChatListScope
    fun provideCommunicatorBaseListViewFoldersHolderHelper(
        foldersViewHolderHelper: FoldersViewHolderHelper
    ) = CommunicatorBaseListViewFoldersHolderHelper(foldersViewHolderHelper)

    @Provides
    @CRMChatListScope
    fun provideFoldersViewHolderHelper(
        viewModelFactory: BaseListFoldersViewModelFactory,
        viewModelStoreOwner: ViewModelStoreOwner,
        scope: LifecycleCoroutineScope
    ) = FoldersViewHolderHelper(viewModelFactory, viewModelStoreOwner, scope)

    @Provides
    @CRMChatListScope
    fun provideHighlightsColorProvider(context: SbisThemedContext) =
        HighlightsColorProvider(context)

    @Provides
    @CRMChatListScope
    fun provideCRMChatSwipeMenuProvider(crmChatSwipeMenuHelper: CRMChatSwipeMenuHelper) =
        CRMChatSwipeMenuProvider(crmChatSwipeMenuHelper)

    @Provides
    @CRMChatListScope
    fun provideCRMChatSwipeMenuHelper(scope: LifecycleCoroutineScope) =
        CRMChatSwipeMenuHelper(scope)

    @Provides
    @CRMChatListScope
    fun provideCRMChatListInteractor(consultationServiceProvider: DependencyProvider<ConsultationService>): CRMChatListInteractor =
        CRMChatListInteractorImpl(consultationServiceProvider)

    @Provides
    @CRMChatListScope
    fun provideConsultationService(): DependencyProvider<ConsultationService> =
        DependencyProvider.create(ConsultationService::instance)

    @Provides
    @CRMChatListScope
    fun provideCRMChatListOnScrollListener(): CRMChatListOnScrollListener =
        CRMChatListOnScrollListener()

    @Provides
    @CRMChatListScope
    fun provideCRMCollectionSynchronizeHelper(): CRMCollectionSynchronizeHelper =
        CRMCollectionSynchronizeHelper()

    @Provides
    @CRMChatListScope
    fun provideCRMChatFilterPreferences(context: SbisThemedContext): CRMChatFilterPreferences =
        CRMChatFilterPreferences(context)
}