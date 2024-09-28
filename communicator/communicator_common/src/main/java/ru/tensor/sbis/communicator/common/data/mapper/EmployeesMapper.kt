package ru.tensor.sbis.communicator.common.data.mapper

import ru.tensor.sbis.communicator.common.util.mapInitialsStubDataToPersonDecoration
import ru.tensor.sbis.communicator.common.util.mapPersonDecorationToInitialsStubData
import ru.tensor.sbis.person_decl.profile.model.Gender
import ru.tensor.sbis.persons.PersonName
import ru.tensor.sbis.profile_service.models.employee_profile.EmployeeProfile
import ru.tensor.sbis.profile_service.models.employee_profile.EmployeeProfileModel
import ru.tensor.sbis.profile_service.models.person.Person
import ru.tensor.sbis.profile_service.models.person.PersonModel
import ru.tensor.sbis.profiles.generated.Gender as CppGender
import ru.tensor.sbis.profiles.generated.PersonName as CppPersonName
import ru.tensor.sbis.profiles.generated.EmployeeProfile as CppEmployeeProfile
import ru.tensor.sbis.profiles.generated.Person as CppPerson

/**
 * Маппинг модели профиля в облегченную модель профиля сотрудника
 */
 val CppEmployeeProfile.asNative: EmployeeProfile
    get() = EmployeeProfileModel(
        person = person.asNative,
        isPhysic = isPhysic,
        companyOrDepartment = companyOrDepartment ?: "",
        position = position
    )

/**
 * Маппинг модели профиля в облегченную модель персоны
 */
@Suppress("DEPRECATION")
internal val CppPerson.asNative: Person
    get() = PersonModel(
        uuid = uuid,
        name = name.asNative,
        photoUrl = photoUrl ?: "",
        gender = gender.asNative,
        initialsStubData = photoDecoration.mapPersonDecorationToInitialsStubData(),
        hasAccess = hasAccess
    )

/**
 * Маппинг модели имени персоны контроллера в UI модель
 */
 val CppPersonName.asNative: PersonName
    get() = PersonName(first, last, patronymic)

/**
 * Маппинг пола персоны контроллера в UI пол
 */
internal val CppGender.asNative: Gender
    get() = when (this) {
        CppGender.MALE    -> Gender.MALE
        CppGender.FEMALE  -> Gender.FEMALE
        CppGender.UNKNOWN -> Gender.UNKNOWN
    }

/**
 * Маппинг модели сотрудника в модель профиля сотрудника контроллера
 */
val EmployeeProfile.asNative: CppEmployeeProfile
    get() = CppEmployeeProfile(
        person.asNative,
        isPhysic,
        companyOrDepartment,
        inMyCompany,
        position
    )

/**
 * Маппинг модели персоны в модель персоны контроллера
 */
@Suppress("DEPRECATION")
internal val Person.asNative: CppPerson
    get() = CppPerson(
        uuid,
        faceId,
        name.asNative,
        photoUrl,
        initialsStubData.mapInitialsStubDataToPersonDecoration(),
        gender.asNative,
        false
    )

/**
 * Маппинг UI модели имени персоны в модель контроллера
 */
internal val PersonName.asNative: CppPersonName
    get() = CppPersonName(firstName, lastName, patronymicName, null)

/**
 * Маппинг UI пола в пол персоны контроллера
 */
internal val Gender.asNative: CppGender
    get() = when (this) {
        Gender.MALE    -> CppGender.MALE
        Gender.FEMALE  -> CppGender.FEMALE
        Gender.UNKNOWN -> CppGender.UNKNOWN
    }