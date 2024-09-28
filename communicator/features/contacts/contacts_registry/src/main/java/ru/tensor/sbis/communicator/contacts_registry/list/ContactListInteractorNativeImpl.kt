package ru.tensor.sbis.communicator.contacts_registry.list

import androidx.annotation.WorkerThread
import io.reactivex.Observable
import io.reactivex.Single
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.common.generated.CommandStatus
import ru.tensor.sbis.common.util.asArrayList
import ru.tensor.sbis.communicator.common.util.CommunicatorActivityStatusSubscriptionInitializer
import ru.tensor.sbis.communicator.contacts_declaration.controller.ContactsControllerWrapper
import ru.tensor.sbis.communicator.contacts_declaration.model.filter.ContactListFilter
import ru.tensor.sbis.communicator.contacts_declaration.model.filter.SortContacts
import ru.tensor.sbis.communicator.contacts_declaration.model.result.ContactListResult
import ru.tensor.sbis.communicator.contacts_registry.ui.contactlist.mapper.ContactRegistryModelMapper
import ru.tensor.sbis.communicator.contacts_registry.ui.contactlist.model.ContactRegistryModel
import ru.tensor.sbis.communicator.contacts_registry.ui.contactlist.model.ContactsModel
import ru.tensor.sbis.communicator.contacts_registry.ui.spinner.ContactSortOrder
import ru.tensor.sbis.communicator.contacts_registry.utils.subscribeDataRefresh
import ru.tensor.sbis.communicator.contacts_registry.utils.subscribeMessageSentEvents
import ru.tensor.sbis.communicator.contacts_registry.utils.subscribeProfileSettingsEvents
import ru.tensor.sbis.communicator.generated.MessageController
import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.mvp.interactor.BaseInteractor
import ru.tensor.sbis.profile_service.controller.profile_settings.ProfileSettingsControllerWrapper
import java.util.UUID

/**
 * Реализация интерактора реестра контактов
 *
 * @author da.zhukov
 */
@WorkerThread
internal class ContactListInteractorNativeImpl(
    private val contactsControllerWrapper: ContactsControllerWrapper,
    private val contactRegistryModelMapper: ContactRegistryModelMapper,
    private val activityStatusSubscriptionsInitializer: CommunicatorActivityStatusSubscriptionInitializer,
    private val messageControllerProvider: DependencyProvider<MessageController>,
    private val profileSettingsControllerWrapperProvider: DependencyProvider<ProfileSettingsControllerWrapper>? = null
) : BaseInteractor(), ContactListInteractor {

    private val messageController: MessageController
        get() = messageControllerProvider.get()

    override fun searchContacts(
        searchString: String?,
        folderUuid: UUID?,
        lastItem: ContactRegistryModel?,
        from: Int, count: Int,
        order: ContactSortOrder,
        reload: Boolean
    ): Observable<PagedListResult<ContactRegistryModel>> =
        listOrRefresh(searchString, folderUuid, from, count, order, reload)
            .doOnNext { profiles: ContactListResult? ->
                activityStatusSubscriptionsInitializer.initialize(
                    profiles!!.contacts.map { it.uuid }
                )
            }
            .map { (contacts, hasMore, metadata): ContactListResult ->
                contactRegistryModelMapper.lastItem = if (lastItem is ContactsModel) lastItem else null
                PagedListResult(
                    contactRegistryModelMapper.apply(contacts),
                    hasMore,
                    metadata
                )
            }
            .compose(getObservableBackgroundSchedulers())

    private fun listOrRefresh(
        searchString: String?,
        folderUuid: UUID?,
        from: Int,
        count: Int,
        order: ContactSortOrder,
        reload: Boolean
    ): Observable<ContactListResult> =
        Observable.fromCallable {
            val contactFilter = ContactListFilter(
                searchString,
                if (order == ContactSortOrder.BY_NAME) SortContacts.BY_NAME else SortContacts.BY_DATE,
                folderUuid,
                false,
                true,
                from,
                count
            )
            return@fromCallable if (reload) {
                contactsControllerWrapper.list(contactFilter)
            } else {
                contactsControllerWrapper.refresh(contactFilter)
            }
        }

    // region Contact actions
    override fun moveContacts(
        contacts: Collection<UUID>,
        targetFolderUuid: UUID?,
        currentFolderUuid: UUID?
    ): Single<CommandStatus> =
        Single.fromCallable {
            contactsControllerWrapper.moveContacts(
                contacts.asArrayList(),
                currentFolderUuid,
                targetFolderUuid
            )
        }.compose(getSingleBackgroundSchedulers())

    override fun moveContact(
        contactUuid: UUID,
        targetFolderUuid: UUID?,
        currentFolderUuid: UUID?
    ): Single<CommandStatus> =
        Single.fromCallable {
            contactsControllerWrapper.moveContact(
                contactUuid,
                currentFolderUuid,
                targetFolderUuid
            )
        }.compose(getSingleBackgroundSchedulers())

    override fun deleteContacts(contacts: Collection<UUID>, folderUuid: UUID?): Single<CommandStatus> =
        Single.fromCallable {
            contactsControllerWrapper.removeContacts(contacts.asArrayList(), folderUuid)
        }.compose(getSingleBackgroundSchedulers())

    override fun blockContacts(contactUuids: List<UUID>): Single<CommandStatus> =
        Single.fromCallable {
            contactsControllerWrapper.blockContacts(contactUuids.asArrayList())
        }.compose(getSingleBackgroundSchedulers())

    override fun observeContactsListCacheChanges(): Observable<Unit> =
        Observable.fromCallable { contactsControllerWrapper }
            .flatMap { it.subscribeDataRefresh() }
            .compose(getObservableBackgroundSchedulers())

    override fun observeMessageSentEvents(): Observable<Unit> =
        Observable.fromCallable { messageController }
            .flatMap { it.subscribeMessageSentEvents() }
            .compose(getObservableBackgroundSchedulers())

    override fun canAddNewContacts(): Single<Boolean> =
        Single.fromCallable { contactsControllerWrapper.canAddNewContacts() }
            .compose(getSingleBackgroundSchedulers())

    override fun cancelContactsControllerSynchronizations() {
        contactsControllerWrapper.cancelContactsControllerSynchronizations()
    }

    override fun observeProfileSettingsEvents(): Observable<Unit> =
        Observable.fromCallable { profileSettingsControllerWrapperProvider?.get() }
            .flatMap { it.subscribeProfileSettingsEvents() }
            .compose(getObservableBackgroundSchedulers())

    override fun getNeedImportContactsFromPhone(): Single<Boolean> =
        Single.fromCallable { profileSettingsControllerWrapperProvider?.get()?.getNeedImportContactsFromPhone() }
            .compose(getSingleBackgroundSchedulers())
}