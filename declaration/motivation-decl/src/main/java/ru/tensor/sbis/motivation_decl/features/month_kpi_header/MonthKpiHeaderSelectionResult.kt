package ru.tensor.sbis.motivation_decl.features.month_kpi_header

import android.os.Parcelable
import java.util.Date
import java.util.UUID

/**
 * Модель с результатом выбора c шапки KPI за месяц.
 */
interface MonthKpiHeaderSelectionResult: Parcelable {

    /** @SelfDocumented */
    val personUuid: UUID

    /** @SelfDocumented */
    val month: Date

    /** @SelfDocumented */
    val schemeId: Long

    /** @SelfDocumented */
    val schemeName: String

    /** Фабрика для создания возвращаемого результата на основе выбранного экрана. */
    interface Factory : Parcelable {

        /** @SelfDocumented */
        fun getResult(personUuid: UUID, month: Date, schemeId: Long, schemeName: String): MonthKpiHeaderSelectionResult

    }
}