package ru.tensor.sbis.communicator.base_folders.list_section

import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import ru.tensor.sbis.common.util.UUIDUtils
import ru.tensor.sbis.communicator.base_folders.ROOT_FOLDER_UUID
import ru.tensor.sbis.communicator.base_folders.list_section.CommunicatorFoldersAction.*
import ru.tensor.sbis.design.folders.FoldersView
import ru.tensor.sbis.design.folders.data.model.Folder
import ru.tensor.sbis.design.folders.data.model.ROOT_FOLDER_ID
import ru.tensor.sbis.design.folders.support.FoldersViewModel
import ru.tensor.sbis.design.folders.support.extensions.attach
import ru.tensor.sbis.design.folders.support.extensions.detach
import ru.tensor.sbis.design.folders.support.listeners.FolderActionListener
import ru.tensor.sbis.design.folders.support.listeners.FoldersDataUpdateListener
import java.util.UUID

/**
 * Вспомагательный класс для работы с папками в крудовом списке.
 *
 * @author da.zhukov
 */
class FoldersViewHolderHelper(
    viewModelFactory: BaseListFoldersViewModelFactory,
    private val viewModelStoreOwner: ViewModelStoreOwner,
    private val scope: LifecycleCoroutineScope
) : FoldersDataUpdateListener {

    private val foldersViewModel by lazy {
        ViewModelProvider(viewModelStoreOwner, viewModelFactory)[FoldersViewModel::class.java]
    }

    private var foldersView: FoldersView? = null

    /** @SelfDocumented */
    val foldersActionFlow: MutableSharedFlow<CommunicatorFoldersAction> = MutableSharedFlow()

    private var foldersIsEmpty = true

    private var currentFolderIsVisible: Boolean = false

    /**
     * Слушатель событий связанных с папками.
     */
    val folderActionListener = object : FolderActionListener {
        override fun opened(folder: Folder) {
            emitFoldersActions(OpenedFolder(folder))
        }
        override fun closed() {
            emitFoldersActions(ClosedFolder(null))
        }
        override fun selected(folder: Folder) {
            emitFoldersActions(SelectedFolder(folder))
        }
    }

    /** @SelfDocumented */
    fun attachFoldersView(view: FoldersView) {
        foldersView = view
        view.let { foldersView ->
            foldersViewModel.attach(
                host = viewModelStoreOwner as Fragment,
                foldersView = foldersView,
                actionsListener = folderActionListener,
                dataUpdateListener = this@FoldersViewHolderHelper
            )
        }
    }

    private fun emitFoldersActions(folder: CommunicatorFoldersAction) {
        scope.launch {
            foldersActionFlow.emit(folder)
        }
    }

    /** @SelfDocumented */
    fun detachFoldersView(view: FoldersView) {
        foldersViewModel.detach(viewModelStoreOwner as Fragment, view)
    }

    /** @SelfDocumented */
    fun hideFoldersView(currentFolderIsVisible: Boolean? = null) {
        currentFolderIsVisible?.let {
            this.currentFolderIsVisible = it
        }
        if (!this.currentFolderIsVisible) return
        foldersView?.let {
            it.isVisible = false
            it.children.forEach { child -> child.isVisible = false }
        }
    }

    /** @SelfDocumented */
    fun showFoldersView(currentFolderIsVisible: Boolean? = null) {
        currentFolderIsVisible?.let {
            this.currentFolderIsVisible = it
        }
        if (this.currentFolderIsVisible) return
        foldersView?.let {
            it.isVisible = true
            it.children.forEach { child -> child.isVisible = true }
            it.showCompactFolders()
        }
    }

    fun showFolderSelection(currentFolder: UUID?) {
        val folderId = when (currentFolder) {
            ROOT_FOLDER_UUID -> ROOT_FOLDER_ID
            else -> UUIDUtils.toString(currentFolder)
        }
        foldersViewModel.onFolderSelectionClicked(folderId)
    }

    override fun updated(isEmpty: Boolean) {
        if (foldersIsEmpty == isEmpty) return

        foldersIsEmpty = isEmpty
        if (isEmpty) {
            hideFoldersView()
        } else {
            showFoldersView()
        }
    }
}

/** @SelfDocumented */
sealed interface CommunicatorFoldersAction {

    /** @SelfDocumented */
    val folder: Folder?

    /** @SelfDocumented */
    data class OpenedFolder(
        override val folder: Folder
    ) : CommunicatorFoldersAction

    /** @SelfDocumented */
    data class ClosedFolder(
        override val folder: Folder?
    ) : CommunicatorFoldersAction

    /** @SelfDocumented */
    data class SelectedFolder(
        override val folder: Folder
    ) : CommunicatorFoldersAction
}