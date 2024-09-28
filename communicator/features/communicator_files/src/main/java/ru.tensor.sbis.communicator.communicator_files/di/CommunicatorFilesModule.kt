package ru.tensor.sbis.communicator.communicator_files.di

import android.view.View
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.ViewModelStoreOwner
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import dagger.Module
import dagger.Provides
import io.reactivex.subjects.PublishSubject
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.communicator.base_folders.list_section.BaseListFoldersViewModelFactory
import ru.tensor.sbis.communicator.base_folders.list_section.CommunicatorBaseFolderOptions
import ru.tensor.sbis.communicator.base_folders.list_section.CommunicatorBaseListFolderViewSectionFactory
import ru.tensor.sbis.communicator.base_folders.list_section.CommunicatorBaseListViewFoldersHolderHelper
import ru.tensor.sbis.communicator.base_folders.list_section.FoldersViewHolderHelper
import ru.tensor.sbis.communicator.communicator_files.data.CommunicatorFilesCollectionWrapperImpl
import ru.tensor.sbis.communicator.communicator_files.data.CommunicatorFilesFilterHolder
import ru.tensor.sbis.communicator.communicator_files.data.CommunicatorFilesWrapper
import ru.tensor.sbis.communicator.communicator_files.databinding.CommunicatorFilesFragmentBinding
import ru.tensor.sbis.communicator.communicator_files.mapper.CommunicatorFilesHolderHelper
import ru.tensor.sbis.communicator.communicator_files.mapper.CommunicatorFilesMapper
import ru.tensor.sbis.communicator.communicator_files.store.CommunicatorFilesStoreFactory
import ru.tensor.sbis.communicator.communicator_files.ui.CommunicatorFilesListComponentFactory
import ru.tensor.sbis.communicator.communicator_files.ui.CommunicatorFilesView
import ru.tensor.sbis.communicator.communicator_files.ui.CommunicatorFilesViewImpl
import ru.tensor.sbis.communicator.communicator_files.ui.ConversationFileOriginDecoration
import ru.tensor.sbis.communicator.communicator_files.ui.folders.CommunicatorFilesFolderProvider
import ru.tensor.sbis.communicator.communicator_files.utils.CommunicatorFilesAttachmentViewPool
import ru.tensor.sbis.communicator.communicator_files.utils.calculateQuantityOfViews
import ru.tensor.sbis.communicator.generated.ThemeAttachmentCollectionProvider
import ru.tensor.sbis.communicator.generated.ThemeAttachmentFilter
import ru.tensor.sbis.design.folders.data.model.Folder
import ru.tensor.sbis.mvi_extension.AndroidStoreFactory
import ru.tensor.sbis.plugin_struct.utils.SbisThemedContext
import java.util.UUID

/**
 * DI модуль файлов переписки.
 *
 * @author da.zhukov
 */
@Module
internal class CommunicatorFilesModule {

    @Provides
    @CommunicatorFilesScope
    fun provideViewFactory(
        listComponentFactory: CommunicatorFilesListComponentFactory,
        foldersViewHolderHelper: FoldersViewHolderHelper
    ): (View) -> CommunicatorFilesView {
        return {
            CommunicatorFilesViewImpl(
                CommunicatorFilesFragmentBinding.bind(it),
                listComponentFactory,
                foldersViewHolderHelper
            )
        }
    }

    @Provides
    @CommunicatorFilesScope
    fun provideStoreFactory(): StoreFactory {
        return AndroidStoreFactory(DefaultStoreFactory())
    }

    @Provides
    @CommunicatorFilesScope
    fun provideCommunicatorFilesStoreFactory(
        storeFactory: StoreFactory,
        listComponentFactory: CommunicatorFilesListComponentFactory,
        filterHolder: CommunicatorFilesFilterHolder,
    ): CommunicatorFilesStoreFactory {
        return CommunicatorFilesStoreFactory(
            storeFactory,
            listComponentFactory,
            filterHolder
        )
    }

