package ru.tensor.sbis.recipient_selection.profile.mapper

import android.content.Context
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.common.modelmapper.BaseModelMapper
import ru.tensor.sbis.persons.GroupContactVM
import ru.tensor.sbis.communicator.generated.FolderType
import ru.tensor.sbis.communicator.generated.RecipientFolder
import ru.tensor.sbis.recipient_selection.profile.data.group_profiles.GroupItem

/**
 * Маппер, преобразующий модель контроллера [RecipientFolder] в UI модель [GroupItem]
 */
internal class FolderAndGroupItemMapper(context: Context) : BaseModelMapper<RecipientFolder, GroupItem>(context) {

    /** @SelfDocumented */
    override fun apply(recipientFolder: RecipientFolder): GroupItem {
        return GroupItem(
            GroupContactVM(
                recipientFolder.id,
                recipientFolder.uuid!!,
                recipientFolder.name,
                recipientFolder.count,
                if (recipientFolder.chief == null) StringUtils.EMPTY else recipientFolder.chief,
                mapFolderTypeToNativeModel(recipientFolder.type!!),
                null,
                null))
    }

    private fun mapFolderTypeToNativeModel(folderType: FolderType): ru.tensor.sbis.communication_decl.model.FolderType {
        return when (folderType) {
            FolderType.DEPARTMENT -> ru.tensor.sbis.communication_decl.model.FolderType.DEPARTMENT
            FolderType.BRANCH -> ru.tensor.sbis.communication_decl.model.FolderType.BRANCH
            FolderType.OFFICE -> ru.tensor.sbis.communication_decl.model.FolderType.OFFICE
            FolderType.WORK_GROUP -> ru.tensor.sbis.communication_decl.model.FolderType.WORK_GROUP
            FolderType.TASK_EXECUTOR -> ru.tensor.sbis.communication_decl.model.FolderType.TASK_EXECUTOR
        }
    }
}