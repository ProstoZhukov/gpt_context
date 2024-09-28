package ru.tensor.sbis.communicator.contacts_registry.contract

import ru.tensor.sbis.communication_decl.analytics.AnalyticsUtil
import ru.tensor.sbis.communicator.common.data.model.CommunicatorHostFragmentFactory
import ru.tensor.sbis.communicator.common.import_contacts.ImportContactsConfirmationFragmentFactory
import ru.tensor.sbis.communicator.common.import_contacts.ImportContactsHelper
import ru.tensor.sbis.communicator.contacts_declaration.registry.ContactsRouter
import ru.tensor.sbis.communicator.declaration.tab_history.EmployeesContactsTabHistory
import ru.tensor.sbis.employee.employees_declaration.EmployeesRegistryFragmentFactory
import ru.tensor.sbis.feature_ctrl.SbisFeatureServiceProvider
import ru.tensor.sbis.main_screen_decl.navigation.service.NavigationService
import ru.tensor.sbis.person_decl.employee.person_card.factory.PersonCardIntentFactory
import ru.tensor.sbis.profile_service.controller.profile_settings.ProfileSettingsControllerWrapper
import ru.tensor.sbis.toolbox_decl.toolbar.ToolbarTabsController
import ru.tensor.sbis.verification_decl.login.LoginInterface
import ru.tensor.sbis.verification_decl.red_button.RedButtonActivatedProvider

/**
 * Внешние зависимости модуля реестра контактов
 * @see PersonCardIntentFactory
 * @see RedButtonActivatedProvider
 * @see ContactsRouter.Provider
 * @see LoginInterface.Provider
 * @see ImportContactsHelper.Provider
 * @see ImportContactsConfirmationFragmentFactory
 * @see CommunicatorHostFragmentFactory
 *
 * @author vv.chekurda
 */
internal interface ContactsRegistryDependency :
    PersonCardIntentFactory,
    ContactsRouter.Provider,
    LoginInterface.Provider,
    CommunicatorHostFragmentFactory,
    ToolbarTabsController,
    NavigationService,
    EmployeesContactsTabHistory {

    interface Provider {
        val contactsDependency: ContactsRegistryDependency
    }

    val employeesRegistryFragmentFactory: EmployeesRegistryFragmentFactory?

    val importContactsHelperProvider: ImportContactsHelper.Provider?

    val importContactsConfirmationFragmentFactory: ImportContactsConfirmationFragmentFactory?

    val redButtonActivatedProvider: RedButtonActivatedProvider?

    /** Поставщик интерфейса утилиты для отправки аналитики. */
    val analyticsUtilProvider: AnalyticsUtil.Provider?

    /** Поставщик UI обертки контроллера настроек пользователя. */
    val profileSettingsControllerWrapperProvider: ProfileSettingsControllerWrapper.Provider?

    val sbisFeatureServiceProvider: SbisFeatureServiceProvider?
}