    @Provides
    @CommunicatorFilesScope
    fun provideCommunicatorFilesListComponentFactory(
        viewModelStoreOwner: ViewModelStoreOwner,
        listCollectionWrapper: CommunicatorFilesWrapper,
        listMapper: CommunicatorFilesMapper,
        listViewSectionFactory: CommunicatorBaseListFolderViewSectionFactory
    ): CommunicatorFilesListComponentFactory {
        return CommunicatorFilesListComponentFactory(
            viewModelStoreOwner,
            listCollectionWrapper,
            listMapper,
            listViewSectionFactory
        )
    }

    @Provides
    @CommunicatorFilesScope
    fun provideCommunicatorFilesHolderHelper(
        viewPool: CommunicatorFilesAttachmentViewPool
    ): CommunicatorFilesHolderHelper =
        CommunicatorFilesHolderHelper(viewPool)

    @Provides
    @CommunicatorFilesScope
    fun provideCommunicatorFilesMapper(
        context: SbisThemedContext,
        holderHelper: CommunicatorFilesHolderHelper,
        itemDecoration: ConversationFileOriginDecoration
    ): CommunicatorFilesMapper =
        CommunicatorFilesMapper(context, holderHelper, itemDecoration)

    @Provides
    @CommunicatorFilesScope
    fun provideConversationFileOriginDecoration(context: SbisThemedContext): ConversationFileOriginDecoration =
        ConversationFileOriginDecoration(context)

    @Provides
    @CommunicatorFilesScope
    fun provideCommunicatorFilesWrapper(
        channelHierarchyCollectionProvider: DependencyProvider<ThemeAttachmentCollectionProvider>,
        filterHolder: CommunicatorFilesFilterHolder
    ): CommunicatorFilesWrapper {
        return CommunicatorFilesCollectionWrapperImpl(channelHierarchyCollectionProvider, filterHolder)
    }

    @Provides
    @CommunicatorFilesScope
    fun provideThemeAttachmentCollectionProvider(): DependencyProvider<ThemeAttachmentCollectionProvider> =
        DependencyProvider.create(ThemeAttachmentCollectionProvider::instance)

    @Provides
    @CommunicatorFilesScope
    fun provideFilterHolder(
        themeId: UUID,
        context: SbisThemedContext
    ): CommunicatorFilesFilterHolder {
        return CommunicatorFilesFilterHolder(
            ThemeAttachmentFilter(themeId).apply {
                rowItemCount = context.applicationContext.calculateQuantityOfViews()
            }
        )
    }

    @Provides
    @CommunicatorFilesScope
    fun provideCommunicatorBaseListFolderViewSectionFactory(
        listViewFoldersHolderHelper: CommunicatorBaseListViewFoldersHolderHelper
    ) = CommunicatorBaseListFolderViewSectionFactory(listViewFoldersHolderHelper)

    @Provides
    @CommunicatorFilesScope
    fun provideCommunicatorBaseListViewFoldersHolderHelper(
        foldersViewHolderHelper: FoldersViewHolderHelper
    ) = CommunicatorBaseListViewFoldersHolderHelper(
        foldersViewHolderHelper,
        CommunicatorBaseFolderOptions(
            isExpandable = true,
            isShownCurrentFolder = false
        )
    )

    @Provides
    @CommunicatorFilesScope
    fun provideFoldersViewHolderHelper(
        viewModelFactory: BaseListFoldersViewModelFactory,
        viewModelStoreOwner: ViewModelStoreOwner,
        scope: LifecycleCoroutineScope
    ) = FoldersViewHolderHelper(viewModelFactory, viewModelStoreOwner, scope)

    @Provides
    @CommunicatorFilesScope
    fun provideBaseListFoldersViewModelFactory(
        foldersProvider: CommunicatorFilesFolderProvider
    ): BaseListFoldersViewModelFactory {
        return BaseListFoldersViewModelFactory(
            foldersProvider
        )
    }

    @Provides
    @CommunicatorFilesScope
    fun provideInitialFolderListSubject(): PublishSubject<List<Folder>> {
        return PublishSubject.create()
    }

    @Provides
    @CommunicatorFilesScope
    fun provideCommunicatorFilesFolderProvider(
        initialFolderListObservable: PublishSubject<List<Folder>>,
        filterHolder: CommunicatorFilesFilterHolder,
        context: SbisThemedContext
    ): CommunicatorFilesFolderProvider {
        return CommunicatorFilesFolderProvider(
            initialFolderListObservable,
            filterHolder,
            context
        )
    }
}