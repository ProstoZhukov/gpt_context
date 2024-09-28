package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.dialogs.folders

import ru.tensor.sbis.communicator.base_folders.folderfilter.CommunicatorFoldersInteractor
import ru.tensor.sbis.communicator.generated.DialogFilter
import ru.tensor.sbis.design.folders.data.model.Folder
import ru.tensor.sbis.design.folders.support.FoldersProvider

/**
 * Интерактор для загрузки папок в диалогах
 *
 * @author vv.chekurda
 */
internal interface ThemeFoldersInteractor:
    CommunicatorFoldersInteractor<Folder>,
    FoldersProvider {

    /**
     * Установить фильтр реестре диалогов
     */
    fun setDialogFilter(dialogFilter: DialogFilter)

    /**
     * Попытаться установить папки из кэша, если они уже загружены.
     */
    fun trySetFoldersSync()
}