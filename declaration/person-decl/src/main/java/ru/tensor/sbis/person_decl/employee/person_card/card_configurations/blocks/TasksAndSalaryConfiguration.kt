package ru.tensor.sbis.person_decl.employee.person_card.card_configurations.blocks

import android.os.Parcelable

/**
 * Конфигурация блока отображения задач и зарплаты
 *
 * @author ra.temnikov
 */
interface TasksAndSalaryConfiguration : Parcelable {
    /** Необходимо ли отображать задачи */
    val needShowTasks: Boolean

    /** Необходимо ли отображать зарплату */
    val needShowSalary: Boolean
}