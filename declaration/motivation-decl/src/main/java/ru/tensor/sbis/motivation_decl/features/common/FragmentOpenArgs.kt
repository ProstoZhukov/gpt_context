package ru.tensor.sbis.motivation_decl.features.common

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Общие аргументы для конфигурации открытия фрагментов Мотивации.
 */
@Parcelize
data class FragmentOpenArgs(
    /** Должен ли фрагмент закрываться через swipe */
    val swipeBackEnabled: Boolean = false,
    /** Выделено ли фрагменту место на экране вместе со строкой статус бара. */
    val givenSpaceForStatus: Boolean = false
) : Parcelable