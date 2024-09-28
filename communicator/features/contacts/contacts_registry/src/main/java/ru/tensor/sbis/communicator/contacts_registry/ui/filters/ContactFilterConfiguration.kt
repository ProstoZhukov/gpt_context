package ru.tensor.sbis.communicator.contacts_registry.ui.filters

import ru.tensor.sbis.communicator.contacts_registry.ui.spinner.ContactSortOrder
import ru.tensor.sbis.communicator.base_folders.ROOT_FOLDER_UUID
import ru.tensor.sbis.communicator.core.ui.filters.FilterConfiguration
import ru.tensor.sbis.design.folders.data.model.Folder
import java.io.Serializable

/**
 * Конфигурация фильтра контактов
 *
 * @param contactSortOrder тип сортировки реестра контактов
 * @param selectedFolder выбранная папка
 *
 * @author da.zhukov
 */
internal data class ContactFilterConfiguration(
    val contactSortOrder: ContactSortOrder,
    override val selectedFolder: Folder?
) : FilterConfiguration<Folder>, Serializable {

    /** @SelfDocumented */
    override val folderUuid: String
        get() = selectedFolder?.id ?: ROOT_FOLDER_UUID.toString()

    /** @SelfDocumented */
    override fun copyWithUpdatedFolder(folder: Folder?): ContactFilterConfiguration =
        copy(selectedFolder = folder?.takeIf { it.id != ROOT_FOLDER_UUID.toString() })
}