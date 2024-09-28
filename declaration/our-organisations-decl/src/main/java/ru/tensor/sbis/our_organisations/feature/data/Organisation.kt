package ru.tensor.sbis.our_organisations.feature.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

/**
 * Модель организации.
 *
 * @param originalId
 * @param uuid
 * @param name
 * @param inn
 * @param kpp
 * @param isSalePoint
 * @param isPrimaryOrg
 * @param parentId
 * @param parentUUID
 * @param isFolder
 * @param isEliminated
 * @param branchCode
 *
 * @author aa.mezencev
 */
@Parcelize
data class Organisation(
    val originalId: Int,
    val uuid: UUID,
    val name: String,
    val inn: String,
    val kpp: String?,
    val isSalePoint: Boolean,
    val isPrimaryOrg: Boolean,
    val parentId: Int?,
    val parentUUID: UUID?,
    val isFolder: Boolean,
    val isEliminated: Boolean,
    val branchCode: String?
) : Parcelable