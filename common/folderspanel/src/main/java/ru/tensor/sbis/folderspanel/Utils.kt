package ru.tensor.sbis.folderspanel

import java.util.*

/**@SelfDocumented*/
object Utils {

    /**
     * "Вычитание" одного списка папок из другого.
     * Из первого списка удаляются те папки, которые есть во втором (сравнение папок производится по uuid)
     * @param allFolders список всех папок
     * @param subFolders список папок, которых нужно исключить из общего списка
     * @return результирующий список папок
     */
    @JvmStatic
    fun <T: Folder> substractFolders(allFolders: List<T>, subFolders: List<T>): List<T> {
        val subFoldersUuids = subFolders.map { it.getStringUuid()}
        return allFolders.filter { !subFoldersUuids.contains(it.getStringUuid())}
    }

    /**
     * Поиск и удаление родительской папки из списка по uuid
     * @param allFolders список всех папок
     * @param uuid uuid папки, для которой нужно удалить родительскую
     * @return результирующий список папок
     */
    @JvmStatic
    fun <T: Folder> removeParentFolder(allFolders: List<T>, uuid: String): List<T> {
        val parentUuid = allFolders.first { it.getStringUuid() == uuid }.getParentStringUuid()
        return allFolders.filter { it.getStringUuid() != parentUuid }
    }

    /**@SelfDocumented*/
    @JvmStatic
    fun toFolderViewModelList(folders: List<Folder>): List<FolderViewModel> {
        val size = folders.size
        val dialogFolderModels = ArrayList<FolderViewModel>(size)
        for (i in 0 until size) dialogFolderModels.add(folders[i].toFolderViewModel(""))
        return dialogFolderModels
    }
}