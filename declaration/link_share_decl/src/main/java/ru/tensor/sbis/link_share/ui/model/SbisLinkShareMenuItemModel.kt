package ru.tensor.sbis.link_share.ui.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Модель пункта меню
 * @param type Тип пункта меню
 * @param icon Иконка пункта меню
 * @param title Название пункта меню
 * @param action Действие, выполняемое при выборе пункта
 */
@Parcelize
data class SbisLinkShareMenuItemModel(
    val type: SbisLinkShareMenuItem = SbisLinkShareMenuItem.CUSTOM,
    val icon: String = "",
    val title: String = "",
    val action: () -> Unit = {}
) : Parcelable