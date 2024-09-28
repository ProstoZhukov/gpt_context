package ru.tensor.sbis.calendar_decl.calendar.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

/**
 * Открывать документ ГО/ИГО в режиме на себя для персоны [personUuid]
 */
@Parcelize
data class DocScheduleVacationSelfOpen(val personUuid: UUID): Parcelable

/**
 * Открывать документ ГО/ИГО с открытой панелью ДЗЗ
 */
@Parcelize
object DocScheduleVacationShowPassage: Parcelable

/**
 * Открывать документ с активной фазой [activeEvent]
 */
@Parcelize
data class DocScheduleVacationTaskOpen(val activeEvent: UUID?): Parcelable