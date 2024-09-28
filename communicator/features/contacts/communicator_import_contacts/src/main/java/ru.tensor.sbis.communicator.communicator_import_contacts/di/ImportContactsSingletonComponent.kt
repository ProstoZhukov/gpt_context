package ru.tensor.sbis.communicator.communicator_import_contacts.di

import android.content.Context
import dagger.Component
import ru.tensor.sbis.common.di.CommonSingletonComponent
import ru.tensor.sbis.common.util.NetworkUtils
import ru.tensor.sbis.common.util.di.PerApp
import ru.tensor.sbis.communicator.communicator_import_contacts.contract.ImportContactsDependency
import ru.tensor.sbis.communicator.communicator_import_contacts.sync.CommunicatorSyncAdapter
import ru.tensor.sbis.communicator.common.import_contacts.ImportContactsHelper
import ru.tensor.sbis.communicator.communicator_import_contacts.importcontactsconfirmation.ImportContactsConfirmationFragment

/**
 * Di singleton копмонент модуля communicator_import_contacts
 *
 * @author da.zhukov
 */
@PerApp
@Component(
    dependencies = [ImportContactsDependency::class, CommonSingletonComponent::class],
    modules = [ImportContactsSingletonModule::class]
)
interface ImportContactsSingletonComponent {

    fun getContext(): Context

    fun getNetworkUtils(): NetworkUtils

    val importContactsHelper: ImportContactsHelper

    val importContactsConfirmationFragment: ImportContactsConfirmationFragment

    fun inject(syncAdapter: CommunicatorSyncAdapter)

    @Component.Factory
    interface Factory {
        fun create(dependency: ImportContactsDependency, commonSingletonComponent: CommonSingletonComponent): ImportContactsSingletonComponent
    }
}