package ru.tensor.sbis.communicator.contacts_registry.ui.addcontacts

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Модель результата добавления контакта
 *
 * @property success true, если контакт успешно добавлен в реестр контактов
 * @property message строковый литерал сообщения для пользователя
 *
 * @author da.zhukov
 */
@Parcelize
internal data class AddContactResult(val success: Boolean, val message: String) : Parcelable
