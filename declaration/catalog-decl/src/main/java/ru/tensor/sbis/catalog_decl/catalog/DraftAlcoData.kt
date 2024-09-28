package ru.tensor.sbis.catalog_decl.catalog

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

/** Информация о маркированном разливном алкоголе. */
@Parcelize
data class DraftAlcoData(
    val isBeer: Boolean,
    val isLitrage: Boolean,
    val linkedNomUUIDs: Set<UUID>
) : Parcelable