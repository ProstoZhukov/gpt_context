package ru.tensor.sbis.communicator.contacts_registry.ui.folders

import io.reactivex.Observable
import io.reactivex.Single
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.common.generated.CommandStatus
import ru.tensor.sbis.communicator.base_folders.CommunicatorBaseFoldersProvider
import ru.tensor.sbis.communicator.base_folders.ResultCreateFolder
import ru.tensor.sbis.communicator.common.ControllerHelper.checkExecutionTime
import ru.tensor.sbis.communicator.contacts_registry.ContactsRegistryFeatureFacade
import ru.tensor.sbis.communicator.contacts_registry.data.mapper.ContactFolderMapper
import ru.tensor.sbis.communicator.generated.*
import ru.tensor.sbis.design.folders.data.model.Folder
import ru.tensor.sbis.feature_ctrl.SbisFeatureServiceProvider
import ru.tensor.sbis.platform.generated.Subscription
import timber.log.Timber
import java.util.*
import kotlin.collections.HashMap

/**
 * Реализация интерактора для загрузки папок реестра контактов
 *
 * @author vv.chekurda
 */
internal class ContactListFoldersInteractorImpl(
    private val controllerProvider: DependencyProvider<ContactFoldersController>,
    private val mapper: ContactFolderMapper,
    initialFolderListObservable: Observable<List<Folder>>
) : CommunicatorBaseFoldersProvider(initialFolderListObservable),
    ContactListFoldersInteractor {

    private val controller: ContactFoldersController
        get() = controllerProvider.get()
    
    private val featureService: SbisFeatureServiceProvider? =
        ContactsRegistryFeatureFacade.contactsDependency.sbisFeatureServiceProvider

    override val dataRefreshObservable = Observable.create<List<Folder>> { emitter ->
        val subscription2 = featureService?.sbisFeatureService
            ?.getFeatureInfoObservable(listOf("useful_contacts"))
            ?.subscribe {
                if (it.state == true) {
                    loadFolders()
                }
            }
        val subscriptionHolder = controller.dataRefreshed().subscribe(
            object : DataRefreshedContactFoldersControllerCallback() {
                override fun onEvent(param: HashMap<String, String>) {
                    try {
                        emitter.onNext(loadFoldersFromCache())
                    } catch (e: Throwable) {
                        Timber.e(e)
                    }
                }
            }
        )
        subscriptionHolder.enable()
        emitter.setCancellable {
            subscriptionHolder.disable()
            subscription2?.dispose()
        }
    }

    override fun list(): Single<List<Folder>> =
        Single.fromCallable(::loadFolders)
            .compose(getSingleBackgroundSchedulers())

    override fun refresh(): Single<List<Folder>> =
        Single.fromCallable(::loadFoldersFromCache)
            .compose(getSingleBackgroundSchedulers())

    override fun setDataRefreshCallback(callback: (HashMap<String, String>) -> Unit): Single<Subscription> =
        Single.fromCallable {
            controller.dataRefreshed().subscribe(
                object : DataRefreshedContactFoldersControllerCallback() {
                    override fun onEvent(param: HashMap<String, String>) {
                        try {
                            callback(param)
                        } catch (e: Throwable) {
                            Timber.e(e)
                        }
                    }
                }
            )
        }.compose(getSingleBackgroundSchedulers())

    override fun loadFolders(): List<Folder> =
        controller.list(ContactFoldersFilter())
            .result
            .map()

    override fun loadFoldersFromCache(): List<Folder> =
        controller.refresh(ContactFoldersFilter())
            .result
            .map()

    override fun createFolder(parentUuid: UUID?, name: String): ResultCreateFolder =
        checkExecutionTime("ContactFoldersController.createFolder") {
            val result = controller.createFolder(parentUuid, name)
            ResultCreateFolder(result.status, result.folder?.uuid)
        }

    override fun renameFolder(uuid: UUID, newName: String): CommandStatus =
        checkExecutionTime("ContactFoldersController.renameFolder") {
            controller.renameFolder(uuid, newName)
        }

    override fun deleteFolder(uuid: UUID): CommandStatus =
        checkExecutionTime("ContactFoldersController.deleteFolder") {
            controller.deleteFolder(uuid)
        }

    fun getSelectedFolderSubject(id: String): Observable<Folder> =
        Observable.create<Folder?> { emitter ->
            val subscription = folders.subscribe { folderList ->
                folderList.find { it.id == id }?.also(emitter::onNext)
            }
            emitter.setCancellable { subscription.dispose() }
        }.compose(getObservableBackgroundSchedulers())

    private fun List<ContactFolder>.map(): List<Folder> {
        return map {
            mapper.apply(it)
        }
    }

}