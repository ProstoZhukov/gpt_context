package ru.tensor.sbis.communicator.communicator_share_messages.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds.Phone
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.tensor.sbis.communicator.communicator_share_messages.R
import ru.tensor.sbis.design.text_span.text.masked.formatter.phone.formatPhone
import timber.log.Timber
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.util.*

/**
 * Инструмент для получения информации о контактах при шаринге.
 * Реализация получения контактов из .vcf файлов заимствована у Telegram.
 *
 * @author dv.baranov
 */
class ContactsInfoUtil(private val context: Context) {

    /**
     * Запросить разрешение на чтение контактов устройства.
     *
     * @param fragment - фрагмент.
     */
    fun requestPermissions(fragment: Fragment) {
        fragment.requestPermissions(arrayOf(READ_CONTACTS_PERMISSION_NAME), READ_CONTACTS_PERMISSION_REQUEST_CODE)
    }

    /**
     * Получить контакты в виде текста для шаринга.
     *
     * @param files - uri контактов.
     * @return строковое отображение этих контактов.
     */
    suspend fun getTextContacts(files: List<Uri>): String =
        withContext(Dispatchers.IO) {
            if (!isSharingWithContacts(files)) return@withContext ""

            val stringBuilder = StringBuilder()
            for (contactUri in files) {
                stringBuilder.append(getShareContactText(contactUri))
            }
            return@withContext stringBuilder.toString()
        }

    private suspend fun getShareContactText(uri: Uri): String {
        return if (uri.toString().endsWith(".vcf")) {
            getContactInfoFromVCF(uri)
        } else {
            // Если мы выбираем несколько контактов, то все uri пишутся подряд после /as_multi_vcard/
            // и разделителем является %3A
            if (uri.toString().contains("/as_multi_vcard/")) {
                val stringBuilder = StringBuilder()
                val lookupsString = uri.toString().split("/").last()
                val lookupList = lookupsString.split("%3A").map { it.toUri() }
                for (lookup in lookupList) {
                    val result = getContactInfoFromDatabase(lookup)
                    stringBuilder.appendLine(result)
                }
                stringBuilder.toString()
            } else {
                getContactInfoFromDatabase(uri)
            }
        }
    }

    private suspend fun getContactInfoFromVCF(uri: Uri): String {
        val stringBuilder = StringBuilder()
        val stream = context.contentResolver.openInputStream(uri)
        val bufferedReader = BufferedReader(InputStreamReader(stream, Charset.forName("UTF-8")))
        while (true) {
            val line = withContext(Dispatchers.IO) {
                bufferedReader.readLine()
            } ?: break
            if (line.startsWith(FULL_NAME_RUSSIAN) || line.startsWith(FULL_NAME_OTHER)) {
                val name = if (line.contains(QUOTED_PRINTABLE)) {
                    bufferedReader.getContactFullName(line.split(QUOTED_PRINTABLE)[1])
                } else {
                    line.replace(FULL_NAME_OTHER, "")
                }
                stringBuilder.appendLine("${context.getString(R.string.communicator_share_message_contact_name_row)} $name")
            } else if (line.startsWith(PHONE_PREFIX)) {
                val number = line.split(":")[1].replace("-", "")
                stringBuilder.appendLine(formatPhone(number))
            }
        }
        return stringBuilder.toString()
    }

    private suspend fun BufferedReader.getContactFullName(startOfName: String): String {
        var isLastNameLine = !startOfName.endsWith("=")
        val stringBuilder = StringBuilder().append(startOfName.removeSuffix("="))
        while (!isLastNameLine) {
            val line = withContext(Dispatchers.IO) {
                readLine()
            } ?: break
            isLastNameLine = !line.endsWith("=")
            stringBuilder.append(line.removeSuffix("="))
        }
        return QuotedPrintable.decode(stringBuilder.toString())
    }

