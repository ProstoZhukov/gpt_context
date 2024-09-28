package ru.tensor.sbis.communicator.communicator_import_contacts.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds.Email
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.provider.ContactsContract.CommonDataKinds.StructuredName
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import io.reactivex.Maybe
import io.reactivex.Single
import ru.tensor.sbis.common.generated.CommandStatus
import ru.tensor.sbis.common.generated.ErrorCode
import ru.tensor.sbis.common.util.PermissionUtil
import ru.tensor.sbis.common.util.SharedPreferencesUtils
import ru.tensor.sbis.common.util.asArrayList
import ru.tensor.sbis.communicator.common.import_contacts.ImportContactsHelper
import ru.tensor.sbis.communicator.contacts_declaration.model.import_contact.Communication
import ru.tensor.sbis.communicator.contacts_declaration.model.import_contact.CommunicationType
import ru.tensor.sbis.communicator.contacts_declaration.model.import_contact.ImportContactData
import ru.tensor.sbis.communicator.design.R as RCommunicatorDesign
import ru.tensor.sbis.communicator.contacts_declaration.controller.ContactsControllerWrapper
import timber.log.Timber

/**
 * Реализация делегата для импортирования контактов с устройства
 */
class ImportContactsHelperImpl(
    private val context: Context,
    private val contactsControllerWrapper: ContactsControllerWrapper
): ImportContactsHelper {

    @Suppress("unused")
    override fun importContactsUnsafe() {
        importContactsInternalSync()
    }

    @Suppress("UNUSED_PARAMETER")
    override fun importContactsSafe(fallback: () -> Unit): Maybe<CommandStatus> {
        disableRequestContactPermissions(context)
        return if (safeCheckIfNeedRequestContactsPermission()) {
            fallback()
            Maybe.empty()
        } else {
            importContactsInternalAsync().toMaybe()
        }
    }

    @Suppress("UNUSED_PARAMETER")
    override fun requestPermissions(fragment: Fragment) {
        fragment.requestPermissions(arrayOf(READ_CONTACTS_PERMISSION_NAME), READ_CONTACTS_PERMISSION_REQUEST_CODE)
    }

    override fun disableRequestContactPermissions(context: Context) {
        SharedPreferencesUtils.disableRequestContactPermissions(context)
    }

    override fun onRequestPermissionsResult(requestCode: Int, grantResults: IntArray): Single<CommandStatus> {
        SharedPreferencesUtils.disableRequestContactPermissions(context)

        return if (requestCode == READ_CONTACTS_PERMISSION_REQUEST_CODE) {
            verifyPermissions(grantResults)
        } else {
            Single.just(CommandStatus(ErrorCode.SUCCESS, ""))
        }
    }

    override fun onDestroy() {
        // как можно очищать здесь подписку, если это синглтон?
    }

    /**
     *  Метод для проверки результата выданных разрешений.
     */
    private fun verifyPermissions(grantResults: IntArray): Single<CommandStatus> {
        return if (PermissionUtil.verifyPermissions(grantResults)) {
            importContactsInternalAsync()
        } else {
            Single.just(CommandStatus(ErrorCode.OTHER_ERROR, context.resources.getString(RCommunicatorDesign.string.communicator_toast_no_permission_to_import_contacts)))
        }
    }

    /**
     * Метод для проверки разрешения на чтение контактов устройства.
     * Проверка обернута в try-catch из-за вероятности данной ошибки:
     * https://online.sbis.ru/opendoc.html?guid=26e958e4-107c-474d-b351-b7f825f91528
     */
    @Suppress("UNUSED")
    private fun safeCheckIfNeedRequestContactsPermission(): Boolean {
        return try {
            ContextCompat.checkSelfPermission(context, READ_CONTACTS_PERMISSION_NAME) != PackageManager.PERMISSION_GRANTED
        } catch (error: Throwable) {
            Timber.d(error, "Error when trying check $READ_CONTACTS_PERMISSION_NAME permission!")
            false
        }
    }

    /**
     * Метод для импорта контактов.
     * Сначала загружает список телефонов из провайдера контактов устройства,
     * затем отправляет их в контроллер (если список контактов не пустой).
     */
    @SuppressLint("CheckResult")
    private fun importContactsInternalAsync(): Single<CommandStatus> {
        SharedPreferencesUtils.disableRequestContactPermissions(context)
        return Single.fromCallable {
            val contacts = loadContactDataFromContentProvider()
            contactsControllerWrapper.importPhoneBook(contacts)
        }
    }

    private fun importContactsInternalSync() {
        val contacts = loadContactDataFromContentProvider()
        if (contacts.isNotEmpty()) {
            contactsControllerWrapper.importPhoneBook(contacts)
        }
    }

    /**
     * Метод для загрузки данных контактов из системного провайдера.
     */
    private fun loadContactDataFromContentProvider(): ArrayList<ImportContactData> {
        val communications = mutableMapOf<String, MutableList<Communication>>()
        val contactsNames = mutableMapOf<String, Triple<String, String, String>>()

        val cursor = context.contentResolver.query(
            ContactsContract.Data.CONTENT_URI,
            arrayOf(
                MIMETYPE,
                LOOKUP_KEY,
                Phone.NUMBER,
                Phone.TYPE,
                Email.ADDRESS,
                StructuredName.GIVEN_NAME,
                StructuredName.FAMILY_NAME,
                StructuredName.MIDDLE_NAME
            ),
            null,
            null,
            null
        )

        cursor?.let {
            if (it.moveToFirst()) {
                do {
                    val globalId = it.getSafeString(LOOKUP_KEY)
                    when (it.getSafeString(MIMETYPE)) {
                        Phone.CONTENT_ITEM_TYPE -> {
                            val communication = it.readContactCommunication(Phone.NUMBER, false)
                            communications.update(communication, globalId)
                        }
                        StructuredName.CONTENT_ITEM_TYPE -> {
                            contactsNames[globalId] = it.readContactFullName()
                        }
                        Email.CONTENT_ITEM_TYPE -> {
                            val communication = it.readContactCommunication(Email.ADDRESS, true)
                            communications.update(communication, globalId)
                        }
                        else -> continue
                    }
                } while (it.moveToNext())
            }
            it.close()
        }
        return contactsNames.map {
            ImportContactData(
                globalId = it.key,
                surname = it.value.second,
                name = it.value.first,
                patronymic = it.value.third,
                communications = communications[it.key]?.toList()?.filter { item ->
                    item.second != CommunicationType.NONE
                } ?: emptyList()
            )
        }.asArrayList()
    }

    private fun Cursor.readContactCommunication(value: String, isEmail: Boolean): Communication {
        val valueOfCommunication = getSafeString(value)
        val typeOfCommunication = if (isEmail) {
            CommunicationType.EMAIL
        } else {
            getSafeString(Phone.TYPE).toCommunicationType(valueOfCommunication)
        }
        return Pair(valueOfCommunication, typeOfCommunication)
    }

    private fun MutableMap<String, MutableList<Communication>>.update(value: Communication, mapKey: String) {
        if (containsKey(mapKey)) {
            this[mapKey]?.add(value)
        } else {
            this[mapKey] = mutableListOf(value)
        }
    }

    private fun Cursor.readContactFullName(): Triple<String, String, String> = Triple(
        getSafeString(StructuredName.GIVEN_NAME), // имя
        getSafeString(StructuredName.FAMILY_NAME), // фамилия
        getSafeString(StructuredName.MIDDLE_NAME) // отчество
    )

    private fun Cursor.getSafeString(columnName: String): String {
        val index = getColumnIndex(columnName)
        return getString(if (index != -1) index else 0) ?: "null"
    }

    private fun String?.toCommunicationType(associatedValue: String): CommunicationType = when {
        associatedValue == "null" || this == null -> CommunicationType.NONE
        equals(TYPE_HOME) -> CommunicationType.HOME_PHONE
        equals(TYPE_MOBILE) -> CommunicationType.MOBILE_PHONE
        equals(TYPE_WORK) -> CommunicationType.WORK_PHONE
        else -> CommunicationType.NONE
    }

    companion object {
        private const val READ_CONTACTS_PERMISSION_REQUEST_CODE = 1
        private const val READ_CONTACTS_PERMISSION_NAME = android.Manifest.permission.READ_CONTACTS
        private const val MIMETYPE = ContactsContract.CommonDataKinds.Callable.MIMETYPE
        private const val LOOKUP_KEY = ContactsContract.CommonDataKinds.Callable.LOOKUP_KEY
        private const val TYPE_HOME = "1"
        private const val TYPE_MOBILE = "2"
        private const val TYPE_WORK = "3"
    }
}