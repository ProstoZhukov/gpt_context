package ru.tensor.sbis.communicator.contacts_registry.ui.folders

import ru.tensor.sbis.communicator.base_folders.folderfilter.CommunicatorFoldersInteractor
import ru.tensor.sbis.design.folders.data.model.Folder
import ru.tensor.sbis.design.folders.support.FoldersProvider

/**
 * Интерактор для загрузки папок реестра контактов
 * @see ContactListFoldersInteractorImpl
 *
 * @author da.zhukov
 */
internal interface ContactListFoldersInteractor :
    CommunicatorFoldersInteractor<Folder>,
    FoldersProvider