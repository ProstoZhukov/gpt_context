package ru.tensor.sbis.catalog_decl.warehouse.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

/**
 *  Модель данных склад.
 *
 *  @author mv.ilin
 */
@Parcelize
data class WarehouseData(
    val uuid: UUID,
    val id: Long,
    val isFolder: Boolean?,
    val name: String?,
    val parent: UUID?,
    val isInUse: Boolean?,
    val searchRange: List<Pair<Int, Int>> = listOf()
) : Parcelable