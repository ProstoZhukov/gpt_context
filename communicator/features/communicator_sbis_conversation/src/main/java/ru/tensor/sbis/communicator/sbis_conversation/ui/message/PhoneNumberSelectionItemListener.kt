package ru.tensor.sbis.communicator.sbis_conversation.ui.message

/**
 * Слушатель выбранного пункта действия с номером телефона(копировать, позвонить, добавить).
 *
 * @author da.zhukov
 */
internal interface PhoneNumberSelectionItemListener {

    /**
     * Обработать клик на выбранное действие с номером телефона.
     *
     * @param actionOrder порядковый номер действия.
     */
    fun onPhoneNumberActionClick(actionOrder: Int)
}

/**
 * Обработчик ошибки проверки номера телефона.
 *
 * @author da.zhukov
 */
internal interface PhoneNumberVerificationErrorHandler {

    /** @SelfDocumented */
    fun onPhoneVerificationRequired(message: CharSequence?)
}