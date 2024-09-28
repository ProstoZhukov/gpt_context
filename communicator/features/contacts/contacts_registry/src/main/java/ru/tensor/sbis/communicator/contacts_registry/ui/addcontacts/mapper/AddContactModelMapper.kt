package ru.tensor.sbis.communicator.contacts_registry.ui.addcontacts.mapper

import ru.tensor.sbis.common.util.ResourceProvider
import ru.tensor.sbis.communicator.contacts_registry.ui.addcontacts.model.AddContactModel
import ru.tensor.sbis.communicator.contacts_registry.ui.addcontacts.model.AddContactModelImpl
import ru.tensor.sbis.communicator.contacts_registry.R
import ru.tensor.sbis.profile_service.models.employee_profile.EmployeeProfile
import ru.tensor.sbis.design.R as RDesign

/**
 * Маппер моделей экрана добавления нового контакта
 *
 * @author vv.chekurda
 */
internal class AddContactModelMapper(
    private val resourceProvider: ResourceProvider,
) {

    fun apply(sourceList: List<EmployeeProfile>, nameHighLight: List<List<Int>>): List<AddContactModel> {
        val addContactModels: ArrayList<AddContactModel> = arrayListOf()
        sourceList.forEachIndexed { index, employeeProfile ->
            addContactModels.add(
                AddContactModelImpl(
                    employee = employeeProfile,
                    subtitleTextColor = resourceProvider.getColor(RDesign.color.text_color_black_3),
                    nameHighlight = nameHighLight[index]
                )
            )
        }
        return addContactModels
    }
}