package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.dialogs.folders

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.common.generated.CommandStatus
import ru.tensor.sbis.communicator.base_folders.CommunicatorBaseFoldersProvider
import ru.tensor.sbis.communicator.base_folders.ResultCreateFolder
import ru.tensor.sbis.communicator.common.ControllerHelper.checkExecutionTime
import ru.tensor.sbis.communicator.themes_registry.data.mapper.DialogFolderMapperNew
import ru.tensor.sbis.communicator.generated.*
import ru.tensor.sbis.design.folders.data.model.Folder
import ru.tensor.sbis.platform.generated.Subscription
import timber.log.Timber
import java.util.*
import kotlin.collections.HashMap

/**
 * Реализация интерактора для загрузки папок в диалогах
 *
 * @author rv.krohalev
 */
internal class ThemeFoldersInteractorImpl @JvmOverloads constructor(
    private val dialogFolderControllerProvider: DependencyProvider<DialogFolderController>,
    private val mapper: DialogFolderMapperNew,
    private var dialogFilter: DialogFilter,
    private val initialFoldersList: Subject<List<Folder>> = BehaviorSubject.createDefault(emptyList())
) : CommunicatorBaseFoldersProvider(initialFoldersList, needSync = true),
    ThemeFoldersInteractor {

    private val dialogFolderController: DialogFolderController
        get() = dialogFolderControllerProvider.get()

    @get:Synchronized @set:Synchronized
    private var currentFolders: List<Folder> = emptyList()

    override val dataRefreshObservable = Observable.create<List<Folder>> { emitter ->
        val subscriptionHolder = dialogFolderController.dataRefreshed().subscribe(
            object : DataRefreshedDialogFolderControllerCallback() {
                override fun onEvent() {
                    try {
                        emitter.onNext(loadFoldersFromCache())
                    } catch (e: Throwable) {
                        Timber.e(e)
                    }
                }
            }
        )
        subscriptionHolder.enable()
        emitter.setCancellable { subscriptionHolder.disable() }
    }

    override fun trySetFoldersSync() {
        currentFolders.also {
            initialFoldersList.onNext(it)
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
            dialogFolderController.dataRefreshed().subscribe(
                object : DataRefreshedDialogFolderControllerCallback() {
                    override fun onEvent() {
                        try {
                            callback(hashMapOf())
                        } catch (e: Throwable) {
                            Timber.e(e)
                        }
                    }
                }
            )
        }.compose(getSingleBackgroundSchedulers())

    override fun loadFolders(): List<Folder> {
        return dialogFolderController.list(
            DialogFolderFilter(null, true, dialogFilter)
        ).result.map().also {
            currentFolders = it
        }
    }


    override fun loadFoldersFromCache(): List<Folder> {
        return dialogFolderController.refresh(
            DialogFolderFilter(null, true, dialogFilter)
        ).result.map().also {
            currentFolders = it
        }
    }

    override fun createFolder(parentUuid: UUID?, name: String): ResultCreateFolder =
        checkExecutionTime("DialogFolderController.createFolder") {
            val result = dialogFolderController.createFolder(parentUuid, name)
            ResultCreateFolder(result.status, result.data?.uuid)
        }

    override fun renameFolder(uuid: UUID, newName: String): CommandStatus =
        checkExecutionTime("DialogFolderController.rename") {
            dialogFolderController.rename(uuid, newName).status
        }

    override fun deleteFolder(uuid: UUID): CommandStatus =
        checkExecutionTime("DialogFolderController.deleteFolder") {
            dialogFolderController.deleteFolder(uuid)
        }

    override fun setDialogFilter(dialogFilter: DialogFilter) {
        this.dialogFilter = dialogFilter
        reloadFolders()
    }

    private fun List<DialogFolder>.map(): List<Folder> {
        return map {
            mapper.apply(it)
        }
    }
}
