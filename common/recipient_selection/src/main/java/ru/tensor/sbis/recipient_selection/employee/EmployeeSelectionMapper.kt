package ru.tensor.sbis.recipient_selection.employee

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import ru.tensor.sbis.common.util.UUIDUtils
import ru.tensor.sbis.communication_decl.model.FolderType
import ru.tensor.sbis.design.text_span.span.BreadCrumbsSpan
import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.mvp.multiselection.data.MultiSelectionItem
import ru.tensor.sbis.persons.ContactVM
import ru.tensor.sbis.persons.GroupContactVM
import ru.tensor.sbis.profile_service.models.employee.EmployeeFolderItem
import ru.tensor.sbis.profile_service.models.employee.EmployeeItem
import ru.tensor.sbis.profile_service.models.employee.EmployeePath
import ru.tensor.sbis.profile_service.models.employee.EmployeeSearchResult
import ru.tensor.sbis.recipient_selection.R
import ru.tensor.sbis.recipient_selection.employee.data.EmployeeSelectionPagedListResult
import ru.tensor.sbis.recipient_selection.employee.data.ParentFolderData
import ru.tensor.sbis.recipient_selection.employee.ui.data.item.EmployeeSelectionItem
import ru.tensor.sbis.recipient_selection.employee.ui.data.item.EmployeesFolderSelectionItem
import ru.tensor.sbis.recipient_selection.employee.ui.data.item.EmployeesSelectionPathItem

fun mapEmployeesSearchResult(
        employeeSearchResult: EmployeeSearchResult,
        context: Context,
        needOpenProfileOnPhotoClick: Boolean,
        canSelectFolder: Boolean
): PagedListResult<MultiSelectionItem> {
    val items = employeeSearchResult.result.map {
        when {
            it.employee != null -> mapEmployeeItem(it.employee!!, needOpenProfileOnPhotoClick)
            it.folder != null -> mapEmployeeFolderItem(it.folder!!, canSelectFolder)
            else -> mapEmployeePathItem(it.folderPath!!, context)
        }
    }

    val parentFolder = if (employeeSearchResult.parent != null)
        ParentFolderData(employeeSearchResult.parent!!.uuid, employeeSearchResult.parent!!.name)
    else
        ParentFolderData(UUIDUtils.NIL_UUID, context.resources.getString(R.string.recipient_selection_employees_title))

    return EmployeeSelectionPagedListResult(
            items,
            employeeSearchResult.status,
            employeeSearchResult.hasMore,
            employeeSearchResult.folderSyncCompleted,
            parentFolder
    )
}

private fun mapEmployeeItem(employeeItem: EmployeeItem, needOpenProfileOnPhotoClick: Boolean) = EmployeeSelectionItem(
        ContactVM().apply {
            uuid = employeeItem.profileUuid
            rawPhoto = employeeItem.photoUrl
            name = employeeItem.fullName
            data1 = employeeItem.position
            initialsStubData = employeeItem.initialsStubData
        },
        employeeItem.faceId,
        employeeItem.photoId,
        needOpenProfileOnPhotoClick
)

private fun mapEmployeeFolderItem(folder: EmployeeFolderItem, canSelectFolder: Boolean) = EmployeesFolderSelectionItem(
        mapEmployeeFolderItemToGroupContactVM(folder),
        folder.faceId,
        canSelectFolder
)

private fun mapEmployeeFolderItemToGroupContactVM(folder: EmployeeFolderItem) = GroupContactVM(
        0,
        folder.uuid,
        folder.name,
        folder.employeesCount,
        folder.chiefName,
        FolderType.DEPARTMENT,
        null,
        null
)

private fun mapEmployeePathItem(pathItem: EmployeePath, context: Context): EmployeesSelectionPathItem {
    val folders = pathItem.folders
    val names = pathItem.folders.map { folder -> folder.name }

    /*
     * Передаем пустую строку (не важно какую, т.к. в конструктор кастомного Span-объекта мы
     * передадим коллекцию названий папок, чтобы не делать лишних операций:
     * 1) генерацию единой строки с какими-либо разделителями, например, "/", т.к. спан можно применить только к единой строке)
     * 2) обратный парсинг на сегменты этой строки, чтобы применить логику компоновки и эллипсайза сегментов
     */
    val breadCrumbsSpannable = SpannableString(" ")
    breadCrumbsSpannable.setSpan(BreadCrumbsSpan(context, names, arrayListOf()), 0, breadCrumbsSpannable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

    val lastFolder = mapEmployeeFolderItemToGroupContactVM(folders.last())

    return EmployeesSelectionPathItem(breadCrumbsSpannable, lastFolder)
}
