package ru.tensor.sbis.design.folders.support.listeners

import ru.tensor.sbis.design.folders.data.model.Folder

/**
 * Дефолтная реализация [FolderActionListener]
 * Позволяет использовать только необходимые методы
 *
 * @author ma.kolpakov
 */
open class DefaultFolderActionListener : FolderActionListener {
    override fun opened(folder: Folder) = Unit
    override fun closed() = Unit
    override fun selected(folder: Folder) = Unit
    override fun additionalCommandClicked() = Unit
    override fun additionalCommandTitleClicked() = Unit
    override fun additionalCommandIconClicked() = Unit
}
