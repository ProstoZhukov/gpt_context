package ru.tensor.sbis.person_decl.employee.person_card.card_configurations.blocks

import android.os.Parcelable

/**
 * Конфигурация раздела "О себе"
 * @property isBirthdayVisible - Нужно ли показывать ли дату рождения.
 * @property isAboutMeTextVisible - Нужно ли показывать информацию "О себе".
 *
 * @author ra.temnikov
 */
interface AboutMeConfiguration : Parcelable {
    val isBirthdayVisible: Boolean
    val isAboutMeTextVisible: Boolean
}