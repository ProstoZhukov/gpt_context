package ru.tensor.sbis.person_decl.employee.my_profile.card_configurations.blocks

import android.os.Parcelable
import ru.tensor.sbis.person_decl.employee.card_configuration_common.FabButtonsConfiguration

/** Конфигурация плавающих кнопов для модуля "Мой профиль" */
interface MyProfileFabButtonsConfiguration :
    FabButtonsConfiguration<MyProfileFabButtonsConfiguration.EtcMenuButtonConfiguration> {

    /**
     * Конфигурация кнопки дополнительного меню
     */
    interface EtcMenuButtonConfiguration : Parcelable {
        val possibilityToAddWorkPhone: Boolean
        val possibilityToAddMobilePhone: Boolean
        val possibilityToAddHomePhone: Boolean
        val possibilityToAddTelegram: Boolean
        val possibilityToAddEmail: Boolean
        val possibilityToAddSkype: Boolean
    }
}