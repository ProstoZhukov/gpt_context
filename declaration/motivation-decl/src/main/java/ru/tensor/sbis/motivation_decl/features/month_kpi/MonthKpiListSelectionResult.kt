package ru.tensor.sbis.motivation_decl.features.month_kpi

import android.os.Parcelable

/**
 * Модель с результатом выбора на экране списка KPI за месяц.
 */
interface MonthKpiListSelectionResult: Parcelable {

    /** @SelfDocumented */
    val selection: MonthKpiListSelection

    /** Фабрика для создания возвращаемого результата на основе выбранного экрана. */
    interface Factory : Parcelable {

        /** @SeflDocumented */
        fun getResult(selection: MonthKpiListSelection.KpiDetails): MonthKpiListSelectionResult

    }
}