package ru.tensor.sbis.business_card_host_decl.ui.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date
import java.util.UUID

/** Стиль визитки */
@Parcelize
data class BusinessCardStyle(
    var id: Int?,
    var name: String?,
    var selector: String?,
    var parent: Int?,
    var parentName: String?,
    var authorChanged: Int?,
    var createdTime: Date?,
    var updatedTime: Date?,
    var enabled: Boolean,
    var clientId: Int?,
    var type: Int?,
    var themeType: String?,
    var uuid: UUID?,
    var scopeType: String?,
    var scope: String?,
    var properties: BusinessCardStyleProperties?
) : Parcelable