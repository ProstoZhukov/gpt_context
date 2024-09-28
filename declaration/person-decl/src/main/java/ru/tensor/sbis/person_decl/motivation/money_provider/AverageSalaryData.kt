package ru.tensor.sbis.person_decl.motivation.money_provider

/**
 * Модель данных по средней зарплате сотрудника
 *
 * @property salary - среднее значение зарплаты за [period].
 */
data class AverageSalaryData(val salary: Long, val period: SalaryPeriod)