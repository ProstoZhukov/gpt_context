package ru.tensor.sbis.calendar_decl.clients

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import timber.log.Timber
import java.util.Date

/**
 * Период события записи к мастеру
 * @property selectedStartTime время начала записи
 * @property selectedEndTime время окончания записи
 */
@Parcelize
class SpecialistTimeSelectionPeriod(
    val selectedStartTime: Date,
    val selectedEndTime: Date,
) : Parcelable {

    init {
        if (selectedEndTime.before(selectedStartTime)) {
            Timber.e("Start time should be less than end time")
        }
    }
}