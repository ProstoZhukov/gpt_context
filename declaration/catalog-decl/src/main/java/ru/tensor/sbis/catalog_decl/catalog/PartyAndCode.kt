package ru.tensor.sbis.catalog_decl.catalog

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

/** Штрихкод */
@Parcelize
data class PartyAndCode(
    val code: String?,
    val type: BarcodeType?,
    var id: Long? = null,
    var packing: Long? = null,
    var pack: Packs? = null,
    var partyCode: String? = null,
    var order: Int? = null
) : Parcelable, Serializable