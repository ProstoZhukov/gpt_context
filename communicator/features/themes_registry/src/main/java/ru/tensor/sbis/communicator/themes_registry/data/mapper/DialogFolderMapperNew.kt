package ru.tensor.sbis.communicator.themes_registry.data.mapper

import io.reactivex.functions.Function
import ru.tensor.sbis.common.util.ResourceProvider
import ru.tensor.sbis.communicator.generated.DialogFolder
import ru.tensor.sbis.design.folders.data.model.Folder
import ru.tensor.sbis.design.folders.data.model.FolderType
import ru.tensor.sbis.design.folders.data.model.ROOT_FOLDER_ID
import javax.inject.Inject
import ru.tensor.sbis.communicator.design.R as RCommunicatorDesign

/**
 * Маппер из модели папок контактов в модель панели папок
 *
 * @author rv.krohalev
 */
internal class DialogFolderMapperNew @Inject constructor(
    private val resourceProvider: ResourceProvider
) : Function<DialogFolder, Folder> {

    override fun apply(dialogFolder: DialogFolder) = with(dialogFolder) {
        Folder(
            id = uuid?.toString() ?: ROOT_FOLDER_ID,
            title = uuid?.let { title } ?: resourceProvider.getString(RCommunicatorDesign.string.communicator_folder_conversation_title),
            type = if (canChange) FolderType.EDITABLE else FolderType.DELETABLE,
            depthLevel = folderLevel,
            totalContentCount = dialogCount,
            unreadContentCount = unreadInFolderCount
        )
    }
}
