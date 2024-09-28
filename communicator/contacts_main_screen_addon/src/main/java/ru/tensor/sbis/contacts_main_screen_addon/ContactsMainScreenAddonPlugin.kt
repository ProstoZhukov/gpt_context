package ru.tensor.sbis.contacts_main_screen_addon

import androidx.lifecycle.LiveData
import ru.tensor.sbis.communicator.contacts_declaration.registry.ContactsRegistryFragmentFactory
import ru.tensor.sbis.communicator.declaration.host_factory.ContactsRegistryHostFragmentFactory
import ru.tensor.sbis.communicator.declaration.host_factory.EmployeesRegistryHostFragmentFactory
import ru.tensor.sbis.communicator.declaration.tab_history.EmployeesContactsTabHistory
import ru.tensor.sbis.design.navigation.view.model.NavigationItem
import ru.tensor.sbis.employee.employees_declaration.EmployeesRegistryFragmentFactory
import ru.tensor.sbis.main_screen_decl.ConfigurableMainScreen
import ru.tensor.sbis.main_screen_decl.MainScreenAddon
import ru.tensor.sbis.main_screen_decl.content.MainScreenEntry
import ru.tensor.sbis.main_screen_decl.navigation.service.NavigationService
import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper

/**
 * Плагин аддона раздела контактов/сотрудников на главном экране
 *
 * @author us.bessonov
 */
object ContactsMainScreenAddonPlugin : BasePlugin<ContactsMainScreenAddonPlugin.CustomizationOptions>() {

    internal var employeesRegistryFragmentFactoryProvider: FeatureProvider<EmployeesRegistryFragmentFactory>? = null
    internal lateinit var contactsRegistryFragmentFactoryProvider: FeatureProvider<ContactsRegistryFragmentFactory>

    internal lateinit var employeesRegistryHostFragmentFactoryProvider: FeatureProvider<EmployeesRegistryHostFragmentFactory>
    internal lateinit var contactsRegistryHostFragmentFactoryProvider: FeatureProvider<ContactsRegistryHostFragmentFactory>

    internal var navigationServiceProvider: FeatureProvider<NavigationService>? = null
    internal var employeesContactsTabHistory: FeatureProvider<EmployeesContactsTabHistory>? = null

    override val api: Set<FeatureWrapper<out Feature>> by lazy {
        mutableSetOf<FeatureWrapper<out Feature>>().apply {
            addAll(
                ContactsMainScreenEntry.createEntries(customizationOptions.mode)
                    .map { entry ->
                        FeatureWrapper(MainScreenEntry::class.java) { entry }
                    }
            )
        }
    }

    override val dependency: Dependency by lazy {
        Dependency.Builder()
            .require(EmployeesRegistryHostFragmentFactory::class.java) { employeesRegistryHostFragmentFactoryProvider = it }
            .require(ContactsRegistryHostFragmentFactory::class.java) { contactsRegistryHostFragmentFactoryProvider = it }
            .require(ContactsRegistryFragmentFactory::class.java) { contactsRegistryFragmentFactoryProvider = it }
            .apply {
                if (customizationOptions.mode == Mode.CONTACTS_AND_EMPLOYEES) {
                    require(EmployeesRegistryFragmentFactory::class.java) { employeesRegistryFragmentFactoryProvider = it }
                } else {
                    optional(EmployeesRegistryFragmentFactory::class.java) { employeesRegistryFragmentFactoryProvider = it }
                }
            }
            .optional(NavigationService::class.java) { navigationServiceProvider = it }
            .optional(EmployeesContactsTabHistory::class.java) { employeesContactsTabHistory = it }
            .build()
    }

    override val customizationOptions = CustomizationOptions()

    /**
     * Конфигурация плагина.
     */
    class CustomizationOptions internal constructor() {

        /**
         * Установить мод работы плагина.
         * @see Mode
         */
        var mode: Mode = Mode.CONTACTS_AND_EMPLOYEES
    }

    /**
     * Мод работы плагина.
     * Временно решает проблему https://online.sbis.ru/opendoc.html?guid=574c55d1-51da-430c-8577-4f8be9125905&client=3.
     */
    enum class Mode {

        /**
         * Только контакты, без зависимостей от сотрудников.
         */
        CONTACTS,

        /**
         * Контакты и сотрудники.
         */
        CONTACTS_AND_EMPLOYEES
    }
}

/**
 * Создать аддон раздела контактов/сотрудников на главном экране
 */
fun ContactsMainScreenAddonPlugin.createAddon(
    employeesNavItem: NavigationItem = ContactsMainScreenAddon.createDefaultEmployeesItem(),
    employeesItemVisibilitySourceProvider: (ConfigurableMainScreen) -> LiveData<Boolean> = ContactsMainScreenAddon.defaultEmployeesVisibilitySourceProvider(),
    contactsNavItem: NavigationItem = ContactsMainScreenAddon.createDefaultContactsItem(),
    contactsItemVisibilitySourceProvider: (ConfigurableMainScreen) -> LiveData<Boolean> = ContactsMainScreenAddon.defaultContactsVisibilitySourceProvider()
): MainScreenAddon = ContactsMainScreenAddon(
    employeesNavItem,
    employeesItemVisibilitySourceProvider,
    contactsNavItem,
    contactsItemVisibilitySourceProvider,
    employeesRegistryHostFragmentFactoryProvider.get(),
    contactsRegistryHostFragmentFactoryProvider.get()
)