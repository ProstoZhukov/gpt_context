package ru.tensor.sbis.motivation_decl.features.kpi_scheme_filter

import android.os.Parcelable

/**
 * Модель с результатом выбора фильтра схем KPI.
 */
interface KpiSchemeFilterSelectionResult: Parcelable {

    /** @SelfDocumented */
    val schemeId: Long

    /** @SelfDocumented */
    val schemeName: String

    /** Фабрика для создания возвращаемого результата на основе выбранного действия. */
    interface Factory : Parcelable {

        /** @SelfDocumented */
        fun getResult(schemeId: Long, schemeName: String): KpiSchemeFilterSelectionResult
    }
}