package ru.tensor.sbis.design.folders.support.listeners

import ru.tensor.sbis.design.breadcrumbs.CurrentFolderView
import ru.tensor.sbis.design.folders.data.model.Folder
import ru.tensor.sbis.design.folders.support.presentation.FolderListViewMode

/**
 * Слушатель действий над папками
 *
 * @author ma.kolpakov
 */
interface FolderActionListener {

    /**
     * Открытия папки. Срабатывает при клике на папку
     * Отображение при этом меняется на [CurrentFolderView]
     *
     * @param folder модель открытой папки
     */
    fun opened(folder: Folder)

    /**
     * Закрытия папки. Срабатывает при закрытии папки, открытой в [opened] при клике [CurrentFolderView].
     * [CurrentFolderView] скрывается.
     */
    fun closed()

    /**
     * Выбор папки
     *
     * @param folder модель выбранной папки.
     * Срабатывает при выборе папки из списка, в режиме [FolderListViewMode.SELECTION]
     */
    fun selected(folder: Folder)

    /**
     * Клик по дополнительной команде
     */
    fun additionalCommandClicked() = Unit

    /** Клик по заголовку дополнительной команды */
    fun additionalCommandTitleClicked() = Unit

    /** Клик по иконке дополнительной команды */
    fun additionalCommandIconClicked() = Unit
}
