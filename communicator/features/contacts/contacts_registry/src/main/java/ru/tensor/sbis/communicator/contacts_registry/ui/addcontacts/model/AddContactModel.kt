package ru.tensor.sbis.communicator.contacts_registry.ui.addcontacts.model

import ru.tensor.sbis.profile_service.models.employee_profile.EmployeeProfile

/**
 * Модель контакта для экрана добавления нового контакта
 *
 * @property employee          модель профиля сотрудника
 * @property subtitleTextColor идентификатор ресурса цвета подзаголовка
 * @property nameHighlight     посветка имени при поиске
 *
 * @author vv.chekurda
 */
interface AddContactModel {
    val employee: EmployeeProfile
    val subtitleTextColor: Int
    val nameHighlight: List<Int>
}