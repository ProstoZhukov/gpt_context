package ru.tensor.sbis.design.folders

import android.view.View
import androidx.core.view.isVisible
import ru.tensor.sbis.design.breadcrumbs.CurrentFolderView
import ru.tensor.sbis.design.folders.data.FolderActionHandler
import ru.tensor.sbis.design.folders.data.FoldingStateHandler
import ru.tensor.sbis.design.folders.data.MoreClickHandler
import ru.tensor.sbis.design.folders.data.model.AdditionalCommand
import ru.tensor.sbis.design.folders.data.model.AdditionalCommandType
import ru.tensor.sbis.design.folders.data.model.Folder
import ru.tensor.sbis.design.folders.view.compact.FolderCompactViewControllerImpl
import ru.tensor.sbis.design.folders.view.compact.FoldersCompactView
import ru.tensor.sbis.design.folders.view.full.FolderListView

/**
 * Реализация вьюконтроллера компонента папок
 *
 * @author ma.kolpakov
 */
internal class FoldersViewControllerImpl : FoldersViewController {

    private var foldingStateHandler: FoldingStateHandler? = null

    private var hasAdditionalCommand = false

    /**
     * Принято правило: отсутствие папок <=> все папки верхнего уровня. Удобно, чтобы не показывать иконку
     * разворачивания при установке пустого списка.
     * Иконка раскрытия папок по умолчанию скрыта [FolderCompactViewControllerImpl.isFolderIconVisible]
     *
     * @see updateState
     */
    private var onlyTopLevelFolders = true
    private var isExpansionAvailable = false

    private lateinit var foldersRoot: View
    private lateinit var foldersCompact: FoldersCompactView
    private lateinit var foldersFull: FolderListView
    private lateinit var currentFolder: CurrentFolderView

    override var isExpandable: Boolean = true
        set(value) {
            field = value
            updateState()
        }

    internal var onlyRootFolder: Boolean = false

    /** @SelfDocumented */
    internal fun setViews(
        foldersRoot: View,
        foldersCompact: FoldersCompactView,
        foldersFull: FolderListView,
        currentFolder: CurrentFolderView,
    ) {
        this.foldersRoot = foldersRoot
        this.foldersCompact = foldersCompact
        this.foldersFull = foldersFull
        this.currentFolder = currentFolder

        foldersFull.onFold(::showCompactFolders)
        foldersCompact.onUnfold(::showFullFolders)
        // установим значение по умолчанию для контроллера вложенной view
        foldersCompact.showFolderIcon(isExpansionAvailable)
    }

    override fun setSelectedFolder(id: String?) = foldersFull.setSelectedFolder(id)

    override fun setAdditionalCommand(command: AdditionalCommand?) {
        hasAdditionalCommand = command != null && command.type != AdditionalCommandType.EMPTY
        foldersFull.setAdditionalCommand(command)

        updateState()
    }

    override fun setFolders(folders: List<Folder>) {
        val firstLevelFolders = folders.filter { it.isTopLevel }
        onlyRootFolder = folders.isEmpty()
        onlyTopLevelFolders = onlyRootFolder || firstLevelFolders.size == folders.size

        foldersCompact.setFolders(firstLevelFolders)
        foldersFull.setFolders(folders, isFolderIconVisible = true)

        updateState()
    }

    override fun setActionHandler(handler: FolderActionHandler) {
        foldersFull.setActionHandler(handler)
        foldersCompact.setActionHandler(handler)
    }

    override fun onFoldStateChanged(handler: FoldingStateHandler) {
        this.foldingStateHandler = handler
    }

    override fun onMoreClicked(handler: MoreClickHandler) = foldersFull.onMoreClicked(handler)

    /**
     * Отображение компактного состояния
     */
    internal fun showCompactFolders() {
        foldersFull.isVisible = false
        foldersFull.closeSwipeMenu()
        foldersCompact.visibility = foldersRoot.visibility
        currentFolder.isVisible = false
        foldingStateHandler?.onChanged(true)
    }

    /**
     * Отображение развёрнутого состояния
     */
    internal fun showFullFolders() {
        foldersFull.isVisible = foldersRoot.isVisible
        foldersCompact.visibility = if (foldersRoot.isVisible) View.INVISIBLE else View.GONE
        currentFolder.isVisible = false
        foldingStateHandler?.onChanged(false)
    }

    /**
     * Отображение заголовка папки
     */
    internal fun showCurrentFolder(folderName: String) {
        foldersFull.isVisible = false
        foldersCompact.isVisible = false
        currentFolder.isVisible = foldersRoot.isVisible

        currentFolder.setTitle(folderName)
    }

    /**
     * Установка слушателя клика по заголовку папки
     */
    internal fun onCurrentFolderClicked(handler: () -> Unit) {
        currentFolder.setOnClickListener { handler() }
    }

    /**
     * Удаление подписок на события и обработчиков нажатий в панели папок
     */
    internal fun clearListeners() {
        foldersFull.setActionHandler(null)
        foldersCompact.setActionHandler(null)
        foldingStateHandler = null
        currentFolder.setOnClickListener(null)
        foldersFull.onMoreClicked(null)
    }

    private fun updateState() {
        val wasExpansionAvailable = isExpansionAvailable
        isExpansionAvailable = isExpandable || !onlyTopLevelFolders || hasAdditionalCommand
        if (isExpansionAvailable != wasExpansionAvailable) {
            foldersCompact.showFolderIcon(isExpansionAvailable)
            // раскрытие стало недоступным
            if (!isExpansionAvailable) {
                showCompactFolders()
            }
        }
    }
}