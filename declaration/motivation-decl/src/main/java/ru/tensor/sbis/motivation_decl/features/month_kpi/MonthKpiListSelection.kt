package ru.tensor.sbis.motivation_decl.features.month_kpi

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date
import java.util.UUID

/** Набор поддерживаемых экранов, доступных к открытию с экрана KPI за месяц. */
sealed class MonthKpiListSelection : Parcelable {

    /** Преобразовать экран в результат выбора. */
    abstract fun toResult(factory: MonthKpiListSelectionResult.Factory): MonthKpiListSelectionResult

    @Parcelize
    data class KpiDetails(val kpiId: Long, val personUuid: UUID, val month: Date) : MonthKpiListSelection() {

        override fun toResult(factory: MonthKpiListSelectionResult.Factory): MonthKpiListSelectionResult {
            return factory.getResult(this)
        }
    }
}