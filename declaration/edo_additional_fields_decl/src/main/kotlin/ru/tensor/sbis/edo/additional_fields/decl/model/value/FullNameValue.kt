package ru.tensor.sbis.edo.additional_fields.decl.model.value

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/** @SelfDocumented */
@Parcelize
data class FullNameValue(
    var first: CharSequence? = null,
    var middle: CharSequence? = null,
    var last: CharSequence? = null,
) : Parcelable {

    val isEmpty: Boolean get() = first.isNullOrEmpty() && middle.isNullOrEmpty() && last.isNullOrEmpty()
}