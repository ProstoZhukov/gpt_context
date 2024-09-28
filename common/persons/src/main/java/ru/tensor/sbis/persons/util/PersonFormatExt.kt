package ru.tensor.sbis.persons.util

import ru.tensor.sbis.design.profile_decl.util.PersonNameTemplate
import ru.tensor.sbis.person_decl.profile.model.PersonName
import ru.tensor.sbis.verification_decl.account.PersonalAccount
import ru.tensor.sbis.verification_decl.account.UserAccount

/** @SelfDocumented */
fun PersonalAccount.formatName(template: PersonNameTemplate) = template.format(surname, name)

/** @SelfDocumented */
fun UserAccount.formatName(template: PersonNameTemplate) = template.format(userSurname, userName, userPatronymic)

/** @SelfDocumented */
fun PersonName.formatName(template: PersonNameTemplate) = template.format(lastName, firstName, patronymicName)