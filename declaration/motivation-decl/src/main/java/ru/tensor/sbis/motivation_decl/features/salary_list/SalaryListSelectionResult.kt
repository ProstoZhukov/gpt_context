package ru.tensor.sbis.motivation_decl.features.salary_list

import android.os.Parcelable
import java.util.Date

/**
 * Контракт модели с результатом выбора на экране списка зарплаты.
 */

interface SalaryListSelectionResult : Parcelable {

    /** @SelfDocumented */
    interface Factory : Parcelable {
        /** @SelfDocumented */
        fun getResultForClickOnMonth(month: Date): SalaryListSelectionResult
        /** @SelfDocumented */
        fun getResultForClickOnWantAdvance(): SalaryListSelectionResult
        /** @SelfDocumented */
        fun getResultForClickOnCancelAdvance(): SalaryListSelectionResult
        /** @SelfDocumented */
        fun getResultForClickOnPayouts(): SalaryListSelectionResult
    }
}