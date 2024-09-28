package ru.tensor.sbis.edo.additional_fields.decl.model.value

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/** @SelfDocumented */
@Parcelize
data class IpValue(
    var firstOctet: Int? = null,
    var secondOctet: Int? = null,
    var thirdOctet: Int? = null,
    var fourthOctet: Int? = null,
) : Parcelable {

    constructor(octetsSplitByDot: String?) : this(octetsSplitByDot?.split('.'))

    constructor(octets: List<String>?) : this(
        firstOctet = octets?.getOrNull(0)?.toIntOrNull(),
        secondOctet = octets?.getOrNull(1)?.toIntOrNull(),
        thirdOctet = octets?.getOrNull(2)?.toIntOrNull(),
        fourthOctet = octets?.getOrNull(3)?.toIntOrNull()
    )

    fun asSplitByDotString(): String? =
        if (isEmpty) {
            null
        } else {
            "${firstOctet}.${secondOctet}.${thirdOctet}.${fourthOctet}"
        }

    val isEmpty: Boolean get() = firstOctet == null || secondOctet == null || thirdOctet == null || fourthOctet == null
}