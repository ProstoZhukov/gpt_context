package ru.tensor.sbis.hallscheme.v2.business.model.tableinfo

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

/**
 * Элемент обводки для нескольких объектов.
 */
@Parcelize
data class TableOutline(val outline: Outline, val tableIds: List<UUID>) : Parcelable