package ru.tensor.sbis.edo.additional_fields.decl.model.value

import android.os.Parcelable
import androidx.annotation.IntRange
import kotlinx.parcelize.Parcelize

/**
 * Значение времени
 *
 * @author sa.nikitin
 */
@Parcelize
data class TimeValue(
    @IntRange(from = 0, to = 23) val hours: Int,
    @IntRange(from = 0, to = 59) val minutes: Int
) : Parcelable