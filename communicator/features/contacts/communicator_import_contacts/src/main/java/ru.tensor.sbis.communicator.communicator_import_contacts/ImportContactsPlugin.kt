package ru.tensor.sbis.communicator.communicator_import_contacts

import ru.tensor.sbis.common.ModuleSyncAdapter
import ru.tensor.sbis.common.di.CommonSingletonComponent
import ru.tensor.sbis.communicator.common.import_contacts.ImportContactsConfirmationFragmentFactory
import ru.tensor.sbis.communicator.communicator_import_contacts.contract.ImportContactsDependency
import ru.tensor.sbis.communicator.communicator_import_contacts.contract.ImportContactsFeatureImpl
import ru.tensor.sbis.communicator.communicator_import_contacts.di.DaggerImportContactsSingletonComponent
import ru.tensor.sbis.communicator.communicator_import_contacts.di.ImportContactsSingletonComponent
import ru.tensor.sbis.communicator.communicator_import_contacts.di.ImportContactsSingletonComponetProvider
import ru.tensor.sbis.communicator.communicator_import_contacts.sync.CommunicatorSyncAdapter
import ru.tensor.sbis.communicator.common.import_contacts.ImportContactsHelper
import ru.tensor.sbis.communicator.contacts_declaration.controller.ContactsControllerWrapper
import ru.tensor.sbis.verification_decl.login.LoginInterface
import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper

/**
 * Плагин импорта контактов
 *
 * @author da.zhukov
 */
object ImportContactsPlugin : BasePlugin<ImportContactsPlugin.CustomizationOptions>(), ImportContactsSingletonComponetProvider {

    private val importContactsFeature by lazy(::ImportContactsFeatureImpl)
    internal val singletonComponent by lazy {
        val dependency = object : ImportContactsDependency,
            ContactsControllerWrapper.Provider by contactsControllerWrapperProvider.get(),
            LoginInterface.Provider by loginInterfaceProvider.get() {}
        DaggerImportContactsSingletonComponent.factory()
            .create(dependency, commonSingletonComponent.get())
    }

    private val syncAdapter by lazy {
        CommunicatorSyncAdapter(application)
    }

    private lateinit var loginInterfaceProvider: FeatureProvider<LoginInterface.Provider>
    private lateinit var contactsControllerWrapperProvider: FeatureProvider<ContactsControllerWrapper.Provider>
    private lateinit var commonSingletonComponent: FeatureProvider<CommonSingletonComponent>

    override val api: Set<FeatureWrapper<out Feature>> = setOf(
        FeatureWrapper(ModuleSyncAdapter::class.java) { syncAdapter },
        FeatureWrapper(ImportContactsHelper.Provider::class.java) { importContactsFeature },
        FeatureWrapper(ImportContactsConfirmationFragmentFactory::class.java) { importContactsFeature }
    )
    override val dependency: Dependency by lazy {
        Dependency.Builder()
            .require(CommonSingletonComponent::class.java) { commonSingletonComponent = it }
            .require(LoginInterface.Provider::class.java) { loginInterfaceProvider = it }
            .require(ContactsControllerWrapper.Provider::class.java) { contactsControllerWrapperProvider = it }
            .build()
    }

    override val customizationOptions = CustomizationOptions()

    override fun getImportContactsSingletoneComponent(): ImportContactsSingletonComponent = singletonComponent

    class CustomizationOptions internal constructor() {

        /**
         * Название приложения
         */
        var appName: String? = null
    }
}