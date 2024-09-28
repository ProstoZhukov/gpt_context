package ru.tensor.sbis.communicator.communicator_import_contacts.contract

import androidx.fragment.app.Fragment
import ru.tensor.sbis.communicator.communicator_import_contacts.ImportContactsPlugin.singletonComponent
import ru.tensor.sbis.communicator.common.import_contacts.ImportContactsHelper

/**
 * API модуля communicator_import_contacts, описывающий предоставляемый модулем функционал
 *
 * @author da.zhukov
 */
class ImportContactsFeatureImpl : ImportContactsFeature {

    override val importContactsHelper: ImportContactsHelper
        get() = singletonComponent.importContactsHelper

    override fun createImportContactsConfirmationFragment(): Fragment =
        singletonComponent.importContactsConfirmationFragment
}