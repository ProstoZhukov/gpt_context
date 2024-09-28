package ru.tensor.sbis.communicator.contacts_registry.di.list

import android.content.Context
import dagger.Module
import dagger.Provides
import io.reactivex.subjects.PublishSubject
import ru.tensor.sbis.base_components.adapter.checkable.ObservableCheckCountHelper
import ru.tensor.sbis.base_components.adapter.checkable.impl.ObservableCheckCountHelperImpl
import ru.tensor.sbis.base_components.adapter.selectable.SelectionHelper
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.common.util.DeviceConfigurationUtils
import ru.tensor.sbis.common.util.NetworkUtils
import ru.tensor.sbis.common.util.UUIDUtils
import ru.tensor.sbis.common.util.scroll.ScrollHelper
import ru.tensor.sbis.communicator.common.data.model.NetworkAvailability
import ru.tensor.sbis.communicator.common.util.CommunicatorActivityStatusSubscriptionInitializer
import ru.tensor.sbis.communicator.common.util.SwipeMenuViewPoolLifecycleManager
import ru.tensor.sbis.communicator.contacts_declaration.controller.ContactsControllerWrapper
import ru.tensor.sbis.communicator.contacts_registry.contract.ContactsRegistryDependency
import ru.tensor.sbis.communicator.contacts_registry.data.mapper.ContactFolderMapper
import ru.tensor.sbis.communicator.contacts_registry.list.ContactListInteractor
import ru.tensor.sbis.communicator.contacts_registry.list.ContactListInteractorNativeImpl
import ru.tensor.sbis.communicator.contacts_registry.ui.contactlist.ContactListAdapter
import ru.tensor.sbis.communicator.contacts_registry.ui.contactlist.ContactListContract
import ru.tensor.sbis.communicator.contacts_registry.ui.contactlist.ContactListPresenterImpl
import ru.tensor.sbis.communicator.contacts_registry.ui.contactlist.EXPECTED_VISIBLE_SWIPE_MENU_ITEM_COUNT
import ru.tensor.sbis.communicator.contacts_registry.ui.contactlist.mapper.ContactRegistryModelMapper
import ru.tensor.sbis.communicator.contacts_registry.ui.contactlist.model.ContactRegistryModel
import ru.tensor.sbis.communicator.contacts_registry.ui.contactlist.model.ContactsModel
import ru.tensor.sbis.communicator.contacts_registry.ui.contactlist.stub_helper.ContactsStubHelper
import ru.tensor.sbis.communicator.contacts_registry.ui.contactlist.stub_helper.ContactsStubHelperImpl
import ru.tensor.sbis.communicator.contacts_registry.ui.folders.ContactListFoldersInteractor
import ru.tensor.sbis.communicator.contacts_registry.ui.folders.ContactListFoldersInteractorImpl
import ru.tensor.sbis.communicator.contacts_registry.ui.spinner.ContactSortOrderProvider
import ru.tensor.sbis.communicator.generated.ContactFoldersController
import ru.tensor.sbis.communicator.generated.MessageController
import ru.tensor.sbis.design.folders.data.model.Folder
import ru.tensor.sbis.design.list_header.ListDateViewUpdater
import ru.tensor.sbis.design.list_header.format.ListDateFormatter
import ru.tensor.sbis.swipeablelayout.swipeablevm.SwipeableVmHelper
import ru.tensor.sbis.swipeablelayout.util.SwipeableViewmodelsHolder
import ru.tensor.sbis.swipeablelayout.viewpool.SwipeMenuViewPool
import ru.tensor.sbis.verification_decl.login.LoginInterface
import javax.inject.Named

/**
 * DI модуль реестра контактов
 *
 * @author da.zhukov
 */
@Module
internal class ContactListModule {
    @Provides
    @ContactListScope
    fun providePresenter(
        contactSortOrderProvider: ContactSortOrderProvider,
        interactor: ContactListInteractor,
        foldersInteractor: ContactListFoldersInteractorImpl,
        loginInterface: LoginInterface,
        networkUtils: NetworkUtils,
        selectionHelper: SelectionHelper<ContactRegistryModel>,
        scrollHelper: ScrollHelper,
        checkHelper: ObservableCheckCountHelper<ContactRegistryModel?>,
        contactsDependency: ContactsRegistryDependency?,
        networkAvailability: NetworkAvailability,
        contactsStubHelper: ContactsStubHelper,
        @Named(IS_VIEW_HIDDEN_NAME) isViewHidden: Boolean
    ): ContactListContract.Presenter {
        return ContactListPresenterImpl(
            contactSortOrderProvider,
            interactor,
            foldersInteractor,
            loginInterface,
            networkUtils,
            selectionHelper,
            scrollHelper,
            checkHelper,
            contactsDependency?.redButtonActivatedProvider,
            networkAvailability,
            contactsStubHelper,
            isViewHidden,
            contactsDependency?.importContactsHelperProvider != null &&
                contactsDependency.importContactsConfirmationFragmentFactory != null
        )
    }

