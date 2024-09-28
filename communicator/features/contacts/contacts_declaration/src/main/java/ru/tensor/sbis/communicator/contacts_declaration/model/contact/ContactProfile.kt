package ru.tensor.sbis.communicator.contacts_declaration.model.contact

import android.os.Parcelable
import ru.tensor.sbis.communicator.contacts_declaration.model.contact.data.ContactData
import ru.tensor.sbis.profile_service.models.employee_profile.EmployeeProfile

/**
 * Модель контакта
 * @see ContactData
 * @see EmployeeProfile
 *
 * @author vv.chekurda
 */
interface ContactProfile :
    ContactData,
    EmployeeProfile,
    Parcelable
