package ru.tensor.sbis.contacts_main_screen_addon

import ru.tensor.sbis.common.navigation.NavxId
import ru.tensor.sbis.contacts_main_screen_addon.ContactsMainScreenAddonPlugin.contactsRegistryFragmentFactoryProvider
import ru.tensor.sbis.main_screen_decl.MainScreen
import ru.tensor.sbis.main_screen_decl.content.ContentController
import ru.tensor.sbis.main_screen_decl.content.MainScreenEntry
import ru.tensor.sbis.main_screen_decl.content.SimplifiedContentController

/**
 * Реализация [MainScreenEntry] для реестра контакты.
 * @property navxId идентификатор раздела, см. [NavxId].
 *
 * @author da.zhukov
 */
class ContactsMainScreenEntry(
    override val navxId: String
) : MainScreenEntry {

    override fun createScreen(
        entryPoint: ContentController.EntryPoint,
        mainScreen: MainScreen
    ): SimplifiedContentController.ContentInfo =
        SimplifiedContentController.ContentInfo(
            fragment = contactsRegistryFragmentFactoryProvider.get().createContactsRegistryFragment()
        )
}