    @Provides
    @ContactListScope
    fun provideAdapter(
        swipeMenuViewPool: SwipeMenuViewPool,
        dateUpdater: ListDateViewUpdater
    ): ContactListAdapter {
        return ContactListAdapter(swipeMenuViewPool, dateUpdater)
    }

    @Provides
    @ContactListScope
    fun provideContactListInteractor(
        contactsControllerWrapper: ContactsControllerWrapper,
        contactRegistryModelMapper: ContactRegistryModelMapper,
        subscriptionsInitializer: CommunicatorActivityStatusSubscriptionInitializer,
        messageControllerProvider: DependencyProvider<MessageController>,
        contactsDependency: ContactsRegistryDependency?,
    ): ContactListInteractor {
        return ContactListInteractorNativeImpl(
            contactsControllerWrapper,
            contactRegistryModelMapper,
            subscriptionsInitializer,
            messageControllerProvider,
            contactsDependency?.profileSettingsControllerWrapperProvider?.profileSettingsControllerWrapper
        )
    }

    @Provides
    @ContactListScope
    fun provideContactRegistryModelMapper(): ContactRegistryModelMapper {
        return ContactRegistryModelMapper()
    }

    //region Helpers
    @Provides
    @ContactListScope
    fun provideSelectionHelper(context: Context): SelectionHelper<ContactRegistryModel> {
        return SelectionHelper(DeviceConfigurationUtils.isTablet(context))
    }

    @Provides
    @ContactListScope
    fun provideCheckHelper(): ObservableCheckCountHelper<ContactRegistryModel> {
        return ObservableCheckCountHelperImpl { entity: ContactRegistryModel? ->
            if (entity is ContactsModel) {
                UUIDUtils.toString(entity.contact.uuid)
            } else UUIDUtils.NIL_UUID.toString()
        }
    }

    //endregion
    @Provides
    @ContactListScope
    fun provideEmployeesNetworkAvailability(): NetworkAvailability {
        return NetworkAvailability()
    }

    @Provides
    @ContactListScope
    fun provideContactsStubHelper(): ContactsStubHelper {
        return ContactsStubHelperImpl()
    }

    @Provides
    fun provideSwipeableVmHelper(): SwipeableVmHelper {
        return SwipeableVmHelper()
    }

    @Provides
    fun provideSwipeableVmKeeper(): SwipeableViewmodelsHolder {
        return SwipeableViewmodelsHolder()
    }

    @Provides
    @ContactListScope
    fun provideSwipeMenuViewPool(context: Context): SwipeMenuViewPool {
        return SwipeMenuViewPool.createForItemsWithIcon(context, EXPECTED_VISIBLE_SWIPE_MENU_ITEM_COUNT)
    }

    @Provides
    @ContactListScope
    fun provideItemViewPoolWithSwipeableLayoutLifecycleManager(
        itemViewPool: SwipeMenuViewPool
    ): SwipeMenuViewPoolLifecycleManager {
        return SwipeMenuViewPoolLifecycleManager(itemViewPool)
    }

    @Provides
    @ContactListScope
    fun provideContactFoldersControllerProvider(): DependencyProvider<ContactFoldersController> {
        return DependencyProvider.create { ContactFoldersController.instance() }
    }

    @Provides
    @ContactListScope
    fun provideContactSortOrderProvider(context: Context): ContactSortOrderProvider {
        return ContactSortOrderProvider(context)
    }

    @Provides
    @ContactListScope
    fun provideContactListFoldersInteractorImpl(
        controllerWrapper: DependencyProvider<ContactFoldersController>,
        mapper: ContactFolderMapper?,
        initialFoldersListSubject: PublishSubject<List<Folder>>
    ): ContactListFoldersInteractorImpl {
        return ContactListFoldersInteractorImpl(controllerWrapper, mapper!!, initialFoldersListSubject)
    }

    @Provides
    @ContactListScope
    fun provideCommunicatorFoldersInteractor(
        interactor: ContactListFoldersInteractorImpl
    ): ContactListFoldersInteractor {
        return interactor
    }

    @Provides
    @ContactListScope
    fun provideListDateViewUpdater(
        formatter: ListDateFormatter.DateTimeWithTodayShort
    ): ListDateViewUpdater {
        return ListDateViewUpdater(formatter)
    }

    @Provides
    @ContactListScope
    fun providerContactFolderMapper(): ContactFolderMapper {
        return ContactFolderMapper()
    }

    @Provides
    @ContactListScope
    fun provideInitialFolderListSubject(): PublishSubject<List<Folder>> {
        return PublishSubject.create()
    }
}

internal const val IS_VIEW_HIDDEN_NAME = "IS_VIEW_HIDDEN"