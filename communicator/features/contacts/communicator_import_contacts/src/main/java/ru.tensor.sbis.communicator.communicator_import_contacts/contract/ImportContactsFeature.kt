package ru.tensor.sbis.communicator.communicator_import_contacts.contract

import ru.tensor.sbis.communicator.common.import_contacts.ImportContactsConfirmationFragmentFactory
import ru.tensor.sbis.communicator.common.import_contacts.ImportContactsHelper

/**
 * Фичи модуля реестра контактов
 * @see ImportContactsHelper.Provider
 *
 * @author da.zhukov
 */
internal interface ImportContactsFeature :
    ImportContactsHelper.Provider,
    ImportContactsConfirmationFragmentFactory