package ru.tensor.sbis.link_share.ui.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Модель пользовательского пункта меню
 * @param menuItems Список пунктов меню
 * @param group Название группы, в которой будет отображаться пункт меню (опционально)
 */
@Parcelize
data class SbisLinkShareCustomMenuItem(
    val menuItems : List<SbisLinkShareMenuItemModel> = listOf(),
    val group: String = ""
) : Parcelable