package ru.tensor.sbis.business_card_host_decl.ui.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/** Характеристики стиля визитки */
@Parcelize
data class BusinessCardStyleProperties
    (var accent: String?,
     var background: String?,
     var buttonTextColor: String?,
     var dominantColorRGB: String?,
     var headers: String?,
     var text: String?,
     var logo: String?,
     var picture: String?,
     var texture: String?
) : Parcelable