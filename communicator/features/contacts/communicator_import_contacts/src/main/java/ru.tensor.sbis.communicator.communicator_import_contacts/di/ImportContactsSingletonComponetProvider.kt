package ru.tensor.sbis.communicator.communicator_import_contacts.di

/**
 * Провайдер Di singleton копмонента модуля communicator_import_contacts
 *
 * @author da.zhukov
 */
internal interface ImportContactsSingletonComponetProvider {

    fun getImportContactsSingletoneComponent(): ImportContactsSingletonComponent
}