package ru.tensor.sbis.folderspanel

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.SerialDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import ru.tensor.sbis.common.util.ItemSwipeState
import ru.tensor.sbis.common.util.ResourceProvider
import ru.tensor.sbis.toolbox_decl.Result
import ru.tensor.sbis.design.utils.formatCount
import ru.tensor.sbis.mvp.fragment.selection.SelectionWindowPresenter
import ru.tensor.sbis.swipeablelayout.AddFolderIcon
import ru.tensor.sbis.swipeablelayout.DefaultMenuItem
import ru.tensor.sbis.swipeablelayout.RenameIcon
import ru.tensor.sbis.swipeablelayout.swipeablevm.SwipeableVmHelper
import ru.tensor.sbis.swipeablelayout.util.SwipeableViewmodelsHolder
import timber.log.Timber
import java.util.*
import ru.tensor.sbis.common.R as RCommon

/**
 * Презентер, управляющий [BaseFolderPickPanel].
 * Реализует отображение папок и действия из свайп-меню (создание, удаление, переименование, перемещение).
 * Управляет выделением текущей (активной) папки
 */
abstract class BaseFolderPresenter<FOLDER : Folder>(
    protected val interactor: FoldersInteractor<FOLDER>,
    protected val resourceProvider: ResourceProvider,
    protected var swipeableVmHelper: SwipeableVmHelper,
    selectedFolderSubject: BehaviorSubject<String> = BehaviorSubject.create<String>()
) : SelectionWindowPresenter<FolderPanelContract.View>(),
    FolderPanelContract.Presenter<FolderPanelContract.View> {

    private val swipeableVmHolder = SwipeableViewmodelsHolder()
    protected val compositeDisposable = CompositeDisposable()
    private val pickNameSubject = PublishSubject.create<String>()
    private val pickNameDisposable = SerialDisposable()
    private val moveToFolderSubject = PublishSubject.create<String>()
    private val moveToFolderDisposable = SerialDisposable()
    private val deleteSubject = PublishSubject.create<Any>()
    private val deleteSubjectDisposable = SerialDisposable()
    private val folderListDisposable: SerialDisposable = SerialDisposable()
    private var folders: List<FolderViewModel> = ArrayList()
    protected val foldersSelectedStateDisposables = CompositeDisposable()
    protected var selectedFolder = selectedFolderSubject
    protected val swipedItemSubject = PublishSubject.create<FolderViewModel>()
    protected val swipedItemDisposables = CompositeDisposable()
    protected var deletingItem: FolderViewModel? = null

    init {
        @Suppress("DEPRECATION")
        showAppearAnimationWhenAttached = false
        initDisposables()
        updateFolderList()
    }

    //region FolderPanelContract
    /**
     * Callback подтверждения переименования папки
     * @param name введенное имя папки
     */
    override fun onNameAcceptedFromPickNameDialog(name: String) {
        pickNameSubject.onNext(name)
    }

    /**
     * Callback подтверждения перемещения папки в другую папку
     * @param pickedFolder uuid выбранной (целевой) папки
     */
    override fun onFolderPicked(pickedFolder: String) {
        moveToFolderSubject.onNext(pickedFolder)
    }

    /**
     * Callback подтверждения удаления папки
     */
    override fun onFolderDeletionAccepted() {
        deleteSubject.onNext(Any())
    }

    /**
     * Обработчик нажатия на кнопку создания папки
     */
    override fun onNewFolderClick() {
        view?.showPickNameDialog("")

        pickNameDisposable.set(pickNameSubject
                .observeOn(Schedulers.io())
                .flatMap { name ->
                    selectedFolder.value?.let { interactor.create(it, name) }
                            ?: interactor.createInRoot(name)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { resultAndFolder ->
                    updateFolderListOrShowError(resultAndFolder.first)
                })
    }

    /**
     * Установка текущей папки при инициализации
     * @param folder uuid папки
     */
    override fun setInitFolder(folder: String) {
        setFolderToSelect(folder)
    }

    /**
     * Установка выбранной папки при смене папки
     * @param folder uuid папки
     */
    override fun setFolderToSelect(folder: String) {
        selectedFolder.onNext(folder)
    }
    //endregion

    //region BasePresenter
    override fun onDestroy() {
        compositeDisposable.dispose()
    }

    override fun attachView(view: FolderPanelContract.View) {
        super.attachView(view)
        showFolders()
        showProgress()
    }
    //endregion

    //region to override
    protected abstract fun onFolderSelected(uuid: String)

    protected abstract fun mapFolderItemToViewModel(folder: FOLDER, longestTotalCount: String): FolderViewModel
    //endregion

    private fun initDisposables() {
        compositeDisposable.add(deleteSubjectDisposable)
        compositeDisposable.add(pickNameDisposable)
        compositeDisposable.add(moveToFolderDisposable)
        compositeDisposable.add(folderListDisposable)
        compositeDisposable.add(foldersSelectedStateDisposables)
        compositeDisposable.add(selectedFolder.distinctUntilChanged().subscribe { onFolderSelected(it) })
        compositeDisposable.add(swipedItemDisposables)
    }

    private fun showFolders() {
        if (folders.isNotEmpty()) {
            swipeableVmHolder.useRetainedSwipeableViewModels(folders)
            swipeableVmHelper.setupSwipeableViewModels(folders)
            @Suppress("DEPRECATION")
            showAppearAnimationIfNotShownBefore()
        }
        view?.showFolders(folders)
    }

    private fun showProgress() {
        if (folders.isEmpty()) {
            view?.showProgress()
        }
    }

    protected fun updateFolderList() {
        folderListDisposable.set(interactor.getFolders()
                .map { folderList ->
                    prepareFolderListItems(folderList)
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(::onFoldersLoaded, ::onLoadingError)
        )
    }

    /**
     * Обработка успешной загрузки списка папок
     *
     * @param folderList список загруженных папок
     */
    protected open fun onFoldersLoaded(folderList: List<FolderViewModel>) {
        folders = folderList
        showFolders()
    }

    /**
     * Обработка ошибки при загрузке папок
     *
     * @param error выброшенное исключение
     */
    protected open fun onLoadingError(error: Throwable) {
        Timber.e(error)
    }

    open fun prepareFolderListItems(folderList: List<FOLDER>): List<FolderViewModel> {
        foldersSelectedStateDisposables.clear()
        val longestTotalCount = calcLongestTotalCountForFolders(folderList)
        return folderList.map { folder ->
            val uuid = folder.getStringUuid()
            mapFolderItemToViewModel(folder, longestTotalCount).apply {
                foldersSelectedStateDisposables.add(selectedFolder.subscribe {
                    setSelected(it == uuid)
                })
                swipedItemDisposables.add(swipedItemSubject.subscribe { swipedItem ->
                    if (this != swipedItem) {
                        swipeState.set(ItemSwipeState.CLOSED_WITH_ANIMATION)
                    }
                })
                onClick = { onItemClick(folder) }
                actionOnSwipeOpenStart = Runnable { swipedItemSubject.onNext(this) }
                val folderToSelect = findFolderToSelectAfterDelete(folderList, uuid)
                val actionOnDismiss = Runnable { onItemDeleteClick(folder, folderToSelect) }
                setSwipeMenu(getSwipeMenuItems(this, folder, folderList), false, actionOnDismiss)
            }
        }.apply {
            swipeableVmHelper.setupSwipeableViewModels(this)

        }
    }

    /**
     * Фабрика-создатель для свайп меню
     *
     * @return список айтемов свайп меню
     * */
    open fun getSwipeMenuItems(
            viewModel: FolderViewModel,
            folder: FOLDER,
            folderList: List<FOLDER>
    ): List<DefaultMenuItem> = ArrayList<DefaultMenuItem>().apply {
        if (viewModel.canChange) {
            addAll(listOf(
                // rename
                DefaultMenuItem.createWithNoLabel(
                    RenameIcon,
                    onClick = {
                        onItemRenameClick(folder)
                        viewModel.swipeState.set(ItemSwipeState.CLOSED_WITH_ANIMATION)
                    }
                ),
                // add
                DefaultMenuItem.createWithNoLabel(
                    AddFolderIcon,
                    onClick = {
                        onItemAddNewClick(folder)
                        viewModel.swipeState.set(ItemSwipeState.CLOSED_WITH_ANIMATION)
                    }
                )
            ))
        }
        if (viewModel.canRemove) {
            add(
                // delete
                DefaultMenuItem.createRemoveOption(
                    onClick = {
                        val folderToSelect = findFolderToSelectAfterDelete(folderList, viewModel.uuid)
                        onItemDeleteClick(folder, folderToSelect)
                        viewModel.swipeState.set(ItemSwipeState.CLOSED_WITH_ANIMATION)
                    }
                )
            )
        }
    }


    /**@SelfDocumented*/
    protected fun findFolderToSelectAfterDelete(folderList: List<FOLDER>, deletedFolder: String): String? {
        val folderMap = folderList.map { it.getStringUuid() to it }.toMap()
        var result: String? = null
        var uuid = selectedFolder.value
        // обход родительских папок, начиная с uuid, и сравнение с ожидаемым родителем
        while (true) {
            val folder = folderMap[uuid] ?: break

            if (uuid.equals(deletedFolder)) {
                result = folder.getParentStringUuid()
                break
            } else {
                if (uuid.equals(folder.getParentStringUuid())) {
                    break
                } else {
                    uuid = folder.getParentStringUuid()
                }
            }
        }
        return result
    }

    /**@SelfDocumented*/
    protected fun onItemDeleteClick(folder: FOLDER, folderToSelect: String?, doesConfirmationDialogShow: Boolean = false) {
        closeAllSwipedItems()
        // сначала подписка
        deleteSubjectDisposable.set(
            deleteSubject
                .observeOn(Schedulers.io())
                .flatMap { interactor.delete(folder.getStringUuid()) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { result ->
                    deletingItem = null
                    updateFolderListOrShowError(result)
                    if (result.success && folderToSelect != null) {
                        onSelectedFolderSuccessfullyDeleted(folderToSelect)
                    } else if (!result.success) {
                        onFolderDeletionError()
                    }
                }
        )
        deletingItem = folders.firstOrNull { it.uuid == folder.getStringUuid() }

        // подтверждение сразу или через диалог
        if (doesConfirmationDialogShow) {
            onFolderDeletionAccepted()
        } else {
            view?.showDeletionAcceptDialog(
                title = getDeletionDialogTitle(folder),
                message = getDeletionDialogMessage(folder),
                acceptButtonText = getDeletionDialogPositiveButtonText(folder),
                cancelButtonText = getDeletionDialogNegativeButtonText(folder)
            )
        }
    }

    /**@SelfDocumented*/
    protected open fun getDeletionDialogTitle(folder: FOLDER) =
        resourceProvider.getString(RCommon.string.common_folder_delete_dialog, folder.getTitle())

    /**@SelfDocumented*/
    protected open fun getDeletionDialogMessage(folder: FOLDER) =
        if (folder.isNotEmpty()) resourceProvider.getString(RCommon.string.common_folder_detail_message_move_to_parent_folder) else null

    /**@SelfDocumented*/
    protected open fun getDeletionDialogPositiveButtonText(folder: FOLDER) =
        resourceProvider.getString(RCommon.string.common_folder_delete_dialog_submit)

    /**@SelfDocumented*/
    protected open fun getDeletionDialogNegativeButtonText(folder: FOLDER) =
        resourceProvider.getString(RCommon.string.common_folder_edit_dialog_cancle)

    /**@SelfDocumented*/
    protected open fun onSelectedFolderSuccessfullyDeleted(substitutionFolderUuid: String) {
        deletingItem = null
        setFolderToSelect(substitutionFolderUuid)
    }

    /**
     * В случае ошибки при удалении элемента свайпом необходимо откатить состояние свайп меню
     */
    protected open fun onFolderDeletionError() = closeItemForDeletion()

    /**
     * Возврат свайп-меню удаляемого элемента к исходному состоянию после отмены удаления
     */
    override fun onCancelFolderDeletion() = closeItemForDeletion()

    /**@SelfDocumented*/
    protected fun onItemAddNewClick(folder: FOLDER) {
        closeAllSwipedItems()
        view?.showPickNameDialog("")

        pickNameDisposable.set(pickNameSubject
                .observeOn(Schedulers.io())
                .flatMap { newFolderName -> interactor.create(folder.getStringUuid(), newFolderName.trim()) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { resultAndFolder ->
                    updateFolderListOrShowError(resultAndFolder.first)
                })
    }

    /**@SelfDocumented*/
    protected fun onItemRenameClick(folder: FOLDER) {
        closeAllSwipedItems()
        view?.showPickNameDialog(folder.getTitle())

        pickNameDisposable.set(pickNameSubject
                .observeOn(Schedulers.io())
                .flatMap { folderNewName -> interactor.rename(folder.getStringUuid(), folderNewName.trim()).map { Pair(it, folderNewName) } }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { resultAndFolderNewName ->
                    updateFolderListOrShowError(resultAndFolderNewName.first)
                    if (resultAndFolderNewName.first.success && folder.getStringUuid() == selectedFolder.value) {
                        onCurrentFolderRenamed(resultAndFolderNewName.second)
                    }
                })
    }

    /**@SelfDocumented*/
    protected open fun onCurrentFolderRenamed(newFolderName: String) = Unit

    /**@SelfDocumented*/
    protected open fun prepareFoldersForFolderPickDialog(folders: List<FOLDER>): List<FolderViewModel> {
        return folders.map { folder -> mapFolderItemToViewModel(folder, "") }.map { folder ->
            FolderViewModel(
                folder.uuid,
                folder.level,
                folder.title,
                folder.statusIcon,
                0,
                false,
                0,
                showTotalCount = false,
                swipeEnabled = false
            )
        }
    }

    /**@SelfDocumented*/
    protected open fun updateFolderListOrShowError(result: Result) {
        if (result != Result.SUCCESS) {
            view?.showError(result.errorText)
        } else {
            updateFolderList()
        }
    }

    /**@SelfDocumented*/
    protected open fun onItemClick(folder: FOLDER) {
        closeAllSwipedItems()
        selectedFolder.onNext(folder.getStringUuid())
    }

    /**
     * Хак, необходимый при инициализации невидимой View для выравнивания столбца со счетчиками непрочитанных задач
     * @see R.layout#folder_list_item_layout
     */
    protected fun calcLongestTotalCountForFolders(folders: List<FOLDER>): String {
        return folders.map { formatCount(it.getTotalCount()) }.maxByOrNull { it.length } ?: ""
    }

    private fun closeAllSwipedItems() {
        swipeableVmHelper.closeAll(folders)
    }

    private fun closeItemForDeletion() {
        deletingItem?.also { swipeableVmHelper.closeAll(listOf(it), considerClosedUntilReset = true, forceClose = true) }
        deletingItem = null
    }
}