    @SuppressLint("Range")
    private suspend fun getContactInfoFromDatabase(uri: Uri): String = withContext(Dispatchers.IO){
        val stringBuilder = StringBuilder()
        val lookup = uri.toString().split("/").lastOrNull() ?: uri

        val cursor: Cursor? = context.contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            null,
            ContactsContract.Contacts.LOOKUP_KEY + " = " + "\'$lookup\'",
            null,
            null,
        )
        while (cursor?.moveToNext() == true) {
            val id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.NAME_RAW_CONTACT_ID))
            val name: String = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
            stringBuilder.appendLine("${context.getString(R.string.communicator_share_message_contact_name_row)} $name")

            val phones: Cursor? = context.contentResolver.query(
                Phone.CONTENT_URI,
                null,
                Phone.CONTACT_ID + " = " + id,
                null,
                null,
            )
            while (phones?.moveToNext() == true) {
                val number: String = phones.getString(phones.getColumnIndex(Phone.NUMBER))
                stringBuilder.appendLine(number)
            }
            phones?.close()
        }
        cursor?.close()
        return@withContext stringBuilder.toString()
    }

    companion object {
        /**
         * Проверить список файлов на наличие контактов.
         *
         * @param files - uri файлов.
         * @return true - если файлы - контакты, false - иначе.
         */
        fun isSharingWithContacts(files: List<Uri>?): Boolean {
            return files?.let {
                files.isNotEmpty() && files.first().toString().contains(CONTACT_CONTENT)
            } ?: false
        }

        /**
         * Проверить разрешение на чтение контактов устройства.
         * Проверка обернута в try-catch из-за вероятности данной ошибки:
         * https://online.sbis.ru/opendoc.html?guid=26e958e4-107c-474d-b351-b7f825f91528
         *
         * @param context - контекст.
         * @return true - если разрешение есть.
         */
        fun checkContactsPermission(context: Context): Boolean {
            return try {
                ContextCompat.checkSelfPermission(context, READ_CONTACTS_PERMISSION_NAME) == PackageManager.PERMISSION_GRANTED
            } catch (error: Throwable) {
                Timber.d(error, "Error when trying check $READ_CONTACTS_PERMISSION_NAME permission!")
                false
            }
        }

        internal const val READ_CONTACTS_PERMISSION_REQUEST_CODE = 3
        private const val READ_CONTACTS_PERMISSION_NAME = android.Manifest.permission.READ_CONTACTS
        private const val CONTACT_CONTENT = "android.contacts"
        private const val QUOTED_PRINTABLE = "QUOTED-PRINTABLE:"
        private const val FULL_NAME_RUSSIAN = "FN;"
        private const val FULL_NAME_OTHER = "FN:"
        private const val PHONE_PREFIX = "TEL"
    }
}

// Заимствовано
private object QuotedPrintable {

    /**
     * Декодировать символы для vcf файла контакта.
     */
    fun decode(str: String): String {
        val bytes = ArrayList<Byte>()
        var i = 0
        while (i < str.length) {
            val currentChar = str[i]
            try {
                if (currentChar == '=' && i + 2 <= str.length - 1) {
                    val nextChar1 = str[i + 1]
                    val nextChar2 = str[i + 2]
                    val value = (nextChar1.toString() + nextChar2.toString()).toInt(16)
                    bytes.add(value.toByte())
                    i += 2
                    i++
                    continue
                }
            } catch (ignored: Exception) {}
            bytes.add(currentChar.code.toByte())
            i++
        }
        return try {
            String(toByteArray(bytes), Charset.forName("UTF-8"))
        } catch (ignored: UnsupportedEncodingException) {
            str
        }
    }

    private fun toByteArray(bytes: ArrayList<Byte>): ByteArray {
        val n = bytes.size
        val ret = ByteArray(n)
        for (i in 0 until n) {
            ret[i] = bytes[i]
        }
        return ret
    }
}
