package ru.tensor.sbis.communicator.themes_registry.ui.communication.filters

import ru.tensor.sbis.communicator.base_folders.ROOT_FOLDER_UUID
import ru.tensor.sbis.communicator.core.ui.filters.FilterConfiguration
import ru.tensor.sbis.communicator.declaration.model.ChatType
import ru.tensor.sbis.communicator.declaration.model.DialogType
import ru.tensor.sbis.design.folders.data.model.Folder
import java.io.Serializable

/**
 * Конфигурация фильтра диалогов/чатов
 *
 * @param dialogType тип диалога
 * @param chatType тип чата
 * @param selectedFolder выбранная папка
 */
internal data class ConversationFilterConfiguration(
    val dialogType: DialogType = DialogType.ALL,
    val chatType: ChatType = ChatType.ALL,
    override val selectedFolder: Folder?
) : FilterConfiguration<Folder>, Serializable {

    /** @SelfDocumented */
    override val folderUuid: String
        get() = selectedFolder?.id ?: ROOT_FOLDER_UUID.toString()

    /** @SelfDocumented */
    override fun copyWithUpdatedFolder(folder: Folder?): ConversationFilterConfiguration =
        copy(selectedFolder = folder?.takeIf { it.id != ROOT_FOLDER_UUID.toString() })
}