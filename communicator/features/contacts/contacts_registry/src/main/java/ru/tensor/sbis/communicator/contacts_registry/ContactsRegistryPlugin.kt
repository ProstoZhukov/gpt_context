package ru.tensor.sbis.communicator.contacts_registry

import ru.tensor.sbis.communication_decl.analytics.AnalyticsUtil
import ru.tensor.sbis.communicator.common.data.model.CommunicatorHostFragmentFactory
import ru.tensor.sbis.communicator.common.di.CommunicatorCommonComponent
import ru.tensor.sbis.communicator.common.import_contacts.ImportContactsConfirmationFragmentFactory
import ru.tensor.sbis.communicator.common.import_contacts.ImportContactsHelper
import ru.tensor.sbis.communicator.contacts_declaration.registry.ContactsRegistryFragmentFactory
import ru.tensor.sbis.communicator.contacts_declaration.registry.ContactsRouter
import ru.tensor.sbis.communicator.contacts_registry.ContactsRegistryPlugin.ContactsRegistryOptions
import ru.tensor.sbis.communicator.contacts_registry.contract.ContactsRegistryDependency
import ru.tensor.sbis.communicator.contacts_registry.utils.EmployeesContactsTabHistoryImpl
import ru.tensor.sbis.communicator.declaration.tab_history.EmployeesContactsTabHistory
import ru.tensor.sbis.employee.employees_declaration.EmployeesRegistryFragmentFactory
import ru.tensor.sbis.feature_ctrl.SbisFeatureServiceProvider
import ru.tensor.sbis.main_screen_decl.navigation.service.NavigationService
import ru.tensor.sbis.person_decl.employee.person_card.factory.PersonCardIntentFactory
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.Plugin
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper
import ru.tensor.sbis.profile_service.controller.profile_settings.ProfileSettingsControllerWrapper
import ru.tensor.sbis.toolbox_decl.toolbar.ToolbarTabsController
import ru.tensor.sbis.verification_decl.login.LoginInterface
import ru.tensor.sbis.verification_decl.red_button.RedButtonActivatedProvider

/**
 * Плагин модуля реестра контактов.
 *
 * @author vv.chekurda
 */
object ContactsRegistryPlugin : Plugin<ContactsRegistryOptions> {

    private lateinit var communicatorCommonComponentFeatureProvider: FeatureProvider<CommunicatorCommonComponent>

    private lateinit var personCardIntentFactoryFeatureProvider: FeatureProvider<PersonCardIntentFactory>
    private lateinit var communicatorContactsRouterFeatureProvider: FeatureProvider<ContactsRouter.Provider>
    private lateinit var communicatorHostFragmentFactoryProvider: FeatureProvider<CommunicatorHostFragmentFactory>
    private lateinit var loginInterfaceFeatureProvider: FeatureProvider<LoginInterface.Provider>
    private lateinit var tabsVisibilityControllerProvider: FeatureProvider<ToolbarTabsController>
    private lateinit var navigationService: FeatureProvider<NavigationService>

    private var employeesRegistryFragmentFactoryProvider: FeatureProvider<EmployeesRegistryFragmentFactory>? = null
    private var importContactsHelperFeatureProvider: FeatureProvider<ImportContactsHelper.Provider>? = null
    private var importContactsConfirmationFragmentFactoryProvider: FeatureProvider<ImportContactsConfirmationFragmentFactory>? = null
    private var redButtonActivatedFeatureProvider: FeatureProvider<RedButtonActivatedProvider>? = null
    private var analyticsUtilFeatureProvider: FeatureProvider<AnalyticsUtil.Provider>? = null
    private var profileSettingsControllerWrapperFeatureProvider: FeatureProvider<ProfileSettingsControllerWrapper.Provider>? = null
    private var featureServiceProvider: FeatureProvider<SbisFeatureServiceProvider>? = null

    private val employeesContactsTabHistory by lazy {
        EmployeesContactsTabHistoryImpl(communicatorCommonComponentFeatureProvider.get().context)
    }

