package ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.model

import ru.tensor.sbis.communicator.common.data.mapper.asNative
import ru.tensor.sbis.communicator.common.util.mapPersonDecorationToInitialsStubData
import ru.tensor.sbis.communicator.generated.ParticipantRole
import ru.tensor.sbis.design.profile_decl.person.InitialsStubData
import ru.tensor.sbis.profile_service.models.employee_profile.EmployeeProfile
import ru.tensor.sbis.communicator.generated.ThemeParticipant as ControllerThemeParticipant
import ru.tensor.sbis.profiles.generated.EmployeeProfile as ControllerEmployeeProfile

/**
 * Дата-класс сущности "Участник диалога/чата"
 */
internal data class ThemeParticipant(
    val employeeProfile: EmployeeProfile,
    var role: ParticipantRole,
    var initialsStubData: InitialsStubData? = null
)

/**
 * Метод, преобразующий модель контроллера [ControllerThemeParticipant] в UI модель [ThemeParticipant]
 */
internal val ControllerThemeParticipant.asNative: ThemeParticipant
    get() = ThemeParticipant(
        employeeProfile = employee.asNative,
        role = role,
        initialsStubData = employee.person.photoDecoration.mapPersonDecorationToInitialsStubData()
    )

/**
 * Метод, преобразующий модель контроллера [ControllerEmployeeProfile] в UI модель [ThemeParticipant]
 */
internal val ControllerEmployeeProfile.asNative: ThemeParticipant
    get() = ThemeParticipant(
        this.asNative,
        ParticipantRole.MEMBER,
        this.person.photoDecoration.mapPersonDecorationToInitialsStubData()
    )
