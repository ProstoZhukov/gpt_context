package ru.tensor.sbis.person_decl.employee.person_card.card_configurations.blocks

import android.os.Parcelable

/**
 * Конфигурация достижений
 *
 * @author ra.temnikov
 */
interface BadgesConfiguration : Parcelable {
    /**
     * Доступность перехода на список достижений
     */
    val isClickToBadgesAvailable: Boolean

    /**
     * Доступность показа достижений с актуальными бейджами
     */
    val isContainerWithActualBadgesAvailable: Boolean

    /**
     * Доступность показа достижений без актуальных бейджей
     */
    val isContainerWithoutActualBadgesAvailable: Boolean
}