package ru.tensor.sbis.design.folders.view.compact

import ru.tensor.sbis.design.folders.data.FolderActionHandler
import ru.tensor.sbis.design.folders.data.model.Folder
import ru.tensor.sbis.design.folders.data.model.FolderActionType
import ru.tensor.sbis.design.folders.data.model.FolderButton
import ru.tensor.sbis.design.folders.data.model.FolderItem
import ru.tensor.sbis.design.folders.view.compact.adapter.FoldersCompactAdapter

/**
 * Реализация вьюконтроллера свёрнутых папок
 *
 * @author ma.kolpakov
 */
internal class FolderCompactViewControllerImpl : FolderCompactViewController {

    private var folderVisibilitySet = false
    private var isFolderIconVisible = false
    private var folders: List<Folder> = emptyList()
    private val generalIconVisibility
        get() = isFolderIconVisible && isShownLeftFolderIcon

    private var unfoldListener: (() -> Unit)? = null
    private var folderActionHandler: FolderActionHandler? = null

    private lateinit var adapter: FoldersCompactAdapter

    override var isShownLeftFolderIcon: Boolean = true
        set(value) {
            field = value
            showFolderIcon(isFolderIconVisible)
        }

    override fun setAdapter(adapter: FoldersCompactAdapter) {
        this.adapter = adapter

        adapter.onFolderIconClick {
            unfoldListener?.invoke()
        }

        adapter.onFolderClick {
            folderActionHandler?.handleAction(FolderActionType.CLICK, it)
        }
    }

    override fun showFolderIcon(isVisible: Boolean) {
        isFolderIconVisible = isVisible
        folderVisibilitySet = true
        adapter.setFolderIconVisible(generalIconVisibility)
        updateList()
    }

    override fun setFolders(folders: List<Folder>) {
        this.folders = folders
        updateList()
    }

    override fun onUnfold(action: () -> Unit) {
        unfoldListener = action
    }

    override fun setActionHandler(handler: FolderActionHandler?) {
        folderActionHandler = handler
    }

    private fun updateList() {
        if (!folderVisibilitySet) {
            return
        }

        val listToDisplay =
            if (generalIconVisibility && folders.isNotEmpty()) {
                mutableListOf<FolderItem>().apply {
                    add(FolderButton)
                    addAll(folders)
                }
            } else {
                folders
            }.takeIf { it.isNotEmpty() }
        adapter.submitList(listToDisplay)
    }
}
