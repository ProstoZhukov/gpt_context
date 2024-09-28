package ru.tensor.sbis.edo.additional_fields.decl.model.value

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/** @SelfDocumented */
@Parcelize
data class FlagValue(val id: String, val title: String, var isSelected: Boolean) : Parcelable