    override val api: Set<FeatureWrapper<out Feature>> = setOf(
        FeatureWrapper(ContactsRegistryFragmentFactory::class.java) { ContactsRegistryFeatureFacade },
        FeatureWrapper(EmployeesContactsTabHistory::class.java) { employeesContactsTabHistory }
    )

    override val dependency: Dependency = Dependency.Builder()
        .require(CommunicatorCommonComponent::class.java) { communicatorCommonComponentFeatureProvider = it }
        .require(PersonCardIntentFactory::class.java) { personCardIntentFactoryFeatureProvider = it }
        .require(ContactsRouter.Provider::class.java) { communicatorContactsRouterFeatureProvider = it }
        .require(CommunicatorHostFragmentFactory::class.java) { communicatorHostFragmentFactoryProvider = it }
        .require(LoginInterface.Provider::class.java) { loginInterfaceFeatureProvider = it }
        .require(ToolbarTabsController::class.java) { tabsVisibilityControllerProvider = it }
        .require(NavigationService::class.java) { navigationService = it }
        .optional(EmployeesRegistryFragmentFactory::class.java) { employeesRegistryFragmentFactoryProvider = it }
        .optional(ImportContactsHelper.Provider::class.java) { importContactsHelperFeatureProvider = it }
        .optional(ImportContactsConfirmationFragmentFactory::class.java) { importContactsConfirmationFragmentFactoryProvider = it }
        .optional(RedButtonActivatedProvider::class.java) { redButtonActivatedFeatureProvider = it }
        .optional(AnalyticsUtil.Provider::class.java) { analyticsUtilFeatureProvider = it }
        .optional(ProfileSettingsControllerWrapper.Provider::class.java) { profileSettingsControllerWrapperFeatureProvider = it }
        .optional(SbisFeatureServiceProvider::class.java) { featureServiceProvider = it }
        .build()

    override val customizationOptions = ContactsRegistryOptions()

    override fun initialize() {
        val dependency = object : ContactsRegistryDependency,
            PersonCardIntentFactory by personCardIntentFactoryFeatureProvider.get(),
            ContactsRouter.Provider by communicatorContactsRouterFeatureProvider.get(),
            CommunicatorHostFragmentFactory by communicatorHostFragmentFactoryProvider.get(),
            LoginInterface.Provider by loginInterfaceFeatureProvider.get(),
            ToolbarTabsController by tabsVisibilityControllerProvider.get(),
            NavigationService by navigationService.get(),
            EmployeesContactsTabHistory by employeesContactsTabHistory {
            override val employeesRegistryFragmentFactory: EmployeesRegistryFragmentFactory? =
                employeesRegistryFragmentFactoryProvider?.get()

            override val redButtonActivatedProvider: RedButtonActivatedProvider? =
                redButtonActivatedFeatureProvider?.get()

            override val importContactsHelperProvider: ImportContactsHelper.Provider? =
                importContactsHelperFeatureProvider?.get()

            override val importContactsConfirmationFragmentFactory: ImportContactsConfirmationFragmentFactory? =
                importContactsConfirmationFragmentFactoryProvider?.get()

            override val analyticsUtilProvider: AnalyticsUtil.Provider?
                get() = analyticsUtilFeatureProvider?.get()

            override val profileSettingsControllerWrapperProvider: ProfileSettingsControllerWrapper.Provider?
                get() = profileSettingsControllerWrapperFeatureProvider?.get()

            override val sbisFeatureServiceProvider: SbisFeatureServiceProvider?
                get() = featureServiceProvider?.get()
        }
        ContactsRegistryFeatureFacade.configure(dependency)
    }

    /**
     * Опции плагина модуля реестра контактов.
     */
    class ContactsRegistryOptions internal constructor() {

        /**
         * Есть ли у приложения элемент меню "контакты" в боковой или нижней навигации.
         * Эта опция нужна для того чтобы знать нужно ли слать событие навигации при переключении в контакты или нет.
         */
        var appHasContactsNavigationMenuItem: Boolean = false

        /**
         * Активна ли возможность занесения контактов в черный список.
         */
        var blackListEnabled: Boolean = false
    }
}