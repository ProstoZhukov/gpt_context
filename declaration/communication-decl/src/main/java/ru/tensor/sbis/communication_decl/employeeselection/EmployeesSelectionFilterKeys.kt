package ru.tensor.sbis.communication_decl.employeeselection

import ru.tensor.sbis.communication_decl.recipient_selection.FilterKey

/**
 * Enum ключей для Bundle фильтра выбора сотрудников
 */
enum class EmployeesSelectionFilterKeys(private var key: String): FilterKey {

    FOLDER_UUID("FOLDER_UUID"),
    IS_SINGLE_CHOICE("IS_SINGLE_CHOICE"),
    NEED_OPEN_PROFILE_ON_PHOTO_CLICK("NEED_OPEN_PROFILE_ON_PHOTO_CLICK"),
    ONLY_WITH_ACCESS_TO_SBIS("ONLY_WITH_ACCESS_TO_SBIS"),
    EXCLUDE_CURRENT_USER("EXCLUDE_CURRENT_USER"),
    CAN_SELECT_FOLDER("CAN_SELECT_FOLDER");

    override fun key() = key
}