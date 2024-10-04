package ru.tensor.sbis.design_selection.ui.content.di

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import ru.tensor.sbis.communication_decl.selection.SelectionConfig
import ru.tensor.sbis.crud3.ItemWithSection
import ru.tensor.sbis.crud3.ListComponentViewViewModel
import ru.tensor.sbis.crud3.createListComponentViewViewModel
import ru.tensor.sbis.crud3.domain.ItemInSectionMapper
import ru.tensor.sbis.crud3.view.StubFactory
import ru.tensor.sbis.design_selection.contract.controller.SelectionCollectionWrapper
import ru.tensor.sbis.design_selection.contract.customization.selection.SelectableItemsCustomization
import ru.tensor.sbis.design_selection.contract.customization.selection.SelectionClickDelegate
import ru.tensor.sbis.design_selection.contract.data.SelectionFolderItem
import ru.tensor.sbis.design_selection.contract.data.SelectionItem
import ru.tensor.sbis.design_selection.domain.SelectionControllerHolder
import ru.tensor.sbis.design_selection.domain.SelectionInteractor
import ru.tensor.sbis.design_selection.domain.list.SelectionListComponent
import ru.tensor.sbis.design_selection.domain.list.SelectionListItemMapperImpl
import ru.tensor.sbis.design_selection.domain.list.SelectionModeProvider
import ru.tensor.sbis.design_selection.domain.list.SelectionPageSizeHelper
import ru.tensor.sbis.design_selection.domain.list.SelectionStrategyHelper
import ru.tensor.sbis.design_selection.ui.content.listener.SelectionClickDelegateImpl
import ru.tensor.sbis.design_selection.ui.content.listener.SelectionItemClickListener
import ru.tensor.sbis.design_selection.ui.content.vm.SelectionContentViewModel
import ru.tensor.sbis.design_selection.ui.content.vm.SelectionContentViewModelFactory
import ru.tensor.sbis.design_selection.ui.content.vm.SelectionContentViewModelImpl
import ru.tensor.sbis.design_selection.ui.content.vm.search.SelectionSearchViewModel
import ru.tensor.sbis.design_selection.ui.main.di.SelectionController
import ru.tensor.sbis.design_selection.ui.main.utils.SelectionRulesHelper
import ru.tensor.sbis.design_selection.ui.main.vm.contract.SelectionContentDelegate
import ru.tensor.sbis.list.view.item.AnyItem
import ru.tensor.sbis.list.view.item.ViewHolderHelper
import javax.inject.Provider

/**
 * DI модуль области контента компонента выбора.
 *
 * @author vv.chekurda
 */
@Module(includes = [SelectionContentSearchModule::class])
internal class SelectionContentModule {

    @Provides
    @SelectionContentScope
    fun provideFragmentActivity(fragment: Fragment): FragmentActivity =
        fragment.requireActivity()

    @Provides
    @SelectionContentScope
    fun provideItemClickListener(): SelectionItemClickListener<SelectionItem> =
        SelectionItemClickListener()

    @Provides
    @SelectionContentScope
    fun provideClickDelegate(
        itemClickListener: SelectionItemClickListener<SelectionItem>
    ): SelectionClickDelegate =
        SelectionClickDelegateImpl(itemClickListener)

    @Provides
    @SelectionContentScope
    fun provideViewHolderHelpers(
        itemsCustomization: SelectableItemsCustomization<SelectionItem>,
        clickDelegate: SelectionClickDelegate,
        activityProvider: Provider<FragmentActivity>
    ): MutableMap<Any, ViewHolderHelper<SelectionItem, *>> =
        itemsCustomization.createViewHolderHelpers(clickDelegate, activityProvider).toMutableMap()

