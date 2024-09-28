package ru.tensor.sbis.recipient_selection.employeenew.data

import ru.tensor.sbis.communication_decl.model.FolderType
import ru.tensor.sbis.design.profile_decl.person.PersonData
import ru.tensor.sbis.design.selection.ui.contract.list.ListMapper
import ru.tensor.sbis.mvp.multiselection.data.MultiSelectionItem
import ru.tensor.sbis.persons.ContactVM
import ru.tensor.sbis.persons.GroupContactVM
import ru.tensor.sbis.persons.PersonName
import ru.tensor.sbis.profile_service.models.employee.EmployeeFolderItem
import ru.tensor.sbis.profile_service.models.employee.EmployeeItem
import ru.tensor.sbis.profile_service.models.employee.EmployeeSearchResult
import ru.tensor.sbis.recipient_selection.employee.ui.data.item.EmployeeSelectionItem
import ru.tensor.sbis.recipient_selection.employee.ui.data.item.EmployeesFolderSelectionItem

/**
 * Маппер данных для экрана выбора сотрудников
 */
internal class EmployeeResultMapper: ListMapper<EmployeeSearchResult, BaseEmployeeSelectorItemModel> {

    override fun invoke(result: EmployeeSearchResult): List<BaseEmployeeSelectorItemModel> {
        val dataList = mutableListOf<BaseEmployeeSelectorItemModel>()
        for (item in result.result) {
            val employeeOrFolder = item.employee?.let { employee ->
                EmployeeSelectorItemModel(
                    PersonData(employee.profileUuid, employee.photoUrl, employee.initialsStubData),
                    mapEmployeeName(employee.name),
                    employee.name,
                    employee.position,
                    employee.profileUuid.toString(),
                    oldVm = mapOldVm(employee)
                )
            } ?: item.folder?.run(::toSelectorItem)

            if (employeeOrFolder != null) {
                dataList.add(employeeOrFolder)
                continue
            } else {
                val folders = item.folderPath?.let { (_, folders) -> folders.take(1).map(::toSelectorItem) } ?: emptyList()
                dataList.addAll(folders)
            }
        }
        return dataList
    }

    private fun mapOldVm(folder: EmployeeFolderItem): MultiSelectionItem {
        return EmployeesFolderSelectionItem(
            GroupContactVM(
                0,
                folder.uuid,
                folder.name,
                folder.employeesCount,
                folder.chiefName,
                FolderType.DEPARTMENT,
                null,
                null
            ),
            folder.faceId,
            true
        )
    }

    private fun mapOldVm(employee: EmployeeItem): MultiSelectionItem {
        return EmployeeSelectionItem(
            ContactVM().apply {
                uuid = employee.profileUuid
                rawPhoto = employee.photoUrl
                name = mapEmployeeName(employee.name)
                data1 = employee.position
                initialsStubData = employee.initialsStubData
            },
            employee.faceId,
            employee.photoId,
            false,
            employee.uuid
        )
    }

    private fun toSelectorItem(folder: EmployeeFolderItem) = EmployeeFolderSelectorItemModel(
        folder.name,
        "%s (%s)".format(folder.chiefName, folder.employeesCount).trim(),
        folder.uuid.toString(),
        folder.hasSubfolders || folder.employeesCount > 0,
        folder.employeesCount,
        mapOldVm(folder)
    )

}

internal fun mapEmployeeName(fullName: String): PersonName {
    val splitIndex = fullName.indexOf(' ')
    return if (splitIndex == -1) PersonName("", fullName, "") else
        PersonName(fullName.substring(splitIndex + 1, fullName.length), fullName.substring(0, splitIndex), "")
}