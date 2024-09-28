package ru.tensor.sbis.calendar_decl.clients

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/** Результат выбора периода [period] на экране записи к специалисту */
@Parcelize
data class SpecialistTimeSelectionResult(val period: SpecialistTimeSelectionPeriod, val queueIds: ArrayList<Int>) :
    Parcelable

/** @SelfDocumented */
const val SPECIALIST_TIME_SELECTION_RESULT_KEY = "specialist_time_selection_result_key"

/** @SelfDocumented */
const val SPECIALIST_TIME_SELECTION_BUNDLE_KEY = "specialist_time_selection_bundle_key"