    @Provides
    @SelectionContentScope
    fun provideDefaultSelectionListItemMapper(
        itemsCustomization: SelectableItemsCustomization<SelectionItem>,
        viewHolderHelpers: MutableMap<Any, ViewHolderHelper<SelectionItem, *>>,
        clickDelegate: SelectionClickDelegate,
        selectionModeProvider: SelectionModeProvider
    ): SelectionListItemMapper =
        SelectionListItemMapperImpl(
            itemsCustomization,
            viewHolderHelpers,
            clickDelegate,
            selectionModeProvider.isMultiSelection
        )

    @Provides
    @SelectionContentScope
    fun provideSelectionController(
        controllerHolder: SelectionControllerHolder,
        folderItem: SelectionFolderItem?
    ): SelectionController =
        controllerHolder.createSelectionController(folderItem = folderItem)

    @Provides
    @SelectionContentScope
    fun provideSelectionInteractor(
        selectionController: SelectionController
    ): SelectionInteractor<SelectionItem> =
        SelectionInteractor(selectionController)

    @Provides
    @SelectionContentScope
    fun provideSelectionListCollectionWrapper(
        selectionController: SelectionController
    ): SelectionListCollectionWrapper =
        selectionController.getSelectionCollectionWrapper()

    @Provides
    @SelectionContentScope
    fun provideListComponentViewModel(
        fragment: Fragment,
        collectionWrapper: SelectionListCollectionWrapper,
        itemMapper: SelectionListItemMapper,
        stubFactory: SelectionStubFactory,
        pageSizeHelper: SelectionPageSizeHelper
    ): ListComponentViewViewModel<ItemWithSection<AnyItem>, Any, SelectionItem> =
        createListComponentViewViewModel(
            viewModelStoreOwner = fragment,
            wrapper = lazy { collectionWrapper },
            mapper = lazy { itemMapper },
            stubFactory = lazy { stubFactory },
            pageSize = pageSizeHelper.pageSize,
            viewPostSize = pageSizeHelper.viewPostSize
        ).apply {
            needScrollToFirstOnReset = false
        }

    @Provides
    @SelectionContentScope
    fun provideSelectionHelper(
        selectionModeProvider: SelectionModeProvider,
        searchVM: SelectionSearchViewModel,
        contentDelegate: SelectionContentDelegate<SelectionItem>,
        rulesHelper: SelectionRulesHelper
    ): SelectionStrategyHelper<SelectionItem> =
        SelectionStrategyHelper(
            selectionModeProvider,
            searchVM,
            contentDelegate,
            rulesHelper
        )

    @Provides
    @SelectionContentScope
    fun provideContentViewModelFactory(
        contentDelegate: SelectionContentDelegate<SelectionItem>,
        searchVM: SelectionSearchViewModel,
        selectionInteractor: SelectionInteractor<SelectionItem>,
        listComponent: SelectionListComponent,
        selectionHelper: SelectionStrategyHelper<SelectionItem>,
        itemClickListener: SelectionItemClickListener<SelectionItem>,
        rulesHelper: SelectionRulesHelper,
        folderItem: SelectionFolderItem?,
        config: SelectionConfig
    ): SelectionContentViewModelFactory =
        SelectionContentViewModelFactory(
            contentDelegate,
            searchVM,
            selectionInteractor,
            listComponent,
            selectionHelper,
            rulesHelper,
            itemClickListener,
            folderItem,
            config
        )

    @Provides
    @SelectionContentScope
    @Suppress("UNCHECKED_CAST")
    fun provideContentViewModel(
        fragment: Fragment,
        factory: SelectionContentViewModelFactory
    ): SelectionContentViewModel<SelectionItem> =
        ViewModelProvider(fragment, factory)[SelectionContentViewModelImpl::class.java]
            as SelectionContentViewModel<SelectionItem>
}

internal typealias SelectionListCollectionWrapper =
    SelectionCollectionWrapper<Any, Any, Any, Any, Any, Any, SelectionItem>
internal typealias SelectionListItemMapper = ItemInSectionMapper<SelectionItem, AnyItem>
typealias SelectionStubFactory = StubFactory