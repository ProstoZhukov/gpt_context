package ru.tensor.sbis.message_panel.model.mapper

import android.content.Context

import ru.tensor.sbis.persons.ContactVM
import ru.tensor.sbis.common.modelmapper.BaseModelMapper
import ru.tensor.sbis.profile_service.models.employee_profile.EmployeeProfile

class ContactVMItemMapper(context: Context) : BaseModelMapper<EmployeeProfile, ContactVM>(context) {

    @Throws(Exception::class)
    override fun apply(employee: EmployeeProfile): ContactVM =
        ContactVM().apply {
            uuid = employee.uuid
            rawPhoto = employee.photoUrl
            name = employee.name
            if (!employee.isPhysic) {
                data1 = employee.companyOrDepartment
            }
        }
}