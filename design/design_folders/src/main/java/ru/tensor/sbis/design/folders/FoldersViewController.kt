package ru.tensor.sbis.design.folders

import ru.tensor.sbis.design.folders.data.FolderActionHandler
import ru.tensor.sbis.design.folders.data.FoldingStateHandler
import ru.tensor.sbis.design.folders.data.MoreClickHandler
import ru.tensor.sbis.design.folders.data.model.AdditionalCommand
import ru.tensor.sbis.design.folders.data.model.Folder

/**
 * Вьюконтроллер компонента папок
 *
 * @author ma.kolpakov
 */
internal interface FoldersViewController {

    /**
     * Можно ли развернуть панель папок, если в есть папки только первого уровня и нет дополнительной команды.
     * По умоланию разворачивание доступно независимо от папок и дополнительной команды
     */
    var isExpandable: Boolean

    /**
     * Указывает id папки, которая будет выделена при отображении маркером
     */
    fun setSelectedFolder(id: String?)

    /**
     * Установка дополнительной команды. Команда всегда отображается сверху и может быть только одна.
     * Если задаётся новая команда, она заменяет старую
     *
     * @param command команда для отображения. Для удаления команды передать null
     */
    fun setAdditionalCommand(command: AdditionalCommand?)

    /**
     * Установка папок
     *
     * @param folders список папок для отображения
     */
    fun setFolders(folders: List<Folder>)

    /**
     * Установка слушателя действия папки
     *
     * @param handler реализация слушателя действия папки
     */
    fun setActionHandler(handler: FolderActionHandler)

    /**
     * Установка слушателя на разворачивание\сворачивание компонента
     */
    fun onFoldStateChanged(handler: FoldingStateHandler)

    /**
     * Установка слушателя на нажатие кнопки "Ещё"
     */
    fun onMoreClicked(handler: MoreClickHandler)
}
