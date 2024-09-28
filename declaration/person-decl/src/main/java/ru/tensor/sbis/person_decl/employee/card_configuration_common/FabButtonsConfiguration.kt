package ru.tensor.sbis.person_decl.employee.card_configuration_common

import android.os.Parcelable

/** Конфигурация блока 8, плавающие кнопки */
interface FabButtonsConfiguration<T : Parcelable> : Parcelable {

    /** Флаг необходимости кнопки создания нового диалога */
    val needSendMessageButton: Boolean

    /** Флаг необходимости кнопки звонка */
    val needPhoneCallButton: Boolean

    /** Флаг необходимости кнопки видеозвонка */
    val needVideoCallButton: Boolean

    /**
     * Конфигурация кнопки показа дополнительного меню
     * см. [PopupEtcMenuButtonConfiguration].
     * При отсутствии конфигурации кнопка не показывается.
     */
    val popupEtcMenuButtonConfiguration: T?
}