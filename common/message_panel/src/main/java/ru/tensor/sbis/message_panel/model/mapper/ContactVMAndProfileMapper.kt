package ru.tensor.sbis.message_panel.model.mapper

import ru.tensor.sbis.persons.ContactVM
import ru.tensor.sbis.persons.PersonName
import ru.tensor.sbis.person_decl.profile.model.Gender
import ru.tensor.sbis.profiles.generated.*
import ru.tensor.sbis.profiles.generated.Gender as GeneratedGender

internal object ContactVMAndProfileMapper {

    internal fun modelFromProfile(person: Person): ContactVM {
        val model = ContactVM()
        model.uuid = person.uuid
        model.rawPhoto = person.photoUrl
        model.name = PersonName(person.name.first, person.name.last, person.name.patronymic)

        when (person.gender) {
            GeneratedGender.FEMALE -> model.gender = Gender.FEMALE
            GeneratedGender.MALE -> model.gender = Gender.MALE
            else -> model.gender = Gender.UNKNOWN
        }
        return model
    }
}
