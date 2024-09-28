package ru.tensor.sbis.communicator.contacts_registry.ui.contactlist.stub_helper

import ru.tensor.sbis.communicator.contacts_registry.ContactsRegistryFeatureFacade
import ru.tensor.sbis.communicator.contacts_registry.R
import ru.tensor.sbis.design.stubview.ImageStubContent
import ru.tensor.sbis.design.stubview.StubViewCase
import ru.tensor.sbis.design.stubview.StubViewContent
import ru.tensor.sbis.design.stubview.StubViewImageType
import java.util.HashMap
import ru.tensor.sbis.communicator.design.R as RCommunicatorDesign

/**
 * Коды загрушек реестра контактов
 *
 * @property stringValue ключ ошибки
 *
 * @author da.zhukov
 */
internal enum class ContactsStubCodes(val stringValue: String) {
    /**
     * Ошибка сети
     */
    NETWORK_ERROR("network_error"),

    /**
     * Папка пуста
     */
    EMPTY_FOLDER("empty_folder"),

    /**
     * Реестр контактов пуст, начните общение первым
     */
    START_FIRST("start_first"),

    /**
     * Нет найденных результатов
     */
    NO_RESULTS("no_results");

    /**
     * Преобразование в enum типов заглушек списка
     */
    fun toContactsStub(): ContactsStubs = when (this) {
        NETWORK_ERROR -> ContactsStubs.NO_CONNECTION
        EMPTY_FOLDER  -> ContactsStubs.EMPTY_FOLDER
        START_FIRST   -> ContactsStubs.START_FIRST
        NO_RESULTS    -> ContactsStubs.NOT_FOUND
    }
}

/**
 * Типы заглушек реестра контактов
 */
internal enum class ContactsStubs {
    /**
     * Нет сети
     */
    NO_CONNECTION,

    /**
     * Начните общение первым
     */
    START_FIRST,

    /**
     * Нет найденных результатов
     */
    NOT_FOUND,

    /**
     * Папка пуста
     */
    EMPTY_FOLDER;

    /**
     * Маппинг в модель контента компонента заглушки
     */
    fun toStubCaseContent(actions: Map<Int, () -> Unit>): StubViewContent = when (this) {
        NO_CONNECTION -> StubViewCase.NO_CONNECTION.getContent(actions)
        NOT_FOUND     -> StubViewCase.NO_SEARCH_RESULTS.getContent(actions)
        EMPTY_FOLDER  -> ImageStubContent(
            StubViewImageType.NOT_FOUND,
            null,
            RCommunicatorDesign.string.communicator_no_contacts_folder_to_display
        )
        START_FIRST   -> ContactsStubHelperImpl.noContactsStubViewContent(actions)
    }
}

/**
 * Реализация хелпера заглушек для реестра контактов
 *
 * @author da.zhukov
 */
internal class ContactsStubHelperImpl : ContactsStubHelper {

    companion object {
        const val DUMMY_CODE = "dummy_code"

        fun noContactsStubViewContent(actions: Map<Int, () -> Unit> = emptyMap()): StubViewContent {
            return if (ContactsRegistryFeatureFacade.importContactsFeatureEnabled) {
                ImageStubContent(
                    StubViewCase.NO_CONTACTS.imageType,
                    R.string.contacts_registry_import_enabled_no_contacts_stub_message,
                    R.string.contacts_registry_import_enabled_no_contacts_stub_details,
                    actions
                )
            } else {
                StubViewCase.NO_CONTACTS.getContent(actions)
            }
        }
    }

    private var stub: ContactsStubs? = null

    override val currentStub: ContactsStubs?
        get() = stub

    override fun createStub(metadata: HashMap<String, String>): ContactsStubs? {
        ContactsStubCodes.values().firstOrNull { metadata[DUMMY_CODE] == it.stringValue }.let {
            stub = it?.toContactsStub()
            return stub
        }
    }
}