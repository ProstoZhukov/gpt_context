package ru.tensor.sbis.person_decl.employee.person_card.card_configurations.blocks

import android.os.Parcelable
import ru.tensor.sbis.person_decl.employee.card_configuration_common.FabButtonsConfiguration

/** Конфигурация плавающих кнопок для карточки сотрудника */
interface PersonCardFabButtonsConfiguration :
    FabButtonsConfiguration<PersonCardFabButtonsConfiguration.PopupEtcMenuButtonConfiguration> {
    /** Конфигурация кнопки дополнительного меню */
    interface PopupEtcMenuButtonConfiguration : Parcelable {
        /** Флаг для пункта переход в календарь */
        val needActionCalendarOpen: Boolean

        /** Флаг для пункта копирования ссылки на профиль */
        val needActionCopyLink: Boolean

        /** Флаг для пункта подписки/отписки от сотрудника */
        val needActionSubscribeToPerson: Boolean

        /** Флаг для пункта создания нового поощрения/взыскания */
        val needActionCreateAchievement: Boolean

        /** Флаг для пункта создания жалобы на сотрудника */
        val needActionComplain: Boolean
    }
}