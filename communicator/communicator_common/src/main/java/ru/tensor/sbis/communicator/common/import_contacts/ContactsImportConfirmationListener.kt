package ru.tensor.sbis.communicator.common.import_contacts

/**
 * Слушатель импорта контактов
 *
 * @author da.zhukov
 */
interface ContactsImportConfirmationListener {

    /**
     * Подтверждение импорта контактов
     */
    fun contactsImportConfirmed()

    /**
     * Отклонение импорта контактов
     */
    fun contactsImportDeclined()
}