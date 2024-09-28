package ru.tensor.sbis.communicator.contacts_registry.ui.contactlist;

import androidx.annotation.StringRes;


/**
 * Опции меню добавленя нового контакта\
 *
 * @author da.zhukov
 */
public enum AddContactOption {

    /**
     * Импорт контактов
     */
    IMPORT_CONTACT(ru.tensor.sbis.communicator.design.R.string.communicator_contacts_adding_import_contact),

    /**
     * Поиск сотрудника внутри компании
     */
    CONTACT_IN_COMPANY(ru.tensor.sbis.communicator.design.R.string.communicator_adding_internal_employee_label),

    /**
     * Поиск нового контакта
     */
    NEW_CONTACT(ru.tensor.sbis.communicator.design.R.string.communicator_activity_add_new_contacts_label);

    /**
     * Строковый ресурс пункта
     */
    @StringRes
    private final int mItemStringRes;

    AddContactOption(@StringRes int itemStringRes) {
        mItemStringRes = itemStringRes;
    }

    @StringRes
    public int getItemStringRes() {
        return mItemStringRes;
    }

}
