package ru.tensor.sbis.communicator.core.ui.filters

/**
 * Интерфейс конфигурации crud фильтра
 *
 * @author rv.krohalev
 */
interface FilterConfiguration<FOLDER> {

    val selectedFolder: FOLDER?

    val folderUuid: String

    fun copyWithUpdatedFolder(folder: FOLDER?): FilterConfiguration<FOLDER>
}