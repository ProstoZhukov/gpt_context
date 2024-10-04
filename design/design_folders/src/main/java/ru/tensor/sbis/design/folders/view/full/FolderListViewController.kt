package ru.tensor.sbis.design.folders.view.full

import android.view.View
import androidx.core.view.isVisible
import ru.tensor.sbis.design.folders.data.FolderActionHandler
import ru.tensor.sbis.design.folders.data.MoreClickHandler
import ru.tensor.sbis.design.folders.data.model.AdditionalCommand
import ru.tensor.sbis.design.folders.data.model.AdditionalCommandType
import ru.tensor.sbis.design.folders.data.model.Folder
import ru.tensor.sbis.design.folders.data.model.FolderItem
import ru.tensor.sbis.design.folders.data.model.MoreButton
import ru.tensor.sbis.design.folders.view.full.adapter.FoldersFullAdapter

/**
 * Вьюконтроллер развёрнутых папок
 *
 * @param adapter адаптер списка папок
 * @param folderIcon view иконки папки
 *
 * @author ma.kolpakov
 */
internal class FolderListViewController(
    private val adapter: FoldersFullAdapter,
    private val folderIcon: View
) {

    private companion object {
        const val MAX_FOLDERS_COUNT = 20
    }

    private var items: List<FolderItem> = emptyList()
    private val generalIconVisibility
        get() = isFolderIconVisible && isShownLeftFolderIcon

    private var isFolderIconVisible = true

    /**
     * Флаг принудительного скрытия иконки показа компактной панели папок.
     */
    var isShownLeftFolderIcon = true
        set(value) {
            if (field == value) return
            field = value
            folderIcon.isVisible = generalIconVisibility
        }

    /**
     * Установка дополнительной команды. Команда всегда отображается сверху и может быть только одна.
     * Если задаётся новая команда, она заменяет старую.
     *
     * @param command команда для отображения. Для удаления команды передать null или [AdditionalCommand.EMPTY]
     */
    fun setAdditionalCommand(command: AdditionalCommand?) {
        val newFolders = mutableListOf<FolderItem>()

        if (command.isNotNullAndNotEmpty()) {
            newFolders.add(command!!)
        }
        items = items.filterIsInstanceTo<Folder, MutableList<FolderItem>>(newFolders)

        adapter.submitList(items.trimmed())
    }

    /**
     * Установка данных папок
     *
     * @param folders список папок
     * @param isFolderIconVisible отображать ли иконку папки
     */
    fun setFolders(folders: List<Folder>, isFolderIconVisible: Boolean) {
        this.isFolderIconVisible = isFolderIconVisible
        val additionalCommands: List<FolderItem> =
            if (items.isNotEmpty() && items.first() is AdditionalCommand) {
                items.filterIsInstance<AdditionalCommand>()
            } else {
                emptyList()
            }

        folderIcon.isVisible = generalIconVisibility

        items = additionalCommands + folders
        adapter.submitList(items.trimmed())
    }

    /**
     * Установка слушателя действия папки
     *
     * @param handler реализация слушателя действия папки
     */
    fun setActionHandler(handler: FolderActionHandler?) = adapter.setActionHandler(handler)

    /**
     * Установка слушателя на нажатие кнопки "Ещё"
     */
    fun onMoreClicked(handler: MoreClickHandler?) = adapter.onMoreClicked(handler?.run { ::onMoreClicked })

    /**
     * Слушатель сворачивания
     */
    fun onFold(action: () -> Unit) {
        folderIcon.setOnClickListener {
            val hasOnlySingleCommand = items.size == 1 && items.first() is AdditionalCommand

            if (!hasOnlySingleCommand) {
                action()
            }
        }
    }

    /** @SelfDocumented */
    fun closeSwipeMenu() = adapter.closeSwipeMenu()

    private fun List<FolderItem>.trimmed(): List<FolderItem> =
        when {
            isFolderIconVisible && size <= MAX_FOLDERS_COUNT -> this
            isFolderIconVisible && size > MAX_FOLDERS_COUNT ->
                this.take(MAX_FOLDERS_COUNT) + MoreButton
            else -> filterIsInstance<Folder>()
        }

    private fun AdditionalCommand?.isNotNullAndNotEmpty(): Boolean =
        this != null && this.type != AdditionalCommandType.EMPTY
}
