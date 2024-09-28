package ru.tensor.sbis.communicator.base_folders

import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.Single
import io.reactivex.SingleTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import ru.tensor.sbis.common.generated.CommandStatus
import ru.tensor.sbis.common.generated.ErrorCode
import ru.tensor.sbis.common.util.UUIDUtils
import ru.tensor.sbis.toolbox_decl.Result
import ru.tensor.sbis.design.folders.data.model.AdditionalCommand
import ru.tensor.sbis.design.folders.data.model.Folder
import ru.tensor.sbis.design.folders.support.FoldersProvider
import java.util.UUID

/**
 * Базовый функционал провайдера папок
 * @property initialFolderListObservable - иницилизирующий Observable для подписки на получение папок
 *
 * @author vv.chekurda
 */
abstract class CommunicatorBaseFoldersProvider(
    private val initialFolderListObservable: Observable<List<Folder>>? = null,
    needSync: Boolean = false
) : FoldersProvider {

    private val CommandStatus.isSuccess: Boolean
        get() = errorCode == ErrorCode.SUCCESS

    private val foldersRefreshTrigger = PublishSubject.create<Unit>()

    private val supplier by lazy {
        if (needSync) loadFolders() else loadFoldersFromCache()
    }

    private val dataListObservable: Observable<List<Folder>> by lazy {
        Observable.fromCallable(::supplier)
            .mergeWith(dataRefreshObservable)
            .compose(getObservableBackgroundSchedulers())
    }

    private val refreshObservable: Observable<List<Folder>> by lazy {
        foldersRefreshTrigger
            .map { loadFoldersFromCache() }
            .compose(getObservableBackgroundSchedulers())
    }

    protected val folders: BehaviorSubject<List<Folder>> = BehaviorSubject.createDefault(emptyList())

    private val newFolder = PublishSubject.create<UUID>()
    val newFolderObservable: Observable<UUID> = newFolder.compose(getObservableBackgroundSchedulers())

    protected abstract val dataRefreshObservable: Observable<List<Folder>>

    protected abstract fun loadFolders(): List<Folder>

    protected abstract fun loadFoldersFromCache(): List<Folder>

    protected abstract fun createFolder(parentUuid: UUID?, name: String): ResultCreateFolder

    protected abstract fun renameFolder(uuid: UUID, newName: String): CommandStatus

    protected abstract fun deleteFolder(uuid: UUID): CommandStatus

    override fun getFolders(): Observable<List<Folder>> =
        Observable.merge(
            initialFolderListObservable ?: Observable.never(),
            dataListObservable,
            refreshObservable
        ).doOnNext(folders::onNext)

    override fun getAdditionalCommand(): Observable<AdditionalCommand> =
        Observable.never()

    override fun create(parentId: String, name: String): Single<Result> {
        val onlyRootFolder = parentId.isEmpty()
        val parentUuid = if (onlyRootFolder) null else UUID.fromString(parentId)
        return Single.fromCallable { createFolder(parentUuid, name) }
            .compose(getCreateFolderTransformer(onlyRootFolder))
            .compose(getSingleBackgroundSchedulers())
            .doAfterSuccess { reloadFolders() }
    }

    override fun rename(id: String, newName: String): Single<Result> =
        Single.fromCallable { renameFolder(UUID.fromString(id), newName) }
            .compose(getFolderActionTransformer())
            .doAfterSuccess { reloadFolders() }

    override fun delete(id: String): Single<Result> =
        Single.fromCallable { deleteFolder(UUID.fromString(id)) }
            .compose(getFolderActionTransformer())
            .doAfterSuccess { reloadFolders() }

    override fun unshare(id: String): Single<Result> =
        Single.error { IllegalStateException("Unexpected unshare request in communicator register") }

    protected fun reloadFolders() =
        foldersRefreshTrigger.onNext(Unit)

    protected fun <T> getObservableBackgroundSchedulers(): ObservableTransformer<T, T> =
        ObservableTransformer { observable: Observable<T> ->
            observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread(), false)
        }

    protected fun <T> getSingleBackgroundSchedulers(): SingleTransformer<T, T> =
        SingleTransformer { observable: Single<T> ->
            observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        }

    private fun getCreateFolderTransformer(onlyRootFolder: Boolean): SingleTransformer<ResultCreateFolder, Result> =
        SingleTransformer { upstream: Single<ResultCreateFolder> ->
            upstream.map { result ->
                if (result.status.isSuccess) {
                    // испускать событие, если была только корневая папка
                    if (onlyRootFolder) {
                        result.uuidFolder?.let {
                            newFolder.onNext(it)
                        }
                    }
                    Result.SUCCESS
                } else {
                    Result.newInstanceForFail(result.status.errorMessage)
                }
            }.onErrorReturn { throwable: Throwable ->
                Result.newInstanceForFail(throwable.localizedMessage ?: throwable.javaClass.name)
            }
        }

    private fun getFolderActionTransformer(): SingleTransformer<CommandStatus, Result> =
        SingleTransformer { upstream: Single<CommandStatus> ->
            upstream.map { commandStatus: CommandStatus ->
                if (commandStatus.isSuccess) {
                    Result.SUCCESS
                } else {
                    Result.newInstanceForFail(commandStatus.errorMessage)
                }
            }.onErrorReturn { throwable: Throwable ->
                Result.newInstanceForFail(throwable.localizedMessage ?: throwable.javaClass.name)
            }.compose(getSingleBackgroundSchedulers())
        }
}

/**
 * Идентификатор корневой папки
 */
@JvmField
val ROOT_FOLDER_UUID: UUID = UUIDUtils.NIL_UUID

data class ResultCreateFolder(val status: CommandStatus, val uuidFolder: UUID?)