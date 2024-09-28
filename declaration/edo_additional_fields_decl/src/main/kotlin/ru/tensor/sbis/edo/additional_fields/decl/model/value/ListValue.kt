package ru.tensor.sbis.edo.additional_fields.decl.model.value

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/** @SelfDocumented */
@Parcelize
data class ListItem(
    val id: String,
    val title: String,
    var isSelected: Boolean
) : Parcelable

/** @SelfDocumented */
@Parcelize
data class AppliedListItem(
    val id: Long,
    val cloudId: String,
    val title: String,
    val subtitle: String,
    val isFolder: Boolean
) : Parcelable