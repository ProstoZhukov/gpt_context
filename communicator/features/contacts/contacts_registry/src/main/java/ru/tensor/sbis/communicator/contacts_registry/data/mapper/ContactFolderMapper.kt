package ru.tensor.sbis.communicator.contacts_registry.data.mapper

import io.reactivex.functions.Function
import ru.tensor.sbis.common.util.UUIDUtils
import ru.tensor.sbis.communicator.generated.ContactFolder
import ru.tensor.sbis.design.folders.data.model.Folder
import ru.tensor.sbis.design.folders.data.model.FolderType
import ru.tensor.sbis.design.folders.data.model.ROOT_FOLDER_ID
import javax.inject.Inject


/**
 * Маппер из модели папок контактов в модель панели папок
 *
 * @author ao.zanin
 */
internal class ContactFolderMapper @Inject constructor() : Function<ContactFolder, Folder> {

    override fun apply(contactFolders: ContactFolder) = with(contactFolders) {
        Folder(
            id = if (uuid == UUIDUtils.NIL_UUID) ROOT_FOLDER_ID else uuid.toString(),
            title = name,
            type = if (changesIsForbidden) FolderType.DEFAULT else FolderType.EDITABLE,
            depthLevel = folderLevel,
            totalContentCount = contactsCount,
            unreadContentCount = 0,
            canMove = !changesIsForbidden
        )
    }
}
