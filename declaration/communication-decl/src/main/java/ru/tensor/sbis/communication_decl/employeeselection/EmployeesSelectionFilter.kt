package ru.tensor.sbis.communication_decl.employeeselection

import android.os.Bundle
import ru.tensor.sbis.communication_decl.recipient_selection.FilterKey

/**
 * Класс, позволяющий задать конфигурацию компоненту выбора сотрудников
 */
class EmployeesSelectionFilter() {

    private var bundle = Bundle()

    /**
     * @param isSingleChoice                передать true, если требуется выбор только одного сотрудника или подразделения
     * @param needOpenProfileOnPhotoClick   передать true, если по нажатию на фото сотрудника необходимо открыть его профиль
     * @param onlyWithAccessToSbis          передать true, если нужно скрыть сотрудников без доступа к СБИС
     * @param excludeCurrentUser            передать true, если нужно исключить себя из выбора сотрудников
     * @param canSelectFolder               передать true, если нужно чтобы была возможность выбора папки целиком
     */
    @JvmOverloads
    constructor(
        isSingleChoice: Boolean,
        needOpenProfileOnPhotoClick: Boolean,
        onlyWithAccessToSbis: Boolean = false,
        excludeCurrentUser: Boolean = false,
        canSelectFolder: Boolean = true
    ) : this() {
        with(EmployeesSelectionFilterKeys.IS_SINGLE_CHOICE, isSingleChoice)
        with(EmployeesSelectionFilterKeys.NEED_OPEN_PROFILE_ON_PHOTO_CLICK, needOpenProfileOnPhotoClick)
        with(EmployeesSelectionFilterKeys.ONLY_WITH_ACCESS_TO_SBIS, onlyWithAccessToSbis)
        with(EmployeesSelectionFilterKeys.EXCLUDE_CURRENT_USER, excludeCurrentUser)
        with(EmployeesSelectionFilterKeys.CAN_SELECT_FOLDER, canSelectFolder)
    }

    constructor(bundle: Bundle) : this() {
        this.bundle = bundle
    }

    fun getBundle() = bundle

    private fun with(key: FilterKey, value: Boolean) {
        bundle.putBoolean(key.key(), value)
    }

    fun isSingleChoice(): Boolean {
        return getBoolean(EmployeesSelectionFilterKeys.IS_SINGLE_CHOICE)
    }

    fun needOpenProfileOnPhotoClick(): Boolean {
        return getBoolean(EmployeesSelectionFilterKeys.NEED_OPEN_PROFILE_ON_PHOTO_CLICK)
    }

    fun onlyWithAccessToSbis(): Boolean {
        return getBoolean(EmployeesSelectionFilterKeys.ONLY_WITH_ACCESS_TO_SBIS)
    }

    fun excludeCurrentUser(): Boolean {
        return getBoolean(EmployeesSelectionFilterKeys.EXCLUDE_CURRENT_USER)
    }

    fun canSelectFolder(): Boolean {
        return getBoolean(EmployeesSelectionFilterKeys.CAN_SELECT_FOLDER)
    }

    private fun getBoolean(key: FilterKey): Boolean {
        return bundle.getBoolean(key.key())
    }
}