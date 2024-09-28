package ru.tensor.sbis.communicator.contacts_registry.ui.contactlist.stub_helper

import java.util.HashMap

/**
 * Хелпер заглушек для реестра контактов
 *
 * @author da.zhukov
 */
internal interface ContactsStubHelper {

    /**
     * Текущая заглушка
     */
    val currentStub: ContactsStubs?

    /**
     * Создание заглушки
     */
    fun createStub(metadata: HashMap<String, String>): ContactsStubs?
}