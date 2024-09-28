package ru.tensor.sbis.wheel_time_picker.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.joda.time.LocalDateTime

/**
 * Предназначен для хранения и передачи параметров конфигурации пикера.
 *
 * @author us.bessonov
 */
@Parcelize
internal class TimePickerParameters(
    val defaultAllDayLong: Boolean,
    val defaultStartDateTime: LocalDateTime?,
    val defaultEndDateTime: LocalDateTime?,
    val periodPickerMode: PeriodPickerMode?,
    val durationMode: DurationMode?,
    val minutesStep: Int,
    val canCreateZeroLengthEvent: Boolean,
    val isOneDay: Boolean,
    val viewModelKey: String? = null
) : Parcelable