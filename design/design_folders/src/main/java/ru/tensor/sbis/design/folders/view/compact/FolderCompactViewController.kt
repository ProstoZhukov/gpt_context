package ru.tensor.sbis.design.folders.view.compact

import ru.tensor.sbis.design.folders.data.FolderActionHandler
import ru.tensor.sbis.design.folders.data.model.Folder
import ru.tensor.sbis.design.folders.view.compact.adapter.FoldersCompactAdapter

/**
 * Вьюконтроллер свёрнутых папок
 *
 * @author ma.kolpakov
 */
internal interface FolderCompactViewController {
    /**
     * Флаг принудительного скрытия иконки показа полной панели папок.
     */
    var isShownLeftFolderIcon: Boolean

    /**
     * Установка адаптера
     */
    fun setAdapter(adapter: FoldersCompactAdapter)

    /**
     * Установка видимости иконки папки
     *
     * @param isVisible видима ли иконка папки
     */
    fun showFolderIcon(isVisible: Boolean)

    /**
     * Установка данных папок
     */
    fun setFolders(folders: List<Folder>)

    /**
     * Слушатель разворачивания (клик по иконке папки)
     */
    fun onUnfold(action: () -> Unit)

    /**
     * Установка слушателя действия папки
     *
     * @param handler реализация слушателя действия папки
     */
    fun setActionHandler(handler: FolderActionHandler?)
}
