package ru.tensor.sbis.person_decl.employee.person_card.card_configurations

import android.os.Parcelable
import ru.tensor.sbis.person_decl.employee.card_configuration_common.SocialNetworksConfiguration
import ru.tensor.sbis.person_decl.employee.card_configuration_common.WorkInfoConfiguration
import ru.tensor.sbis.person_decl.employee.person_card.card_configurations.blocks.AboutMeConfiguration
import ru.tensor.sbis.person_decl.employee.person_card.card_configurations.blocks.BadgesConfiguration
import ru.tensor.sbis.person_decl.employee.person_card.card_configurations.blocks.ContactsConfiguration
import ru.tensor.sbis.person_decl.employee.person_card.card_configurations.blocks.CredentialsPhotoConfiguration
import ru.tensor.sbis.person_decl.employee.person_card.card_configurations.blocks.PersonCardFabButtonsConfiguration
import ru.tensor.sbis.person_decl.employee.person_card.card_configurations.blocks.TasksAndSalaryConfiguration

/**
 * Конфигурация экрана карточки сотрудника
 * @property credentialsPhotoConfiguration - конфигурация экрана и шапки с ФИО
 * @property workInfoConfiguration - конфигурация данных о месте работы
 * @property tasksAndSalaryConfiguration - конфигурация данных о задачах и зарплате
 * @property aboutMeConfiguration - конфигурация информации о себе
 * @property socialNetworksConfiguration - конфигурация привязанных соц. сетей
 * @property fabButtonsConfiguration - конфигурация плаващих кнопок
 *
 * @author ra.temnikov
 */
interface PersonCardConfiguration : Parcelable {
    var credentialsPhotoConfiguration: CredentialsPhotoConfiguration?
    var workInfoConfiguration: WorkInfoConfiguration?
    var badgesConfiguration: BadgesConfiguration?
    var contactsConfiguration: ContactsConfiguration?
    var tasksAndSalaryConfiguration: TasksAndSalaryConfiguration?
    var aboutMeConfiguration: AboutMeConfiguration?
    var socialNetworksConfiguration: SocialNetworksConfiguration?
    var fabButtonsConfiguration: PersonCardFabButtonsConfiguration?
}