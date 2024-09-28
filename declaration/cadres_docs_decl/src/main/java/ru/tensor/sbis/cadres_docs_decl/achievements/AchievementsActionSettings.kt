package ru.tensor.sbis.cadres_docs_decl.achievements

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Модель с допустимыми действиями над документом ПиВ в доп.меню,
 * если на них есть разрешения с онлайна.
 */
@Parcelize
data class AchievementsActionSettings(
    val editingPermissible: Boolean = true,
    val deletionPermissible: Boolean = true
): Parcelable