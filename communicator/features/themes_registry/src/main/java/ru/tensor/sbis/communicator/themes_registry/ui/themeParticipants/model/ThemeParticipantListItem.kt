package ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.model

import ru.tensor.sbis.communicator.common.data.mapper.asNative
import ru.tensor.sbis.communicator.common.util.mapPersonDecorationToInitialsStubData
import ru.tensor.sbis.communicator.generated.ParticipantRole
import ru.tensor.sbis.design.profile_decl.person.InitialsStubData
import ru.tensor.sbis.profile_service.models.employee_profile.EmployeeProfile
import java.util.*
import ru.tensor.sbis.communicator.generated.ThemeParticipant as ControllerThemeParticipant
import ru.tensor.sbis.communicator.generated.ThemeParticipantFolder as ControllerThemeParticipantFolder
import ru.tensor.sbis.communicator.generated.ThemeParticipantListItem as ControllerThemeParticipantListItem
import ru.tensor.sbis.profiles.generated.EmployeeProfile as ControllerEmployeeProfile

/**
 * Базовый класс участников диалога/чата.
 *
 * @author dv.baranov
 */
internal sealed class ThemeParticipantListItem {

    /**
     * Дата-класс сущности "Участник диалога/чата"
     *
     * @param employeeProfile модель сотрудника по главной организации.
     * @param role роль участника (админ, кастомная роль с облака, обычный участник, гость).
     * @param initialsStubData модель данных для отображения заглушки с инициалами.
     */
    internal data class ThemeParticipant(
        val employeeProfile: EmployeeProfile,
        var role: ParticipantRole,
        var initialsStubData: InitialsStubData? = null,
    ) : ThemeParticipantListItem()

    /**
     * Дата-класс сущности "Папка участников чата"
     *
     * @param uuid - uuid папки
     * @param name - имя папки
     * @param count - количество элементов в папке
     */
    internal data class ThemeParticipantFolder(
        val uuid: UUID,
        val name: String,
        val count: Int,
    ) : ThemeParticipantListItem()
}

/**
 * Метод, преобразующий модель контроллера [ControllerThemeParticipantListItem] в UI модель [ThemeParticipantListItem]
 */
internal val ControllerThemeParticipantListItem.asNative: ThemeParticipantListItem
    get() = if (themeParticipant != null) {
        themeParticipant!!.asListItemNative
    } else if (themeParticipantFolder != null) {
        themeParticipantFolder!!.asNative
    } else {
        throw java.lang.IllegalArgumentException("themeParticipant and themeParticipantFolder is null in ControllerThemeParticipantListItem")
    }

/**
 * Метод, преобразующий модель контроллера [ControllerThemeParticipant] в UI модель [ThemeParticipantListItem.ThemeParticipant]
 */
internal val ControllerThemeParticipant.asListItemNative: ThemeParticipantListItem.ThemeParticipant
    get() = ThemeParticipantListItem.ThemeParticipant(
        employeeProfile = employee.asNative,
        role = role,
        initialsStubData = employee.person.photoDecoration.mapPersonDecorationToInitialsStubData(),
    )

/**
 * Метод, преобразующий модель контроллера [ControllerEmployeeProfile] в UI модель [ThemeParticipantListItem.ThemeParticipant]
 */
internal val ControllerEmployeeProfile.asListItemNative: ThemeParticipantListItem.ThemeParticipant
    get() = ThemeParticipantListItem.ThemeParticipant(
        this.asNative,
        role = ParticipantRole.MEMBER,
        this.person.photoDecoration.mapPersonDecorationToInitialsStubData(),
    )

/**
 * Метод, преобразующий модель контроллера [ControllerThemeParticipantFolder] в UI модель [ThemeParticipantListItem.ThemeParticipantFolder]
 */
internal val ControllerThemeParticipantFolder.asNative: ThemeParticipantListItem.ThemeParticipantFolder
    get() = ThemeParticipantListItem.ThemeParticipantFolder(
        uuid = uuid,
        name = name,
        count = count,
    )
