package ru.tensor.sbis.communicator.sbis_conversation.utils

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.ContactsContract
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import ru.tensor.sbis.common.R
import ru.tensor.sbis.common.util.CommonUtils
import ru.tensor.sbis.common.util.PermissionUtil
import java.util.ArrayList

/**
 * Хелпер для выполнений действий над номером телефона в переписке.
 *
 * @author da.zhukov
 */
internal class PhoneNumberActionHelper(private val fragment: Fragment) {

    private lateinit var phoneNumber: String

    /**
     * Сначала проверяет наличие разрешений для вызова, если их нет
     * запрашивает нужное разрешение, а если есть сразу выполняет вызов.
     * @see requestNotGrantedPermissions
     */
    fun tryCallTheNumber(phoneNumber: String) {
        this.phoneNumber = phoneNumber
        if (requestNotGrantedPermissions(REQUEST_CALL_PERMISSIONS, Manifest.permission.CALL_PHONE)) return
        callTheNumber(phoneNumber)
    }

    /**
     * Вызов номера.
     */
    private fun callTheNumber(phoneNumber: String) {
        val callIntent = Intent(Intent.ACTION_CALL, Uri.parse(phoneNumber))
        CommonUtils.startAction(fragment.requireContext(), callIntent, R.string.common_no_permission_error)
    }

    /**
     * Добавление номера в телефонную книгу.
     */
    fun addNumberToPhoneBook(phoneNumber: String) {
        val contactIntent = Intent(Intent.ACTION_INSERT).apply {
            type = ContactsContract.Contacts.CONTENT_TYPE
            putExtra(ContactsContract.Intents.Insert.PHONE, phoneNumber)
        }
        fragment.requireContext().startActivity(contactIntent)
    }

    /**
     * Открывает экран с набранным номером(дефолтная обработка нажатия на номер через LinkSpan).
     */
    fun dialPhoneNumber(phoneNumber: String) {
        CommonUtils.callPhone(fragment.requireContext(), Uri.parse(phoneNumber))
    }

    /**
     * Обработка запроса на выдачу разрешений.
     */
    fun onRequestPermissionsResult(requestCode: Int, grantResults: IntArray, error: () -> Unit) {
        if (requestCode == REQUEST_CALL_PERMISSIONS ) {
            when (PermissionUtil.verifyPermissions(grantResults)) {
                true -> callTheNumber(phoneNumber)
                false -> error()
            }
        }
    }

    /** @SelfDocumented */
    private fun requestNotGrantedPermissions(requestCode: Int, vararg requiredPermissions: String): Boolean {
        val notGrantedPermissions = ArrayList<String>().apply {
            requiredPermissions.filter {
                ContextCompat.checkSelfPermission(
                    fragment.context ?: return false,
                    it
                ) != PackageManager.PERMISSION_GRANTED
            }
                .forEach { add(it) }
        }
        return if (notGrantedPermissions.isNotEmpty()) {
            fragment.requestPermissions(requiredPermissions, requestCode)
            true
        } else {
            false
        }
    }
}

private const val REQUEST_CALL_PERMISSIONS = 23