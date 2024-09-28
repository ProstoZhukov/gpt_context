package ru.tensor.sbis.communicator.contacts_registry.ui.contactlist.mapper

import ru.tensor.sbis.communicator.contacts_declaration.model.contact.ContactProfile
import ru.tensor.sbis.communicator.contacts_registry.ui.contactlist.model.ContactRegistryModel
import ru.tensor.sbis.communicator.contacts_registry.ui.contactlist.model.ContactsModel

/**
 * Маппер моделей реестра контактов
 *
 * @author vv.chekurda
 */
internal class ContactRegistryModelMapper {
    var lastItem: ContactsModel? = null

    fun apply(sourceList: List<ContactProfile>): List<ContactRegistryModel> =
        sourceList.mapIndexed { index, contact ->
            ContactsModel(
                contact = contact,
                lastMessageDate = contact.lastMessageDate,
                isSegmentDividerVisible = getSegmentDividerVisibility(sourceList, index),
            )
        }

    /**
     * Логика добавления разделителя "Найдено еще в сотрудниках"
     * Не мешало бы реализовать декоратором ресайклера
     */
    private fun getSegmentDividerVisibility(sourceList: List<ContactProfile>, currentIndex: Int): Boolean {
        val lastItemCopy = lastItem?.contact
        val currentItem = sourceList[currentIndex]
        return if (currentIndex == 0) {
            if (lastItemCopy == null) {
                !currentItem.isInMyContacts
            } else {
                !currentItem.isInMyContacts && lastItemCopy.isInMyContacts
            }
        } else {
            val previousItem = sourceList[currentIndex - 1]
            !currentItem.isInMyContacts && previousItem.isInMyContacts
        }
    }
}