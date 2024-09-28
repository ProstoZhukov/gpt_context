package ru.tensor.sbis.edo.additional_fields.decl.model.value

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/** @SelfDocumented */
@Parcelize
data class DoubleValue(val value: Double?) : Parcelable {

    override fun toString(): String = value?.toBigDecimal()?.stripTrailingZeros()?.toPlainString() ?: ""
}