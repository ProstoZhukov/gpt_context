package ru.tensor.sbis.communicator.communicator_import_contacts.di

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.tensor.sbis.common.util.di.PerApp
import ru.tensor.sbis.communicator.common.import_contacts.ImportContactsHelper
import ru.tensor.sbis.communicator.communicator_import_contacts.importcontactsconfirmation.ImportContactsConfirmationFragment
import ru.tensor.sbis.communicator.communicator_import_contacts.utils.ImportContactsHelperImpl
import ru.tensor.sbis.communicator.contacts_declaration.controller.ContactsControllerWrapper

/**
 * Di singleton модуль модуля communicator_import_contacts
 *
 * @author da.zhukov
 */
@Module
internal class ImportContactsSingletonModule {

    @Provides
    @PerApp
    fun provideImportContactsHelper(
        context: Context,
        contactsControllerWrapperProvider: ContactsControllerWrapper
    ): ImportContactsHelper =
       ImportContactsHelperImpl(context, contactsControllerWrapperProvider)

    @Provides
    @PerApp
    fun provideImportContactsConfirmationFragment(): ImportContactsConfirmationFragment =
        ImportContactsConfirmationFragment()
}