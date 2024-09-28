package ru.tensor.sbis.contacts_main_screen_addon

import androidx.fragment.app.Fragment
import ru.tensor.sbis.common.navigation.NavxId
import ru.tensor.sbis.contacts_main_screen_addon.ContactsMainScreenAddonPlugin.contactsRegistryFragmentFactoryProvider
import ru.tensor.sbis.contacts_main_screen_addon.ContactsMainScreenAddonPlugin.employeesRegistryFragmentFactoryProvider
import ru.tensor.sbis.main_screen_decl.MainScreen
import ru.tensor.sbis.main_screen_decl.content.ContentController
import ru.tensor.sbis.main_screen_decl.content.MainScreenEntry
import ru.tensor.sbis.main_screen_decl.content.SimplifiedContentController
import ru.tensor.sbis.toolbox_decl.navigation.NavxIdDecl

/**
 * Реализация [MainScreenEntry] для реестров сотрудники/контакты.
 * @property id идентификатор раздела, см. [NavxId].
 *
 * @author dv.baranov
 */
class ContactsMainScreenEntry(
    override val id: NavxIdDecl
) : MainScreenEntry {

    override fun createScreen(
        entryPoint: ContentController.EntryPoint,
        mainScreen: MainScreen
    ): SimplifiedContentController.ContentInfo =
        SimplifiedContentController.ContentInfo(createFragment())

    private fun createFragment(): Fragment =
        if (id == NavxId.CONTACTS) {
            contactsRegistryFragmentFactoryProvider.get().createContactsRegistryFragment()
        } else {
            employeesRegistryFragmentFactoryProvider!!.get().createEmployeesRegistryFragment()
        }

    companion object {
        fun createEntries(mode: ContactsMainScreenAddonPlugin.Mode) =
            listOfNotNull(
                ContactsMainScreenEntry(NavxId.CONTACTS),
                ContactsMainScreenEntry(NavxId.STAFF).takeIf {
                    mode == ContactsMainScreenAddonPlugin.Mode.CONTACTS_AND_EMPLOYEES
                }
            )
    }
}