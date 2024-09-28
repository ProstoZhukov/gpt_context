package ru.tensor.sbis.recipient_selection.employee.ui.data.result

import ru.tensor.sbis.communication_decl.employeeselection.result.EmployeeSelectionData
import ru.tensor.sbis.communication_decl.employeeselection.result.EmployeeSelectionItemType
import ru.tensor.sbis.communication_decl.employeeselection.result.EmployeesSelectionResultDataContract
import ru.tensor.sbis.communication_decl.employeeselection.result.RecipientSelectionForRepostItemType
import ru.tensor.sbis.mvp.multiselection.data.BaseSelectionResultData
import ru.tensor.sbis.mvp.multiselection.data.MultiSelectionItem
import ru.tensor.sbis.recipient_selection.employee.ui.data.item.EmployeeSelectionItem
import ru.tensor.sbis.recipient_selection.employee.ui.data.item.EmployeesFolderSelectionItem
import ru.tensor.sbis.recipient_selection.profile.data.group_profiles.ContactItem
import ru.tensor.sbis.recipient_selection.profile.data.group_profiles.GroupItem
import ru.tensor.sbis.recipient_selection.profile.data.repost.RecipientSelectionResultDataForRepost

/**
 * Результат выбора сотрудников
 */
class EmployeesSelectionResultData: BaseSelectionResultData, EmployeesSelectionResultDataContract {

    constructor(items: List<MultiSelectionItem>?) : super(items)

    constructor(resultCode: Int, items: List<MultiSelectionItem>?) : super(resultCode, items)

    constructor(resultCode: Int, requestCode: Int, items: List<MultiSelectionItem>?) : super(resultCode, requestCode, items)

    /**
     * @return Список моделей выбранных сотрудников и папок для репоста новости
     */
    override fun getEmployeesAndFoldersData(): List<EmployeeSelectionData> {
        return mItems?.map {
            if (ContactItem.CONTACT_TYPE == it.itemType) {
                EmployeeSelectionData(
                    it.uuid,
                    (it as ContactItem).contact.name.fullName,
                    (it as EmployeeSelectionItem).faceId,
                    it.photoId,
                    (it as ContactItem).contact.rawPhoto,
                    it.contact.data1,
                    EmployeeSelectionItemType.PERSON
                )
            } else {
                EmployeeSelectionData(
                    it.uuid,
                    (it as GroupItem).group.groupName,
                    (it as EmployeesFolderSelectionItem).faceId,
                    null,
                    null,
                    null,
                    EmployeeSelectionItemType.FOLDER
                )
            }
        }.orEmpty()
    }

    /**
     * @return Список моделей выбранных сотрудников и папок
     */
    override fun getEmployeesAndFoldersDataForRepost(): List<RecipientSelectionResultDataForRepost> {
        return mItems?.map {
            if (ContactItem.CONTACT_TYPE == it.itemType) {
                RecipientSelectionResultDataForRepost(
                    (it as EmployeeSelectionItem).uuid,
                    it.contact.name.fullName,
                    it.contact.data1,
                    it.contact.rawPhoto,
                    false,
                    it.itemCount,
                    RecipientSelectionForRepostItemType.PERSON
                )
            } else {
                RecipientSelectionResultDataForRepost(
                    it.uuid,
                    (it as GroupItem).group.groupName,
                    it.group.groupChiefName,
                    null,
                    true,
                    it.itemCount,
                    RecipientSelectionForRepostItemType.FOLDER
                )
            }
        }.orEmpty()
    }
}