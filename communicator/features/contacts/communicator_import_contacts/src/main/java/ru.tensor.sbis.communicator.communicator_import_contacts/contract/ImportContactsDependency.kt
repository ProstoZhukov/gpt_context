package ru.tensor.sbis.communicator.communicator_import_contacts.contract

import ru.tensor.sbis.communicator.contacts_declaration.controller.ContactsControllerWrapper
import ru.tensor.sbis.verification_decl.login.LoginInterface

/**
 * Внешние зависимости модуля импорта контактов
 * @see ContactsControllerWrapper.Provider
 * @see LoginInterface.Provider
 *
 * @author da.zhukov
 */
interface ImportContactsDependency :
    ContactsControllerWrapper.Provider,
    LoginInterface.Provider