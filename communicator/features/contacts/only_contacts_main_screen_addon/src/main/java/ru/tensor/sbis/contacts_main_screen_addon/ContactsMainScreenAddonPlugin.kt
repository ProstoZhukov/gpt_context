package ru.tensor.sbis.contacts_main_screen_addon

import androidx.lifecycle.LiveData
import ru.tensor.sbis.common.navigation.NavxId
import ru.tensor.sbis.communicator.contacts_declaration.registry.ContactsRegistryFragmentFactory
import ru.tensor.sbis.main_screen_decl.ConfigurableMainScreen
import ru.tensor.sbis.main_screen_decl.MainScreenAddon
import ru.tensor.sbis.main_screen_decl.content.install.FragmentInstallationStrategy
import ru.tensor.sbis.design.navigation.view.model.NavigationItem
import ru.tensor.sbis.main_screen_decl.content.MainScreenEntry
import ru.tensor.sbis.main_screen_decl.content.install.VisibilityFragmentInstallationStrategy
import ru.tensor.sbis.plugin_struct.BasePlugin
import ru.tensor.sbis.plugin_struct.Dependency
import ru.tensor.sbis.plugin_struct.feature.Feature
import ru.tensor.sbis.plugin_struct.feature.FeatureProvider
import ru.tensor.sbis.plugin_struct.feature.FeatureWrapper

/**
 * Плагин аддона раздела контактов.
 *
 * @author da.zhukov
 */
object ContactsMainScreenAddonPlugin : BasePlugin<Unit>() {

    /** @SelfDocumented */
    internal lateinit var contactsRegistryFragmentFactoryProvider: FeatureProvider<ContactsRegistryFragmentFactory>

    override val api: Set<FeatureWrapper<out Feature>> = setOf(
        FeatureWrapper(MainScreenEntry::class.java) { ContactsMainScreenEntry(NavxId.CONTACTS.id) }
    )

    override val customizationOptions = Unit

    override val dependency: Dependency by lazy {
        Dependency.Builder()
            .require(ContactsRegistryFragmentFactory::class.java) { contactsRegistryFragmentFactoryProvider = it }
            .build()
    }
}

/**
 * Создать аддон раздела контактов на главном экране
 */
fun ContactsMainScreenAddonPlugin.createAddon(
    contactsNavItem: NavigationItem = ContactsMainScreenAddon.createDefaultContactsItem(),
    contactsItemVisibilitySourceProvider: (ConfigurableMainScreen) -> LiveData<Boolean> = ContactsMainScreenAddon.defaultContactsVisibilitySourceProvider(),
    fragmentInstallationStrategy: FragmentInstallationStrategy = VisibilityFragmentInstallationStrategy()
): MainScreenAddon = ContactsMainScreenAddon(
    contactsNavItem,
    contactsItemVisibilitySourceProvider,
    contactsRegistryFragmentFactoryProvider.get(),
    fragmentInstallationStrategy
)