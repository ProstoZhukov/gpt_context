package ru.tensor.sbis.recipient_selection.profile.mapper

import android.content.Context
import ru.tensor.sbis.persons.ContactVM
import ru.tensor.sbis.persons.PersonName
import ru.tensor.sbis.common.modelmapper.BaseModelMapper
import ru.tensor.sbis.person_decl.profile.model.Gender
import ru.tensor.sbis.profiles.generated.EmployeeProfile
import ru.tensor.sbis.profiles.generated.Gender as CppGender
import ru.tensor.sbis.recipient_selection.profile.data.group_profiles.ContactItem
import ru.tensor.sbis.recipient_selection.profile.ui.mapPersonDecorationToInitialsStubData

/**
 * Маппер, преобразующий модель контроллера [EmployeeProfile] в UI модель [ContactItem]
 */
internal class ProfileAndContactItemMapper(context: Context): BaseModelMapper<EmployeeProfile, ContactItem>(context) {

    /** @SelfDocumented */
    override fun apply(t: EmployeeProfile): ContactItem {
        return ContactItem(createContactVM(t))
    }

    private fun createContactVM(rawModel: EmployeeProfile) = ContactVM().apply {
        uuid = rawModel.person.uuid
        rawPhoto = rawModel.person.photoUrl
        name = PersonName(rawModel.person.name.first, rawModel.person.name.last, rawModel.person.name.patronymic)
        if (!rawModel.isPhysic) {
            data1 = rawModel.companyOrDepartment
        } else {
            // Для физ.лица не выводим данных о работе
            data1 = null
            data2 = null
        }
        gender = when (rawModel.person.gender) {
            CppGender.MALE -> Gender.MALE
            CppGender.FEMALE -> Gender.FEMALE
            CppGender.UNKNOWN -> Gender.UNKNOWN
        }
        initialsStubData =  rawModel.person.photoDecoration.mapPersonDecorationToInitialsStubData()
    }
}