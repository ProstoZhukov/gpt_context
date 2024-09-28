package ru.tensor.sbis.person_decl.motivation.money_provider

import io.reactivex.Observable
import ru.tensor.sbis.plugin_struct.feature.Feature
import java.util.UUID


/**
 * Провайдер общего количество денег по разделу мотивации,
 * для отображения в пунктах навигации.
 */
interface MotivationMoneyProvider : Feature {
    /**
     * @param profileUUID - UUID профиля. Если null, то для текущего.
     * @param salaryPeriod - Период для расчёта средней зарплаты
     * @param salaryType - Тип расчета средней зарплаты
     * @return возвращает значение по средней зарплате если есть права
     * или константу [SALARY_RESTRICTED], если прав нет
     */
    fun getAverageSalaryObservable(
        profileUUID: UUID? = null,
        salaryPeriod: SalaryPeriod = SalaryPeriod.YEAR,
        salaryType: SalaryType = SalaryType.WITHOUT_TAX
    ): Observable<AverageSalaryData>

    /**
     * Обновляет период для расчёта средней зарплаты
     * @param salaryPeriod - Период для расчёта средней зарплаты
     */
    fun updateAverageSalaryPeriod(salaryPeriod: SalaryPeriod)

    /**
     * @param profileUUID - UUID профиля. Если null, то для текущего.
     * @param salaryType - Тип расчета зарплаты
     * @return возвращает значение по зарплате если есть права
     * или константу [SALARY_RESTRICTED], если прав нет
     */
    fun getSalaryObservable(
        profileUUID: UUID? = null,
        salaryType: SalaryType = SalaryType.WITHOUT_TAX
    ): Observable<Long>

    companion object {
        /**
         * константа для [getAverageSalaryObservable]
         * возвращается в случае отстутствия прав
         */
        const val SALARY_RESTRICTED = -1L
    }
}