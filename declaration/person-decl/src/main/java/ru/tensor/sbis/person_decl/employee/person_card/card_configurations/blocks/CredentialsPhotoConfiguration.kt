package ru.tensor.sbis.person_decl.employee.person_card.card_configurations.blocks

import android.os.Parcelable

/**
 * Конфигурация раздела 1 (Фотография + ФИО)
 *
 * @author ra.temnikov
 */
interface CredentialsPhotoConfiguration : Parcelable {
    /**
     * доступность перехода в календарь сотрудника
     */
    val isCalendarFeatureAvailable: Boolean
}
