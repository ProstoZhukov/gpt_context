package ru.tensor.sbis.communicator.contacts_declaration.model

import java.util.HashMap

/**
 * Data refresh callback реестра контактов
 *
 * @author vv.chekurda
 */
interface ContactsDataRefreshCallback {

    fun execute(params: HashMap<String, String>)
}