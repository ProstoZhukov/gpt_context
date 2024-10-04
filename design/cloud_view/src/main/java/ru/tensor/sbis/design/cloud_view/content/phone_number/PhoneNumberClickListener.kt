package ru.tensor.sbis.design.cloud_view.content.phone_number

import java.util.UUID

/**
 * Слушатель нажатий на номер телефона в переписке.
 *
 * @author da.zhukov
 */
interface PhoneNumberClickListener {

    /**@SelfDocumented*/
    fun onPhoneNumberClicked(phoneNumber: String)

    /**@SelfDocumented*/
    fun onPhoneNumberLongClicked(phoneNumber: String, messageUUID: UUID? = null)
}