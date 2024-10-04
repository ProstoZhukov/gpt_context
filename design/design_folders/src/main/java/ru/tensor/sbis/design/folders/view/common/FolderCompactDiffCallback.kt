package ru.tensor.sbis.design.folders.view.common

import androidx.recyclerview.widget.DiffUtil
import ru.tensor.sbis.design.folders.data.model.*

/**
 * Колбэк для сравнения [FolderItem]
 *
 * @author ma.kolpakov
 */
internal class FolderCompactDiffCallback : DiffUtil.ItemCallback<FolderItem>() {

    override fun areItemsTheSame(old: FolderItem, new: FolderItem): Boolean =
        when {
            old is Folder && old.isFirst -> false
            old is FolderButton && new is FolderButton -> true
            old is AdditionalCommand && new is AdditionalCommand -> true
            old is MoreButton && new is MoreButton -> true
            old is Folder && new is Folder -> old.id == new.id
            else -> false
        }

    override fun areContentsTheSame(old: FolderItem, new: FolderItem): Boolean =
        when {
            old is Folder && old.isFirst -> false
            old is FolderButton && new is FolderButton -> true
            old is MoreButton && new is MoreButton -> true
            old is AdditionalCommand && new is AdditionalCommand -> areCommandsContentTheSame(old, new)
            old is Folder && new is Folder -> areFoldersContentTheSame(old, new)
            else -> false
        }

    private fun areCommandsContentTheSame(old: AdditionalCommand, new: AdditionalCommand): Boolean =
        old.title == new.title && old.type == new.type

    private fun areFoldersContentTheSame(old: Folder, new: Folder): Boolean =
        old.title == new.title &&
            old.type == new.type &&
            old.depthLevel == new.depthLevel &&
            old.totalContentCount == new.totalContentCount &&
            old.unreadContentCount == new.unreadContentCount
}
